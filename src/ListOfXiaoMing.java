import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import includes.*;
import data.*;


enum COMMAND_TYPE {
	LOG_IN, LOG_OUT, CREATE_ACCOUNT, HELP, ADD, UPDATE, DELETE, SEARCH
}

class Pair {
	public Object head;
	public Object tail;
	public Pair(Object thisHead, Object thisTail) {
		this.head = thisHead;
		this.tail = thisTail;
	}
}

public class ListOfXiaoMing {
	
	private static Scanner scanner_ = new Scanner(System.in);
	
	//a property to store the current user
	private User thisUser;
	
	
	
	/**
	 * Constructor
	 */
	public ListOfXiaoMing(String recordFilePath) {
		thisUser = new User(recordFilePath);
	}
	
	
	public static void main(String[] args) {
		ListOfXiaoMing list = null;
		showToUser("Welcome to 小鸣的清单(List of Xiao Ming), you can log in or create a new accout.");
		while (list == null) {
			showToUser("You can type in 'log in', 'create account' or 'help'.");
			String userInput = ListOfXiaoMing.readCommand();
			String recordFilePath = ListOfXiaoMing.exectueUpperLevelCommand(userInput);
			if (recordFilePath != null) {
				//already find the record
				list = new ListOfXiaoMing(recordFilePath);
			}
		}
		
		boolean willContinue = true;
		while (willContinue) {
			String userInput = ListOfXiaoMing.readCommand();
			String result = list.execute(userInput);
			if (result.equals("log out")) {
				willContinue = false;
			}
		}
    }
	
	
	/**
	 * method to read in an user command
	 * @return
	 */
	public static String readCommand() {
		return scanner_.nextLine();
	}
	
	/**
	 * method to execute the system-level command like log in or log out
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String exectueUpperLevelCommand(String commandString) {
		Pair commandPair = ListOfXiaoMing.parse(commandString);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		Object parameter = commandPair.tail;
		
		switch(thisCommand) {
			case LOG_IN:
				return ListOfXiaoMing.userLogIn((ArrayList<String>)parameter);
			
			case CREATE_ACCOUNT:

				showToUser(ListOfXiaoMing.createAccount((ArrayList<String>)parameter));
				return null;
		
			case HELP:
				showToUser(ListOfXiaoMing.showHelp());
				return null;
			
			default:
				showToUser("you have not logged in yet");
				return null;
		}
	}
	
	
	/**
	 * method for users to log in
	 * @return
	 */
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
			showToUser("Please enter your user name");
			String inputUsername = readCommand();
			if (!DataStore.isAccountExisting(username)) {
				showToUser("The account doesn't exist! Do you want to enter the name again? (Y/N)");
				 if (readCommand().equalsIgnoreCase("N")) {
					return "user cancelled logging in";
				 }
			} else {
				username = inputUsername;
			}
		}
			
		while (password == null) {
			showToUser("Please enter your password");
			password = readCommand();
		}
		
		int incorrectPasswordCount = 0;
		while (!DataStore.authenticate(username, password)) {
			incorrectPasswordCount++;
			if (incorrectPasswordCount >= 3) {
				return "Authentication failed.";
			}
			showToUser("Incorrect password! Please enter again");
			password = readCommand();
		}
		
		return username;
	}
	
	
	/**
	 * method for users to log out
	 * @return
	 */
	public static String userLogOut() {
		return "";
	}
	
	
	/**
	 * method for users to create an account
	 * @return
	 */
	public static String createAccount(ArrayList<String> parameters) {
		String username = null;
		String passwordInput1 = null;
		String passwordInput2 = null;
		
		if (parameters.size() >= 1) {
			username = parameters.get(0);
		}
		
		while (username == null) {
			showToUser("Please enter your user name");
			String inputUsername = readCommand();
			if (DataStore.isAccountExisting(username)) {
				showToUser("The account has exsited already. Do you want to change a name? (Y/N)");
				 if (readCommand().equalsIgnoreCase("N")) {
					return "user cancelled creating an account";
				 }
			} else {
				username = inputUsername;
			}
		}
		
		while(!(passwordInput1 != null && passwordInput2 != null && passwordInput1.equals(passwordInput2))) {
			passwordInput1 = null;
			passwordInput2 = null;
			showToUser("Please enter your password:");
			passwordInput1 = readCommand();
			showToUser("Please enter again");
			passwordInput2 = readCommand();
		}
		
		DataStore.createAccount(username, passwordInput1);
		return "Account Created";
	}
	
	
	public static String showHelp(){
		
		return"";
	}
	
	
	/**
	 * method to print contents on users' screen
	 * @param contentsToBeShown
	 */
	public static void showToUser(String contentsToBeShown) {
		System.out.println(contentsToBeShown);
	}
	
	
	
	
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		
		return COMMAND_TYPE.HELP;
	}
	
	
	/**
	 * method to interpret users' input string
	 * @param userInput
	 * @return
	 */
	private static Pair parse(String userInput) {
		ArrayList<String> parameterList = new ArrayList<String>(Arrays.asList(userInput.trim().split("@")));
		COMMAND_TYPE thisCommand = ListOfXiaoMing.determineCommandType(parameterList.get(0));
		parameterList.remove(0);
		return new Pair(thisCommand, parameterList);
	}
	
	private static Task getTaskFromParameterList(ArrayList<String> parameterList) {
		
		
		return null;
	}
	
	
	/**
	 * method to execute a command
	 * @param command
	 * @return
	 */
	private String execute (String userInput) {
		Pair commandPair = ListOfXiaoMing.parse(userInput);
		COMMAND_TYPE thisCommand = (COMMAND_TYPE) commandPair.head;
		Object parameter = commandPair.tail;
		
		switch(thisCommand) {
			case ADD:
				return this.add(parameter);
			
			case DELETE:
				break;
				
			case UPDATE:
				break;
			
			case SEARCH:
				break;
			
			case LOG_OUT:
				break;
			
			default:
				break;
		}
		
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private String add(Object parameters) {
		Task taskToAdd = getTaskFromParameterList((ArrayList<String>) parameters);
		this.thisUser.add(taskToAdd);
		return "";
	}
}
