package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Message;

public class ConnectedClient implements Runnable {
	
	private static int nbClients = 0;
	private int id; // id du client ( = 'nbclients')
	private String name; // 
	private Server server; // référence vers le serveur
	private Socket socket; // référence vers la socket du client (qui est lié avec la socket du server `serverSocket` puisque je l'ai `.accept()`)
	private ObjectOutputStream out; //  va permettre d'envoyer des messages au client (en utilisant le flux de sortie de sa socket)
	private ObjectInputStream in; // va permettre de recevoir des messages du client
	private Game game;
	
	ConnectedClient(Server server, Socket socket) throws IOException {
		this.id = nbClients;
		this.server = server;
		this.socket = socket;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
		nbClients++;
		System.out.println("Client id=" + id + " created and connected");

		Message mess = new Message(Message.TypeMsg.ID, Integer.toString(id));
		this.sendMessage(mess);
	}
	
	// guette constamment les incoming `Message` du client et les traitent
	public void run() {
		while(true) {
			try {
				// On attend un nouveau message / instruction bloquante
				Message mess = (Message) in.readObject();
				mess.setId(this.getId());
				String content = mess.getContent();
				System.out.println("\n--- NEW MESSAGE RECEIVED ---");
				mess.printMsg();
				switch(mess.getType()) {
					case MP:
						if(this.game.gameHasStarted()) {
							game.sendMP(mess);
						}
						else {
							System.out.println("You are alone, you can't send message");
						}
						break;
					case NAME:
						setName(content);
						break;
					case RECHERCHE:
						server.searchGame(this, content);
						break;
					case REQUETE:
						if(this.game.gameHasStarted()) {
							game.playMove(content, this);
						}
						else {
							System.out.println("Wait for the game to start before playing a move");
						}
						break;
					default:
						System.out.println("Could not handle this message:");
						mess.printMsg();	
				}
				
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	
	// envoie le `Message` au client
	public void sendMessage(Message mess) throws IOException {
		out.writeObject(mess);
		out.flush();
	}
	
	// Ferme : les flux in/out + la socket
	public void closeClient() throws IOException {
		this.in.close();
		this.out.close();
		this.socket.close();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return(name);
	}
	
	public int getId() {
		return(id);
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
}
