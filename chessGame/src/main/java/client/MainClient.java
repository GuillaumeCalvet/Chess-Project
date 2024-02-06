package client;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
/*
COMPILE + LAUNCH:
>> cd C:\Users\raphg\eclipse-workspace\chessGame\src\main\java
>> $env:PATH_TO_FX = "C:\Program Files\javafx-sdk-21.0.2\lib"
>?> javac --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.graphics,javafx.base $(Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
>?> javac $(Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
>?> javac --module-path $env:PATH_TO_FX $(Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
>> java --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.graphics,javafx.base client.MainClient localhost 2024

*/


public class MainClient extends Application implements SceneSwitchListener {
	
	private Stage stage;
	private Client client;

	StackPane root;
	IdentificationPanel identificationPanel;
	MenuPanel menuPanel;
	GamePanel gamePanel;

	double screen_width;
	double screen_height;
	double scene_height;
	double scene_width;
	double stageX;
	double stageY;

	@Override
	public void start(Stage stage) throws Exception {
		
		this.stage = stage;

		Parameters params = getParameters();
		String address = params.getRaw().get(0);
		int port = Integer.parseInt(params.getRaw().get(1));
		client = new Client(address, port);

		String positionScene = "normal";
		boolean smallWindow = false;
        if (params.getRaw().size() == 3) {
            positionScene = params.getRaw().get(2);
			smallWindow = true;
        }

		// Get screen size 
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		screen_width = screenBounds.getWidth();
		screen_height = screenBounds.getHeight();

		if(smallWindow) {
			scene_height = screen_height * 0.49;
			scene_width = screen_width * 0.49;
		}
		else {
			scene_height = screen_height;
			scene_width = screen_width;
		}

		switch(positionScene) {
			case "left":
				stageX = ((screen_width/2) - scene_width) / 2;
				stageY = 20;
				break;
			case "right":
				stageX = ((screen_width/2) + ( (screen_width/2 - scene_width) /2) );
				stageY = 20;
				break;
			default:
				stageX = (screen_width - scene_width) / 2;
				stageY = (screen_height - scene_height) / 2;
				break;
			
		}

		StackPane root = new StackPane();

		// On créer nos 3 vues (~scènes)
		identificationPanel = new IdentificationPanel(client, this);
		
		// Group root = new Group(); // groupe racine
		root.getChildren().add(identificationPanel); // auquel on ajoute la vue identification
		Scene scene = new Scene(root, 500, 500); // une nouvelle scène
		
		stage.setX(stageX);
        stage.setY(stageY);
		stage.setTitle("Chess");
		stage.setScene(scene);
		stage.show();
	}

    public void switchSceneToMenu() {
		root = new StackPane();
		menuPanel = new MenuPanel(client, this, scene_width, scene_height);
		menuPanel.setProperties(root);
		stage.setScene(new Scene(root, scene_width, scene_height));
		// stage.setScene(new Scene(new Group(menuPanel), scene_width, scene_height));
        stage.setX(stageX);
        stage.setY(stageY);
		stage.show();
    }

	public void switchSceneToGame(String format) {
		root = new StackPane();
		gamePanel = new GamePanel(client, this, scene_width, scene_height, format);
		gamePanel.setUpBoard();
		client.setGamePanel(gamePanel);
		gamePanel.setProperties(root);
        stage.setScene(new Scene(root, scene_width, scene_height));
        stage.setX(stageX);
        stage.setY(stageY);
		stage.show();
    }
	
	public static void main(String[] args) {
		Application.launch(MainClient.class, args);
	}

}

interface SceneSwitchListener {
    void switchSceneToMenu();
	void switchSceneToGame(String format);
}