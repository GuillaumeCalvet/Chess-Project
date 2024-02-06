package client;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.io.IOException;

import common.Message;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class MenuPanel extends Parent {
	
	StackPane root;
	GridPane gridFormats;
	// Button btn_1_0, btn_2_1, btn_3_0, btn_3_2, btn_5_0, btn_5_3, btn_10_0, btn_10_5, btn_15_10, btn_30_0, btn_30_20, btn_60_10;
	Button[] buttons;
	Client client;
	SceneSwitchListener sceneSwitchListener;

	MenuPanel(Client client, SceneSwitchListener sceneSwitchListener, double scene_width, double scene_height) {

		this.client = client;
		this.sceneSwitchListener = sceneSwitchListener;

		gridFormats = new GridPane();
		System.out.println("scene width / height: " + scene_width + " / " + scene_height);
		gridFormats.setMinSize(scene_width, scene_height); // Taille grid
		gridFormats.setAlignment(Pos.CENTER); // Positionne la grid au centre de la fenêtre

		buttons = new Button[] {
            createButton("1+0", 0, 0),
			createButton("2+1", 1, 0),
			createButton("3+0", 2, 0),
			createButton("3+2", 3, 0),
			createButton("5+0", 0, 1),
			createButton("5+3", 1, 1),
			createButton("10+0", 2, 1),
			createButton("10+5", 3, 1),
			createButton("15+10", 0, 2),
			createButton("30+0", 1, 2),
			createButton("30+20", 2, 2),
			createButton("60+10", 3, 2)
        };

		for (Button button : buttons) {
            gridFormats.add(button, GridPane.getColumnIndex(button), GridPane.getRowIndex(button));
        }

        this.getChildren().add(gridFormats);
	}
	
	private Button createButton(String text, int columnIndex, int rowIndex) {

		Button button = new Button(text);
		GridPane.setConstraints(button, columnIndex, rowIndex); // Set column and row indices
		// Makes the buttons occupies all the gridPane space
		button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setHgrow(button, Priority.ALWAYS);
        GridPane.setVgrow(button, Priority.ALWAYS);

		// Add an EventHandler to the button
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Button " + text + " clicked!");
				Message mess = new Message(client.getId(), Message.TypeMsg.RECHERCHE, text);
				try {
					client.sendMessage(mess);
					// Notify MainClient to switch the scene
                    sceneSwitchListener.switchSceneToGame(text);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	
		return button;
	}

	public void setProperties(StackPane root) {
        this.root = root;
        root.getChildren().add(gridFormats);
        // Set up layout constraints to make the gridFormats resize with the window
        StackPane.setMargin(gridFormats, new javafx.geometry.Insets(10));
        gridFormats.prefWidthProperty().bind(root.widthProperty().subtract(20));
        gridFormats.prefHeightProperty().bind(root.heightProperty().subtract(20));
    }
}
