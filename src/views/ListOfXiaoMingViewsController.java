package views;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import userInterface.ListOfXiaoMing;
import dataStore.TestingCache;
import dataStructure.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class ListOfXiaoMingViewsController extends GridPane {
	@FXML
	private TextField input;
	
	@FXML
	private ScrollPane display;
	
	private VBox content;
	
	private ListOfXiaoMing core = null;
	
	public ListOfXiaoMingViewsController() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListOfXiaoMingViews.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		fxmlLoader.load();
        
		this.core = new ListOfXiaoMing("haha");
        updatePage();
	}

	private void updatePage() {
		this.setDisplay(Constant.PROMPT_MESSAGE_WELCOME + '\n' + Constant.PROMPT_MESSAGE_INSTRUCTION);
	}
	
	public String getUserInput() {
		String text = input.getText();
		input.clear();
		return text;
	}
	
	@FXML
    private void onEnter() {
		String command = getUserInput();
		setDisplay(this.core.executeNLP(command));
    }
	
	public void setDisplay(String displayedText) {
		content = new VBox();
		Label text = new Label(displayedText);
		content.getChildren().clear();
		content.getChildren().add(text);
		display.setContent(content);
	}
}
