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
import java.util.Collections;

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
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ListOfXiaoMingViewsController extends GridPane implements HotKeyListener{
	@FXML
	private TextField input;
	
	@FXML
	private GridPane dragNode;
	
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
	private static final String HOT_KEY_RELOAD					= "control M";
	
	private static final String[] CSS_COLORS = {"rgba(74, 137, 220, 0.7)", 
												"rgba(59, 175, 218, 0.7)", 
												"rgba(55, 188, 155, 0.7)",  
												"rgba(246, 187, 66, 0.7)", 
												"rgba(140, 193, 82, 0.7)",
												"rgba(233, 87, 63, 0.7)", 
												"rgba(218, 68, 84, 0.7)"};
	
	private static final String HOT_KEY_LAST_COMMAND			= "UP";
	private static final String HOT_KEY_NEXT_COMMAND			= "DOWN";
	
	private static final double GRID_ROW_HEIGHT = 30.0;
	
	private static final int MODIFIER_ALT = 520;
	private static final int MODIFIER_CTRL = 130;
	
	private String descriptionTag 	= "</DESCRIPTION>";
	private String dateTag 			= "</DATE>";
	private String tagTag 			= "</TAG>";
	private String commandTag		= "</COMMAND>";
	private String indexTag 		= "</INDEX>";
	private String priorityTag 		= "</PRIORITY>";
	
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int currentCommandIndex = 0;
	
	
	
	public ListOfXiaoMingViewsController(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListOfXiaoMingViews.fxml"));
		Font.loadFont(getClass().getResource("Akagi-SB.ttf").toExternalForm(), 10);
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
        
        UtilityMethod.makeDraggable(stage, dragNode);
        
        if (!ERROR_PRINT_ON) {
			// now make all writes to the System.err stream silent
			System.setErr(new PrintStream(new OutputStream() {
				public void write(int b) {
				}
			}));
		}
        
	}

	private void updatePage() {
		setPreview("\n\n\n\t\t\t  Welcome to List of Xiao Ming");
		this.setDisplay("HELP:" + "\n\n" + Constant.GUI_MESSAGE_SHORTCUT_INSTRUCTION);
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
			setDisplayGrid(this.display());			
		} else {
//			setPreview("\n\n\n\t\t  Welcome to List of Xiao Ming");
			this.commandHistory.add(command);
			this.currentCommandIndex = this.commandHistory.size();
			this.execute(command);
		}
    }
	
	public void setDisplay(String displayedText) {
		content = new VBox();
		Label text = new Label(displayedText);
		content.getChildren().clear();
		content.getChildren().add(text);
		display.setContent(content);
		display.getStyleClass().add("mylistview");
		display.setHbarPolicy(ScrollBarPolicy.NEVER);
		display.setFocusTraversable(true);
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
				priorityPane.setStyle("-fx-padding: 8 8 0 8; -fx-background-color: " + bannerColor);
				priorityPane.setPrefWidth(getWidth());
				Label priority = new Label("\t\t\t\t\t\t\t\t " + index);
				priority.setStyle("-fx-font: 19px \"Akagi\";");
				priority.setPrefWidth(getWidth());
				priorityPane.add(priority, 0, 0);
				taskPane.add(priorityPane, 0, row, 2, 1);
				setDisplayRow(taskPane, GRID_ROW_HEIGHT);
				row ++;
				
				
				GridPane contentPane = new GridPane();
				
				if (System.getProperty("os.name").equals("Mac OS X")) {
					contentPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.3 - 29));
					contentPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.7 - 29));
		        } else {
		        	contentPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.3 - 29));
					contentPane.getColumnConstraints().add(new ColumnConstraints(getWidth() * 0.7 - 29));
		        }

				int subRow = 0;
				contentPane.setStyle("-fx-padding: 0 8 0 8; -fx-background-color: " + bodyColor);
				contentPane.setPrefWidth(getWidth());
				
				Label description = new Label(task.getDescription());
				description.setStyle("-fx-font: 17px \"Akagi\";");
				description.setPrefWidth(getWidth());
				
				contentPane.add(description, 0, subRow, 2, 1);
				setDisplayRow(contentPane, GRID_ROW_HEIGHT);
				subRow ++;
				
				if (task.isDeadline()) {
					Label deadlineText = new Label("Deadline:");
					Label deadline = new Label(Converter.convertDateToString(task.getInterval().getEndDate()));
					deadlineText.setStyle("-fx-font: 12px \"Akagi\";");
					deadline.setStyle("-fx-font: 12px \"Akagi\";");
					contentPane.add(deadlineText, 0, subRow);
					contentPane.add(deadline, 1, subRow);
					setDisplayRow(contentPane, GRID_ROW_HEIGHT);
					subRow ++;
				} else if (task.isFloating()) {
					
				} else if (task.isTimed()) {
					Label startText = new Label("Start time:");
					Label start = new Label(Converter.convertDateToString(task.getInterval().getStartDate()));
					startText.setStyle("-fx-font: 12px \"Akagi\";");
					start.setStyle("-fx-font: 12px \"Akagi\";");
					
					contentPane.add(startText, 0, subRow);
					contentPane.add(start, 1, subRow);
					setDisplayRow(contentPane, GRID_ROW_HEIGHT);
					subRow ++;
					
					Label endText = new Label("End time:");
					Label end= new Label(Converter.convertDateToString(task.getInterval().getEndDate()));
					endText.setStyle("-fx-font: 12px \"Akagi\";");
					end.setStyle("-fx-font: 12px \"Akagi\";");
					
					contentPane.add(endText, 0, subRow);
					contentPane.add(end, 1, subRow);
					setDisplayRow(contentPane, GRID_ROW_HEIGHT);
					subRow ++;
				}
				
				GridPane tagPane = new GridPane();
				tagPane.setStyle("-fx-padding: 5 0 0 0;");
				tagPane.setHgap(10);
				ArrayList<String> tags = task.getTag();
				int columnIndex = 0;
				for (String tag : tags) {
					Label tagLabel = new Label(tag);
					
					int colorOffset = 0;
					for (char c : tag.toCharArray()) {
						colorOffset += c;
					}
					
					colorOffset = colorOffset%CSS_COLORS.length;
					tagLabel.setStyle("-fx-font: 10px \"Akagi\";"
							+ "-fx-padding: 5 10 5 10;"
							+ "-fx-background-color:" + CSS_COLORS[colorOffset]+ ";"
							+ "-fx-background-radius: 5px;"
							+ "-fx-text-fill: white;"
							+ "-fx-margin: 0 5 0 5;");
					tagPane.add(tagLabel, columnIndex, 0);
					columnIndex ++;
				}
				contentPane.add(tagPane, 0, subRow, 2, 1);
				setDisplayRow(contentPane, GRID_ROW_HEIGHT);
				subRow ++;
				
				taskPane.add(contentPane, 0, row, 1, subRow);
				setDisplayRow(taskPane, GRID_ROW_HEIGHT * subRow + 3);
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
		previewContent.setStyle("-fx-font: 12px \"Akagi\";");
		previewContent.getChildren().add(text);
		preview.setStyle("-fx-padding:5 0 0 7; -fx-background-color: rgb(244, 244, 244)");
		preview.setContent(previewContent);
	}

	
	
	/**
	 * initialize the short cut settings
	 */
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
	
	/**
	 * register the shortcuts to the system
	 * @param instance
	 */
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
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_RELOAD), instance);
		
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_LAST_COMMAND), instance);
		keyShortCuts.register(KeyStroke.getKeyStroke(HOT_KEY_NEXT_COMMAND), instance);
	}
	
	
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
	
	
	/**
	 * insert the given text to the given position in the textField
	 * @param cursorPosition
	 * @param text
	 */
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
	
	private void updateTextField(final String text) {
		final ListOfXiaoMingViewsController instance = this;
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
				
			case KeyEvent.VK_M + MODIFIER_CTRL:
				insertTextToTextField(cursorPosition, "reload model ");	
				break;
				
			case KeyEvent.VK_BACK_SPACE + MODIFIER_CTRL:
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
					setDisplayGrid(this.display());
					break;
					
				case DELETE:
					setPreview(this.delete(userInput));
					setDisplayGrid(this.display());
					break;
					
				case UPDATE:
					setPreview(this.update(userInput));
					setDisplayGrid(this.display());
					break;
					
				case SEARCH:
					ArrayList<Task> queryList = this.search(userInput);
					if (queryList != null) {
						setDisplayGrid(queryList);
					}
					break;
				
				case DISPLAY:
					ArrayList<Task> displayList = this.display();
					if (displayList != null) {
						setDisplayGrid(displayList);
					}
					break;
					
				case UNDO:
					setPreview(this.undo());
					setDisplayGrid(this.display());
					break;
					
				case REDO:
					setPreview(this.redo());
					setDisplayGrid(this.display());
					break;
					
				case CLEAR:
					setPreview(this.clear());
					setDisplayGrid(this.display());
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
					setDisplayGrid(this.display());
					break;
					
				case RELOAD:
					setPreview(this.reloadNLPModel());
					setDisplayGrid(this.display());
					break;
					
				case DONE:
					setPreview(this.done(userInput));
					setDisplayGrid(this.display());
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
//					offset ++;
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
