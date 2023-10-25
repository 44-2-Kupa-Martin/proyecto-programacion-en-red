package com.mygdx.drop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.drop.etc.EventCapable;
import com.mygdx.drop.etc.events.listeners.EventListener;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.PlayerManager;
import com.mygdx.drop.game.Stats;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.protocol.InputReport;
import com.mygdx.drop.game.protocol.InputReport.Type;
import com.mygdx.drop.game.protocol.InventoryUpdate;
import com.mygdx.drop.game.protocol.SessionRequest;
import com.mygdx.drop.game.protocol.SessionResponse;
import com.mygdx.drop.game.protocol.WorldUpdate;

public class Client implements Disposable, PlayerManager {
	private String playerName;
	private final UDPThread udpThread;
	private FrameComponent[] lastestFrameData;
	private PhonyItem[] latestItemData;
	private Stats playerStats;
	private int lastSelectedSlot;
	private Vector2 lastPlayerPosition;
	public volatile boolean notConnected = true;
	private int worldWidth_tl, worldHeight_tl;
	private static Vector2 temp = new Vector2();
	
	
	public Client(String playerName) {
		this.playerName = playerName;
		UDPThread udpThread = null;
		try {
			udpThread = new UDPThread(InetAddress.getByName("127.0.0.1"), 5669, this::recievedPacket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.lastPlayerPosition = new Vector2();
		this.lastestFrameData = new FrameComponent[0];
		this.udpThread = udpThread;
		udpThread.start();
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new SessionRequest(playerName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final void recievedPacket(DatagramPacket packet) {
		ObjectInputStream input = null;
		Serializable object = deserializeObjectFromPacket(packet);
		if (object == null) {
			System.out.println("Couldnt deserialize");
		} else if (object instanceof WorldUpdate) {
			WorldUpdate update = (WorldUpdate) object;
			this.lastestFrameData = update.frameData;
			this.playerStats = update.playerStats; 
			this.lastSelectedSlot = update.lastSelectedSlot;
			this.lastPlayerPosition.set(update.playerX, update.playerY);
			this.latestItemData = new PhonyItem[update.itemData.length];
			for (int i = 0; i < update.itemData.length; i++) {
				ItemData data = update.itemData[i];
				latestItemData[i] = data != null ? new PhonyItem(data.category, data.textureId) : null;
			}
			this.notConnected = false;
		} else if (object instanceof SessionResponse) {
			SessionResponse response = (SessionResponse)object;
			System.out.println("Received session response. Request " + (response.accepted ? "succeeded" : "failed"));
			if (response.accepted) {
				this.worldHeight_tl = response.worldHeight_tl;
				this.worldWidth_tl = response.worldWidth_tl;
			}
		} else {
			System.out.println("dont know what the fuck I got");
		}
	}
	
	private final DatagramPacket serializeObjectToPacket(SocketAddress address, Serializable object) {
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(byteArrayStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			outputStream.writeObject(object);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] payload = byteArrayStream.toByteArray();
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DatagramPacket(payload, payload.length, address);
	}
	
	private final <ObjectType extends Serializable> ObjectType deserializeObjectFromPacket(DatagramPacket packet) {
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ObjectType object = null;
		try {
			object = (ObjectType)input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	@Override
	public void dispose() {
		udpThread.interrupt();
	}

	@Override
	public boolean keyDown(String playerName, int keycode) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.keyDown, keycode)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true; 
	}

	@Override
	public boolean keyUp(String playerName, int keycode) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.keyUp, keycode)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true; 
	}

	@Override
	public boolean keyTyped(String playerName, char character) { 
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.keyTyped, character)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true; 
	}

	@Override
	public boolean touchDown(String playerName, float worldX, float worldY, int pointer, int button) { 
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.touchDown, worldX, worldY, pointer, button)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true; 
	}

	@Override
	public boolean touchUp(String playerName, float worldX, float worldY, int pointer, int button) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.touchUp, worldX, worldY, pointer, button)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true; 
	}

	@Override
	public boolean touchCancelled(String playerName, float worldX, float worldY, int pointer, int button) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.touchCancelled, worldX, worldY, pointer, button)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true; 
	}

	@Override
	public boolean touchDragged(String playerName, float worldX, float worldY, int pointer) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.touchDragged, worldX, worldY, pointer)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true; 
	}

	@Override
	public boolean mouseMoved(String playerName, float worldX, float worldY) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.mouseMoved, worldX, worldY)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean scrolled(String playerName, float amountX, float amountY) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InputReport(playerName, Type.scrolled, 0, 0, amountX, amountX, 0, 0, 0, (char) 0)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public Vector2 getPlayerPosition(String playerName) { return lastPlayerPosition; }

	@Override
	public int getWorldHeight() { return Constants.WORLD_HEIGHT_tl; }

	@Override
	public int getWorldWidth() { return Constants.WORLD_WIDTH_tl; }

	@Override
	public FrameComponent[] getFrameData() { return lastestFrameData; }

	@Override
	public Item getItem(String playerName, int index) { return this.latestItemData[index]; }

	@Override
	public Item getCursorItem(String playerName) { return latestItemData[Player.PlayerInventory.CURSOR_ITEM]; }

	@Override
	public int getSelectedSlot(String playerName) { return lastSelectedSlot; }

	@Override
	public void swapItem(String playerName, int index1, int index2) {
		try {
			udpThread.socket.send(serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new InventoryUpdate(playerName, InventoryUpdate.Type.ITEM_SWAP, index1, index2)));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public Stats getStats(String playerName) { return playerStats; }

}
