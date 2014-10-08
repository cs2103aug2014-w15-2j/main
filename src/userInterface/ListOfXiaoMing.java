package userInterface;
import java.util.ArrayList;

import reference.*;
import reference.Constant.COMMAND_TYPE;
import dataStore.*;
import dataStructure.CommandFailedException;
import dataStructure.Constraint;
import dataStructure.Pair;
import dataStructure.Task;
import dataStructure.TimeInterval;
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
		while (true) {
			ListOfXiaoMing list = null;
			String cached = DataStore.getCachedAccount();
			if (!((cached == "") || (cached == null))) {
				System.out.println(cached);
				list = new ListOfXiaoMing(cached);
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
				}
			}
			
			
			assert(list != null);
			UtilityMethod.showToUser(list.execute("display"));
			
			boolean willContinue = true;
			while (willContinue) {
				String userInput = UtilityMethod.readCommand();
				String result = list.execute(userInput);
				if (result.equals(Constant.RETURN_VALUE_LOGGED_OUT)) {
					willContinue = false;
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
	@SuppressWarnings("unchecked")
	public static String executeUpperLevelCommand(String commandString) {
		Pair commandPair = Parser.parse(commandString);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		Object parameter = commandPair.tail;
		
		switch(thisCommand) {
			case LOG_IN:
				return User.userLogIn((ArrayList<String>)parameter);
			
			case CREATE_ACCOUNT:

				UtilityMethod.showToUser(User.createAccount((ArrayList<String>)parameter));
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
	
	@SuppressWarnings("unchecked")
	public String execute (String userInput) {
		Pair commandPair = Parser.parse(userInput);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		ArrayList<String> parameterList = (ArrayList<String>) commandPair.tail;
		
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
		Task taskToAdd = Parser.getTaskFromParameterList(taskParameters);
		
		assert(taskToAdd != null);
		
		
		
		return (this.user.add(taskToAdd)) ? "Task added!" : "Failed to add task";
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
				return "--- no task found ---      _(:з」∠)_ ";
			} else {
				return queryResultString;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}
}
