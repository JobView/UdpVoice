package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 
 * @author Leo Luo
 * 
 */
public class UDPAgent{
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
//		receive();
	}

	
	public DatagramSocket getDatagramSocket(){
		return ds;
	}
//	public void receive() {
//		for (;;) {
//			try {
//				ds.receive(rec);
//				String msg = new String(rec.getData(), rec.getOffset(),
//						rec.getLength());
//				String line = rec.getSocketAddress() + ":" + msg;
//				println(line);
//				onReceive(rec);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

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

	public void println(String s) {
		System.out.println(System.currentTimeMillis() + ":" + s);
	}
}