package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Le serveur de l'application. Responsable des clients et des parties
 */

public class Server {
	
	private int port; // Port sur lequel le server écoute les nouvelles connexions
	private List<ConnectedClient> clients; // Liste des clients connectés
	private List<Game> games; // Liste des parties crées
	
	Server(int port) throws IOException {
		this.port = port;
		this.clients = new ArrayList<ConnectedClient>();
		this.games = new ArrayList<Game>();
		Thread threadConnection = new Thread(new Connection(this));
		threadConnection.start();
	}
	
	// Ajoute le client connecté à notre liste de clients
	public void addClient(ConnectedClient newClient) {
		clients.add(newClient);
	}
	
	// Créer une partie
	public void createGame(ConnectedClient client, String format) {
		Game newGame = new Game(client, format);
		games.add(newGame);
	}
	
	// Recherche parmi les 'games' dont nb player < 2, s'il en existe avec ce 'format'
	// 1. Si oui, appel `Game.addPlayerB(client)` puis `Game.start()`
	// 2. Si non, appel `createGame(client, format)`
	public void searchGame(ConnectedClient client, String format) throws IOException {
		boolean gameFound = false;
		for(Game game : games) {
			if( (game.getPlayers().size() < 2) && (game.getFormat().equals(format)) ) {
				System.out.println("Found one game");
				game.addPlayerB(client);
				game.startGame();
				gameFound = true;
				break;
			}
		}
		if(!gameFound) {
			System.out.println("No games found, creating one");
			createGame(client, format);
		}
	}
	
	
	// Déconnecte le client et le retire de la liste 'clients'
	public void disconnectedClient(ConnectedClient client) throws IOException {
		client.closeClient();
		System.out.println(client.getName() + " disconnected");
		clients.removeIf(c -> (c.getId() == client.getId()));
	}
	
	public int getPort() {
		return port;
	}
}
