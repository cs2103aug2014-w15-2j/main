package views;

import infrastructure.Constant;

import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import userInterface.ListOfXiaoMing;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ListOfXiaoMingViewsController extends GridPane implements HotKeyListener{
	@FXML
	private TextField input;
	
	@FXML
	private ScrollPane display;
	private VBox content;
	
	public ScrollPane preview;
	private VBox previewContent;
	
	private ListOfXiaoMing core = null;
	
	private Provider keyShortCuts = null;
	
	private static final String HOT_KEY_ADD_DESCRIPTION_TAG 	= "alt D";
	private static final String HOT_KEY_ADD_DATE_TAG 			= "alt A";
	private static final String HOT_KEY_ADD_TAG_TAG 			= "alt T";
	private static final String HOT_KEY_ADD_COMMAND_TAG 		= "alt C";
	private static final String HOT_KEY_ADD_INDEX_TAG 			= "alt I";
	private static final String HOT_KEY_ADD_PRIORITY_TAG 		= "alt P";
	
	private static final String HOT_KEY_PREVIEW 				= "control ENTER";
	private static final String HOT_KEY_CREATE 					= "control C";
	private static final String HOT_KEY_READ 					= "control R";
	private static final String HOT_KEY_UPDATE 					= "control U";
	private static final String HOT_KEY_DELETE 					= "control D";
	private static final String HOT_KEY_SEARCH					= "control F";
	
	
	private static final int MODIFIER_ALT = 520;
	private static final int MODIFIER_CTRL = 130;
	
	private String descriptionTag 	= "</DESCRIPTION>";
	private String dateTag 			= "</DATE>";
	private String tagTag 			= "</TAG>";
	private String commandTag		= "</COMMAND>";
	private String indexTag 		= "</INDEX>";
	private String priorityTag 		= "</PRIORITY>";
	
	
	
	
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
		this.setPreview("\n\n\n\t\tHit CTRL+ENTER to load a preview");
		this.setDisplay(Constant.GUI_MESSAGE_WELCOME + "\n\n" + Constant.GUI_MESSAGE_SHORTCUT_INSTRUCTION);
	}
	
	public String getUserInput(boolean willClear) {
		String text = input.getText();
		if (willClear) {
			input.clear();
		}
		return text;
	}
	
	@FXML
    private void onEnter() {
		String command = getUserInput(true);
		setPreview("\n\n\n\t\t  Hit CTRL+ENTER to load a preview");
		setDisplay(this.core.executeNLP(command));
    }
	
	public void setDisplay(String displayedText) {
		content = new VBox();
		Label text = new Label(displayedText);
		content.getChildren().clear();
		content.getChildren().add(text);
		display.setContent(content);
	}
	
	public void setPreview(String displayedText) {
		previewContent = new VBox();
		Label text = new Label(displayedText);
		previewContent.getChildren().clear();
		previewContent.getChildren().add(text);
		preview.setContent(previewContent);
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
					registerKeyShortCuts(instance);
					
				} catch (Exception e) {
					keyShortCuts = null;
				}
			}
		}).start();
	}
	
	
	private void registerKeyShortCuts(HotKeyListener instance) {
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_DESCRIPTION_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_DATE_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_TAG_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_COMMAND_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_INDEX_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_ADD_PRIORITY_TAG), instance);
		
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_PREVIEW), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_CREATE), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_READ), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_UPDATE), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_DELETE), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_SEARCH), instance);
	}
	
	
//	private void stopShortCuts() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					if (keyShortCuts != null) {
//						keyShortCuts.reset();
//						keyShortCuts.stop();
//					}
//				} catch (Exception e) {
//					keyShortCuts = null;
//				}
//			}
//		}).start();
//	}
	
	private String toggleTag(String tag) {
		if (tag.contains("</")) {
			return tag.replace("</", "<");
		} else {
			return tag.replace("<", "</");
		}
		
	}
	
	private void loadPreview() {
		//open a new thread to execute Java FX
		final ListOfXiaoMingViewsController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	setPreview(instance.core.getPreview(getUserInput(false)));
	        }
	   });
	}
	
	private void insertTextToTextField(final int cursorPosition, final String text) {
		//open a new thread to execute Java FX
		final ListOfXiaoMingViewsController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.input.insertText(cursorPosition, text);
	        }
	   });
	}
	
	@Override
	public void onHotKey(HotKey key) {
		System.out.println("HOTKEY CODE: " + key.keyStroke.getKeyCode());
		System.out.println("HOTKEY CHAR: " + key.keyStroke.getModifiers());
		String tag = "";
		int cursorPosition = this.input.getCaretPosition();
		switch (key.keyStroke.getKeyCode() + key.keyStroke.getModifiers()) {
			case KeyEvent.VK_D + MODIFIER_ALT:
				descriptionTag = toggleTag(descriptionTag);
				tag = descriptionTag;
				insertTextToTextField(cursorPosition, tag);	
				break;

			case KeyEvent.VK_A + MODIFIER_ALT:
				dateTag = toggleTag(dateTag);
				tag = dateTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_C + MODIFIER_ALT:
				commandTag = toggleTag(commandTag);
				tag = commandTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_T + MODIFIER_ALT:
				tagTag = toggleTag(tagTag);
				tag = tagTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_I + MODIFIER_ALT:
				indexTag = toggleTag(indexTag);
				tag = indexTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_P + MODIFIER_ALT:
				priorityTag = toggleTag(priorityTag);
				tag = priorityTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
			
			case KeyEvent.VK_ENTER + MODIFIER_CTRL:
				this.loadPreview();	
				break;
				
			case KeyEvent.VK_C + MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "add ");	
				break;
				
			case KeyEvent.VK_R + MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "display ");	
				break;
				
			case KeyEvent.VK_U + MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "update ");	
				break;
				
			case KeyEvent.VK_D + MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "delete ");	
				break;
				
			case KeyEvent.VK_F + MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "search ");	
				break;
		}
		
		
	}
}
