package userInterface;
import infrastructure.Constant;
//import infrastructure.NERParser;
import infrastructure.Parser;
import infrastructure.UtilityMethod;
import infrastructure.Constant.COMMAND_TYPE;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import reference.*;
import dataStore.*;
import dataStructure.Task;
import dataStructure.User;


public class ListOfXiaoMing {
	

	//a property to store the current user
	private User user;
	
	
	/**
	 * Constructor
	 */
	public ListOfXiaoMing(String recordFilePath) {
		try {
			user = new User(recordFilePath);
		} catch (Exception e) {
			// impossible errors
			e.printStackTrace();
		}
	}
	
	//main
	public static void main(String[] args) {
		LogManager.getLogManager().reset();

//		try {
//			NERParser.parseTask("Add attend the group meeting this Saturday, 10am to 1pm, tag with important, urgent");
//		} catch (CommandFailedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("MMMMdd_HHmmss");
			FileHandler handler = new FileHandler("main " + format.format(Calendar.getInstance().getTime())+ ".log");
			handler.setFormatter(new SimpleFormatter());
			Constant.logger.addHandler(handler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (true) {
			ListOfXiaoMing list = null;
			String cached = DataStore.getCachedAccount();
			if (!((cached == "") || (cached == null))) {
				System.out.println(cached);
				list = new ListOfXiaoMing(cached);
				Constant.logger.log(Level.INFO, "reading from cache: " + cached);
			} else {
				Constant.logger.log(Level.INFO, "no cache exists");
			}
			
			while (list == null) {
				UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_WELCOME);
				UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_INSTRUCTION);
				String userInput = UtilityMethod.readCommand();
				String recordFilePath = ListOfXiaoMing.executeUpperLevelCommand(userInput);
				if (recordFilePath != null  && !recordFilePath.equalsIgnoreCase(Constant.RETURN_VALUE_LOG_IN_CANCELLED)) {
					//already find the record
					System.out.println(recordFilePath);
					list = new ListOfXiaoMing(recordFilePath);
					DataStore.cacheAccount(recordFilePath);
					Constant.logger.log(Level.INFO, "user: " + recordFilePath + " -- has been cached");
				} else {
					
				}
			}
			
			assert(list != null);
			Constant.logger.log(Level.INFO, "List initiated successfully");
			
			UtilityMethod.showToUser(list.execute("display"));
			Constant.logger.log(Level.INFO, "User tasks displayed");
			
			
			boolean willContinue = true;
			while (willContinue) {
				String userInput = UtilityMethod.readCommand();
				String result = list.execute(userInput);
				if (result.equals(Constant.RETURN_VALUE_LOGGED_OUT)) {
					willContinue = false;
					Constant.logger.log(Level.INFO, "user log out");
					UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY);
				} else {
					UtilityMethod.showToUser(result);
				}
			}
		}
    }
	

	
	
//system level commands
	/**
	 * method to execute the system-level command like log in or log out
	 * @return
	 */
	public static String executeUpperLevelCommand(String commandString) {
		Pair<COMMAND_TYPE, ArrayList<String>> commandPair = Parser.parseCommandPair(commandString);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		ArrayList<String> parameter = commandPair.tail;
		
		switch(thisCommand) {
			case LOG_IN:
				return User.userLogIn(parameter);
			
			case CREATE_ACCOUNT:

				UtilityMethod.showToUser(User.createAccount(parameter));
				return null;
		
			case DELETE_ACCOUNT:	
				UtilityMethod.showToUser(User.deleteAccount());
				return null;
				
			case HELP:
				UtilityMethod.showToUser(User.showHelp());
				return null;
			
			case EXIT:
				User.exit();
				
			default:
				UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_NOT_LOG_IN);
				return null;
		}
	}
	

	
	
	
		
	
//User level commands
	
	public String execute (String userInput) {
		Pair<COMMAND_TYPE, ArrayList<String>> commandPair = Parser.parseCommandPair(userInput);
		COMMAND_TYPE thisCommand = commandPair.head;
		ArrayList<String> parameterList = commandPair.tail;
		
		switch(thisCommand) {
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
			
			default:
				return "";
		}
	}

	private String add(ArrayList<String> taskParameters) {
		Task taskToAdd;
		try {
			taskToAdd = Parser.getTaskFromParameterList(taskParameters);
			assert(taskToAdd != null);
			return (this.user.add(taskToAdd)) ? "Task added!" : "Failed to add task";
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}
	
	
	
	private String delete(ArrayList<String> taskParameters) {
		int index = Integer.parseInt(taskParameters.get(0));
		try {
			return (this.user.delete(index - 1)) ? "task deleted" : "deletion failed";
		} catch (CommandFailedException e) {
			return e.toString();
		}
	}	
	
	
	private String update(ArrayList<String> taskParameters) {
		int index = Integer.parseInt(taskParameters.get(0).trim());
		try {
			taskParameters.remove(0);
			this.user.update(index - 1, Parser.getTaskMap(taskParameters));
		} catch (CommandFailedException e) {
			e.printStackTrace();
			return "update failed";
		}
		
		return "task updated";
	}
	
	
	private String display() {
		ArrayList<Task> queryResult;
		try {
			queryResult = this.user.getTaskList();
			String resultString  = UtilityMethod.taskListToString(queryResult);
			if (resultString.equalsIgnoreCase("")) {
				return "--- no task in the list ---      _(:з」∠)_ ";
			} else {
				return resultString;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "display error";
		}
	}
	
	private String logOut() {
		
		if (DataStore.clearCache()) {
			return Constant.RETURN_VALUE_LOGGED_OUT;
		} else {
			return "error";
		}
	}
	
	
	
	private String undo() {
		try {
			this.user.undo();
		} catch (CommandFailedException e) {
			UtilityMethod.showToUser(e.toString());
		}
		return "undone";
	}
	
	
	
	private String redo() {
		try {
			this.user.redo();
		} catch (CommandFailedException e) {
			UtilityMethod.showToUser(e.toString());
		}
		return "redone";
	}
	
	
	
	private String search(ArrayList<String> taskParameters) {
		
		try {	
			TimeInterval timeInterval = new TimeInterval();
			String keyword = "";
			for (String parameter : taskParameters) {
				String key = UtilityMethod.getFirstWord(parameter);
				String value = UtilityMethod.removeFirstWord(parameter);
				if (key.equalsIgnoreCase("time")) {
					timeInterval = Parser.parseTimeInterval(value);
					UtilityMethod.showToUser("searching for tasks within time Interval: " + timeInterval);
				} else {
					keyword = parameter;
					UtilityMethod.showToUser("searching for tasks containing keywords: " + keyword);
				}
			}
			
			Constraint thisConstraint = new Constraint(keyword, timeInterval);
			ArrayList<Task> queryResult = this.user.find(thisConstraint);
			String queryResultString =  UtilityMethod.taskListToString(queryResult);
			if (queryResultString.equals("")) {
				return "--- no task found ---";
			} else {
				return queryResultString;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}
}
