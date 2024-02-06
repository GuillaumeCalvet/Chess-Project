package client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;
import javafx.scene.paint.Paint;



/*
 * Scene 3/3 de l'application
 * => Jeu : affichage du plateau d'échec, du chat, nom des joueurs, etc...
 */
public class GamePanel extends Parent{
	
    SceneSwitchListener sceneSwitchListener;
    StackPane root;
    Client client;
    HBox mainPane;

    // ----------- LEFT : BOARD ----------- // 
	GridPane boardPane; //  plateau composé de 8x8 cases
    Paint[][] colorSquares; 
    int[] firstClick = new int[2]; // row / col / pieceName
    private ImageView selectedPiece = null;
    private final String imagePath = "C:/Users/raphg/eclipse-workspace/chessGame/src/main/java/client/images/pieces/";
    HashMap<Integer, String> piecePositions = new HashMap<>();
    // Pour chaque 64 cases: "num_case" => "nom_piece(si existe)"
    // INITIAL SET UP:
        // Key: 0, Value: rook_black
        // ...
        // Key: 15, Value: pawn_black
        // Key: 16, Value:
        // ...
        // Key: 47, Value:
        // Key: 48, Value: pawn_white
        // ...
        // Key: 63, Value: rook_white

    // ----------- RIGHT : CHAT + TIMER & NAME ----------- // 
    BorderPane rightPane;
    // ----------- TIMER & NAME ----------- // 
	HBox playerAPane;
	Text namePlayerA;
    Text timePlayerA;

	HBox playerBPane;
	Text namePlayerB;
    Text timePlayerB;

    // ----------- CHAT ----------- //
    BorderPane chatPane;
	// Zone de texte affichant les messages recus
	ScrollPane scrollReceivedText;
	TextFlow receivedText;
    HBox sendBox;
    TextArea textToSend; // Pour saisir du texte
	Button sendBtn; // Pour envoyer du text

    String format;
    String namePlayerAStr;
    String colorPlayerA;
    String namePlayerBStr;
    String colorOpponenent;

    GamePanel(Client client, SceneSwitchListener sceneSwitchListener , double sceneWidth, double sceneHeight, String format) {

        this.client = client;
        this.sceneSwitchListener = sceneSwitchListener;
        this.format = format;

        // ----------- LEFT : BOARD ----------- // 
        boardPane = new GridPane();
        colorSquares = new Paint[8][8];
        for(int i=0; i<64; i++) {
            piecePositions.put(i, ""); // Aucune pièces sur le plateau par défaut
        }

        // ----------- RIGHT : CHAT + TIMER & NAME ----------- //
        rightPane = new BorderPane();
        mainPane = new HBox(boardPane, rightPane);
        // ----------- TIMER & NAME ----------- // 
        namePlayerA = new Text("PlayerA");
        timePlayerA = new Text(format);
        playerAPane = new HBox(namePlayerA, timePlayerA);
        HBox.setMargin(namePlayerA, new Insets(2, 25, 10, 0));
        HBox.setMargin(timePlayerA, new Insets(2, 0, 10, 0));

        namePlayerB = new Text("PlayerB");
        timePlayerB = new Text(format);
        playerBPane = new HBox(namePlayerB, timePlayerB);
        HBox.setMargin(namePlayerB, new Insets(0, 25, 0, 0));
        HBox.setMargin(timePlayerB, new Insets(0, 0, 0, 0));

        // ----------- CHAT ----------- //

        chatPane = new BorderPane();
        receivedText = new TextFlow();
		scrollReceivedText = new ScrollPane();
        
        textToSend = new TextArea();
		sendBtn = new Button();
        sendBox = new HBox(textToSend, sendBtn);

		scrollReceivedText.setContent(receivedText); // on encapsule receivedText dans scrollRec...
		scrollReceivedText.vvalueProperty().bind(receivedText.heightProperty());
		
		sendBtn.setText("Send");
		sendBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// 1: Rajouter le texte dans la zone receivedText
                
				Message mess = new Message(Message.TypeMsg.MP, textToSend.getText());
                printNewMessage(mess, "you");
				textToSend.setText("");
				// 2: Envoyer le message par le réseau
				try {
					client.sendMessage(mess);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

        // SETTING NODE DIMENSIONS (w/h)
        
        // For...
        //  - HBox       : 
        //  - GridPane   : 
        //  - BorderPane : 
        //  - TextFlow   : 
        //  - TextArea   :  
        // ======>>>>>>>>>> .setPrefSize(width, height)
        // ======>>>>>>>>>> .setMaxHeight() : make child take the height place of parent

        // VARIABLES
        int sceneWidthReduced = (int) (sceneWidth * 0.95);
        int sceneHeightReduced = (int) (sceneHeight * 0.95);
        int boardPaneWidthHeight = (int) Math.min(sceneWidthReduced * 0.6, sceneHeightReduced * 0.95);
        int rightPaneWidth = (int) (sceneWidthReduced * 0.35);
        int playerABHeight = (int) (sceneHeightReduced * 0.05);
        int chatPaneHeight = (int) (sceneHeightReduced * 0.85);
        int receivedTextHeight = (int) (chatPaneHeight * 0.6);
        int sendBoxHeight = (int) (chatPaneHeight * 0.35);
        int textToSendWidth = (int) (rightPaneWidth * 0.75);
        int sendBtnWidth = (int) (rightPaneWidth * 0.20);

        // SCHEMA
        // mainPane  (HBox)     => (sceneWidth / sceneHeight)
            // boardPane  (GridPane)   => (boardPaneWidthHeight / boardPaneWidthHeight)
            // rightPane (BorderPane)  => (rightPaneWidth / sceneHeight)
                // playerAPane (HBox)       => (rightPaneWidth / playerABHeight)
                    // namePlayerA (Text)
                    // timerPlayerA (Text)
                // playerBPane (HBox)       => (rightPaneWidth / playerABHeight)
                    // namePlayerB (Text)
                    // timerPlayerB (Text)
                // chatPane (BorderPane)    => (rightPaneWidth, chatPaneHeight)
                    // receivedText (TextFlow)  => (rightPaneWidth, receivedTextHeight)
                    // sendBox (HBox)           => (rightPaneWidth, sendBoxHeight)
                        // textToSend (TextArea)    => (textToSendWidth, sendBoxHeight)
                        // sendBtn (Button)         => (sendBtnWidth, sendBoxHeight)

        ///////////////// CHAT : RECEIVED TEXT /////////////////////////////
        // Adding border using StackPane
        StackPane receivedTextBox = new StackPane();
        receivedTextBox.getChildren().add(receivedText);
        
        // Adding border to stackPane
        Rectangle borderRect = new Rectangle(rightPaneWidth*0.9, receivedTextHeight);
        borderRect.setStroke(Color.BLACK);
        borderRect.setFill(null);
        receivedTextBox.getChildren().add(borderRect);
        
        // Adding padding to TextFlow
        StackPane.setMargin(receivedText, new Insets(10));
        ////////////////////////////////////////////////////////////////////

        mainPane.setPrefSize(sceneWidthReduced, sceneHeightReduced);
        boardPane.setPrefSize(boardPaneWidthHeight, boardPaneWidthHeight);
        rightPane.setPrefSize(rightPaneWidth, sceneHeightReduced);
        playerAPane.setPrefSize(rightPaneWidth, playerABHeight);
        playerBPane.setPrefSize(rightPaneWidth, playerABHeight);
        chatPane.setPrefSize(rightPaneWidth, chatPaneHeight);
        receivedText.setPrefSize(rightPaneWidth, receivedTextHeight);
        receivedTextBox.setPrefSize(rightPaneWidth, receivedTextHeight);
        sendBox.setPrefSize(rightPaneWidth, sendBoxHeight);
        textToSend.setPrefSize(textToSendWidth, sendBoxHeight);
        sendBtn.setPrefSize(sendBtnWidth, sendBoxHeight);

        mainPane.setAlignment(Pos.BASELINE_CENTER); // Centrer sur l'écran
        mainPane.setSpacing(100); // Horizontal space between compocents
        
		chatPane.setTop(receivedTextBox);
        chatPane.setBottom(sendBox);
        rightPane.setTop(playerBPane);
        rightPane.setCenter(chatPane);
        rightPane.setBottom(playerAPane);
        this.getChildren().add(mainPane);

    }


    public void printNewMessage(Message mess, String sender) {
		Platform.runLater(new Runnable() { // Un thread
			@Override
			public void run() {
				Label text = new Label("[" + sender + "] " + mess.getContent());
				text.setPrefWidth(receivedText.getPrefWidth() - 20);
				text.setAlignment(Pos.CENTER_LEFT);
				receivedText.getChildren().add(text);
			}
        });
    }

    public void setProperties(StackPane root) {
        this.root = root;
        root.getChildren().add(mainPane);
        // Set up layout constraints to make the mainPane resize with the window
        StackPane.setMargin(mainPane, new javafx.geometry.Insets(10));
        mainPane.prefWidthProperty().bind(root.widthProperty().subtract(20));
        mainPane.prefHeightProperty().bind(root.heightProperty().subtract(20));
    }

    public void setPlayersNames(String playerA, String playerB) {
        namePlayerAStr = playerA;
        namePlayerBStr = playerB;
        namePlayerA.setText(playerA);
        namePlayerB.setText(playerB);
    }

    public void setPlayersColors(String colorOpp) {
        colorOpponenent = colorOpp;
        if(colorOpp.equals("white")) {
            colorPlayerA = "black";
        } else {
            colorPlayerA = "white";
        }
        System.out.println("You are " + colorPlayerA);
    }

    public GridPane setUpBoard() {
        // Loop through the chessboard and create squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Rectangle square = new Rectangle(50, 50, (col + row) % 2 == 0 ? Color.WHITE : Color.GRAY);
                colorSquares[row][col] = square.getFill();
                final int r = row;
                final int c = col;
                square.setOnMouseClicked(event -> {
                    try {
                        handleSquareClick(r, c);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                boardPane.add(square, col, row);
            }
        }

        return boardPane;
    }

    // Sets up pieces disposition so that current player has its pieces at the bottom
    public void setUpPieces(String colorBottom) {
        // row 0 = top
        // row 7 = bottom
        System.out.println("Setting piece disposition");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int rowPieceWhite = 7;
                int rowPieceBlack = 0;
                int rowPawnsWhite = 6;
                int rowPawnsBlack = 1;

                int colQueens = 3;
                int colKings = 4;

                if(colorBottom == "black") {
                    rowPieceWhite = 0;
                    rowPawnsWhite = 1;
                    rowPieceBlack = 7;
                    rowPawnsBlack = 6;

                    colQueens = 4;
                    colKings = 3;
                }

                // Place pieces on the board
                // For simplicity, let's place all the pieces in their starting positions
                placePiece(rowPieceBlack, 0, "rook_black");
                placePiece(rowPieceBlack, 1, "knight_black");
                placePiece(rowPieceBlack, 2, "bishop_black");
                placePiece(rowPieceBlack, colQueens, "queen_black");
                placePiece(rowPieceBlack, colKings, "king_black");
                placePiece(rowPieceBlack, 5, "bishop_black");
                placePiece(rowPieceBlack, 6, "knight_black");
                placePiece(rowPieceBlack, 7, "rook_black");

                placePiece(rowPieceWhite, 0, "rook_white");
                placePiece(rowPieceWhite, 1, "knight_white");
                placePiece(rowPieceWhite, 2, "bishop_white");
                placePiece(rowPieceWhite, colQueens, "queen_white");
                placePiece(rowPieceWhite, colKings, "king_white");
                placePiece(rowPieceWhite, 5, "bishop_white");
                placePiece(rowPieceWhite, 6, "knight_white");
                placePiece(rowPieceWhite, 7, "rook_white");

                for (int i = 0; i < 8; i++) {
                    placePiece(rowPawnsBlack, i, "pawn_black");
                    placePiece(rowPawnsWhite, i, "pawn_white");
                }

                for(Map.Entry<Integer, String> entry : piecePositions.entrySet()) {
                    System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }
            }

        });
        
    }

    private void placePiece(int row, int col, String pieceName) {
        
        /////// 1) If pieces on this square => Remove it
        ImageView pieceToRemove = getPieceAt(row, col);
        if(pieceToRemove != null) {
            removePiece(row, col);
        }

        /////// 2) Place the piece on the square
        // Load the image
        String pieceImagePath = imagePath + pieceName + ".png";
        File file = new File(pieceImagePath);
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        
        int key = (row*8+col);
        piecePositions.put(key, pieceName);

        // Resize the image to fit the square
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        imageView.setMouseTransparent(true); // Pour pouvoir cliquer sur la case, à travers la pièce

        // Add the image to the board at the specified row and column
        System.out.println("Placing piece: " + pieceImagePath + " at row: " + row + ", col: " + col);
        boardPane.add(imageView, col, row);
    }

    private void removePiece(int row, int col) {
        System.out.println("Removing piece at row: " + row + ", col: " + col);
        ImageView toRemoveImageView = getPieceAt(row, col);
        boardPane.getChildren().remove(toRemoveImageView);
        int key = (row*8+col);
        piecePositions.put(key, "");
    }

    private void handleSquareClick(int row, int col) throws IOException {

        ImageView clickedImageView = getPieceAt(row, col);
        Rectangle clickedSquare = getSquareAt(row, col);

        if (selectedPiece == null) { // c'est le 1er clique (sur la pièce/case à déplacer))
            if (clickedImageView != null) { // présent clique => sur une ImageView (une des 64 cases)
                firstClick[0] = row;
                firstClick[1] = col;
                selectedPiece = clickedImageView;
                clickedSquare.setFill(Color.RED); // Change the background color to red
            }
        } else { // C'est le 2ème clique (sur la case où déplacer la pièce)
        
            int firstCaseClicked = firstClick[0] * 8 + firstClick[1];
            int secondCaseClicked = row * 8 + col;
            String pieceName = piecePositions.get(firstCaseClicked);
            String content = pieceName + "/" + firstCaseClicked + "/" + pieceName + "/" + secondCaseClicked;
            Message mess = new Message(Message.TypeMsg.REQUETE, content);
            client.sendMessage(mess);

            // Resets default values 
            int firstClickRow = firstClick[0];
            int firstClickCol = firstClick[1];
            Rectangle firstClickedSquare = getSquareAt(firstClickRow, firstClickCol);
            firstClickedSquare.setFill(colorSquares[firstClickRow][firstClickCol]); // Reset to original color
            selectedPiece = null;
        }
    }

    // Recois ordre du serveur (envoyé aux 2 clients) en réponse à la requête (demande de déplacement d'une pièce) d'un des 2 clients/joueurs
    public void movePiece(String ordre) {

        String[] ordreSplit = ordre.split("/");

        // PIECE TO REMOVE
        String removePieceName = ordreSplit[0];
        int removeCaseNb = Integer.parseInt(ordreSplit[1]);
        int removeRow = removeCaseNb / 8;
        int removeCol = removeCaseNb - (removeRow*8);
        ImageView removeImageView = getPieceAt(removeRow, removeCol);

        // PIECE TO ADD
        String addPieceName = ordreSplit[2];
        int addCaseNb = Integer.parseInt(ordreSplit[3]);
        int addRow = addCaseNb / 8;
        int addCol = addCaseNb - (addRow*8);
        
        Platform.runLater(new Runnable() { // Un thread
			@Override
			public void run() {
				boardPane.getChildren().remove(removeImageView);
                removePiece(removeRow, removeCol);
                placePiece(addRow, addCol, addPieceName);
			}
        });
    }

    private ImageView getPieceAt(int row, int col) {
        for (javafx.scene.Node node : boardPane.getChildren()) {
            if (node instanceof ImageView && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (ImageView) node;
            }
        }
        System.out.println("No pieces (ImageView) at row " + row + ", col " + col);
        return null;
    }

    private Rectangle getSquareAt(int row, int col) {
        for (javafx.scene.Node node : boardPane.getChildren()) {
            if (node instanceof Rectangle && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Rectangle) node;
            }
        }
        return null;
    }

    public String getColorPlayerA() {
        return(colorPlayerA);
    }

    
}
