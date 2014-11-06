package userInterface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainView extends Application {

	//author A0119447Y
	@Override
    public void start(Stage stage) throws Exception {
        MainViewController control = new MainViewController(stage);
        Scene scene;
        if (System.getProperty("os.name").equals("Mac OS X")) {
        	scene = new Scene(control, 990, 700);
        } else {
        	scene = new Scene(control, 980, 690);
        }
        scene.getStylesheets().add("resource/style.css");
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.setTitle("List of Xiao Ming");
        stage.show();
        stage.setResizable(false);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
