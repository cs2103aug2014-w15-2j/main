package views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ListOfXiaoMingViews extends Application {

	@Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ListOfXiaoMingViews.fxml"));

        Scene scene = new Scene(root, 600, 800);
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.setTitle("List of Xiao Ming");
        stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
