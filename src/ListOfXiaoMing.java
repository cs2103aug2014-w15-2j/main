import java.util.ArrayList;
import java.util.Scanner;

import includes.*;
import data.*;

import includes.Constant.COMMAND_TYPE;


public class ListOfXiaoMing {
	
	private static Scanner scanner_ = new Scanner(System.in);

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
			Toolbox.showToUser(Constant.PROMPT_MESSAGE_WELCOME);
			while (list == null) {
				Toolbox.showToUser(Constant.PROMPT_MESSAGE_INSTRUCTION);
				String userInput = ListOfXiaoMing.readCommand();
				String recordFilePath = ListOfXiaoMing.exectueUpperLevelCommand(userInput);
				if (recordFilePath != null  && !recordFilePath.equalsIgnoreCase(Constant.RETURN_VALUE_LOG_IN_CANCELLED)) {
					//already find the record
					System.out.println(recordFilePath);
					list = new ListOfXiaoMing(recordFilePath);
				}
			}
			
			boolean willContinue = true;
			while (willContinue) {
				String userInput = ListOfXiaoMing.readCommand();
				String result = list.execute(userInput);
				if (result.equals(Constant.RETURN_VALUE_LOGGED_OUT)) {
					willContinue = false;
					Toolbox.showToUser(Constant.PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY);
				}
			}
		}
    }
	public static String readCommand() {
		return scanner_.nextLine();
	}
	
	
//system level commands
	/**
	 * method to execute the system-level command like log in or log out
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String exectueUpperLevelCommand(String commandString) {
		Pair commandPair = Parser.parse(commandString);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		Object parameter = commandPair.tail;
		
		switch(thisCommand) {
			case LOG_IN:
				return ListOfXiaoMing.userLogIn((ArrayList<String>)parameter);
			
			case CREATE_ACCOUNT:

				Toolbox.showToUser(ListOfXiaoMing.createAccount((ArrayList<String>)parameter));
				return null;
		
			case HELP:
				Toolbox.showToUser(ListOfXiaoMing.showHelp());
				return null;
			
			case EXIT:
				ListOfXiaoMing.exit();
				
			default:
				Toolbox.showToUser(Constant.PROMPT_MESSAGE_NOT_LOG_IN);
				return null;
		}
	}
	
	public static String userLogIn(ArrayList<String> parameters) {
		String username = null;
		String password = null;
		
		if (parameters.size() >= 1) {
			username = parameters.get(0);
			if (parameters.size() >= 2) {
				password = parameters.get(1);
			}
		}
		
		while (username == null) {
			Toolbox.showToUser(Constant.PROMPT_MESSAGE_NEED_USERNAME);
			String inputUsername = readCommand();
			if (!DataStore.isAccountExisting(inputUsername)) {
				Toolbox.showToUser(Constant.PROMPT_MESSAGE_ACCOUNT_NOT_EXSIT);
				 if (!readCommand().equalsIgnoreCase("Y")) {
					return Constant.RETURN_VALUE_LOG_IN_CANCELLED;
				 }
			} else {
				username = inputUsername;
			}
		}
			
		while (password == null) {
			Toolbox.showToUser(Constant.PROMPT_MESSAGE_NEED_PASSWORD);
			password = readCommand();
		}
		
		int incorrectPasswordCount = 0;
		while (!DataStore.authenticate(username, password)) {
			incorrectPasswordCount++;
			if (incorrectPasswordCount >= 3) {
				return Constant.RETURN_VALUE_AUTHENTICATION_FAILED;
			}
			Toolbox.showToUser(Constant.PROMPT_MESSAGE_PASSWORD_INCORRECT);
			password = readCommand();
		}
		
		return username;
	}
	
	public static String createAccount(ArrayList<String> parameters) {
		String username = null;
		String passwordInput1 = null;
		String passwordInput2 = null;
		
		if (parameters.size() >= 1) {
			username = parameters.get(0);
		}
		
		while (username == null) {
			Toolbox.showToUser(Constant.PROMPT_MESSAGE_NEED_USERNAME);
			String inputUsername = readCommand();
			if (DataStore.isAccountExisting(inputUsername)) {
				Toolbox.showToUser(Constant.PROMPT_MESSAGE_ACCOUNT_EXSIT);
				 if (!readCommand().equalsIgnoreCase("Y")) {
					return Constant.RETURN_VALUE_LOG_IN_CANCELLED;
				 }
			} else {
				username = inputUsername;
			}
		}
		
		while(!(passwordInput1 != null && passwordInput2 != null && passwordInput1.equals(passwordInput2))) {
			passwordInput1 = null;
			passwordInput2 = null;
			Toolbox.showToUser(Constant.PROMPT_MESSAGE_NEED_PASSWORD);
			passwordInput1 = readCommand();
			Toolbox.showToUser(Constant.PROMPT_MESSAGE_NEED_ENTER_AGAIN);
			passwordInput2 = readCommand();
		}
		
		boolean successCreated = DataStore.createAccount(username, passwordInput1);
		return successCreated ?  Constant.PROMPT_MESSAGE_ACCOUNT_CREATED: Constant.PROMPT_MESSAGE_ACCOUNT_NOT_CREATED;
	}
	
	
	public static String showHelp(){
		
		return "'Help' has not been implemented yet";
	}
	
	public static void exit() {
		Toolbox.showToUser(Constant.PROMPT_MESSAGE_SESSION_END);
		System.exit(0);
	}
	
	
	
		
	
//User level commands
	
	@SuppressWarnings("unchecked")
	private String execute (String userInput) {
		Pair commandPair = Parser.parse(userInput);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		ArrayList<String> parameterList = (ArrayList<String>) commandPair.tail;
		
		switch(thisCommand) {
			case ADD:
				return this.add(parameterList);
			
			case DELETE:
				return this.delete(parameterList);
				
			case UPDATE:
				return "";
			
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
		this.user.add(taskToAdd);
		return "";
	}
	
	
	
	private String delete(ArrayList<String> taskParameters) {
		int index = Integer.parseInt(taskParameters.get(0));
		try {
			this.user.delete(index);
		} catch (CommandFailedException e) {
			Toolbox.showToUser(e.toString());
		}
		return "";
	}	
	
	
	
	private String logOut() {
		return Constant.RETURN_VALUE_LOGGED_OUT;
	}
	
	
	
	private String undo() {
		try {
			this.user.undo();
		} catch (CommandFailedException e) {
			Toolbox.showToUser(e.toString());
		}
		return "";
	}
	
	
	
	private String redo() {
		try {
			this.user.redo();
		} catch (CommandFailedException e) {
			Toolbox.showToUser(e.toString());
		}
		return "";
	}
	
	
	
	private String search(ArrayList<String> taskParameters) {
		
		try {
			TimeInterval timeInterval = null;
			for (String parameter : taskParameters) {
				timeInterval = Parser.parseTimeInterval(parameter);
				if (timeInterval != null) {
					taskParameters.remove(parameter);
					break;
				}
			}
		
			String keyword = taskParameters.get(0);	
			Constraint thisConstraint = new Constraint(keyword, timeInterval);
			ArrayList<Task> queryResult = this.user.find(thisConstraint);
			
			return Toolbox.taskListToString(queryResult);
		} catch(Exception e) {
			Toolbox.showToUser(e.toString());
			return null;
		}
		
	}
}
