package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection implements Runnable {
	
	private Server server;
	private ServerSocket serverSocket;
	
	Connection(Server server) throws IOException {
		this.server = server;
		this.serverSocket = new ServerSocket(server.getPort());
		System.out.println("Server is listening on port " + server.getPort() + "...");
	}
	
	public void run() {
		while(true) { // Attend et gère en permanence les demandes de connections de nouveau clients
			try {
				// 1: Un client essaie de communiquer via la socket du server, on accepte
				Socket sockNewClient = serverSocket.accept();
				// accept() est bloquante: exec stoppée si pas de connex
				
				// 2: On créer un ConnectedClient (sans nom pour l'instant) 
				// et l'ajoute à la liste 'Server.clients'
				ConnectedClient newClient = new ConnectedClient(server, sockNewClient);
				server.addClient(newClient);
				
				// 3: On lance un thread sur ConnectedClient
				Thread threadNewClient = new Thread(newClient);
				threadNewClient.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
