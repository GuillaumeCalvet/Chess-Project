package client;

import java.io.IOException;

import javafx.application.Platform;
import common.Message;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class IdentificationPanel extends Parent {
	
	TextArea playerNameToSend;
	Button sendBtn;
	Client client;
	SceneSwitchListener sceneSwitchListener;
	
	IdentificationPanel(Client client, SceneSwitchListener sceneSwitchListener) {

		this.client = client;
		this.sceneSwitchListener = sceneSwitchListener;
		this.playerNameToSend = new TextArea();
		this.sendBtn = new Button();
		
		playerNameToSend.setLayoutX(50);
		playerNameToSend.setLayoutY(50);
		playerNameToSend.setPrefWidth(320);
		playerNameToSend.setPrefHeight(50);
		
		sendBtn.setText("Send");
		sendBtn.setLayoutX(50);
		sendBtn.setLayoutY(120);
		sendBtn.setPrefWidth(70);
		sendBtn.setPrefHeight(35);
		sendBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				String name = playerNameToSend.getText();
				Message mess = new Message(Message.TypeMsg.NAME, name);
				try {
					client.sendMessage(mess);
					client.setName(name);
					System.out.println("Welcome " + name);
					// Notify MainClient to switch the scene
                    sceneSwitchListener.switchSceneToMenu();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// Attache ces éléments à cette vue
		this.getChildren().add(playerNameToSend);
		this.getChildren().add(sendBtn);
	}
}
