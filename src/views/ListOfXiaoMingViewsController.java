package views;

import infrastructure.Constant;
import infrastructure.NERParser;
import infrastructure.Parser;
import infrastructure.UtilityMethod;
import infrastructure.Constant.COMMAND_TYPE;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.KeyStroke;

import reference.CommandFailedException;
import reference.Constraint;
import reference.Pair;
import reference.TimeInterval;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import dataStore.TestingCache;
import dataStructure.Task;
import dataStructure.User;
import userInterface.ListOfXiaoMing;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ListOfXiaoMingViewsController extends GridPane implements HotKeyListener{
	@FXML
	private TextField input;
	
	@FXML
	private ScrollPane display;
	private VBox content;
	
	public ScrollPane preview;
	private VBox previewContent;
	
	private final static boolean ERROR_PRINT_ON = false;
	private boolean isNlpOn = true;
	private Parser parser = new Parser();
	private static PrintStream err = System.err;
	// a property to store the current user
	private User user;
	
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
        
		try {
			this.user = new User("haha");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.initializeShortCuts();
        updatePage();
        
        final ListOfXiaoMingViewsController instance = this;
        this.input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
            	instance.loadPreview();	
            }
        });
        
        if (!ERROR_PRINT_ON) {
			// now make all writes to the System.err stream silent
			System.setErr(new PrintStream(new OutputStream() {
				public void write(int b) {
				}
			}));
		}
        
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
//		setPreview("\n\n\n\t\t  Hit CTRL+ENTER to load a preview");
		
		setPreview(this.executeNLP(command));
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
	        	String userInput = getUserInput(false);
	        	if (userInput.length() > 0) {
	        		System.out.println(userInput);
	        		setPreview(instance.getPreview(userInput));
	        	}
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
	
	/**
	 * method to execute the system-level command like log in or log out
	 * 
	 * @return
	 */
	public static String executeUpperLevelCommand(String commandString) {
		Pair<COMMAND_TYPE, ArrayList<String>> commandPair = Parser
				.parseCommandPair(commandString);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		ArrayList<String> parameter = commandPair.tail;
		if (commandString.equals("") || commandString.equalsIgnoreCase("clear")) {
			for (int i = 0; i < 24; i++) {
				System.out.println();
			}
			return null;
		}

		switch (thisCommand) {
		case LOG_IN:
			return User.userLogIn(parameter);

		case CREATE_ACCOUNT:
			return User.createAccount(parameter);

		case DELETE_ACCOUNT:
			UtilityMethod.showToUser(User.deleteAccount());
			return null;

		case HELP:
			UtilityMethod.showToUser(User.showHelp());
			return null;

		case EXIT:
			System.setErr(err);
			User.exit();

		default:
			UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_NOT_LOG_IN);
			return null;
		}
	}
	
	
//User level commands

	public String executeNLP (String userInput) {
		
//			if (!userInput.equals("")) {
//				try {
//					NERParser.updateTsvFile(userInput);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}

		COMMAND_TYPE thisCommand;
		try {
			if (userInput.equals("")) {
				return "";
			}

			thisCommand = this.parser.nerParser.pickCommand(userInput);
			System.err.println("CMD - executeNER: " + thisCommand);

			switch(thisCommand) {
				case ADD:
					return this.addNLP(userInput);
					
				case DELETE:
					return this.deleteNLP(userInput);
					
				case UPDATE:
					return this.updateNLP(userInput);
					
				case SEARCH:
					return this.searchNLP(userInput);
				
				case DISPLAY:
					return this.display();
					
				case LOG_OUT:
					return this.logOut();
					
				case UNDO:
					return this.undo();
					
				case REDO:
					return this.redo();
					
				case CLEAR:
					return this.clear();
					
				case EXIT:
					System.setErr(err); 
					NERParser.updateModal();
					User.exit();
					break;
					
				case NLP:
					return this.toggleNLP();
					
				default:
					return "";

			}
			return "";
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}
	}

	public String execute(String userInput) {
		Pair<COMMAND_TYPE, ArrayList<String>> commandPair = Parser
				.parseCommandPair(userInput);
		COMMAND_TYPE thisCommand = commandPair.head;
		ArrayList<String> parameterList = commandPair.tail;

		switch (thisCommand) {
		case ADD:
			return this.add(parameterList);

		case DELETE:
			return this.delete(parameterList);

		case UPDATE:
			return this.update(parameterList);

		case DISPLAY:
			return this.display();

		case SEARCH:
			return this.search(parameterList);

		case LOG_OUT:
			return this.logOut();

		case UNDO:
			return this.undo();

		case REDO:
			return this.redo();

		case CLEAR:
			return this.clear();

		case EXIT:
			System.setErr(err);
			User.exit();

		case NLP:
			return this.toggleNLP();

		default:
			return "";
		}
	}

	
	public String getPreview(String userInput) {
		try {
			Task taskToAdd = parser.nerParser.getTask(userInput);
			return taskToAdd.toStringForDisplaying();
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return "Failure in Parsing The Task";
		}
	}
	
	
	
	/**
	 * toggle between NLP mode and non-NLP mode
	 * 
	 * @return
	 */
	private String toggleNLP() {
		this.isNlpOn = !this.isNlpOn;
		return (this.isNlpOn) ? "NLP ON" : "NLP OFF";
	}

	// add

	private String add(ArrayList<String> taskParameters) {
		Task taskToAdd;
		try {
			taskToAdd = parser.getTaskFromParameterList(taskParameters);
			assert (taskToAdd != null);
			return (this.user.add(taskToAdd)) ? Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY
					: Constant.PROMPT_MESSAGE_ADD_TASK_FAILED;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}

	private String addNLP(String userInput) {
		try {
			Task taskToAdd = parser.nerParser.getTask(userInput);
			assert (taskToAdd != null);
			return (this.user.add(taskToAdd)) ? Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY
					: Constant.PROMPT_MESSAGE_ADD_TASK_FAILED;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}

	// delete

	private String delete(ArrayList<String> taskParameters) {
		if (taskParameters.size() == 0) {
			return "Please enter the task index you want to delete";
		} else {
			int index = Integer.parseInt(taskParameters.get(0));
			try {
				return (this.user.delete(index - 1)) ? Constant.PROMPT_MESSAGE_DELETE_TASK_SUCCESSFULLY
						: Constant.PROMPT_MESSAGE_DELETE_TASK_FAILED;
			} catch (CommandFailedException e) {
				return e.toString();
			}
		}
	}

	private String deleteNLP(String userInput) {
		try {
			int index = parser.nerParser.pickIndex(userInput);
			return (this.user.delete(index - 1)) ? Constant.PROMPT_MESSAGE_DELETE_TASK_SUCCESSFULLY
					: Constant.PROMPT_MESSAGE_DELETE_TASK_FAILED;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}

	// update

	private String update(ArrayList<String> taskParameters) {
		int index = Integer.parseInt(taskParameters.get(0).trim());
		try {
			taskParameters.remove(0);
			this.user.update(index - 1, parser.getTaskMap(taskParameters));
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
		}

		return Constant.PROMPT_MESSAGE_UPDATE_TASK_SUCCESSFULLY;
	}

	private String updateNLP(String userInput) {
		try {
			int index = parser.nerParser.pickIndex(userInput);
			this.user.update(index - 1,
					parser.nerParser.getUpdatedTaskMap(userInput));
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
		}

		return Constant.PROMPT_MESSAGE_UPDATE_TASK_SUCCESSFULLY;
	}

	// search

	private String search(ArrayList<String> taskParameters) {

		try {
			TimeInterval timeInterval = new TimeInterval();
			String keyword = "";
			for (String parameter : taskParameters) {
				String key = UtilityMethod.getFirstWord(parameter);
				String value = UtilityMethod.removeFirstWord(parameter);
				if (key.equalsIgnoreCase("time")) {
					timeInterval = parser.parseTimeInterval(value);
					UtilityMethod.showToUser(String.format(
							Constant.PROMPT_MESSAGE_SEARCH_TIME_INTERVAL,
							timeInterval));
				} else {
					keyword = parameter;
					UtilityMethod.showToUser(String.format(
							Constant.PROMPT_MESSAGE_SEARCH_KEYWORD, keyword));
				}
			}

			Constraint thisConstraint = new Constraint(keyword, timeInterval);
			ArrayList<Task> queryResult = this.user.find(thisConstraint);
			String queryResultString = UtilityMethod
					.taskListToString(queryResult);
			if (queryResultString.equals("")) {
				return Constant.PROMPT_MESSAGE_NO_TASK_FOUNDED;
			} else {
				return queryResultString;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	private String searchNLP(String userInput) {
		Constraint thisConstraint;
		try {
			thisConstraint = parser.nerParser.getConstraint(userInput);
			ArrayList<Task> queryResult = this.user.find(thisConstraint);
			String queryResultString = UtilityMethod
					.taskListToString(queryResult);
			if (queryResultString.equals("")) {
				return Constant.PROMPT_MESSAGE_NO_TASK_FOUNDED;
			} else {
				return queryResultString;
			}
		} catch (CommandFailedException e) {
			return e.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}

	}

	// common methods for NLP and non-NLP

	private String display() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getTaskList();
			String resultString = UtilityMethod.taskListToString(queryResult);
			if (resultString.equalsIgnoreCase("")) {
				return Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK;
			} else {
				return resultString;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Constant.PROMPT_MESSAGE_DISPLAY_ERROR;
		}
	}

	private String clear() {
		try {
			this.user.clear();
			return "All tasks trashed";
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

	private String logOut() {		
		
		NERParser.updateModal();

		if (TestingCache.clearCache()) {
			return Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY;
		} else {
			return Constant.PROMPT_MESSAGE_CLEAR_CACHE_FAILED;
		}
	}
}
