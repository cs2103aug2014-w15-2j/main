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

import modal.CommandFailedException;
import modal.Constraint;
import modal.Task;
import modal.User;

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
	private String currentListName = Constant.TASK_LIST_TODO;
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
	 * ================= Constructor
	 * ============================================
	 * =============================================
	 */
	//@author A0119447Y
	public MainViewController(Stage stage) {
		try {
			this.copyUserNlpFiles();
			this.user = new User();
			this.loadFont();
			this.loadFxml();
			this.loadParser();
			this.addTextFieldListener();
			this.initializeShortCuts();
			this.updatePage();
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
	 * ================ Methods which might be called during initialization (in
	 * constructor)
	 * ==============================================================
	 * ==========================
	 */
	//@author A0119379R
	private void loadFont() {
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_BASE_BOLD)
				.toExternalForm(), 10);
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_BASE)
				.toExternalForm(), 10);
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_TIME)
				.toExternalForm(), 10);
	}

	//@author A0119379R
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
	private void loadFxml() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"MainView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		fxmlLoader.load();
	}

	//@author A0119379R
	private void loadParser() {
		final MainViewController instance = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				instance.updateTextField("NLP MODEL LOADING");
				instance.parser = new NerParser();
				instance.updateTextField("");
				instance.updatePreviewLater();
			}
		}).start();
	}

	//@author A0119379R
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

	//@author A0119379R
	private void registerKeyShortCuts(HotKeyListener instance) {
		keyShortCuts.register(
				KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_DESCRIPTION_TAG),
				instance);
		keyShortCuts
				.register(
						KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_DATE_TAG),
						instance);
		keyShortCuts.register(
				KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_TAG_TAG), instance);
		keyShortCuts.register(
				KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_COMMAND_TAG),
				instance);
		keyShortCuts.register(
				KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_INDEX_TAG),
				instance);
		keyShortCuts.register(
				KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_PRIORITY_TAG),
				instance);

		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_PREVIEW),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_CREATE),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_READ),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_UPDATE),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_DELETE),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_SEARCH),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_RELOAD),
				instance);

		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_TO_DO),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_TRASHED),
				instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_DONE),
				instance);

		keyShortCuts
				.register(
						KeyStroke.getKeyStroke(Constant.HOT_KEY_LAST_COMMAND),
						instance);
		keyShortCuts
				.register(
						KeyStroke.getKeyStroke(Constant.HOT_KEY_NEXT_COMMAND),
						instance);
	}

	//@author A0119379R
	private void updatePage() {
		setPreviewPane(
				"Welcome to List of Xiao Ming. \nPlease wait for the NLP model loading...",
				this.getCurrentListName());
		displayScrollPane.setStyle("-fx-font: 12px \"Monaco\";");
		this.setDisplayText("HELP:" + "\n\n"
				+ Constant.GUI_MESSAGE_SHORTCUT_INSTRUCTION);
	}

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

	//@author A0119379R
	private void silentErrorStream() {
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
	}

	/**
	 * ========================================================================
	 * =================================== I/O & hot key responding methods
	 * ======
	 * ====================================================================
	 * =================================
	 */
	//@author A0119447Y
	public String getUserInput(boolean willClear) {
		String text = input.getText();
		if (willClear) {
			input.clear();
		}
		return text;
	}

	//@author A0119379R
	public void setPreviewText(String textToDisplay) {
		VBox previewContentBox = new VBox();
		Label text = new Label(textToDisplay);
		previewContentBox.getChildren().clear();
		previewContentBox.setStyle(Constant.CSS_STYLE_PRIVIEW_CONTENT_BOX);
		previewContentBox.getChildren().add(text);
		previewScrollPane.setStyle(Constant.CSS_STYLE_PREVIEW_SCROLL_PANE);
		previewScrollPane.setContent(previewContentBox);
	}

	//@author A0119379R
	public void setPreviewPane(String textToDisplay, String listName) {
		GridPane previewContentPane = new GridPane();
		previewContentPane.setStyle("-fx-padding: 8 8 8 8;");
		previewContentPane.setHgap(15);

		VBox parsingFeedbackBox = new VBox();
		parsingFeedbackBox.setPrefWidth(650);
		parsingFeedbackBox.setPrefHeight(138);

		consoleTextLabel = new Label(textToDisplay);
		consoleTextLabel.setStyle("-fx-font: 12px \"Monaco\";"
				+ "-fx-text-fill: white;");
		parsingFeedbackBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"
				+ "-fx-padding: 8 8 8 8;" 
				+ Constant.CSS_STYLE_SHADOW);
		parsingFeedbackBox.getChildren().add(consoleTextLabel);

		VBox listIndicatorBox = new VBox();
		listIndicatorBox.setAlignment(Pos.BOTTOM_RIGHT);
		listIndicatorBox.setPrefWidth(getWidth() - 720);
		Label listLabel = new Label(listName.toUpperCase());
		listLabel.setFont(Font.font("Akagi", FontWeight.EXTRA_BOLD, 50));

		listLabel.setStyle( "-fx-text-fill: white;"
				+ "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 5, 0.1, 3, 1);");
		
		if (listName.equalsIgnoreCase(Constant.TASK_LIST_TODO)) {
			listIndicatorBox.setStyle("-fx-background-color: rgba(251, 235, 178, 1);"
					+ "-fx-padding: 8 16 8 8;"
					+ Constant.CSS_STYLE_SHADOW);
		} else if (listName.equalsIgnoreCase(Constant.TASK_LIST_TRASHED)) {
			listIndicatorBox.setStyle("-fx-background-color: rgba(150, 150, 150, 1);"
					+ "-fx-padding: 8 16 8 8;"
					+ "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 5, 0.1, 1, 1);");
		} else if (listName.equalsIgnoreCase(Constant.TASK_LIST_FINISHED)){
			listIndicatorBox.setStyle("-fx-background-color: rgba(222, 236, 147, 1);"
					+ "-fx-padding: 8 16 8 8;"
					+ "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 5, 0.1, 1, 1);");
		} else {
			listIndicatorBox.setStyle("-fx-background-color: rgba(21, 107, 182, 1);"
					+ "-fx-padding: 8 16 8 8;"
					+ "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 5, 0.1, 1, 1);");

		}

		listIndicatorBox.getChildren().add(listLabel);

		previewContentPane.add(parsingFeedbackBox, 0, 0);
		previewContentPane.add(listIndicatorBox, 1, 0);

		previewScrollPane.setStyle(Constant.CSS_STYLE_PREVIEW_SCROLL_PANE);
		previewScrollPane.setContent(previewContentPane);
	}

	//@author A0119447Y
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
	public void setDisplayPane(ArrayList<Task> taskList) {
		if (taskList != null && taskList.isEmpty()) {
			this.loadPreview();
		} else {
			VBox displayContent = new VBox();
			displayContent.setAlignment(Pos.CENTER);
			GridPane taskPane = LayoutManager.getTaskPane(taskList, getWidth());

			System.out.println("taskPane width: " + taskPane.getWidth());
			displayContent.getChildren().add(taskPane);
			displayScrollPane.setContent(displayContent);

		}
	}

	//@author A0119379R
	/**
	 * insert the given text to the given position in the textField
	 * 
	 * @param cursorPosition
	 * @param text
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
	private void updateTextField(final String text) {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (text == null) {
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

	/**
	 * toggle between e.g. <DATE> & </DATE>
	 * 
	 * @param tag
	 * @return
	 */
	//@author A0119379R
	private String toggleTag(String tag) {
		if (tag.contains("</")) {
			return tag.replace("</", "<");
		} else {
			return tag.replace("<", "</");
		}

	}

	//@author A0119379R
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
					setPreviewPane("We're ready for your commands!",
							instance.getCurrentListName());
				}
			}
		});
	}

	//@author A0119447Y
	@FXML
	private void onEnter() {
		String command = getUserInput(true);
		if (command.equals("")) {
			refresh(Constant.TASK_LIST_TODO);
		} else {
			this.commandHistory.add(command);
			this.currentCommandIndex = this.commandHistory.size();
			this.execute(command);
		}
	}

	//@author A0119379R
	private void loadEmptyImage() {
		HBox emptyImageBox = new HBox();
		emptyImageBox.setPrefHeight(200);
		emptyImageBox.setAlignment(Pos.CENTER_RIGHT);
		Image emptyImage = new Image(getClass().getResourceAsStream(
				"/resource/empty.png"));
		ImageView emptyImageView = new ImageView(emptyImage);
		emptyImageView.fitHeightProperty().bind(emptyImageBox.heightProperty());
		emptyImageBox.getChildren().add(emptyImageView);
		this.displayScrollPane.setContent(emptyImageBox);
	}

	//@author A0119444E
	/**
	 * set the scroll bar style using css and set 2 event handlers
	 */
	private void setDisplayScrollbarStyle() {
		displayScrollPane.getStyleClass().add("mylistview");
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
	 * ========================== Demux methods
	 * ==================================
	 * ================================================================
	 */

	//@author A0119379R
	public void execute(String userInput) {

		if (!userInput.equals("")) {
			try {
				NerParser.updateTsvFile(userInput);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		COMMAND_TYPE thisCommand;
		try {
			if (userInput.equals("")) {
				return;
			}

			thisCommand = this.parser.pickCommand(userInput
					.toLowerCase());
			System.err.println("CMD - executeNER: " + thisCommand);

			switch(thisCommand) {
				case ADD:
					setPreviewPane(this.add(userInput), this.getCurrentListName());
					refresh(Constant.TASK_LIST_TODO);
					break;
					
				case DELETE:
					setPreviewPane(this.delete(userInput), this.getCurrentListName());
					refreshCurrentList();
					break;
					
				case UPDATE:
					setPreviewPane(this.update(userInput), this.getCurrentListName());
					refresh(Constant.TASK_LIST_TODO);
					break;
					
				case SEARCH:
					ArrayList<Task> queryList = this.search(userInput);
					if (queryList != null) {
						setDisplayPane(queryList);
					}
					refresh(Constant.TASK_LIST_SEARCH);
					break;
				
				case DISPLAY:
					refresh(Constant.TASK_LIST_TODO);
					break;
					
				case UNDO:
					setPreviewPane(this.undo(), this.getCurrentListName());
					refreshCurrentList();
					break;
					
				case REDO:
					setPreviewPane(this.redo(), this.getCurrentListName());
					refreshCurrentList();
					break;
					
				case CLEAR:
					setPreviewPane(this.clear(), this.getCurrentListName());
					refreshCurrentList();
					break;
					
				case EXIT:
					System.setErr(err); 
					NerParser.updateModal();
					User.exit();
					break;
					
				case HELP:
					setDisplayText(this.help());
					break;
					
				case EMPTY_TRASH:
					setPreviewPane(this.emptyTrash(), this.getCurrentListName());
					refreshCurrentList();
					break;
					
				case RELOAD:
					setPreviewPane(this.reloadNLPModel(), this.getCurrentListName());
					refreshCurrentList();
					break;
					
				case DONE:
					setPreviewPane(this.done(userInput), this.getCurrentListName());
					refreshCurrentList();
					break;
					
				case RECOVER:
					setPreviewPane(this.recover(), this.getCurrentListName());
					refresh(Constant.TASK_LIST_TODO);
					break;
					
				default:
					break;

			}
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private String getCurrentListName() {
		return this.currentListName;
	}

	//@author A0119379R
	public String getPreview(String userInput) {
		try {
			if (userInput.equals("") || (userInput == null)) {
				return "The expected result will be shown here";
			}
			
			if (this.parser == null) {
				return "";
			}

			COMMAND_TYPE thisCommand = this.parser
					.pickCommand(userInput.toLowerCase());
			System.err.println("CMD - getPreview: " + thisCommand);

		
			switch(thisCommand) {
				case ADD:
					return getAddPreview(userInput);
					
				case DELETE:
					return getDeletePreview(userInput);
					
				case UPDATE:
					return getUpdatePreview(userInput);
					
				case SEARCH:
					return getSearchPreview(userInput);
				
				case DONE:
					return getDonePreview(userInput);
					
				case DISPLAY:
					return getDisplayPreview();
	
				case UNDO:
					return getUndoPreview();
					
				case REDO:
					return getRedoPreview();
					
				case CLEAR:
					return getClearPreview();
					
				case EMPTY_TRASH:
					return getEmptyTrashPreview();
					
				case RELOAD:
					return getReloadPreview();
					
				case HELP:
					return getHelpPreview();
					
				case EXIT:
					return getExitPreview();
					
				case RECOVER:
					return getRecoverPreview();
					
				default:
					return getDefaultPreview();

			}		
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return "Failure in Parsing The Task";
		}
	}

	/**
	 * ========================================================================
	 * =============================== Specific methods to get previews==========
	 */
	//@author A0119379R

/**
 * 	=======================================================================================================
 *  Specific methods to get previews
 *  =======================================================================================================
 */
	
	//@author A0119379R
	private String getRecoverPreview() {
		return "Command: recover";
	}
	
	//@author A0119379R
	private String getDefaultPreview() {
		return "Command not recognized";
	}

	//@author A0119379R
	private String getReloadPreview() {
		return "Command: reload model";
	}

	//@author A0119379R
	private String getEmptyTrashPreview() {
		return "Command: empty trash";
	}

	//@author A0119379R
	private String getHelpPreview() {
		return "Command: help";
	}

	//@author A0119379R
	private String getExitPreview() {
		return "Command: exit";
	}

	//@author A0119379R
	private String getClearPreview() {
		return "Command: clear";
	}

	//@author A0119379R
	private String getRedoPreview() {
		return "Command: redo";
	}

	//@author A0119379R
	private String getUndoPreview() {
		return "Command: undo";
	}

	//@author A0119379R
	private String getDisplayPreview() {
		return "Command: display";
	}

	//@author A0119379R
	private String getDonePreview(String userInput) {
		try {
			if (this.getCurrentListName().equals(Constant.TASK_LIST_FINISHED)) {
				return "Command: done \n\n" + "All tasks in 'FINISHED' section have been finished already";
			} else if (this.getCurrentListName().equals(Constant.TASK_LIST_TRASHED)) {
				return "Command: done \n\n" + "Move the task out of 'TRASHED' section first to finish it";
			}
			
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			String returnValue = "Command: done \n\n";

			for (int index : indices) {
				Task taskToFinish = this.user.retrieveFromNormalList(index - 1);
				returnValue += (index + ": " + taskToFinish.getDescription() + "\n");
			}
			return returnValue;
		} catch (CommandFailedException de) {
			return "Command: done \n\n" + "No Task Specified";
		}
	}

	//@author A0119379R
	private String getSearchPreview(String userInput) {
		Constraint thisConstraint = parser.getConstraint(userInput);
		return "Command: search \n\n" + thisConstraint.toString();
	}

	//@author A0119379R
	private String getUpdatePreview(String userInput) {
		try {
			if (!this.getCurrentListName().equals(Constant.TASK_LIST_TODO)) {
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

	//@author A0119379R
	private String getDeletePreview(String userInput) {
		try {
			if (this.getCurrentListName().equals(Constant.TASK_LIST_TRASHED)) {
				return "Command: delete \n\n" + "All tasks in 'TRASHED' section have been trashed already.";
			}
			
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			String returnValue = "Command: delete \n\n";

			for (int index : indices) {
				Task taskToDelete = this.user.retrieve(index - 1, this.getCurrentListName());
				returnValue += (index + ": " + taskToDelete.getDescription() + "\n");
			}
			return returnValue;
		} catch (CommandFailedException de) {
			return "Command: delete \n\n" + "No Task Specified";
		}
	}

	//@author A0119379R
	private String getAddPreview(String userInput) {
		Task taskToAdd = parser.getTask(userInput);
		return "Command: create \n\n" + taskToAdd.toStringForDisplaying();
	}

	/**
	 * ===================== Methods to execute the specific operations ================
	 */
	//@author A0119379R
	private String add(String userInput) {
		Task taskToAdd = parser.getTask(userInput);
		assert (taskToAdd != null);
		return (this.user.add(taskToAdd)) ? Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY
				: Constant.PROMPT_MESSAGE_ADD_TASK_FAILED;
	}

	//@author A0119379R
	private String delete(String userInput) {
		try {
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			Collections.sort(indices);
			int offset = 0;
			boolean isAllSucceeded = true;
			String returnValue = "";
			for (int index : indices) {
				try {
					boolean isThisSucceeded = this.user.delete(index - offset - 1, this.getCurrentListName());

					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_DELETE_TASK_FAILED
								+ " for task " + index);
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

	//@author A0119379R
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

	//@author A0119379R
	private ArrayList<Task> search(String userInput) {
		Constraint thisConstraint;
		try {
			thisConstraint = parser.getConstraint(userInput);
			ArrayList<Task> queryResult = this.user.find(thisConstraint, this.getCurrentListName());
			if (queryResult.isEmpty()) {
				setPreviewText(Constant.PROMPT_MESSAGE_NO_TASK_FOUNDED);
				return null;
			} else {
				return queryResult;
			}
		} catch (CommandFailedException e) {
			e.printStackTrace();
			setPreviewText("SEARCH ERROR");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			setPreviewText("SEARCH ERROR");
			e.printStackTrace();
			return null;
		}

	}

	//@author A0119379R
	private ArrayList<Task> displayNormal() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getNormalTaskList();
			if (queryResult.isEmpty()) {
				System.out.println("displaying image");
				this.loadEmptyImage();
				return null;
			} else {
				return queryResult;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setPreviewText(Constant.PROMPT_MESSAGE_DISPLAY_ERROR);
			return null;
		}
	}

	//@author A0119447Y
	private ArrayList<Task> displayTrashed() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getTrashedTaskList();
			System.out.println("displayTrashed queryResult length: "
					+ this.user.currentTasks.trashedTasks.size());
			if (queryResult.isEmpty()) {
				setDisplayText(Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK);
				// TODO: create a box for empty list
				return null;
			} else {
				return queryResult;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setPreviewText(Constant.PROMPT_MESSAGE_DISPLAY_ERROR);
			return null;
		}
	}

	//@author A0119447Y
	private ArrayList<Task> displayFinished() {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			setPreviewText(Constant.PROMPT_MESSAGE_DISPLAY_ERROR);
			return null;
		}
	}

	//@author A0119379R
	private String emptyTrash() {
		this.user.emptyTrash();
		return Constant.PROMPT_MESSAGE_TRASH_EMPTIED;
	}

	//@author A0119379R
	private String help() {
		return Constant.GUI_MESSAGE_WELCOME + "\n"
				+ Constant.GUI_MESSAGE_SHORTCUT_INSTRUCTION;
	}

	//@author A0119379R
	private String clear() {
		try {
			this.user.deleteAll(this.getCurrentListName());
			return "All tasks trashed";
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	//@author A0119379R
	private String undo() {
		try {
			this.user.undo();
			return Constant.PROMPT_MESSAGE_UNDO_SUCCESSFULLY;
		} catch (CommandFailedException e) {
			UtilityMethod.showToUser(e.toString());
			return Constant.PROMPT_MESSAGE_UNDO_FAILED;
		}
	}

	//@author A0119379R
	private String redo() {
		try {
			this.user.redo();
			return Constant.PROMPT_MESSAGE_REDO_SUCCESSFULLY;
		} catch (CommandFailedException e) {
			UtilityMethod.showToUser(e.toString());
			return Constant.PROMPT_MESSAGE_REDO_FAILED;
		}
	}

	//@author A0119379R
	private String reloadNLPModel() {
		NerParser.updateModal();
		this.parser = new NerParser();
		return "Model reloaded!";
	}

	//@author A0119379R
	private String done(String userInput) {
		try {
			ArrayList<Integer> indices = parser.pickIndex(userInput);
			Collections.sort(indices);
			int offset = 0;
			boolean isAllSucceeded = true;
			String returnValue = "";
			for (int index : indices) {
				try {
					boolean isThisSucceeded = this.user
							.done(index - offset - 1);
					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_DONE_TASK_FAILED
								+ " for task " + index);
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

	/**
	 * ========================================================================
	 * == the delegation methods to respond to shortcuts
	 * ========================
	 * ==================================================
	 */
	//@author A0119447Y
	private String recover() {
		return this.user.recover(this.getCurrentListName());
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
			insertTextIntoTextField(cursorPosition, "add ");
			break;

		case KeyEvent.VK_R + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, "display ");
			break;

		case KeyEvent.VK_U + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, "update ");
			break;

		case KeyEvent.VK_D + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, "delete ");
			break;

		case KeyEvent.VK_F + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, "search ");
			break;

		case KeyEvent.VK_M + Constant.MODIFIER_CTRL:
			insertTextIntoTextField(cursorPosition, "reload model ");
			break;

		case KeyEvent.VK_BACK_SPACE + Constant.MODIFIER_CTRL:
			String inputString = (String) input.getCharacters();
			insertTextIntoTextField(cursorPosition,
					inputString.substring(0, inputString.lastIndexOf(" ")));
			break;

		case KeyEvent.VK_1 + Constant.MODIFIER_CTRL:
			changeToToDoList();
			break;

		case KeyEvent.VK_3 + Constant.MODIFIER_CTRL:
			changeToTrashedList();
			break;

		case KeyEvent.VK_2 + Constant.MODIFIER_CTRL:
			changeToFinishedList();
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

	private void changeToToDoList() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			
			@Override
	        public void run() {
	        	instance.setDisplayPane(instance.displayNormal());
	        	instance.currentListName = Constant.TASK_LIST_TODO;
	        	instance.refresh(Constant.TASK_LIST_TODO);
	        }
	   });
	}

	private void changeToTrashedList() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.setDisplayPane(instance.displayTrashed());
	        	instance.currentListName = Constant.TASK_LIST_TRASHED;
	        	instance.refresh(Constant.TASK_LIST_TRASHED);
	        }
	   });
	}

	private void changeToFinishedList() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.setDisplayPane(instance.displayFinished());
	        	instance.currentListName = Constant.TASK_LIST_FINISHED;
	        	instance.refresh(Constant.TASK_LIST_FINISHED);
	        }
	   });
		
	}

	private void updatePreviewLater() {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				instance.setPreviewPane("We'are ready for your commands!",
						instance.getCurrentListName());
			}
		});

	}
	
	private void refresh(String listName) {
		this.currentListName = listName;
		switch(listName) {
			case Constant.TASK_LIST_TODO:
				this.setPreviewPane(this.consoleTextLabel.getText(), Constant.TASK_LIST_TODO);
				this.setDisplayPane(this.displayNormal());
				break;
				
			case Constant.TASK_LIST_FINISHED:
				this.setPreviewPane(this.consoleTextLabel.getText(), Constant.TASK_LIST_FINISHED);
				this.setDisplayPane(this.displayFinished());
				break;
				
			case Constant.TASK_LIST_TRASHED:
				this.setPreviewPane(this.consoleTextLabel.getText(), Constant.TASK_LIST_TRASHED);
				this.setDisplayPane(this.displayTrashed());
				break;
				
			default:
				this.setPreviewPane(this.consoleTextLabel.getText(), Constant.TASK_LIST_SEARCH);
		}
	}
	
	private void refreshCurrentList() {
		this.refresh(this.getCurrentListName());
	}
	
}
