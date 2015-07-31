package com;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * @author Leo Luo
 * 
 */
public class UDPAgent implements Runnable {
	public static List<String> list = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {
		new UDPAgent(-1).start();
	}

	DatagramSocket ds;
	byte[] recbuf = new byte[1024];
	DatagramPacket rec = new DatagramPacket(recbuf, recbuf.length);
	int port;

	public UDPAgent(int port) {
		this.port = port;
	}

	public void init() throws Exception {
		if (port < 1024 || port > 655535) {
			ds = new DatagramSocket();
		} else {
			ds = new DatagramSocket(port);
		}
	}

	public void start() throws Exception {
		println("start");
		println("LocalPort:" + port);
		init();
		new Thread(this).start();// recive thread
		receive();
	}
	public void receive() {
		for (;;) {
			try {
				ds.receive(rec);
				String msg = new String(rec.getData(), rec.getOffset(),
						rec.getLength());
				String line = rec.getSocketAddress()+"";
				
				line = line.substring(1);
				if (msg.startsWith("register")) {
					
					list.add(line);
					System.out.println("reg +++++++++++ "+line);
				}else {
					for (int i = 0; i < list.size(); i++) {
						if (!list.get(i).equals(line)) {
							String[] str = list.get(i).split(":");
							byte[] data = new byte[rec.getLength()];
							System.arraycopy(rec.getData(), 0, data, 0, rec.getLength());
							doCommand(data, str[0], Integer.parseInt(str[1]));
						}
					}
				}
				onReceive(rec);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onReceive(DatagramPacket rec) {
	}

	public void doCommand(byte[] msg,String ip, int port) throws Exception {
		InetSocketAddress target = new InetSocketAddress(ip, port);
		doSend(target, msg);
	}

	public void doSend(SocketAddress addr, byte[] data) throws Exception {
		DatagramPacket pack = new DatagramPacket(data, data.length, addr);
		ds.send(pack);
	}

	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			String line = reader.readLine();
			while (!"exit".equals(line)) {
				doCommand(line.getBytes(),"127.0.0.1",2008);
				line = reader.readLine();
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void println(String s) {
		System.out.println(System.currentTimeMillis() + ":" + s);
	}
}