import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import includes.*;
import data.*;


enum COMMAND_TYPE {
	LOG_IN, LOG_OUT, CREATE_ACCOUNT, HELP, ADD, UPDATE, DELETE, SEARCH, REDO, UNDO
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
	private User user;
	
	
	
	/**
	 * Constructor
	 */
	public ListOfXiaoMing(String recordFilePath) {
		user = new User(recordFilePath);
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
	@SuppressWarnings("unchecked")
	private String execute (String userInput) {
		Pair commandPair = ListOfXiaoMing.parse(userInput);
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
		Task taskToAdd = getTaskFromParameterList(taskParameters);
		this.user.add(taskToAdd);
		return "";
	}
	
	private String delete(ArrayList<String> taskParameters) {
		int index = Integer.parseInt(taskParameters.get(0));
		try {
			this.user.delete(index);
		} catch (CommandFailedException e) {
			showToUser(e.toString());
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
			showToUser(e.toString());
		}
		return "";
	}
	
	private String redo() {
		try {
			this.user.redo();
		} catch (CommandFailedException e) {
			showToUser(e.toString());
		}
		return "";
	}
	
	private String search(ArrayList<String> taskParameters) {
		
		try {
			TimeInterval timeInterval = null;
			for (String parameter : taskParameters) {
				if (TimeInterval.isTimeInterval(parameter)) {
					timeInterval = new TimeInterval(parameter);
					taskParameters.remove(parameter);
				}
			}
		
			String keyword = taskParameters.get(0);
			Constraint thisConstraint = new Constraint(keyword, timeInterval);
			ArrayList<Task> queryResult = this.user.find(thisConstraint);
			return taskListToString(queryResult);
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	
	private static String taskListToString(ArrayList<Task> list) {
		String returnValue = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				returnValue = i+ ". " + list.get(i).toString();
			} else {
				returnValue = returnValue + "\n" + i + ". " + list.get(i).toString();
			}
		}
		
		return returnValue;
	}
	
	
	public static Date parseDateString (String dateString) {
		try {
			//e.g. "20:00 04 Jan 2014"
			Date date = new SimpleDateFormat("hh:mm dd MMMM yyyy", Locale.ENGLISH).parse(dateString);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static TimeInterval parseTimeInterval(String parameter) throws Exception {
		String[] wordList = parameter.trim().split(" ");
		if (wordList.length == 1) {
			if (parameter.equalsIgnoreCase("today")) {
				Calendar startCalendar = Calendar.getInstance();
				startCalendar.setTime(new Date());
				
				Calendar endCalendar = Calendar.getInstance();
				endCalendar.setTime(new Date());
				endCalendar.set(Calendar.HOUR, 23);
				endCalendar.set(Calendar.MINUTE, 59);
				
				return new TimeInterval(startCalendar.getTime(), endCalendar.getTime());
			} else if (parameter.equalsIgnoreCase("tommorrow")) {
				Calendar startCalendar = Calendar.getInstance();
				startCalendar.setTime(new Date());
				startCalendar.add(Calendar.DATE, 1);
				startCalendar.set(Calendar.HOUR, 0);
				startCalendar.set(Calendar.MINUTE, 0);
				
				Calendar endCalendar = Calendar.getInstance();
				endCalendar.setTime(new Date());
				endCalendar.add(Calendar.DATE, 1);
				endCalendar.set(Calendar.HOUR, 23);
				endCalendar.set(Calendar.MINUTE, 59);
				
				return new TimeInterval(startCalendar.getTime(), endCalendar.getTime());
			} else {
				return null;
			}
		} else if (wordList.length == 5) {
			if (wordList[0].equalsIgnoreCase("before")) {
				parameter.replaceFirst("before ", "");
				Date startDate = new Date();
				Date endDate = parseDateString(parameter);
				return new TimeInterval(startDate, endDate);
			} else {
				return null;
			}
		} else if (wordList.length == 10) {
			if (wordList[0].equalsIgnoreCase("from") && wordList[5].equalsIgnoreCase("to")) {
				String startDateString = wordList[1] + " " + wordList[2] + " " + wordList[3] + " " + wordList[4];
				String endDateString = wordList[6] + " " + wordList[7] + " " + wordList[8] + " " + wordList[9];
				Date startDate = parseDateString(startDateString);
				Date endDate = parseDateString(endDateString);
				return new TimeInterval(startDate, endDate);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	
}
