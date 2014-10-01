import java.util.ArrayList;
import java.util.Scanner;

import includes.*;
import data.*;

import includes.Parser.COMMAND_TYPE;


public class ListOfXiaoMing {
	
	private static Scanner scanner_ = new Scanner(System.in);
	
	public static final String PROMPT_MESSAGE_WELCOME = "Welcome to 小鸣的清单(List of Xiao Ming), you can log in or create a new accout.";
	public static final String PROMPT_MESSAGE_INSTRUCTION = "You can type in 'log in', 'create account', 'help' or 'exit'.";
	public static final String PROMPT_MESSAGE_LOG_IN_CANCELLED = "user cancelled logging in";
	public static final String PROMPT_MESSAGE_NOT_LOG_IN = "you have not logged in yet";

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
			Toolbox.showToUser(PROMPT_MESSAGE_WELCOME);
			while (list == null) {
				Toolbox.showToUser(PROMPT_MESSAGE_INSTRUCTION);
				String userInput = ListOfXiaoMing.readCommand();
				String recordFilePath = ListOfXiaoMing.exectueUpperLevelCommand(userInput);
				if (recordFilePath != null  && !recordFilePath.equalsIgnoreCase(PROMPT_MESSAGE_LOG_IN_CANCELLED)) {
					//already find the record
					System.out.println(recordFilePath);
					list = new ListOfXiaoMing(recordFilePath);
				}
			}
			
			boolean willContinue = true;
			while (willContinue) {
				String userInput = ListOfXiaoMing.readCommand();
				String result = list.execute(userInput);
				if (result.equals("log out")) {
					willContinue = false;
					Toolbox.showToUser("Successfully logged out\n\n\n");
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
				Toolbox.showToUser(PROMPT_MESSAGE_NOT_LOG_IN);
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
			Toolbox.showToUser("Please enter your user name");
			String inputUsername = readCommand();
			if (!DataStore.isAccountExisting(inputUsername)) {
				Toolbox.showToUser("The account doesn't exist! Do you want to enter the name again? (Y/N)");
				 if (readCommand().equalsIgnoreCase("N")) {
					return "user cancelled logging in";
				 }
			} else {
				username = inputUsername;
			}
		}
			
		while (password == null) {
			Toolbox.showToUser("Please enter your password");
			password = readCommand();
		}
		
		int incorrectPasswordCount = 0;
		while (!DataStore.authenticate(username, password)) {
			incorrectPasswordCount++;
			if (incorrectPasswordCount >= 3) {
				return "Authentication failed.";
			}
			Toolbox.showToUser("Incorrect password! Please enter again");
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
			Toolbox.showToUser("Please enter your user name");
			String inputUsername = readCommand();
			if (DataStore.isAccountExisting(inputUsername)) {
				Toolbox.showToUser("The account has exsited already. Do you want to change a name? (Y/N)");
				 if (readCommand().equalsIgnoreCase("N")) {
					return PROMPT_MESSAGE_LOG_IN_CANCELLED;
				 }
			} else {
				username = inputUsername;
			}
		}
		
		while(!(passwordInput1 != null && passwordInput2 != null && passwordInput1.equals(passwordInput2))) {
			passwordInput1 = null;
			passwordInput2 = null;
			Toolbox.showToUser("Please enter your password:");
			passwordInput1 = readCommand();
			Toolbox.showToUser("Please enter again");
			passwordInput2 = readCommand();
		}
		
		boolean successCreated = DataStore.createAccount(username, passwordInput1);
		return successCreated ? "Account Created" : "Fail to create account: Please check again";
	}
	public static String showHelp(){
		
		return "'Help' has not been implemented yet";
	}
	
	public static void exit() {
		Toolbox.showToUser("Session end");
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
		return "log out";
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
			Toolbox.showToUser("start time should before end time");
			return null;
		}
		
	}
}
