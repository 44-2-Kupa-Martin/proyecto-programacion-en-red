package com.mygdx.drop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.function.Consumer;

public class UDPThread extends Thread {

	public final DatagramSocket socket;
	public final InetAddress serverIP;
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
	
	@Override
	public void run() {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
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
}
