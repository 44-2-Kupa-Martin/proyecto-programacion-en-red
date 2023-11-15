package com.mygdx.drop.game;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.drop.UDPThread;
import com.mygdx.drop.WorldDiscovery;
import com.mygdx.drop.game.PlayerManager.FrameComponent;
import com.mygdx.drop.game.PlayerManager.ItemData;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.protocol.DiscoverWorld;
import com.mygdx.drop.game.protocol.InputReport;
import com.mygdx.drop.game.protocol.InventoryUpdate;
import com.mygdx.drop.game.protocol.SessionRequest;
import com.mygdx.drop.game.protocol.SessionResponse;
import com.mygdx.drop.game.protocol.TerminateSession;
import com.mygdx.drop.game.protocol.WorldUpdate;

public class ServerThread extends Thread implements Disposable {
	/** Measured in seconds */
	public final float deltaT;
	public final World world;
	private final UDPThread udpThread;
	private long lastUpdate;
	private final HashMap<Class<? extends Serializable>, MessageProccesor> knownObjects;
	private final HashMap<String, PlayerSessionData> sessions;
	private final ConcurrentLinkedQueue<Message> messageQueue;
	private final String playerHost;
	
	//Agregar un mapeo a DiscoverWorld
	
	public ServerThread(int worldWidth_tl, int worldHeight_tl, Vector2 gravity, float deltaT, String playerHost) {
		UDPThread udpThread = null;
		udpThread = new UDPThread(5669, this::recievedPacket, ()->{});
		System.out.println("server thread created");
		this.udpThread = udpThread;
		this.sessions = new HashMap<>();
		this.knownObjects = new HashMap<>();
		this.messageQueue = new ConcurrentLinkedQueue<>();
		knownObjects.put(SessionRequest.class, this::handleSessionRequest);
		knownObjects.put(InputReport.class, this::handleInputReport);
		knownObjects.put(InventoryUpdate.class, this::handleInventoryUpdate);
		knownObjects.put(DiscoverWorld.class, this::handleDiscoverWorld);
		knownObjects.put(TerminateSession.class, this::handleTerminateSession);
		this.world = new World(worldWidth_tl, worldHeight_tl, gravity);
		this.deltaT = deltaT;
		this.lastUpdate = 0;
		this.playerHost = playerHost;
		udpThread.start();
		System.out.println("server thread started");
	}

	@Override
	public void run() {
		while (!ServerThread.interrupted()) {
			while (!messageQueue.isEmpty()) {
				Message message = messageQueue.remove();
				knownObjects.get(message.deserializedObject.getClass()).process(message.sourceDatagram, message.deserializedObject);
			}

			if (TimeUtils.timeSinceMillis(lastUpdate) >= deltaT * 1000) {
				FrameComponent[] frameData = world.getFrameData();
				for (PlayerSessionData session : sessions.values()) {
					Vector2 playerPos = world.getPlayerPosition(session.name);
					ItemData[] itemData = new ItemData[Player.PlayerInventory.N_ITEMS];
					for (int i = 0; i < itemData.length; i++) {
						Item item = session.player.items.items[i].get();
						itemData[i] = item != null ? new ItemData(item.getCategory(), item.getTextureId()) : null;
					}
					WorldUpdate worldUpdate = new WorldUpdate(frameData, itemData, world.getSelectedSlot(session.name), playerPos.x, playerPos.y, session.player.getStats());
					try {
						DatagramPacket packet = UDPThread.serializeObjectToPacket(session.address, worldUpdate);
						udpThread.socket.send(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				world.step(deltaT);
				this.lastUpdate = TimeUtils.millis();
			}
		}
			
	}

	private final void recievedPacket(DatagramPacket packet) {
		Serializable object = UDPThread.deserializeObjectFromPacket(packet);
		if (object == null)
			return;
		if (!knownObjects.containsKey(object.getClass()))
			return;
		messageQueue.add(new Message(packet, object));
	}

	private final void handleSessionRequest(DatagramPacket packet, Serializable message) {
		SessionRequest request = (SessionRequest) message;

		if (sessions.containsKey(request.playerName)) {
			try {
				udpThread.socket.send(UDPThread.serializeObjectToPacket(packet.getSocketAddress(), new SessionResponse(false, 0, 0)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Player player = world.createEntity(new Player.Definition(request.playerName, 0, 10));
			sessions.put(request.playerName, new PlayerSessionData(request.playerName, packet.getSocketAddress(), player));
			try {
				udpThread.socket.send(UDPThread.serializeObjectToPacket(packet.getSocketAddress(),
						new SessionResponse(true, world.worldWidth_tl, world.worldHeight_tl)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	private final void handleInputReport(DatagramPacket packet, Serializable message) {
		InputReport report = (InputReport) message;

		if (!sessions.containsKey(report.playerName))
			return;
		
		if (sessions.get(report.playerName).player.getStats().isDead()) {
			return;
		}
		switch (report.type) {
			case keyDown:
				world.keyDown(report.playerName, report.keyCode);
				return;
			case keyTyped:
				world.keyTyped(report.playerName, report.character);
				return;
			case keyUp:
				world.keyUp(report.playerName, report.keyCode);
				return;
			case mouseMoved:
				world.mouseMoved(report.playerName, report.worldX_mt, report.worldY_mt);
				return;
			case scrolled:
				world.scrolled(report.playerName, report.scrollAmountX, report.scrollAmountY);
				return;
			case touchDown:
				world.touchDown(report.playerName, report.worldX_mt, report.worldY_mt, report.pointer, report.button);
				return;
			case touchDragged:
				world.touchDragged(report.playerName, report.worldX_mt, report.worldY_mt, report.pointer);
				return;
			case touchUp:
				world.touchUp(report.playerName, report.worldX_mt, report.worldY_mt, report.pointer, report.button);
				return;
			case touchCancelled:
				world.touchCancelled(report.playerName, report.worldX_mt, report.worldY_mt, report.pointer, report.button);
				return;
		}
		throw new RuntimeException("Unreachable");
	}
	
	private final void handleInventoryUpdate(DatagramPacket packet, Serializable message) {
		InventoryUpdate update = (InventoryUpdate) message;
		switch (update.type) {
			case ITEM_SWAP:
				world.swapItem(update.playerName, update.swapIndex1, update.swapIndex2);
				return;
		}
		throw new RuntimeException("unreachable");

	}
	
	private final void handleDiscoverWorld(DatagramPacket packet, Serializable message) {
		
		try {
			System.out.println("Llego en el handle");
			System.out.println(packet.getSocketAddress().toString());
			WorldDiscovery worldDiscovery = new WorldDiscovery(world.worldName, InetAddress.getLocalHost());
			try {
				udpThread.socket.send(UDPThread.serializeObjectToPacket(packet.getSocketAddress(), worldDiscovery));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private final void handleTerminateSession(DatagramPacket packet, Serializable message) {
		
		TerminateSession terminateSession = (TerminateSession) message;
		
		sessions.remove(terminateSession.playerName).player.dispose();;
		
		if(terminateSession.playerName == playerHost) {
	
			dispose();
		}
		
	}


	@Override
	public void dispose() { 
		
		
		udpThread.interrupt();
		this.interrupt();
	
	}

	private class Message {
		public final DatagramPacket sourceDatagram;
		public final Serializable deserializedObject;
		public Message(DatagramPacket sourceDatagram, Serializable deserializedObject) {
			super();
			this.sourceDatagram = sourceDatagram;
			this.deserializedObject = deserializedObject;
		}

		

	}

	private class PlayerSessionData {
		public final String name;
		public final SocketAddress address;
		public final Player player;

		public PlayerSessionData(String name, SocketAddress address, Player player) {
			this.name = name;
			this.address = address;
			this.player = player;
		}

	}

	private interface MessageProccesor {
		void process(DatagramPacket packet, Serializable message);

	}

}
