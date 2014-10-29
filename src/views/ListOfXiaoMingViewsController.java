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
        
        updatePage();
	}

	private void updatePage() {
		String cached = TestingCache.getCachedAccount();
		if (!((cached == "") || (cached == null))) {
			this.core = new ListOfXiaoMing(cached, this);
			Constant.logger.log(Level.INFO, String.format(
					Constant.LOG_MESSAGE_READING_CACHE, cached));
		} else {
			Constant.logger.log(Level.INFO,
					Constant.LOG_MESSAGE_NO_CACHE_FOUND);
		}
		
		this.setDisplay(Constant.PROMPT_MESSAGE_WELCOME + '\n' + Constant.PROMPT_MESSAGE_INSTRUCTION);
	}
	
	public String getUserInput() {
		return input.getText();
	}
	
	@FXML
    private void onEnter() {
		String command = getUserInput();
		
		if (!command.equals("")) {			
			if (core == null) {
				String recordFilePath = ListOfXiaoMing.executeUpperLevelCommand(command, this);
				if (recordFilePath != null
						&& !recordFilePath
								.equalsIgnoreCase(Constant.RETURN_VALUE_LOG_IN_CANCELLED)) {
					// already find the record
					this.setDisplay(recordFilePath);
					core = new ListOfXiaoMing(recordFilePath, this);
					TestingCache.cacheAccount(recordFilePath);
					Constant.logger.log(Level.INFO, String.format(
							Constant.LOG_MESSAGE_USER_CACHED, recordFilePath));
				}
			}

			assert (core != null);
			Constant.logger.log(Level.INFO, Constant.LOG_MESSAGE_INITIATE_LIST);

//			UtilityMethod.showToUser(core.execute("display"));
//			UtilityMethod.showToUser("\n\n\n");
//			Constant.logger.log(Level.INFO,
//					Constant.LOG_MESSAGE_USER_TASKS_DISPLAYED);
	//
//			boolean willContinue = true;
//			while (willContinue) {
//				String userInput = UtilityMethod.readCommand();
//				String result;
//				if (list.isNlpOn) {
//					result = list.executeNLP(userInput);
//				} else {
//					result = list.execute(userInput);
//				}
	//
//				if (result.equals(Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY)) {
//					willContinue = false;
//					Constant.logger.log(Level.INFO,
//							Constant.LOG_MESSAGE_USER_LOG_OUT);
//					UtilityMethod
//							.showToUser(Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY);
//				} else {
//					UtilityMethod.showToUser(result);
//					UtilityMethod.showToUser("\n\n\n");
//				}
//			}
		}
    }
	
	public void setDisplay(String displayedText) {
		content = new VBox();
		Label text = new Label(displayedText);
		content.getChildren().clear();
		content.getChildren().add(text);
		display.setContent(content);
	}
}
