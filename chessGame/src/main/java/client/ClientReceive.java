package client;

import java.io.IOException;
import java.io.ObjectInputStream;

import common.Message;

public class ClientReceive implements Runnable{
	
	Client client;
	ObjectInputStream in;
	
	ClientReceive(Client client) throws IOException {
		this.client = client;
		this.in = client.getObjInputStream();
	}
	
	public void run() {
		Message mess;
		while(true) {
			try {
				mess = (Message) in.readObject();
				client.messageReceived(mess);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
