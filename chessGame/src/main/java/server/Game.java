package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import common.Message;

/*
 Représente une partie d'échec. 
 - Stock toutes les informations de la partie et 
 - gère la logique de déplacements
 => La logique de déplacement est donc traitée server-side
 => En déplacant une pièce, le client fera donc une requête au server
 	qui renverra (une authorisation ou non).
 */
public class Game {
	
	private ConnectedClient playerA;
	private ConnectedClient playerB;
	private List<ConnectedClient> players;
	String couleurA, couleurB;
	private String format;
	private boolean hasStart;
	HashMap<Integer, String> piecePositions = new HashMap<>(); 
	// WHITE'S PERSPECTIVE => 0: black's rook, 63: white's rook
    // Pour chaque 64 cases: "num_case" => "nom_piece(si existe)"
    // INITIAL SET UP:
	// Key: 0, Value: rook_black
	// ...
	// Key: 15, Value: pawn_black
	
	Game(ConnectedClient playerA, String format) {
		this.playerA = playerA;
		this.format = format;
		this.players = new ArrayList<ConnectedClient>();
		players.add(playerA);
		playerA.setGame(this);
	}
	
	// Retransmettre le Message envoyé par PlayerA à PlayerB
	public void sendMP(Message mess) throws IOException {
		if(mess.getIdSender() == playerA.getId()) {
			playerB.sendMessage(mess);
		} else {
			playerA.sendMessage(mess);
		}
	}
	
	public void startGame() throws IOException {
		// Définir qui est noir et blanc
		Random rand = new Random();
		int randNb = rand.nextInt(2);
		if(randNb == 0) {
			couleurA = "white";
			couleurB = "black";
		}
		else {
			couleurB = "white";
			couleurA = "black";
		}

		setInitialPiecePositions();
		
		System.out.println("Starting game : " + playerA.getName() + "(" + couleurA + ") VS " + playerB.getName() + "(" + couleurB + ")");

		String contentA = playerB.getName() + "/" + couleurB;
		String contentB = playerA.getName() + "/" + couleurA;
		Message messA = new Message(0,Message.TypeMsg.STARTGAME, contentA);
		playerA.sendMessage(messA);
		Message messB = new Message(0,Message.TypeMsg.STARTGAME, contentB);
		playerB.sendMessage(messB);
		System.out.println("Message to playerA: ");
		messA.printMsg();
		System.out.println("Message to playerB: ");
		messB.printMsg();
		hasStart = true;
	}

	// Position des pièces au début du jeu
	public void setInitialPiecePositions() {

		piecePositions.put(0, "rook_black");
		piecePositions.put(1, "knight_black");
		piecePositions.put(2, "bishop_black");
		piecePositions.put(3, "queen_black");
		piecePositions.put(4, "king_black");
		piecePositions.put(5, "bishop_black");
		piecePositions.put(6, "knight_black");
		piecePositions.put(7, "rook_black");

		for(int i = 8; i<16; i++) {
			piecePositions.put(i, "pawn_black");
		}

		for(int y=46; y<56; y++) {
			piecePositions.put(y, "pawn_white");
		}
		
		piecePositions.put(56, "rook_white");
		piecePositions.put(57, "knight_white");
		piecePositions.put(58, "bishop_white");
		piecePositions.put(59, "queen_white");
		piecePositions.put(60, "king_white");
		piecePositions.put(61, "bishop_white");
		piecePositions.put(62, "knight_white");
		piecePositions.put(63, "rook_white");
	}
	
	// Le client a joué un coup
	// Analyse ce coup et 
		// Si ok: retourne un Message aux clients avec les infos pour update leur interface ('GamePane')
		// Sinon: print error message
		// Ex: "pawn_white/51/pawn_white/35" => "White: Can I move my pawn from d2 (51st square) to d4 (35th square)"
	public void playMove(String requestPlayer, ConnectedClient playerwhoPlayedTheMove) throws IOException {

		if(checkMove(requestPlayer, playerwhoPlayedTheMove)) {
			
			System.out.println("Playing move " + requestPlayer);
		
			String[] ordre2Split = (requestPlayer).split("/");
			int removePieceFromPos = 63 - Integer.parseInt(ordre2Split[1]);
			int addPieceToPos = 63 - Integer.parseInt(ordre2Split[3]);

			String ordre1 = requestPlayer;
			// Puisque le plateau est inversé pour l'adversaire, les positions des pièces à retirer et ajouter ne sont pas les mêmes pour lui
			String ordre2 = ordre2Split[0] + "/" + removePieceFromPos + "/" + ordre2Split[2] + "/" + addPieceToPos;

			Message mess1 = new Message(0,Message.TypeMsg.ORDRE, ordre1);
			Message mess2 = new Message(0,Message.TypeMsg.ORDRE, ordre2);
			
			if(playerwhoPlayedTheMove.equals(playerA)) {
				playerA.sendMessage(mess1);
				playerB.sendMessage(mess2);
			} else {
				playerB.sendMessage(mess1);
				playerA.sendMessage(mess2);
			}

		}

		else {
			System.out.println("ERROR: " + playerwhoPlayedTheMove.getName() + " (id: " + playerwhoPlayedTheMove.getId() + ") cannot play " + requestPlayer);
		}
		
	}

	// Checks if a player's move is legal or not
	// ex, requestPlayer = "pawn_white/51/pawn_white/35" 
	// 					    => Can I move my pawn from d2 (51st square) to d4 (35th square)"
	public boolean checkMove(String requestPlayer, ConnectedClient playerwhoPlayedTheMove) {
		String[] requestSplit = (requestPlayer).split("/");
		String removePieceName = requestSplit[0];
		String removePieceColor = removePieceName.split("_")[0];
		int removeFromPos = 63 - Integer.parseInt(requestSplit[1]);
		String addPieceName = requestSplit[2]; 
		int addPieceToPos = 63 - Integer.parseInt(requestSplit[3]);

		if(getColorClient(playerwhoPlayedTheMove) == "black") {
			removeFromPos = 63 - Integer.parseInt(requestSplit[1]);
			addPieceToPos = 63 - Integer.parseInt(requestSplit[3]);
		}
		
		return(true);
	}
	
	
	
	
	public void addPlayerB(ConnectedClient playerB) {
		this.playerB = playerB;
		players.add(playerB);
		playerB.setGame(this);
	}
	
	public List<ConnectedClient> getPlayers() {
		return(players);
	}
	
	public String getFormat() {
		return(format);
	}

	public boolean gameHasStarted() {
		return(hasStart);
	}

	public String getColorClient(ConnectedClient client) {
		if(client.equals(playerA)) {
			return(couleurA);
		} else if(client.equals(playerB)) {
			return(couleurB);
		}
		return("");
	}
}
