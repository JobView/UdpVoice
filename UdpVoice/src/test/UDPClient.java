package test;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import android.util.Log;

public class UDPClient extends UDPAgent {

	String serverName;
	int serverPort;
	SocketAddress server;

	private static UDPClient client;

	public static UDPClient getInstance() {
		if (client == null) {
			client = new UDPClient("192.168.2.213", 2015, -1);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						client.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}

		return client;
	}

	private UDPClient(String host, int port, int localPort) {
		super(localPort);
		this.server = new InetSocketAddress(host, port);
	}

	public void start() throws Exception {
		init();
		register();
	}

	public void report(DatagramPacket rec) throws Exception {
		String s = rec.getSocketAddress()
				+ new String(rec.getData(), rec.getOffset(), rec.getLength());
		byte[] buf = s.getBytes();
		ds.send(new DatagramPacket(buf, buf.length, server));
	}

	public void register() throws Exception {
		String msg = "register " + getLocalAddress() + " " + ds.getLocalPort();
		Log.i("address", msg);
		doSend(server, msg.getBytes());

		new Thread(new Runnable() {
			String msg = "I am alive";

			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(10000);
						doSend(server, msg.getBytes());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	public String getLocalAddress() throws Exception {
		InetAddress addr = InetAddress.getLocalHost();
		return addr.getHostAddress();
	}
}