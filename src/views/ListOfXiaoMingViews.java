package views;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ListOfXiaoMingViews extends Application {

	@Override
    public void start(Stage stage) throws Exception {
        ListOfXiaoMingViewsController control = new ListOfXiaoMingViewsController();
        Scene scene;
        if (System.getProperty("os.name").equals("Mac OS X")) {
        	scene = new Scene(control, 400, 600);
        } else {
        	scene = new Scene(control, 390, 590);
        }
        scene.getStylesheets().add("views/style.css");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("List of Xiao Ming");
        stage.show();
        stage.setResizable(false);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
