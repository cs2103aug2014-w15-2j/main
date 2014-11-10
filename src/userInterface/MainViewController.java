package userInterface;

import infrastructure.*;
import infrastructure.Constant.COMMAND_TYPE;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.KeyStroke;

import model.CommandFailedException;
import model.Constraint;
import model.Task;
import model.User;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainViewController extends GridPane implements HotKeyListener {

	@FXML
	private TextField input;

	@FXML
	private GridPane dragNode;

	@FXML
	private ScrollPane displayScrollPane;
	public ScrollPane previewScrollPane;

	private NerParser parser;
	private static PrintStream err = System.err;
	// a property to store the current user
	private User user;

	private Provider keyShortCuts = null;
	private String currentListName = Constant.TASK_LIST_ONGOING;
	private Label consoleTextLabel;

	
	private String descriptionTag 	= "</DESCRIPTION>";
	private String dateTag 			= "</DATE>";
	private String tagTag 			= "</TAG>";
	private String commandTag		= "</COMMAND>";
	private String indexTag 		= "</INDEX>";
	private String priorityTag 		= "</PRIORITY>";

	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int currentCommandIndex = 0;

/**
 * ========================================================================
 * Constructor
 * ========================================================================
 */
	//@author A0119447Y
	/**
	 * Constructor for MainViewController
	 * @param stage
	 */
	public MainViewController(Stage stage) {
		try {
			this.copyUserNlpFiles();
			this.user = new User();
			this.loadFont();
			this.loadFxml();
			this.loadParser();
			this.addTextFieldListener();
			this.initializeShortCuts();
			this.updateStartPage();
			UtilityMethod.makeDraggable(stage, dragNode);

			if (!Constant.ERROR_PRINT_ON) {
				silentErrorStream();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

/**
 * ========================================================================
 * Methods which might be called during initialization (in constructor)
 * ========================================================================
 */
	//@author A0119379R
	/**
	 * load the custom font into the system
	 */
	private void loadFont() {
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_BASE_BOLD)
				.toExternalForm(), Constant.FONT_LOAD_SIZE);
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_BASE)
				.toExternalForm(), Constant.FONT_LOAD_SIZE);
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_TIME)
				.toExternalForm(), Constant.FONT_LOAD_SIZE);
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_FEEDBACK)
				.toExternalForm(), Constant.FONT_LOAD_SIZE);
	}

	/**
	 * copy relative NLP files to user directory
	 */
	private void copyUserNlpFiles() {

		InputStream[] propStreams = new InputStream[11];
		InputStream[] tsvStreams = new InputStream[11];
		InputStream[] gzStreams = new InputStream[11];

		for (int i = 0; i < 11; i++) {
			propStreams[i] = getClass().getResourceAsStream(
					Constant.PROPS_SOURCE[i]);
			tsvStreams[i] = getClass().getResourceAsStream(
					Constant.TSVS_SOURCE[i]);
			gzStreams[i] = getClass().getResourceAsStream(
					Constant.GZS_SOURCE[i]);
		}

		try {
			UtilityMethod.copyUserNlpFiles(tsvStreams, propStreams, gzStreams);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//@author A0119447Y
	/**
	 * Load the FXML file to render the graphical user interface
	 * @throws IOException
	 */
	private void loadFxml() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				Constant.RESOURCE_FXML));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		fxmlLoader.load();
	}

	//@author A0119379R
	/**
	 * load the NLP parser in a separate thread to reduce the loading delay
	 */
	private void loadParser() {
		final MainViewController instance = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				instance.updateTextField(Constant.GUI_MESSAGE_PARSER_LOADING);
				instance.parser = new NerParser();
				instance.updateTextField(Constant.EMPTY_STRING);
				instance.updatePreviewLater();
			}
		}).start();
	}

	/**
	 * initialize the shortcuts in a separate thread
	 */
	private void initializeShortCuts() {
		final MainViewController instance = this;
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

	/**
	 * register key shortcuts
	 * 
	 * @param instance	a HotKeyListener instance, normally should be 'this'
	 */
	private void registerKeyShortCuts(HotKeyListener instance) {
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_DESCRIPTION_TAG),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_DATE_TAG),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_TAG_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_COMMAND_TAG),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_INDEX_TAG),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_PRIORITY_TAG),instance);

		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_PREVIEW),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_CREATE),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_READ),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_UPDATE),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_DELETE),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_SEARCH),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_RELOAD),instance);

		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_TO_DO),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_TRASHED),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_DONE),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_HELP),instance);

		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_LAST_COMMAND),instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_NEXT_COMMAND),instance);
	}

	/**
	 * update the start page
	 */
	private void updateStartPage() {
		setPreviewPane(Constant.GUI_MESSAGE_WELCOME,this.getCurrentListName());
		displayScrollPane.getStyleClass().add(Constant.CSS_CLASS_NAME); 
		this.help();
		this.changeViewTo(Constant.TASK_LIST_HELP);
	}

	/**
	 * Apply observer pattern to the input TextField.
	 * Any time the value changed of inputs, the delegate method will be called
	 */
	private void addTextFieldListener() {
		final MainViewController instance = this;
 
		this.input.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				instance.loadPreview();
			}
		});
	}

	/**
	 * this method is used to turn off the System.err PrintStream
	 */
	private void silentErrorStream() {
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
	}

/**
 * ===============================================================================================
 *  I/O, hot key, and view updating methods
 * ===============================================================================================
 */
	
	//@author A0119447Y
	/**
	 * Retrieve the user input from the input box (hooked with 'input')
	 * 
	 * @param willClear		willClear whether the input area will be cleared
	 * @return				the user input as a string
	 */
	public String getUserInput(boolean willClear) {
		String text = input.getText();
		if (willClear) {
			input.clear();
		}
		return text;
	}
	
	//@author A0119379R
	/**
	 * set the content of preview pane
	 * 
	 * @param textToDisplay		the new text the 'console' need to display
	 * @param listName			the new list name
	 */
	public void setPreviewPane(String textToDisplay, String listName) {
		GridPane previewContentPane = new GridPane();
		previewContentPane.setStyle(Constant.CSS_STYLE_PREVIEW_PANE_BASE);
		previewContentPane.setHgap(Constant.PREVIEW_PANE_INNER_GAP);

		VBox parsingFeedbackBox = new VBox();
		parsingFeedbackBox.setPrefWidth(Constant.PREF_WIDTH_FEEDBACK_BOX);
		parsingFeedbackBox.setPrefHeight(Constant.PREF_HEIGHT_FEEDBACK_BOX);

		//set the consoleTextLabel
		consoleTextLabel = new Label(textToDisplay);
		consoleTextLabel.setFont(Font.font(Constant.FONT_CONSOLE_TEXT_LABEL, FontWeight.BOLD, Constant.FONT_SIZE_CONSOLE_TEXT_LABEL));
		consoleTextLabel.setStyle(Constant.CSS_STYLE_CONSOLE_TEXT_LABEL);
		parsingFeedbackBox.setStyle(Constant.CSS_STYLE_FEEDBACK_BOX);
		parsingFeedbackBox.getChildren().add(consoleTextLabel);

		VBox listIndicatorBox = new VBox();
		listIndicatorBox.setAlignment(Pos.BOTTOM_RIGHT);
		listIndicatorBox.setPrefWidth(getWidth() - Constant.PREF_WIDTH_OFFSET_LIST_INDICATOR_BOX);
		Label listLabel = new Label(listName.toUpperCase());
		listLabel.setFont(Font.font(Constant.FONT_LIST_LABEL, FontWeight.EXTRA_BOLD, Constant.FONT_SIZE_LIST_LABEL));
		listLabel.setStyle(Constant.CSS_STYLE_LIST_LABEL);
		
		//set different styles according to the list name
		if (listName.equalsIgnoreCase(Constant.TASK_LIST_ONGOING)) {
			listIndicatorBox.setStyle(Constant.CSS_STYLE_TASK_LIST_ONGOING);
		} else if (listName.equalsIgnoreCase(Constant.TASK_LIST_TRASHED)) {
			listIndicatorBox.setStyle(Constant.CSS_STYLE_TASK_LIST_TRASHED);
		} else if (listName.equalsIgnoreCase(Constant.TASK_LIST_FINISHED)){
			listIndicatorBox.setStyle(Constant.CSS_STYLE_TASK_LIST_FINISHED);
		} else if (listName.equals(Constant.TASK_LIST_SEARCH)){
			listIndicatorBox.setStyle(Constant.CSS_STYLE_TASK_LIST_SEARCH);
		} else {
			listIndicatorBox.setStyle(Constant.CSS_STYLE_TASK_LIST_DEFAULT);
		}

		listIndicatorBox.getChildren().add(listLabel);

		//Add the two boxes into the pane
		previewContentPane.add(parsingFeedbackBox, Constant.ROW_INDEX_FEEDBACK_BOX, Constant.COLUMN_INDEX_FEEDBACK_BOX);
		previewContentPane.add(listIndicatorBox, Constant.ROW_INDEX_LIST_INDICATOR_BOX, Constant.COLUMN_INDEX_LIST_INDICATOR_BOX);

		previewScrollPane.setStyle(Constant.CSS_STYLE_PREVIEW_SCROLL_PANE);
		previewScrollPane.setContent(previewContentPane);
	}

	//@author A0119447Y
	/**
	 * Update the display area to the string specified
	 * 
	 * @param textToDisplay	textToDisplay string of the text to be displayed
	 */
	public void setDisplayText(String textToDisplay) {
		VBox displayContent = new VBox();
		Label textLabel = new Label(textToDisplay);
		displayContent.getChildren().clear();
		displayContent.getChildren().add(textLabel);

		displayScrollPane.setContent(displayContent);
		setDisplayScrollbarStyle();
		displayScrollPane.setFocusTraversable(true);
	}

	//@author A0119447Y
	/**
	 * Update the display area to display the given task list
	 * 
	 * @param taskList	task list to be displayed
	 */
	public void setDisplayPane(ArrayList<Task> taskList) {
		setDisplayScrollbarStyle();
		if (taskList == null || taskList.isEmpty()) {
			// task list is empty, nothing to display
			this.loadPreview();
		} else {
			VBox displayContentBox = new VBox();
			displayContentBox.setStyle(Constant.CSS_STYLE_DISPLAY_CONTENT_BOX);
			displayContentBox.setAlignment(Pos.CENTER);
			GridPane taskPane = LayoutManager.getTaskPane(taskList, getWidth());
			displayContentBox.getChildren().add(taskPane);
			displayScrollPane.setContent(displayContentBox);
		}
	}

	//@author A0119379R
	/**
	 * insert the given text to the given position in the textField
	 * UI updating performed in a new thread
	 * 
	 * @param cursorPosition	an integer representation the cursor position
	 * @param text				a string indicating the text that will be inserted
	 */
	private void insertTextIntoTextField(final int cursorPosition,
			final String text) {
		// open a new thread to execute Java FX
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				instance.input.insertText(cursorPosition, text);
			}
		});
	}

	//@author A0119447Y
	/**
	 * Update the input area to given text
	 * 
	 * @param text	text String of text to be displayed
	 */
	private void updateTextField(final String text) {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (text == null) {
					//clear the input
					instance.input.deleteText(0, instance.input.getText()
							.length());
					;
				}
				instance.input.setText(text);
				instance.input.positionCaret(text.length());
				instance.input.selectAll();
			}
		});
	}

	//@author A0119379R
	/**
	 * toggle between e.g. <DATE> & </DATE>
	 * 
	 * @param tag	"<DATE>" or </DATE> in the above example
	 * @return		<tag> or </tag>  
	 */
	private String toggleTag(String tag) {
		if (tag.contains(Constant.TAG_OPEN_LATTER)) {
			return tag.replace(Constant.TAG_OPEN_LATTER, Constant.TAG_OPEN_FORMER);
		} else {
			return tag.replace(Constant.TAG_OPEN_FORMER, Constant.TAG_OPEN_LATTER);
		}
	}

	/**
	 * load the real time parsing result to the preview pane
	 */
	private void loadPreview() {
		// open a new thread to execute Java FX
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String userInput = getUserInput(false);
				if (userInput.length() > 0) {
					System.out.println(userInput);
					setPreviewPane(instance.getPreview(userInput),
							instance.getCurrentListName());
				} else {
					setPreviewPane(instance.getConsoleText(),
							instance.getCurrentListName());
				}
			}
		});
	}

	/**
	 * get the console label text
	 * 
	 * @return	a string representing the console label text
	 */
	protected String getConsoleText() {
		return this.consoleTextLabel.getText();
	}

	/**
	 * set the text of console
	 * 
	 * @param textToDisplay		the new text needed to be set
	 */
	public void setConsoleText (final String textToDisplay) {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				instance.setPreviewPane(textToDisplay, instance.getCurrentListName());
			}
		});
	}
	
	//@author A0119447Y
	/**
	 * Catch the event when user press the enter key
	 */
	@FXML
	private void onEnter() {
		String command = getUserInput(true);
		if (command.equals("")) {
			performDisplay();
		} else {
			this.commandHistory.add(command);
			this.currentCommandIndex = this.commandHistory.size();
			this.execute(command);
		}
	}

	//@author A0119379R
	/**
	 * load an empty list indicating image
	 * the image will be shown when current list is empty
	 */
	private void loadEmptyImage() {
		HBox emptyImageBox = new HBox();
		emptyImageBox.setPrefHeight(200);
		emptyImageBox.setAlignment(Pos.CENTER_RIGHT);
		Image emptyImage = new Image(getClass().getResourceAsStream(Constant.RESOURCE_EMPTY_IMAGE));
		ImageView emptyImageView = new ImageView(emptyImage);
		emptyImageView.fitHeightProperty().bind(emptyImageBox.heightProperty());
		emptyImageBox.getChildren().add(emptyImageView);
		this.displayScrollPane.setContent(emptyImageBox);
	}

	//@author A0119444E
	/**
	 * only show scroll bar when users scrolling
	 */
	private void setDisplayScrollbarStyle() {
		displayScrollPane.getStyleClass().add(Constant.CSS_CLASS_NAME);
		displayScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		displayScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		displayScrollPane.setOnScrollFinished(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				// TODO Auto-generated method stub
				displayScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
			}
		});

		displayScrollPane.setOnScrollStarted(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				displayScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
			}
		});
	}

/**
 * ========================================================================
 * DEMUX methods
 * ========================================================================
 */

	//@author A0119379R
	/**
	 * The main method to execute user input
	 * 
	 * @param userInput		an unparsed user input string
	 */
	public void execute(String userInput) {

		updateTsv(userInput);

		COMMAND_TYPE thisCommand;
		try {
			if (userInput.equals(Constant.EMPTY_STRING)) {
				return;
			}

			//pick the command
			thisCommand = this.parser.pickCommand(userInput.toLowerCase());

			//demux and execute the command
			switch(thisCommand) {
				case ADD :
					performAdd(userInput);
					break;
					
				case DELETE :
					performDelete(userInput);
					break;
					
				case UPDATE :
					performUpdate(userInput);
					break;
					
				case SEARCH :
					performSearch(userInput);
					break;
				
				case DISPLAY :
					performDisplay();
					break;
					
				case UNDO :
					performUndo();
					break;
					
				case REDO :
					performRedo();
					break;
					
				case CLEAR :
					performClear();
					break;
					
				case EXIT :
					performExit();
					break;
					
				case HELP :
					performHelp();
					break;
					
				case EMPTY_TRASH :
					performEmptyTrash();
					break;
					
				case RELOAD :
					performReloadModel();
					break;
					
				case DONE :
					performDone(userInput);
					break;
					
				case RECOVER :
					performRecover(userInput);
					break;
					
				case UNTAG :
					performUntag(userInput);
					
				default :
					break;

			}
		} catch (CommandFailedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The main method to get real time preview
	 * 
	 * @param userInput		an unparsed user input string
	 * @return				a string to show in the preview pane
	 */
	public String getPreview(String userInput) {
		try {
			
			if (userInput.equals(Constant.EMPTY_STRING) || (userInput == null)) {
				return Constant.GUI_MESSAGE_PREVIEW_DEFAULT;
			}
			
			if (this.parser == null) {
				return Constant.EMPTY_STRING;
			}

			//pick the command
			COMMAND_TYPE thisCommand = this.parser.pickCommand(userInput.toLowerCase());
			System.out.println(thisCommand);
			//get the preview
			switch(thisCommand) {
				case ADD :
					return getAddPreview(userInput);
					
				case DELETE :
					return getDeletePreview(userInput);
					
				case UPDATE :
					return getUpdatePreview(userInput);
					
				case SEARCH :
					return getSearchPreview(userInput);
				
				case DONE :
					return getDonePreview(userInput);
					
				case DISPLAY :
					return getDisplayPreview();
	
				case UNDO :
					return getUndoPreview();
					
				case REDO :
					return getRedoPreview();
					
				case CLEAR :
					return getClearPreview();
					
				case EMPTY_TRASH :
					return getEmptyTrashPreview();
					
				case RELOAD :
					return getReloadPreview();
					
				case HELP :
					return getHelpPreview();
					
				case EXIT :
					return getExitPreview();
					
				case RECOVER :
					return getRecoverPreview(userInput);
				
				case UNTAG :
					return getUntagPreview(userInput);
					
				default :
					return getDefaultPreview();
			}		
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return Constant.EXCEPTION_MESSAGE_PARSING_FAIL;
		}
	}
/**
 * ==========================================================================
 *  Specific methods to perform action, the names self-explain their use
 * ==========================================================================
 */
	private void performUntag(String userInput) {
		setConsoleText(this.untag(userInput));
		refreshCurrentList();
	}

	private void performRecover(String userInput) {
		setConsoleText(this.recover(userInput));
		performDisplay();
	}
	
	private void performDone(String userInput) {
		setConsoleText(this.done(userInput));
		refreshCurrentList();
	}
	
	private void performReloadModel() {
		setConsoleText(this.reloadNLPModel());
	}
	
	private void performEmptyTrash() {
		setConsoleText(this.emptyTrash());
		refreshCurrentList();
	}
	
	private void performHelp() {
		this.help();
		changeViewTo(Constant.TASK_LIST_HELP);
	}
	
	private void performExit() {
		System.setErr(err); 
		performReloadModel();
		User.exit();
	}
	
	private void performClear() {
		setConsoleText(this.clear());
		refreshCurrentList();
	}
	
	private void performRedo() {
		setConsoleText(this.redo());
		refreshCurrentList();
	}
	
	private void performUndo() {
		setConsoleText(this.undo());
		refreshCurrentList();
	}
	
	private void performDisplay() {
		changeViewTo(Constant.TASK_LIST_ONGOING);
	}
	
	private void performSearch(String userInput) {
		ArrayList<Task> queryList = this.search(userInput);
		if (queryList != null) {
			setDisplayPane(queryList);
			changeViewTo(Constant.TASK_LIST_SEARCH);
		}
	}
	
	private void performUpdate(String userInput) {
		setConsoleText(this.update(userInput));
		performDisplay();
	}
	
	private void performDelete(String userInput) {
		setConsoleText(this.delete(userInput));
		refreshCurrentList();
	}
	
	private void performAdd(String userInput) {
		setConsoleText(this.add(userInput));
		performDisplay();
	}
		
	private String getCurrentListName() {
		return this.currentListName;
	}

/**
 * ==========================================================================
 *  Specific methods to get previews, the names self-explain their use
 * ==========================================================================
 */
	
	private String getRecoverPreview(String userInput) {
		try {
			if (this.getCurrentListName().equals(Constant.TASK_LIST_ONGOING)) {
				return Constant.PREVIEW_MESSAGE_RECOVER_ALREADY_IN_LIST;
			}
			
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			String returnValue = Constant.PREVIEW_MESSAGE_RECOVER_INITIAL_VALUE;

			for (int index : indices) {
				if (this.getCurrentListName().equals(Constant.TASK_LIST_FINISHED)) {
					Task taskToFinish = this.user.retrieveFromFinishedList(index - 1);
					returnValue += (index + ": " + taskToFinish.getDescription() + "\n");
				} else if (this.getCurrentListName().equals(Constant.TASK_LIST_TRASHED)) {
					Task taskToFinish = this.user.retrieveFromTrashedList(index - 1);
					returnValue += (index + ": " + taskToFinish.getDescription() + "\n");
				}
			}
			return returnValue;
		} catch (CommandFailedException de) {
			de.printStackTrace();
			return Constant.PREVIEW_MESSAGE_RECOVER_NO_TASK_SPECIFIED;
		}
	}
	
	private String getDefaultPreview() {
		return Constant.PREVIEW_MESSAGE_DEFAULT;
	}

	private String getReloadPreview() {
		return Constant.PREVIEW_MESSAGE_RELOAD_MODEL;
	}

	private String getEmptyTrashPreview() {
		return Constant.PREVIEW_MESSAGE_EMPTY_TRASH;
	}

	private String getHelpPreview() {
		return Constant.PREVIEW_MESSAGE_HELP;
	}

	private String getExitPreview() {
		return Constant.PREVIEW_MESSAGE_EXIT;
	}

	private String getClearPreview() {
		return Constant.PREVIEW_MESSAGE_CLEAR;
	}

	private String getRedoPreview() {
		return Constant.PREVIEW_MESSAGE_REDO;
	}

	private String getUndoPreview() {
		return Constant.PREVIEW_MESSAGE_UNDO;
	}

	private String getDisplayPreview() {
		return Constant.PREVIEW_MESSAGE_DISPLAY;
	}

	private String getDonePreview(String userInput) {
		try {
			if (this.getCurrentListName().equals(Constant.TASK_LIST_FINISHED)) {
				return Constant.PREVIEW_MESSAGE_DONE_FINISHED_ALREADY;
			} else if (this.getCurrentListName().equals(Constant.TASK_LIST_TRASHED)) {
				return Constant.PREVIEW_MESSAGE_DONE_TRASHED;
			}
			
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			String returnValue = Constant.PREVIEW_MESSAGE_INITIAL_VALUE;

			for (int index : indices) {
				Task taskToFinish = this.user.retrieveFromNormalList(index - 1);
				returnValue += (index + ": " + taskToFinish.getDescription() + "\n");
			}
			return returnValue;
		} catch (CommandFailedException de) {
			return Constant.PREVIEW_MESSAGE_DONE_NO_TASK_SPECIFIED;
		}
	}

	private String getSearchPreview(String userInput) {
		if (this.getCurrentListName() != Constant.TASK_LIST_FINISHED &&
				this.getCurrentListName() != Constant.TASK_LIST_ONGOING &&
				this.getCurrentListName() != Constant.TASK_LIST_TRASHED) {
			return Constant.PREVIEW_MESSAGE_SEARCH_NOT_ALLOWED;
		}
		Constraint thisConstraint = parser.getConstraint(userInput);
		return Constant.PREVIEW_MESSAGE_SEARCH_INITIAL_VALUE + thisConstraint.toString();
	}

	private String getUpdatePreview(String userInput) {
		try {
			if (!this.getCurrentListName().equals(Constant.TASK_LIST_ONGOING)) {
				return "Command: update \n\n" + "You can only update the task in 'TODO' section";
			}
			
			int index = parser.pickIndex(userInput).get(0);
			Task taskToUpdate = this.user.getUpdatePreview(index - 1,
					parser.getUpdatedTaskMap(userInput));
			return "Command: update \n\n"
					+ taskToUpdate.toStringForDisplaying();
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return "Command: update \n\n"
					+ Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
		}
	}
	
	private String getUntagPreview(String userInput) {
		try {
			if (!this.getCurrentListName().equals(Constant.TASK_LIST_ONGOING)) {
				return "Command: untag \n\n" + "You can only update the task in 'TODO' section";
			}
			
			int index = parser.pickIndex(userInput).get(0);
			String task = this.user.getOngoingTaskList().get(index).getDescription();
			
			ArrayList<String> tags = parser.pickUntag(userInput);
			String returnValue = String.format(Constant.PREVIEW_MESSAGE_UNTAG_INITIAL_VALUE, task);

			for (String tag : tags) {
				returnValue += tag + ",";
			}
			return returnValue;
			
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return "Command: update \n\n"
					+ Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
		}
	}

	private String getDeletePreview(String userInput) {
		try {
			if (this.getCurrentListName().equals(Constant.TASK_LIST_TRASHED)) {
				return Constant.PREVIEW_MESSAGE_DELETE_ALREADY_TRASHED;
			}
			
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			String returnValue = Constant.PREVIEW_MESSAGE_DELETE_INITIAL_VALUE;

			for (int index : indices) {
				Task taskToDelete = this.user.retrieve(index - 1, this.getCurrentListName());
				returnValue += (index + ": " + taskToDelete.getDescription() + "\n");
			}
			return returnValue;
		} catch (CommandFailedException de) {
			return Constant.PREVIEW_MESSAGE_DELETE_NO_TASK_SPECIFIED;
		}
	}

	private String getAddPreview(String userInput) {
		Task taskToAdd = parser.getTask(userInput);
		return Constant.PREVIEW_MESSAGE_ADD_INITIAL_VALUE + taskToAdd.toStringForDisplaying();
	}

/**
 * =================================================================================================
 * Methods to be called by the above actions performing methods, the names self-explain their use
 * =================================================================================================
 */
	private String add(String userInput) {
		Task taskToAdd = parser.getTask(userInput);
		assert (taskToAdd != null);
		return (this.user.add(taskToAdd)) ? Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY
				: Constant.PROMPT_MESSAGE_ADD_TASK_FAILED;
	}

	private String delete(String userInput) {
		try {
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			Collections.sort(indices);
			int offset = 0;
			boolean isAllSucceeded = true;
			String returnValue = Constant.EMPTY_STRING;
			for (int index : indices) {
				try {
					boolean isThisSucceeded = this.user.delete(index - offset - 1, this.getCurrentListName());

					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_DELETE_TASK_FAILED + ": " + index);
					}
					isAllSucceeded &= isThisSucceeded;
					offset++;
				} catch (CommandFailedException cfe) {
					return cfe.toString();
				}

			}

			return isAllSucceeded ? Constant.PROMPT_MESSAGE_DELETE_TASK_SUCCESSFULLY
					: returnValue;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}

	private String done(String userInput) {
		try {
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			Collections.sort(indices);
			int offset = 0;
			boolean isAllSucceeded = true;
			String returnValue = "";
			for (int index : indices) {
				try {
					boolean isThisSucceeded = this.user.done(index - offset - 1);
					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_DONE_TASK_FAILED + ": " + index);
					}
					isAllSucceeded &= isThisSucceeded;
					offset++;
				} catch (CommandFailedException cfe) {
					return cfe.toString();
				}

			}

			return isAllSucceeded ? Constant.PROMPT_MESSAGE_DONE_TASK_SUCCESSFULLY
					: returnValue;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}
	
	private String update(String userInput) {
		try {
			int index = parser.pickIndex(userInput).get(0);
			this.user.update(index - 1,
					parser.getUpdatedTaskMap(userInput));
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
		}

		return Constant.PROMPT_MESSAGE_UPDATE_TASK_SUCCESSFULLY;
	}

	private ArrayList<Task> search(String userInput) {
		Constraint thisConstraint;
		try {
			if (this.getCurrentListName() != Constant.TASK_LIST_FINISHED &&
					this.getCurrentListName() != Constant.TASK_LIST_ONGOING &&
					this.getCurrentListName() != Constant.TASK_LIST_TRASHED) {
				setConsoleText(Constant.PREVIEW_MESSAGE_SEARCH_NOT_ALLOWED);
				return null;
			}
			thisConstraint = parser.getConstraint(userInput);
			ArrayList<Task> queryResult = this.user.find(thisConstraint, this.getCurrentListName());
			if (queryResult.isEmpty()) {
				setConsoleText(Constant.PROMPT_MESSAGE_NO_TASK_FOUND);
				return null;
			} else {
				return queryResult;
			}
		} catch (CommandFailedException e) {
			e.printStackTrace();
			setConsoleText(Constant.PREVIEW_MESSAGE_SEARCH_ERROR);
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			setConsoleText(Constant.PREVIEW_MESSAGE_SEARCH_ERROR);
			e.printStackTrace();
			return null;
		}

	}

	private ArrayList<Task> displayOngoingList() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getOngoingTaskList();
			if (queryResult.isEmpty()) {
				this.loadEmptyImage();
				return null;
			} else {
				return queryResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			setConsoleText(Constant.PROMPT_MESSAGE_DISPLAY_ERROR);
			return null;
		}
	}

	//@author A0119447Y
	private ArrayList<Task> displayTrashedList() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getTrashedTaskList();
			if (queryResult.isEmpty()) {
				setDisplayText(Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK);
				// TODO: create a box for empty list
				return null;
			} else {
				return queryResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			setConsoleText(Constant.PROMPT_MESSAGE_DISPLAY_ERROR);
			return null;
		}
	}

	private ArrayList<Task> displayFinishedList() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getFinishedTaskList();
			if (queryResult.isEmpty()) {
				setDisplayText(Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK);
				// TODO: create a box for empty list
				return null;
			} else {
				return queryResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			setConsoleText(Constant.PROMPT_MESSAGE_DISPLAY_ERROR);
			return null;
		}
	}

	//@author A0119379R
	private String emptyTrash() {
		this.user.emptyTrash();
		return Constant.PROMPT_MESSAGE_TRASH_EMPTIED;
	}

	private void help() {
		VBox helpBox = LayoutManager.getHelpBox();
		this.displayScrollPane.setContent(helpBox);
	}

	private String clear() {
		try {
			this.user.deleteAll(this.getCurrentListName());
			return Constant.PREVIEW_MESSAGE_TASK_ALL_TRASHED;
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	private String undo() {
		try {
			this.user.undo();
			return Constant.PROMPT_MESSAGE_UNDO_SUCCESSFULLY;
		} catch (CommandFailedException e) {
			UtilityMethod.showToUser(e.toString());
			return Constant.PROMPT_MESSAGE_UNDO_FAILED;
		}
	}

	private String redo() {
		try {
			this.user.redo();
			return Constant.PROMPT_MESSAGE_REDO_SUCCESSFULLY;
		} catch (CommandFailedException e) {
			UtilityMethod.showToUser(e.toString());
			return Constant.PROMPT_MESSAGE_REDO_FAILED;
		}
	}

	private String reloadNLPModel() {
		NerParser.updateModal();
		this.parser = new NerParser();
		return Constant.PREVIEW_MESSAGE_MODEL_RELOADED;
	}

	private String recover(String userInput) {
		try {
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			Collections.sort(indices);
			int offset = 0;
			boolean isAllSucceeded = true;
			String returnValue = "";
			for (int index : indices) {
				try {
					boolean isThisSucceeded = this.user.recover(index - offset - 1, this.getCurrentListName());
					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_DONE_TASK_FAILED + ": " + index);
					}
					isAllSucceeded &= isThisSucceeded;
					offset++;
				} catch (CommandFailedException cfe) {
					return cfe.toString();
				}

			}

			return isAllSucceeded ? Constant.PROMPT_MESSAGE_DONE_TASK_SUCCESSFULLY
					: returnValue;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}
	
	//@author A0119447Y
	/**
	 * 
	 * @param userInput
	 * @return
	 */
	private String untag(String userInput) {
		boolean isAllSucceeded = true;
		String returnValue = "";
		try {
			ArrayList<String> tags = parser.pickUntag(userInput);
			int index = parser.pickIndex(userInput).get(0);
			for (String tag : tags) {
				try {
					boolean isThisSucceeded = this.user.untag(index, tag);
					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_UNTAG_TASK_FAILED + ": " + index);
					}
					isAllSucceeded &= isThisSucceeded;
				} catch (CommandFailedException cfe) {
					return cfe.toString();
				}
			}
		} catch (CommandFailedException e) {
			return e.toString();
		}

		return isAllSucceeded ? Constant.PROMPT_MESSAGE_UNTAG_TASK_SUCCESSFULLY
				: returnValue;
	}


/**
 * ==========================================================================
 * the delegation methods to respond to shortcuts
 * ==========================================================================
 */
	//@author A0119447Y
	@Override
	public void onHotKey(HotKey key) {
		String tag = "";
		int cursorPosition = this.input.getCaretPosition();
		switch (key.keyStroke.getKeyCode() + key.keyStroke.getModifiers()) {
		case KeyEvent.VK_D + Constant.MODIFIER_ALT:
			descriptionTag = toggleTag(descriptionTag);
			tag = descriptionTag;
			insertTextIntoTextField(cursorPosition, tag);
			break;

		case KeyEvent.VK_A + Constant.MODIFIER_ALT:
			dateTag = toggleTag(dateTag);
			tag = dateTag;
			insertTextIntoTextField(cursorPosition, tag);
			break;

		case KeyEvent.VK_C + Constant.MODIFIER_ALT:
			commandTag = toggleTag(commandTag);
			tag = commandTag;
			insertTextIntoTextField(cursorPosition, tag);
			break;

		case KeyEvent.VK_T + Constant.MODIFIER_ALT:
			tagTag = toggleTag(tagTag);
			tag = tagTag;
			insertTextIntoTextField(cursorPosition, tag);
			break;

		case KeyEvent.VK_I + Constant.MODIFIER_ALT:
			indexTag = toggleTag(indexTag);
			tag = indexTag;
			insertTextIntoTextField(cursorPosition, tag);
			break;

		case KeyEvent.VK_P + Constant.MODIFIER_ALT:
			priorityTag = toggleTag(priorityTag);
			tag = priorityTag;
			insertTextIntoTextField(cursorPosition, tag);
			break;

		case KeyEvent.VK_ENTER + Constant.MODIFIER_CTRL:
			this.loadPreview();
			break;

		case KeyEvent.VK_C + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, Constant.TEXT_INSERTED_ADD);
			break;

		case KeyEvent.VK_R + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, Constant.TEXT_INSERTED_DISPLAY);
			break;

		case KeyEvent.VK_U + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, Constant.TEXT_INSERTED_UPDATE);
			break;

		case KeyEvent.VK_D + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, Constant.TEXT_INSERTED_DELETE);
			break;

		case KeyEvent.VK_F + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, Constant.TEXT_INSERTED_SEARCH);
			break;

		case KeyEvent.VK_M + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, Constant.TEXT_INSERTED_RELOAD_MODEL);
			break;

		case KeyEvent.VK_BACK_SPACE + Constant.MODIFIER_CTRL:
			String inputString = (String) input.getCharacters();
			insertTextIntoTextField(cursorPosition,
					inputString.substring(0, inputString.lastIndexOf(Constant.SPLITOR_SPACE)));
			break;

		case KeyEvent.VK_1 + Constant.MODIFIER_CTRL:
			changeToOngoingList();
			break;

		case KeyEvent.VK_2 + Constant.MODIFIER_CTRL:
			changeToFinishedList();
			break;
			
		case KeyEvent.VK_3 + Constant.MODIFIER_CTRL:
			changeToTrashedList();
			break;

		case KeyEvent.VK_4 + Constant.MODIFIER_CTRL:
			changeToHelp();
			break;
		}

		switch (key.keyStroke.getKeyCode()) {
		case KeyEvent.VK_UP:
			this.currentCommandIndex--;
			try {
				updateTextField(this.commandHistory
						.get(this.currentCommandIndex));
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO log
				this.currentCommandIndex++;
			}
			break;

		case KeyEvent.VK_DOWN:
			try {
				this.currentCommandIndex++;
				updateTextField(this.commandHistory
						.get(this.currentCommandIndex));
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO log
				this.currentCommandIndex--;
			}
			break;
		}

	}


/**
 * ==========================================================================
 * methods that related to view changing, the names explain their uses
 * ==========================================================================
 */	
	
	//@author A0119379R
	private void changeToOngoingList() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			
			@Override
	        public void run() {
	        	instance.setDisplayPane(instance.displayOngoingList());
	        	instance.currentListName = Constant.TASK_LIST_ONGOING;
	        	instance.changeViewTo(Constant.TASK_LIST_ONGOING);
	        }
	   });
	}

	private void changeToTrashedList() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.setDisplayPane(instance.displayTrashedList());
	        	instance.currentListName = Constant.TASK_LIST_TRASHED;
	        	instance.changeViewTo(Constant.TASK_LIST_TRASHED);
	        }
	   });
	}

	private void changeToFinishedList() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.setDisplayPane(instance.displayFinishedList());
	        	instance.currentListName = Constant.TASK_LIST_FINISHED;
	        	instance.changeViewTo(Constant.TASK_LIST_FINISHED);
	        }
	   });
		
	}
	
	private void changeToHelp() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.help();
	        	instance.changeViewTo(Constant.TASK_LIST_HELP);
	        }
	   });
		
	}

	private void updatePreviewLater() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				instance.setPreviewPane(Constant.PREVIEW_MESSAGE_COMMAND_READY,instance.getCurrentListName());
			}
		});

	}
	
	private void changeViewTo(String listName) {
		this.currentListName = listName;
		switch(listName) {
			case Constant.TASK_LIST_ONGOING:
				this.setPreviewPane(this.getConsoleText(), Constant.TASK_LIST_ONGOING);
				this.setDisplayPane(this.displayOngoingList());
				break;
				
			case Constant.TASK_LIST_FINISHED:
				this.setPreviewPane(this.getConsoleText(), Constant.TASK_LIST_FINISHED);
				this.setDisplayPane(this.displayFinishedList());
				break;
				
			case Constant.TASK_LIST_TRASHED:
				this.setPreviewPane(this.getConsoleText(), Constant.TASK_LIST_TRASHED);
				this.setDisplayPane(this.displayTrashedList());
				break;
				
			case Constant.TASK_LIST_SEARCH:
				this.setPreviewPane(this.getConsoleText(), Constant.TASK_LIST_SEARCH);
				
			default:
				this.setPreviewPane(this.getConsoleText(), Constant.TASK_LIST_HELP);
		}
	}
	
	private void refreshCurrentList() {
		this.changeViewTo(this.getCurrentListName());
	}
	
/**
 * ==========================================================================
 * other methods
 * ==========================================================================
 */	
	/**
	 * update the model training file according to the given user input
	 * @param userInput
	 */
	private void updateTsv(String userInput) {
		if (!userInput.equals(Constant.EMPTY_STRING)) {
			try {
				NerParser.updateTsvFile(userInput);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
