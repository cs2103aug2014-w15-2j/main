package views;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

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
import java.awt.event.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class ListOfXiaoMingViewsController extends GridPane implements HotKeyListener{
	@FXML
	private TextField input;
	
	@FXML
	private ScrollPane display;
	
	private VBox content;
	
	private ListOfXiaoMing core = null;
	
	private Provider keyShortCuts = null;
	
	private static final String HOT_KEY_ADD_DESCRIPTION_TAG 	= "control D";
	private static final String HOT_KEY_ADD_DATE_TAG 			= "control A";
	private static final String HOT_KEY_ADD_TAG_TAG 			= "control T";
	private static final String HOT_KEY_ADD_COMMAND_TAG 		= "control C";
	private static final String HOT_KEY_ADD_INDEX_TAG 			= "control I";
	private static final String HOT_KEY_ADD_PRIORITY_TAG 		= "control P";
	private static final String HOT_KEY_PREVIEW 				= "alt ENTER";
	
	private String descriptionTag 	= "</DESCRIPTION>";
	private String dateTag 			= "</DATE>";
	private String tagTag 			= "</TAG>";
	private String commandTag		= "</COMMAND>";
	private String indexTag 		= "</INDEX>";
	private String priorityTag 		= "</PRIORITY>";
	
	private static final int KEY_VALUE_CTRL_ENTER = 10;
	private static final int KEY_VALUE_CTRL_A = 65;
	private static final int KEY_VALUE_CTRL_C = 67;
	private static final int KEY_VALUE_CTRL_D = 68;
	private static final int KEY_VALUE_CTRL_I = 73;
	private static final int KEY_VALUE_CTRL_P = 80;
	private static final int KEY_VALUE_CTRL_T = 84;
	
	
	
	
	public ListOfXiaoMingViewsController() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListOfXiaoMingViews.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		fxmlLoader.load();
        
		this.core = new ListOfXiaoMing("haha");
		this.initializeShortCuts();
        updatePage();
        Stage stage = new Stage();
        stage.setResizable(false);
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

	
	private void initializeShortCuts(){
		final ListOfXiaoMingViewsController instance = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (keyShortCuts == null) {
						keyShortCuts = Provider.getCurrentProvider(false);
					}
					keyShortCuts.reset();
					int i = KeyEvent.VK_C;
					keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_DESCRIPTION_TAG), instance);
					keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_DATE_TAG), instance);
					keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_TAG_TAG), instance);
					keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_COMMAND_TAG), instance);
					keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_INDEX_TAG), instance);
					keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_PRIORITY_TAG), instance);
					keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_PREVIEW), instance);
				} catch (Exception e) {
					keyShortCuts = null;
				}
			}
		}).start();
	}
	
	private void stopShortCuts() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (keyShortCuts != null) {
						keyShortCuts.reset();
						keyShortCuts.stop();
					}
				} catch (Exception e) {
					keyShortCuts = null;
				}
			}
		}).start();
	}
	
	private String toggleTag(String tag) {
		if (tag.contains("</")) {
			return tag.replace("</", "<");
		} else {
			return tag.replace("<", "</");
		}
		
	}
	
	private void loadPreview() {
		
	}
	
	@Override
	public void onHotKey(HotKey key) {
		System.out.println("HOTKEY: " + key.keyStroke.getKeyCode());
		String tag = "";
		int cursorPosition = this.input.getCaretPosition();
		switch (key.keyStroke.getKeyCode()) {
			case KEY_VALUE_CTRL_D:
				descriptionTag = toggleTag(descriptionTag);
				tag = descriptionTag;
				break;

			case KEY_VALUE_CTRL_A:
				dateTag = toggleTag(dateTag);
				tag = dateTag;
				break;
				
			case KEY_VALUE_CTRL_C:
				commandTag = toggleTag(commandTag);
				tag = commandTag;
				break;
				
			case KEY_VALUE_CTRL_T:
				tagTag = toggleTag(tagTag);
				tag = tagTag;
				break;
				
			case KEY_VALUE_CTRL_I:
				indexTag = toggleTag(indexTag);
				tag = indexTag;
				break;
				
			case KEY_VALUE_CTRL_P:
				priorityTag = toggleTag(priorityTag);
				tag = priorityTag;
				break;
			
			case KEY_VALUE_CTRL_ENTER:
				this.loadPreview();
				break;
		}
		
		this.input.insertText(cursorPosition, tag);	
	}
}
