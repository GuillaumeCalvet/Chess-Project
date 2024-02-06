package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import common.Message;
import common.Message.TypeMsg;
import server.Game;
import java.util.concurrent.TimeUnit;

public class Client {
	
	private String address; // IP server
	private int port; // port server
	private String name; // nom client
	private int id; // id client
	private Socket socket; // Socket pour communiquer avec le server
	private ObjectInputStream in; // Pour recevoir des messages du server
	private ObjectOutputStream out; // Pour envoyer des messages au server

	public GamePanel gamePanel;
	
	Client(String address, int port) throws UnknownHostException, IOException {
		
		this.address = address;
		this.port = port;
		this.socket = new Socket(address, port);
		this.in = new ObjectInputStream(socket.getInputStream());
		this.out = new ObjectOutputStream(socket.getOutputStream());
		ClientReceive cliRec = new ClientReceive(this);
		Thread threadReceive = new Thread(cliRec);
		threadReceive.start();
	}
	
	public void messageReceived(Message mess) {
		String content = mess.getContent();
		switch(mess.getType()) {
			case ID:
				setId(Integer.valueOf(content));
				break;
			case ORDRE: // "bouge cette piece à tel endroit"
				gamePanel.movePiece(content);
				break;
			case STARTGAME:
				while(gamePanel == null) {
					try {
						Thread.sleep(50);
						System.out.println("gamepanel is null");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				String[] nameColor = content.split("/"); // Ex: content = "Jeff/white" : the opponent name & color
				String nameOpp = nameColor[0];   // "Jeff"
				String colorOpp = nameColor[1];  // "white"
				gamePanel.setPlayersNames(this.getName(), nameOpp); 
				gamePanel.setPlayersColors(colorOpp);
				gamePanel.setUpPieces(gamePanel.getColorPlayerA());
				break;
			case MP:
				gamePanel.printNewMessage(mess, gamePanel.namePlayerBStr);
				break;
			default:
				System.out.println("Message type not handled : " + mess.getType() + " : " + mess.getContent());
		}
	}
	
	public void sendMessage(Message mess) throws IOException {
		out.writeObject(mess);
		out.flush();
	}
	
	public ObjectInputStream getObjInputStream() {
		return(in);
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return(name);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return(id);
	}
	
	public void setGamePanel(GamePanel gamepane) {
		this.gamePanel = gamepane;
	}
	
}
