package userInterface;

import infrastructure.Constant;
import infrastructure.IO;
import infrastructure.Parser;
import infrastructure.UtilityMethod;
import infrastructure.Constant.COMMAND_TYPE;



//import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.logging.FileHandler;
import java.util.logging.Level;
//import java.util.logging.LogManager;
//import java.util.logging.SimpleFormatter;



import reference.*;
import views.ListOfXiaoMingViewsController;
import dataStore.*;
import dataStructure.Task;
import dataStructure.User;

public class ListOfXiaoMing {

	private final static boolean ERROR_PRINT_ON = false;
	private boolean isNlpOn = true;
	private Parser parser = new Parser();
	private static PrintStream err = System.err;
	// a property to store the current user
	private User user;
	public IO io = null;
	public ListOfXiaoMingViewsController GUIController = null;

	/**
	 * Constructor
	 */
	public ListOfXiaoMing(String recordFilePath, ListOfXiaoMingViewsController controller) {
		try {
			user = new User(recordFilePath);
			if (controller == null) {
				this.io = new IO(Constant.UI_MODE.CLI);
			} else {
				this.GUIController = controller;
				this.io = new IO(Constant.UI_MODE.GUI);
			}
		} catch (Exception e) {
			// impossible errors
			e.printStackTrace();
		}
	}

	// main
	public static void main(String[] args) {
		// LogManager.getLogManager().reset();

		if (!ERROR_PRINT_ON) {
			// now make all writes to the System.err stream silent
			System.setErr(new PrintStream(new OutputStream() {
				public void write(int b) {
				}
			}));
		}

		// try {
		// SimpleDateFormat format = new SimpleDateFormat("MMMMdd_HHmmss");
		// FileHandler handler = new FileHandler("main " +
		// format.format(Calendar.getInstance().getTime())+ ".log");
		// handler.setFormatter(new SimpleFormatter());
		// Constant.logger.addHandler(handler);
		// } catch (SecurityException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		while (true) {
			// CLI
			ListOfXiaoMing list = null;
			String cached = TestingCache.getCachedAccount();
			if (!((cached == "") || (cached == null))) {
				System.out.println(cached);
				list = new ListOfXiaoMing(cached, null);
				Constant.logger.log(Level.INFO, String.format(
						Constant.LOG_MESSAGE_READING_CACHE, cached));
			} else {
				Constant.logger.log(Level.INFO,
						Constant.LOG_MESSAGE_NO_CACHE_FOUND);
			}

			while (list == null) {
				IO.cliIO.showToUser(Constant.PROMPT_MESSAGE_WELCOME, list);
				IO.cliIO.showToUser(Constant.PROMPT_MESSAGE_INSTRUCTION, list);
				String userInput = IO.cliIO.readCommand(list);
				String recordFilePath = ListOfXiaoMing
						.executeUpperLevelCommand(userInput, null);
				if (recordFilePath != null
						&& !recordFilePath
								.equalsIgnoreCase(Constant.RETURN_VALUE_LOG_IN_CANCELLED)) {
					// already find the record
					System.out.println(recordFilePath);
					list = new ListOfXiaoMing(recordFilePath, null);
					TestingCache.cacheAccount(recordFilePath);
					Constant.logger.log(Level.INFO, String.format(
							Constant.LOG_MESSAGE_USER_CACHED, recordFilePath));
				} else {

				}
			}
			
			processUserInteraction(list);
		}
	}

	public static void processUserInteraction(ListOfXiaoMing list) {
		assert (list != null);
		IO io = list.io;
		Constant.logger.log(Level.INFO, Constant.LOG_MESSAGE_INITIATE_LIST);

		io.showToUser(list.execute("display"), list);
		io.showToUser("\n\n\n", list);
		Constant.logger.log(Level.INFO,
				Constant.LOG_MESSAGE_USER_TASKS_DISPLAYED);

		boolean willContinue = true;
		while (willContinue) {
			String userInput = io.readCommand(list);
			String result;
			if (list.isNlpOn) {
				result = list.executeNLP(userInput);
			} else {
				result = list.execute(userInput);
			}

			if (result.equals(Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY)) {
				willContinue = false;
				Constant.logger.log(Level.INFO,
						Constant.LOG_MESSAGE_USER_LOG_OUT);
				io.showToUser(Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY, list);
			} else {
				io.showToUser(result, list);
				io.showToUser("\n\n\n", list);
			}
		}
	}

	// system level commands
	/**
	 * method to execute the system-level command like log in or log out
	 * 
	 * @return
	 */
	public static String executeUpperLevelCommand(String commandString, ListOfXiaoMingViewsController controller) {
		Pair<COMMAND_TYPE, ArrayList<String>> commandPair = Parser
				.parseCommandPair(commandString);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		ArrayList<String> parameter = commandPair.tail;
		IO io = null;
		if (controller == null) {
			io = new IO(Constant.UI_MODE.CLI);
		} else {
			io = new IO(Constant.UI_MODE.GUI);
		}
		if (commandString.equals("") || commandString.equalsIgnoreCase("clear")) {
			for (int i = 0; i < 24; i++) {
				System.out.println();
			}
			return null;
		}

		switch (thisCommand) {
		case LOG_IN:
			return User.userLogIn(parameter, controller);

		case CREATE_ACCOUNT:
			return User.createAccount(parameter, controller);

		case DELETE_ACCOUNT:
			io.showToUser(User.deleteAccount(controller), controller);
			return null;

		case HELP:
			io.showToUser(User.showHelp(), controller);
			return null;

		case EXIT:
			System.setErr(err);
			User.exit(controller);

		default:
			io.showToUser(Constant.PROMPT_MESSAGE_NOT_LOG_IN, controller);
			return null;
		}
	}

	// User level commands

	public String executeNLP(String userInput) {
		COMMAND_TYPE thisCommand;
		try {
			if (userInput.equals("")) {
				return "";
			}

			thisCommand = this.parser.nerParser.pickCommand(userInput);
			System.err.println("CMD - executeNER: " + thisCommand);
			switch (thisCommand) {
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
				User.exit(this.GUIController);
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
			User.exit(this.GUIController);

		case NLP:
			return this.toggleNLP();

		default:
			return "";
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
			taskToAdd = parser.getTaskFromParameterList(taskParameters, this.GUIController);
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
			this.user.update(index - 1, parser.getTaskMap(taskParameters, this.GUIController));
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
					this.io.showToUser(String.format(
							Constant.PROMPT_MESSAGE_SEARCH_TIME_INTERVAL,
							timeInterval), this.GUIController);
				} else {
					keyword = parameter;
					this.io.showToUser(String.format(
							Constant.PROMPT_MESSAGE_SEARCH_KEYWORD, keyword), this.GUIController);
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
			this.io.showToUser(e.toString(), this.GUIController);
			return Constant.PROMPT_MESSAGE_UNDO_FAILED;
		}
	}

	private String redo() {
		try {
			this.user.redo();
			return Constant.PROMPT_MESSAGE_REDO_SUCCESSFULLY;
		} catch (CommandFailedException e) {
			this.io.showToUser(e.toString(), this.GUIController);
			return Constant.PROMPT_MESSAGE_REDO_FAILED;
		}
	}

	private String logOut() {

		if (TestingCache.clearCache()) {
			return Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY;
		} else {
			return Constant.PROMPT_MESSAGE_CLEAR_CACHE_FAILED;
		}
	}

}
