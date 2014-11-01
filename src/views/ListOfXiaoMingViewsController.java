package views;

import infrastructure.Constant;
import infrastructure.Converter;
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

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import dataStructure.Task;
import dataStructure.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
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
	
	private final static boolean ERROR_PRINT_ON = true;
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
	
	private static final double GRID_ROW_HEIGHT = 30.0;
	
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
			this.user = new User();
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
//		setPreview(this.executeNLP(command));
		this.executeNLP(command);
//		setDisplayGrid(this.display());
    }
	
	public void setDisplay(String displayedText) {
		content = new VBox();
		Label text = new Label(displayedText);
		content.getChildren().clear();
		content.getChildren().add(text);
		display.setContent(content);
		display.getStyleClass().add("mylistview");
		display.setHbarPolicy(ScrollBarPolicy.NEVER);
	}
	
	public void setDisplayGrid(ArrayList<Task> displayList) {
		content = new VBox();
		GridPane taskPane = new GridPane();
		content.getChildren().clear();
		int row = 0;
		int index = 1;
		taskPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.3 - 21));
		taskPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.7 - 21));
		if (displayList != null) {
			for (Task task : displayList) {
				String bannerColor = null;
				String bodyColor = null;
				switch (task.getPriority()) {
					case Constant.PRIORITY_HIGH:
						bannerColor = "rgba(240, 115, 136, 1)";
						bodyColor = "rgba(240, 115, 136, 0.1)";
						break;
					case Constant.PRIORITY_MEDIUM:
						bannerColor = "rgba(251, 235, 178, 1)";
						bodyColor = "rgba(251, 235, 178, 0.2)";
						break;
						
					case Constant.PRIORITY_LOW:
						bannerColor = "rgba(222, 236, 147, 1)";
						bodyColor = "rgba(222, 236, 147, 0.2)";
						break;
						
					default:
						
						break;
				}
				
				GridPane priorityPane = new GridPane();
				priorityPane.setStyle("-fx-background-color: " + bannerColor);
				priorityPane.setPrefWidth(getWidth());
				Label priority = new Label("");
				priority.setPrefWidth(getWidth());
				priorityPane.add(priority, 0, 0);
				taskPane.add(priorityPane, 0, row, 2, 1);
				setDisplayRow(taskPane, GRID_ROW_HEIGHT);
				row ++;
				
				
				GridPane contentPane = new GridPane();
				contentPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.3 - 21));
				contentPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.7 - 21));
				int subRow = 0;
				contentPane.setStyle("-fx-padding: 0 0 0 10; -fx-background-color: " + bodyColor);
				contentPane.setPrefWidth(getWidth());
				
				Label description = new Label(index + "." + task.getDescription());
				description.setStyle("-fx-font-size: 17");
				description.setPrefWidth(getWidth());
				
				contentPane.add(description, 0, subRow, 2, 1);
				setDisplayRow(contentPane, GRID_ROW_HEIGHT);
				subRow ++;
				if (task.isDeadline()) {
					Label deadlineText = new Label("Deadline:");
					Label deadline = new Label(Converter.convertDateToString(task.getInterval().getEndDate()));
					
					contentPane.add(deadlineText, 0, subRow);
					contentPane.add(deadline, 1, subRow);
					setDisplayRow(contentPane, GRID_ROW_HEIGHT);
					subRow ++;
				} else if (task.isFloating()) {
					
				} else if (task.isTimed()) {
					Label startText = new Label("Start time:");
					Label start = new Label(Converter.convertDateToString(task.getInterval().getStartDate()));
					
					contentPane.add(startText, 0, subRow);
					contentPane.add(start, 1, subRow);
					setDisplayRow(contentPane, GRID_ROW_HEIGHT);
					subRow ++;
					
					Label endText = new Label("End time:");
					Label end= new Label(Converter.convertDateToString(task.getInterval().getEndDate()));
					
					contentPane.add(endText, 0, subRow);
					contentPane.add(end, 1, subRow);
					setDisplayRow(contentPane, GRID_ROW_HEIGHT);
					subRow ++;
				}
				contentPane.add(new Label("Tags:"), 0, subRow);
				contentPane.add(new Label(task.tagToString()), 1, subRow);
				setDisplayRow(contentPane, GRID_ROW_HEIGHT);
				subRow ++;
				
				taskPane.add(contentPane, 0, row, 2, subRow);
				setDisplayRow(taskPane, GRID_ROW_HEIGHT * subRow);
				row ++;
				
				GridPane emptyPane = new GridPane();
				emptyPane.setStyle("-fx-background-color: rgb(244, 244, 244)");
				emptyPane.setPrefWidth(getWidth());
				taskPane.add(emptyPane, 0, row, 2, 1);
				setDisplayRow(taskPane, GRID_ROW_HEIGHT);
				row ++;
				index ++;
			}
		}
		
		content.getChildren().add(taskPane);
		display.setContent(content);
	}
	
	private void setDisplayRow(GridPane pane, double height) {
		pane.getRowConstraints().add(new RowConstraints(height));
	}
	
	public void setPreview(String displayedText) {
		previewContent = new VBox();
		Label text = new Label(displayedText);
		previewContent.getChildren().clear();
		previewContent.getChildren().add(text);
		preview.setStyle("-fx-padding:5 0 0 7; -fx-background-color: rgb(244, 244, 244)");
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
	
	/**
	 * the delegation methods to respond 
	 */
	@Override
	public void onHotKey(HotKey key) {
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
	
	
//User level commands

	public void executeNLP (String userInput) {
		
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
					setPreview(this.addNLP(userInput));
					break;
					
				case DELETE:
					setPreview(this.deleteNLP(userInput));
					break;
					
				case UPDATE:
					setPreview(this.updateNLP(userInput));
					break;
					
				case SEARCH:
					setDisplay(this.searchNLP(userInput));
					break;
				
				case DISPLAY:
					ArrayList<Task> displayList = this.display();
					if (displayList != null) {
						setDisplayGrid(this.display());
					}
					break;
					
				case UNDO:
					setPreview(this.undo());
					break;
					
				case REDO:
					setPreview(this.redo());
					break;
					
				case CLEAR:
					setPreview(this.clear());
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
					break;
					
				default:
					break;

			}
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

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
						int index = parser.nerParser.pickIndex(userInput);
						Task taskToDelete = this.user.retrieve(index - 1);
						return "Command: delete \n\n" + taskToDelete.toStringForDisplaying();
					} catch (CommandFailedException de) {
						return "Command: delete \n\n" + "No Task Specified"; 
					}
					
				case UPDATE:
					try {
						int index = parser.nerParser.pickIndex(userInput);
						Task taskToUpdate = this.user.getUpdatePreview(index - 1, parser.nerParser.getUpdatedTaskMap(userInput));
						return "Command: update \n\n" + taskToUpdate.toStringForDisplaying();
					} catch (CommandFailedException e) {
						e.printStackTrace();
						return "Command: update \n\n" + Constant.PROMPT_MESSAGE_UPDATE_TASK_FAILED;
					}
					
				case SEARCH:
					try {
						Constraint thisConstraint = parser.nerParser.getConstraint(userInput);
						return "Command: search \n\n" + thisConstraint.toString();
					} catch (CommandFailedException e) {
						return "Command: search \n\n" + "Not Understand Search Constraint";
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
					
				default:
					return "Command not recognized";
	
			}
			
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return "Failure in Parsing The Task";
		}
	}

	// add

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
			e.printStackTrace();
			return e.toString();
		}

	}

	// common methods for NLP and non-NLP

	private ArrayList<Task> display() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getTaskList();
//			String resultString = UtilityMethod.taskListToString(queryResult);
//			if (resultString.equalsIgnoreCase("")) {
//				return Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK;
//			} else {
//				return resultString;
//			}
			if (queryResult.isEmpty()) {
				setPreview(Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK);
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
		return "Trash emptyed";
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
	
	private void reloadNLPModel() {
		NERParser.updateModal();
		this.parser = new Parser();
	}
}
