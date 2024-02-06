package common;

import java.io.Serializable;

public class Message implements Serializable{
	
	private int idSender;
	public enum TypeMsg {
		NAME, // Client: "Voici mon nom"
		ID, // Server: Voici ton id
		RECHERCHE, // Client: "je veux jouer une partie avec ce format"
		STARTGAME, // Server: "J'en ai trouvé une, update ton interface"
		MP, // Client "Retransmet ce MP à client B"
		REQUETE, // Client: "puis-je jouer ce coup là ?"
		ORDRE // Server: "Oui. Voici cm update ton interface"
	}

	/* 'REQUETE' & 'ORDRE'
	Syntax: "PieceToRemove/FromThisSquare/PieceToAdd/ToThisSquare" 
		- Ex1: "whiteBishop/9/whiteBishop/2" 
		- Ex2: Promotion: "blackPawn/51/blackQueen/59"
			1) Remove the black pawn
			2) from the C7 square (1=A1, 8=A8, 9=B1, etc..)
			3) and print a black queen
			4) on the C8 square
	 */

	private TypeMsg type;
	private String content;
	
	// The client won't have an id when sending his name
	public Message(TypeMsg type, String content) {
		this.type = type;
		this.content = content;
	}
	
	public Message(int idSender, TypeMsg type, String content) { //
		this.idSender = idSender;
		this.type = type;
		this.content = content;
	}
	
	public TypeMsg getType() {
		return(type);
	}

	public void setId(int idSender) {
		this.idSender = idSender;
	}
	
	public int getIdSender() {
		return(idSender);
	}
	
	public String getContent() {
		return(content);
	}
	
	public void printMsg() {
		System.out.println("- idSender: " + getIdSender() + "\n" +
						   "- type: " + type + "\n" +
						   "- content: " + getContent());
	}
}
