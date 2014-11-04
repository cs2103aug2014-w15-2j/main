package view;
import reference.*;
import infrastructure.*;
import infrastructure.Constant.COMMAND_TYPE;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import dataStructure.Task;
import dataStructure.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainViewController extends GridPane implements HotKeyListener{
	
	@FXML
	private TextField input;
	
	@FXML
	private GridPane dragNode;
	
	@FXML
	private ScrollPane displayScrollPane;
	public ScrollPane previewScrollPane;
	
	private Parser parser = new Parser();
	private static PrintStream err = System.err;
	// a property to store the current user
	private User user;
	
	private Provider keyShortCuts = null;
	

	
	private String descriptionTag 	= "</DESCRIPTION>";
	private String dateTag 			= "</DATE>";
	private String tagTag 			= "</TAG>";
	private String commandTag		= "</COMMAND>";
	private String indexTag 		= "</INDEX>";
	private String priorityTag 		= "</PRIORITY>";
	
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int currentCommandIndex = 0;
	
	
	
	public MainViewController(Stage stage) {
		try {
			this.loadFont();
			this.loadFxml();
			this.user = new User();
			this.initializeShortCuts();
			this.updatePage();
			this.addTextFieldListener();
	        
	        UtilityMethod.makeDraggable(stage, dragNode);
	        
	        if (!Constant.ERROR_PRINT_ON) {
				silentErrorStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
/**
 * ========================================================================================
 *  Methods which might be called during initialization
 * ========================================================================================
 */
	
	private void loadFont() {
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_BASE).toExternalForm(), 10);
		Font.loadFont(getClass().getResource(Constant.FONT_FILE_TIME).toExternalForm(), 10);
	}
	
	private void loadFxml() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		fxmlLoader.load();
	}

	private void initializeShortCuts(){
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
	
	private void registerKeyShortCuts(HotKeyListener instance) {
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_DESCRIPTION_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_DATE_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_TAG_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_COMMAND_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_INDEX_TAG), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_ADD_PRIORITY_TAG), instance);
		
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_PREVIEW), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_CREATE), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_READ), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_UPDATE), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_DELETE), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_SEARCH), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_RELOAD), instance);
		
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_LAST_COMMAND), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(Constant.HOT_KEY_NEXT_COMMAND), instance);
	}
	
	private void updatePage() {
		setPreview("\n\n\n\t\t\t  Welcome to List of Xiao Ming");
		this.setDisplay("HELP:" + "\n\n" + Constant.GUI_MESSAGE_SHORTCUT_INSTRUCTION);
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

	private void silentErrorStream() {
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
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
		if (command.equals("")) {
			displayTaskList(this.display());			
		} else {
//			setPreview("\n\n\n\t\t  Welcome to List of Xiao Ming");
			this.commandHistory.add(command);
			this.currentCommandIndex = this.commandHistory.size();
			this.execute(command);
		}
    }
	
	public void setDisplay(String textToDisplay) {
		VBox displayContent = new VBox();
		Label textLabel = new Label(textToDisplay);
		displayContent.getChildren().clear();
		displayContent.getChildren().add(textLabel);
		displayScrollPane.setContent(displayContent);
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
		
		displayScrollPane.setFocusTraversable(true);
	}
	
	/**
	 * @param taskList
	 */
	public void displayTaskList(ArrayList<Task> taskList) {
		VBox displayContent = new VBox();
		displayContent.setAlignment(Pos.CENTER);
		displayContent.getChildren().clear();
		GridPane taskPane = LayoutManager.getTaskPane(taskList, getWidth());
		displayContent.getChildren().add(taskPane);
		displayScrollPane.setContent(displayContent);
	}
	
	public void setPreview(String textToDisplay) {
		VBox previewContentBox = new VBox();
		Label text = new Label(textToDisplay);
		previewContentBox.getChildren().clear();
		previewContentBox.setStyle(Constant.CSS_STYLE_PRIVIEW_CONTENT_BOX);
		previewContentBox.getChildren().add(text);
		previewScrollPane.setStyle(Constant.CSS_STYLE_PREVIEW_SCROLL_PANE);
		previewScrollPane.setContent(previewContentBox);
	}

	
	
	/**
	 * initialize the short cut settings
	 */

	
	/**
	 * register the shortcuts to the system
	 * @param instance
	 */

	
	
	/**
	 * toggle between e.g. <DATE> & </DATE>
	 * @param tag
	 * @return
	 */
	private String toggleTag(String tag) {
		if (tag.contains("</")) {
			return tag.replace("</", "<");
		} else {
			return tag.replace("<", "</");
		}
		
	}
	
	
	private void loadPreview() {
		//open a new thread to execute Java FX
		final MainViewController instance = this;
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
	
	
	/**
	 * insert the given text to the given position in the textField
	 * @param cursorPosition
	 * @param text
	 */
	private void insertTextToTextField(final int cursorPosition, final String text) {
		//open a new thread to execute Java FX
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.input.insertText(cursorPosition, text);
	        }
	   });
	}
	
	private void updateTextField(final String text) {
		final MainViewController instance = this;
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	instance.input.setText(text);
	        	instance.input.positionCaret(text.length());
	        }
	   });
	}
	
	/**
	 * the delegation methods to respond to shortcuts
	 */
	@Override
	public void onHotKey(HotKey key) {
		String tag = "";
		int cursorPosition = this.input.getCaretPosition();
		switch (key.keyStroke.getKeyCode() + key.keyStroke.getModifiers()) {
			case KeyEvent.VK_D + Constant.MODIFIER_ALT:
				descriptionTag = toggleTag(descriptionTag);
				tag = descriptionTag;
				insertTextToTextField(cursorPosition, tag);	
				break;

			case KeyEvent.VK_A + Constant.MODIFIER_ALT:
				dateTag = toggleTag(dateTag);
				tag = dateTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_C + Constant.MODIFIER_ALT:
				commandTag = toggleTag(commandTag);
				tag = commandTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_T + Constant.MODIFIER_ALT:
				tagTag = toggleTag(tagTag);
				tag = tagTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_I + Constant.MODIFIER_ALT:
				indexTag = toggleTag(indexTag);
				tag = indexTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
				
			case KeyEvent.VK_P + Constant.MODIFIER_ALT:
				priorityTag = toggleTag(priorityTag);
				tag = priorityTag;
				insertTextToTextField(cursorPosition, tag);	
				break;
			
			case KeyEvent.VK_ENTER + Constant.MODIFIER_CTRL:
				this.loadPreview();	
				break;
				
			case KeyEvent.VK_C + Constant.MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "add ");	
				break;
				
			case KeyEvent.VK_R + Constant.MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "display ");	
				break;
				
			case KeyEvent.VK_U + Constant.MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "update ");	
				break;
				
			case KeyEvent.VK_D + Constant.MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "delete ");	
				break;
				
			case KeyEvent.VK_F + Constant.MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "search ");	
				break;
				
			case KeyEvent.VK_M + Constant.MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "reload model ");	
				break;
				
			case KeyEvent.VK_BACK_SPACE + Constant.MODIFIER_CTRL:
				String inputString = (String) input.getCharacters();
				insertTextToTextField(cursorPosition, inputString.substring(0, inputString.lastIndexOf(" ")));
				break;
		}
		
		switch (key.keyStroke.getKeyCode()) {
			case KeyEvent.VK_UP:
				this.currentCommandIndex --;
				try {
					updateTextField(this.commandHistory.get(this.currentCommandIndex));
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO log
					this.currentCommandIndex ++;
				}
				break;
				
			case KeyEvent.VK_DOWN:
				try {
					this.currentCommandIndex ++;
					updateTextField(this.commandHistory.get(this.currentCommandIndex));
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO log
					this.currentCommandIndex --;
				}
				break;
		}
		
	}
	
	
	/**
	 * the important method to execute CRUD operations, etc.
	 * @param userInput
	 */
	public void execute(String userInput) {
		
			if (!userInput.equals("")) {
				try {
					NERParser.updateTsvFile(userInput);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		COMMAND_TYPE thisCommand;
		try {
			if (userInput.equals("")) {
				return ;
			}

			thisCommand = this.parser.nerParser.pickCommand(userInput);
			System.err.println("CMD - executeNER: " + thisCommand);

			switch(thisCommand) {
				case ADD:
					setPreview(this.add(userInput));
					displayTaskList(this.display());
					break;
					
				case DELETE:
					setPreview(this.delete(userInput));
					displayTaskList(this.display());
					break;
					
				case UPDATE:
					setPreview(this.update(userInput));
					displayTaskList(this.display());
					break;
					
				case SEARCH:
					ArrayList<Task> queryList = this.search(userInput);
					if (queryList != null) {
						displayTaskList(queryList);
					}
					break;
				
				case DISPLAY:
					ArrayList<Task> displayList = this.display();
					if (displayList != null) {
						displayTaskList(displayList);
					}
					break;
					
				case UNDO:
					setPreview(this.undo());
					displayTaskList(this.display());
					break;
					
				case REDO:
					setPreview(this.redo());
					displayTaskList(this.display());
					break;
					
				case CLEAR:
					setPreview(this.clear());
					displayTaskList(this.display());
					break;
					
				case EXIT:
					System.setErr(err); 
					NERParser.updateModal();
					User.exit();
					break;
					
				case HELP:
					setDisplay(this.help());
					break;
					
				case EMPTY_TRASH:
					setPreview(this.emptyTrash());
					displayTaskList(this.display());
					break;
					
				case RELOAD:
					setPreview(this.reloadNLPModel());
					displayTaskList(this.display());
					break;
					
				case DONE:
					setPreview(this.done(userInput));
					displayTaskList(this.display());
					break;
					
				default:
					break;

			}
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * get the expected result of a user input
	 * @param userInput
	 * @return
	 */
	public String getPreview(String userInput) {
		try {
			if (userInput.equals("")) {
				return "The expected result will be shown here";
			}
		
			COMMAND_TYPE thisCommand = this.parser.nerParser.pickCommand(userInput);
			System.err.println("CMD - getPreview: " + thisCommand);
			
			switch(thisCommand) {
				case ADD:
					try {
						Task taskToAdd = parser.nerParser.getTask(userInput);
						return "Command: create \n\n" + taskToAdd.toStringForDisplaying();
					} catch (CommandFailedException ae) {
						return "Command: create \n\n" + "Fail to Parse The Task";
					}
					
				case DELETE:
					try {
						ArrayList<Integer> indices = parser.nerParser.pickIndex(userInput);
						String returnValue = "Command: delete \n\n";
						
						for (int index : indices) {
							Task taskToDelete = this.user.retrieve(index - 1);
							returnValue += (index + ": " + taskToDelete.getDescription() + "\n");
						}
						return returnValue;
					} catch (CommandFailedException de) {
						return "Command: delete \n\n" + "No Task Specified"; 
					}
					
				case UPDATE:
					try {
						int index = parser.nerParser.pickIndex(userInput).get(0);
						Task taskToUpdate = this.user.getUpdatePreview(index - 1, parser.nerParser.getUpdatedTaskMap(userInput));
						return "Command: update \n\n" + taskToUpdate.toStringForDisplaying();
					} catch (CommandFailedException e) {
						e.printStackTrace();
						return "Command: update \n\n" + Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
					}
					
				case SEARCH:
					Constraint thisConstraint = parser.nerParser.getConstraint(userInput);
					return "Command: search \n\n" + thisConstraint.toString();
				
				case DONE:
					try {
						ArrayList<Integer> indices = parser.nerParser.pickIndex(userInput);
						String returnValue = "Command: done \n\n";
						
						for (int index : indices) {
							Task taskToFinish = this.user.retrieve(index - 1);
							returnValue += (index + ": " + taskToFinish.getDescription() + "\n");
						}
						return returnValue;
					} catch (CommandFailedException de) {
						return "Command: done \n\n" + "No Task Specified"; 
					}
					
				case DISPLAY:
					return "Command: display";
	
				case UNDO:
					return "Command: undo";
					
				case REDO:
					return "Command: redo";
					
				case CLEAR:
					return "Command: clear";
					
				case EXIT:
					return "Command: exit";
					
				case HELP:
					return "Command: help";
					
				case EMPTY_TRASH:
					return "Command: empty trash";
					
				case RELOAD:
					return "Command: reload model";
					
				default:
					return "Command not recognized";
	
			}
			
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return "Failure in Parsing The Task";
		}
	}

	
	private String add(String userInput) {
		try {
			Task taskToAdd = parser.nerParser.getTask(userInput);
			assert (taskToAdd != null);
			return (this.user.add(taskToAdd)) ? Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY
					: Constant.PROMPT_MESSAGE_ADD_TASK_FAILED;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}

	
	private String delete(String userInput) {
		try {
			ArrayList<Integer> indices = parser.nerParser.pickIndex(userInput);
			Collections.sort(indices);
			int offset = 0;
			boolean isAllSucceeded = true;
			String returnValue = "";
			for (int index : indices) {
				try {
					boolean isThisSucceeded = this.user.delete(index - offset - 1);
					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_DELETE_TASK_FAILED + " for task " + index);
					}
					isAllSucceeded &= isThisSucceeded;
					offset ++;
				} catch (CommandFailedException cfe) {
					return cfe.toString();
				}
				
			}
			
			return isAllSucceeded ? Constant.PROMPT_MESSAGE_DELETE_TASK_SUCCESSFULLY
					:returnValue ;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}


	private String update(String userInput) {
		try {
			int index = parser.nerParser.pickIndex(userInput).get(0);
			this.user.update(index - 1,
					parser.nerParser.getUpdatedTaskMap(userInput));
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
		}

		return Constant.PROMPT_MESSAGE_UPDATE_TASK_SUCCESSFULLY;
	}


	private ArrayList<Task> search(String userInput) {
		Constraint thisConstraint;
		try {
			thisConstraint = parser.nerParser.getConstraint(userInput);
			ArrayList<Task> queryResult = this.user.find(thisConstraint);
			if (queryResult.isEmpty()) {
				setPreview(Constant.PROMPT_MESSAGE_NO_TASK_FOUNDED);
				return null;
			} else {
				return queryResult;
			}
		} catch (CommandFailedException e) {
			e.printStackTrace();
			setPreview("SEARCH ERROR");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			setPreview("SEARCH ERROR");
			e.printStackTrace();
			return null;
		}

	}


	private ArrayList<Task> display() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getTaskList();
			if (queryResult.isEmpty()) {
				setDisplay(Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK);
				// TODO: create a box for empty list
				return null;
			} else {
				return queryResult;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setPreview(Constant.PROMPT_MESSAGE_DISPLAY_ERROR);
			return null;
		}
	}
	
	
	private String emptyTrash() {
		this.user.clear();
		return Constant.PROMPT_MESSAGE_TRASH_EMPTIED;
	}

	private String help() {
		return Constant.GUI_MESSAGE_WELCOME + "\n" + Constant.GUI_MESSAGE_SHORTCUT_INSTRUCTION;
	}

	private String clear() {
		try {
			this.user.deleteAll();
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
	
	private String reloadNLPModel() {
		NERParser.updateModal();
		this.parser = new Parser();
		return "Model reloaded!";
	}
	
	private String done(String userInput) {
		try {
			ArrayList<Integer> indices = parser.nerParser.pickIndex(userInput);
			Collections.sort(indices);
			int offset = 0;
			boolean isAllSucceeded = true;
			String returnValue = "";
			for (int index : indices) {
				try {
					boolean isThisSucceeded = this.user.done(index - offset - 1);
					if (!isThisSucceeded) {
						returnValue += (Constant.PROMPT_MESSAGE_DONE_TASK_FAILED + " for task " + index);
					}
					isAllSucceeded &= isThisSucceeded;
					offset ++;
				} catch (CommandFailedException cfe) {
					return cfe.toString();
				}
				
			}
			
			return isAllSucceeded ? Constant.PROMPT_MESSAGE_DONE_TASK_SUCCESSFULLY
					:returnValue ;
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}
}
