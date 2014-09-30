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
	
	private static final String KEY_TIME = "time";
	private static final String KEY_CATEGORY = "categroy";
	private static final String KEY_PRIORITY = "priority";
	private static final String KEY_REPEATED_PERIOD = "repeat";
	private static final String KEY_TAG = "tag";
	
	private static final String COMMAND_STRING_LOG_IN = "log in";
	private static final String COMMAND_STRING_LOG_OUT = "log out";
	private static final String COMMAND_STRING_CREATE_ACCOUNT = "create account";
	private static final String COMMAND_STRING_HELP = "help";
	private static final String COMMAND_STRING_EXIT = "exit";
	private static final String COMMAND_STRING_ADD = "add";
	private static final String COMMAND_STRING_UPDATE = "update";
	private static final String COMMAND_STRING_DELETE = "delete";
	private static final String COMMAND_STRING_SEARCH = "search";
	private static final String COMMAND_STRING_REDO = "redo";
	private static final String COMMAND_STRING_UNDO = "undo";
	private static final String COMMAND_STRING_CLEAR = "clear";
	
	
	public static final int PRIORITY_DEFAULT = 1;
	public static final int PRIORITY_INVALID = 0;
	public static final int PRIORITY_HIGH = 1;
	public static final int PRIORITY_MEDIUM = 2;
	public static final int PRIORITY_LOW = 3;
	
	public static final int REPEATED_PERIOD_DEFAULT = 1;
	public static final int REPEATED_PERIOD_INVALID = 0;
	public static final int REPEATED_PERIOD_NONE = 1;
	public static final int REPEATED_PERIOD_DAILY = 2;
	public static final int REPEATED_PERIOD_WEEKLY = 3;
	public static final int REPEATED_PERIOD_MONTHLY = 4;
	
	public static final String PROMPT_MESSAGE_WELCOME = "Welcome to 小鸣的清单(List of Xiao Ming), you can log in or create a new accout.";
	public static final String PROMPT_MESSAGE_INSTRUCTION = "You can type in 'log in', 'create account', 'help' or 'exit'.";
	public static final String PROMPT_MESSAGE_LOG_IN_CANCELLED = "user cancelled logging in";
	public static final String PROMPT_MESSAGE_NOT_LOG_IN = "you have not logged in yet";
	
	
	private static enum COMMAND_TYPE {
		LOG_IN, LOG_OUT, CREATE_ACCOUNT, HELP, EXIT, ADD, UPDATE, DELETE, SEARCH, REDO, UNDO, CLEAR
	}
	
	
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
			showToUser(PROMPT_MESSAGE_WELCOME);
			while (list == null) {
				showToUser(PROMPT_MESSAGE_INSTRUCTION);
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
					showToUser("Successfully logged out\n\n\n");
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
			
			case EXIT:
				ListOfXiaoMing.exit();
				
			default:
				showToUser(PROMPT_MESSAGE_NOT_LOG_IN);
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
			showToUser("Please enter your user name");
			String inputUsername = readCommand();
			if (!DataStore.isAccountExisting(inputUsername)) {
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
			if (DataStore.isAccountExisting(inputUsername)) {
				showToUser("The account has exsited already. Do you want to change a name? (Y/N)");
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
			showToUser("Please enter your password:");
			passwordInput1 = readCommand();
			showToUser("Please enter again");
			passwordInput2 = readCommand();
		}
		
		boolean successCreated = DataStore.createAccount(username, passwordInput1);
		return successCreated ? "Account Created" : "Fail to create account: Please check again";
	}
	public static String showHelp(){
		
		return "'Help' has not been implemented yet";
	}
	
	public static void exit() {
		showToUser("Session end");
		System.exit(0);
	}
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		switch (commandTypeString) {
			case COMMAND_STRING_LOG_IN:
				return COMMAND_TYPE.LOG_IN;
			
			case COMMAND_STRING_LOG_OUT:
				return COMMAND_TYPE.LOG_OUT;
				
			case COMMAND_STRING_CREATE_ACCOUNT:
				return COMMAND_TYPE.CREATE_ACCOUNT;
				
			case COMMAND_STRING_HELP:
				return COMMAND_TYPE.HELP;
				
			case COMMAND_STRING_EXIT:
				return COMMAND_TYPE.EXIT;
				
			case COMMAND_STRING_ADD:
				return COMMAND_TYPE.ADD;
				
			case COMMAND_STRING_UPDATE:
				return COMMAND_TYPE.UPDATE;
				
			case COMMAND_STRING_DELETE:
				return COMMAND_TYPE.DELETE;

			case COMMAND_STRING_SEARCH:
				return COMMAND_TYPE.SEARCH;
				
			case COMMAND_STRING_UNDO:
				return COMMAND_TYPE.UNDO;
				
			case COMMAND_STRING_REDO:
				return COMMAND_TYPE.REDO;
	
			case COMMAND_STRING_CLEAR:
				return COMMAND_TYPE.CLEAR;	
		
			default:
				return COMMAND_TYPE.HELP;
		}
	}
	
	
		
	
//User level commands
	
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
				timeInterval = parseTimeInterval(parameter);
				if (timeInterval != null) {
					taskParameters.remove(parameter);
					break;
				}
			}
		
			String keyword = taskParameters.get(0);	
			Constraint thisConstraint = new Constraint(keyword, timeInterval);
			ArrayList<Task> queryResult = this.user.find(thisConstraint);
			
			return taskListToString(queryResult);
		} catch(Exception e) {
			showToUser("start time should before end time");
			return null;
		}
		
	}
	
	
//parse methods
	
	private static Task getTaskFromParameterList(ArrayList<String> parameterList) {
		TimeInterval timeInterval = null;
		String category = null; 
		int priority = PRIORITY_DEFAULT;
		int repeatedPeriod = REPEATED_PERIOD_DEFAULT; 
		ArrayList<String> tag = new ArrayList<String>();
		
		String description = parameterList.get(0);
		parameterList.remove(0);
		
		boolean hasTime = false;
		boolean hasCategory = false;
		boolean hasPriority = false;
		boolean hasRepeatedPeriod = false;
		
		for (String parameter: parameterList) {
			String key = getFirstWord(parameter);
			String value = removeFirstWord(parameter);
			switch(key) {
				case KEY_TIME:
					try {
						if (hasTime) {
							showToUser("You can only assign one time for a task");
						} else {
							timeInterval = parseTimeInterval(value);
							if (timeInterval == null) {
								showToUser("invalid time format: the correct format should be...");
							} else {
								hasTime = true;
							}
						}
					} catch (Exception e) {
						showToUser("start time should be earlier than end time");
					}
					break;
				
				case KEY_CATEGORY:
					break;
				
				case KEY_PRIORITY:
					if (hasPriority) {
						showToUser("You can only assign one priority to a task");
					} else {
						int tempPriority = parsePriority(value);
						if (tempPriority == PRIORITY_INVALID) {
							showToUser("invalid priority format: it should be 'priority none/high/medium/low'");
						} else {
							priority = tempPriority;
							hasPriority = true;
						}

					}
					break;
				
				case KEY_REPEATED_PERIOD:
					if (hasRepeatedPeriod) {
						showToUser("You can only assign one repeated period to a task");
					} else {
						int tempRepeatedPeriod = parseRepeatedPeriod(value);
						if (tempRepeatedPeriod == REPEATED_PERIOD_INVALID) {
							showToUser("invalid repeat format: it should be 'repeat daily/weekly/monthly'");
						} else {
							repeatedPeriod = tempRepeatedPeriod;
							hasRepeatedPeriod = true;
						}
					}
					break;
					
				case KEY_TAG:
					if (!tag.contains(value)) {
						tag.add(value);
					}
					break;
					
				default:
					showToUser("Unrecognized parameter!");
					break;
			}
		}
		
		
		return new Task(description, category, priority, repeatedPeriod, tag, timeInterval);
	}

	private static Pair parse(String userInput) {
		ArrayList<String> parameterList = new ArrayList<String>(Arrays.asList(userInput.trim().split("@")));
		COMMAND_TYPE thisCommand = ListOfXiaoMing.determineCommandType(parameterList.get(0));
		parameterList.remove(0);
		return new Pair(thisCommand, parameterList);
	}
	private static TimeInterval parseTimeInterval(String parameter) throws Exception {
		String[] wordList = parameter.trim().split(" ");
		Date startDate = null;
		Date endDate = null;
		if (wordList.length == 1) {
			if (parameter.equalsIgnoreCase("today")) {
				startDate = new Date();
				
				Calendar endCalendar = Calendar.getInstance();
				endCalendar.setTime(new Date());
				endCalendar.set(Calendar.HOUR, 23);
				endCalendar.set(Calendar.MINUTE, 59);
				startDate = endCalendar.getTime();
			} else if (parameter.equalsIgnoreCase("tommorrow")) {
				Calendar startCalendar = Calendar.getInstance();
				startCalendar.setTime(new Date());
				startCalendar.add(Calendar.DATE, 1);
				startCalendar.set(Calendar.HOUR, 0);
				startCalendar.set(Calendar.MINUTE, 0);
				startDate = startCalendar.getTime();
				
				Calendar endCalendar = Calendar.getInstance();
				endCalendar.setTime(new Date());
				endCalendar.add(Calendar.DATE, 1);
				endCalendar.set(Calendar.HOUR, 23);
				endCalendar.set(Calendar.MINUTE, 59);
				endDate = endCalendar.getTime();
			}
		} else if (wordList.length == 5) {
			if (wordList[0].equalsIgnoreCase("before")) {
				parameter.replaceFirst("before ", "");
				startDate = new Date();
				endDate = parseDateString(parameter);
			}
		} else if (wordList.length == 10) {
			if (wordList[0].equalsIgnoreCase("from") && wordList[5].equalsIgnoreCase("to")) {
				String startDateString = wordList[1] + " " + wordList[2] + " " + wordList[3] + " " + wordList[4];
				String endDateString = wordList[6] + " " + wordList[7] + " " + wordList[8] + " " + wordList[9];
				startDate = parseDateString(startDateString);
				endDate = parseDateString(endDateString);
			}
		}
		
		if (startDate == null || endDate == null) {
			return null;
		} else {
			return new TimeInterval(startDate, endDate);
		}

	}
	

	private static Date parseDateString (String dateString) {
		try {
			//e.g. "20:00 04 Jan 2014"
			Date date = new SimpleDateFormat("hh:mm dd MMMM yyyy", Locale.ENGLISH).parse(dateString);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	private static int parsePriority(String parameter) {
		if (parameter.equalsIgnoreCase("high")) {
			return PRIORITY_HIGH;
		} else if (parameter.equalsIgnoreCase("medium")) {
			return PRIORITY_MEDIUM;
		} else if (parameter.equalsIgnoreCase("low")) {
			return PRIORITY_LOW;
		} else {
			return PRIORITY_INVALID;
		}
	}

	private static int parseRepeatedPeriod(String parameter) {
		if (parameter.equalsIgnoreCase("none")) {
			return REPEATED_PERIOD_NONE;
		} else if (parameter.equalsIgnoreCase("daily")) {
			return REPEATED_PERIOD_DAILY;
		} else if (parameter.equalsIgnoreCase("weekly")) {
			return REPEATED_PERIOD_WEEKLY;
		} else if (parameter.equalsIgnoreCase("monthly")) {
			return REPEATED_PERIOD_MONTHLY;
		} else {
			return REPEATED_PERIOD_INVALID;
		}
	}
	
	
	
	
//basic tools methods

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
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
	public static void showToUser(String contentsToBeShown) {
		System.out.println(contentsToBeShown);
	}
}
