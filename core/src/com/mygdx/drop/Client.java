package com.mygdx.drop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.drop.etc.EventCapable;
import com.mygdx.drop.etc.events.listeners.EventListener;

public class Client implements Disposable {
	private final UDPThread udpThread;
	
	public Client() {
		UDPThread udpThread = null;
		try {
			udpThread = new UDPThread(InetAddress.getByName("192.168.0.18"), 5669, this::recievedPacket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.udpThread = udpThread;
		udpThread.start();
	}
	
	public final void sendString(String string) {
		byte[] payload = string.getBytes();
		DatagramPacket packet = new DatagramPacket(payload, payload.length);
		try {
			udpThread.socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final void recievedPacket(DatagramPacket packet) {
		System.out.println("Client received packet size: " + packet.getLength());
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			TestObject message = (TestObject)input.readObject();
			System.out.println(message.num1);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
