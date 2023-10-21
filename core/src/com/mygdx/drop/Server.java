package com.mygdx.drop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.utils.Disposable;

public class Server implements Disposable {
	private final UDPThread udpThread;
	public Server() {
		UDPThread udpThread = null;
		try {
			udpThread = new UDPThread(5669, InetAddress.getByName("192.168.0.18"), this::recievedPacket);
			System.out.println("server thread created");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.udpThread = udpThread;
		udpThread.start();
		System.out.println("server thread started");
	}
	
	private final void recievedPacket(DatagramPacket packet) {
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(byteArrayStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("server received packet size: " + packet.getLength());
		try {
			outputStream.writeObject(new TestObject(10, 15, Assets.Textures.DebugBox_bucket.getId()));
			System.out.println("Asset id:" + Assets.Textures.DebugBox_bucket.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] payload = byteArrayStream.toByteArray();
		System.out.println("About to send " + payload.length + " bytes");
		try {
			udpThread.socket.send(new DatagramPacket(payload, payload.length, packet.getSocketAddress()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
		udpThread.interrupt();
	}
	

	
}
