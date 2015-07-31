package com;

public class UDPServer extends UDPAgent {
	
	public static void main(String[] args) throws Exception {
		new UDPServer(2015).start();
	}

	public UDPServer(int port) {
		super(port);
	}

}
