package com.mygdx.drop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.function.Consumer;

public class UDPThread extends Thread {

	public final DatagramSocket socket;
	private final InetAddress serverIP;
	private final Consumer<DatagramPacket> onRecieve;
	
	public UDPThread(int runningPort, InetAddress serverIP, Consumer<DatagramPacket> onReceive) {
		this.serverIP = serverIP;
		DatagramSocket socket = null;
		try {
			socket = runningPort == -1 ? new DatagramSocket() : new DatagramSocket(runningPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.socket = socket;
		try {
			socket.setSoTimeout(500);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.onRecieve = onReceive;
	}
	
	
	public UDPThread(InetAddress serverIP, int serverPort, Consumer<DatagramPacket> onReceive) {
		this(-1, serverIP, onReceive);
		socket.connect(serverIP, serverPort);
	}	
	
	public UDPThread(int runningPort, InetAddress serverIP, int serverPort, Consumer<DatagramPacket> onReceive) {
		this(runningPort, serverIP, onReceive);
		socket.connect(serverIP, serverPort);
	}
	
	public UDPThread(int runningPort, Consumer<DatagramPacket> onReceive) {
		this(runningPort, null, onReceive);
		
	}
	
	@Override
	public void run() {
		DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
		while (true) {
			try {
				socket.receive(packet);;
				if (onRecieve != null) 
					onRecieve.accept(packet);
			} catch (SocketTimeoutException e) {
				if (isInterrupted())
					break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
	}
	
	public static final DatagramPacket serializeObjectToPacket(SocketAddress address, Serializable object) {
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
	
	public static final <ObjectType extends Serializable> ObjectType deserializeObjectFromPacket(DatagramPacket packet) {
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ObjectType object = null;
		try {
			object = (ObjectType) input.readObject();
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
	
}
