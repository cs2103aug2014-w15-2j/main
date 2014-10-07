package reference;

public class Constant {
	
	//prompt messages that will be printed for users
	public static final String PROMPT_MESSAGE_WELCOME = "Welcome to 小鸣的清单(List of Xiao Ming), you can log in or create a new accout.";
	public static final String PROMPT_MESSAGE_INSTRUCTION = "You can type in 'log in', 'create account', 'help' or 'exit'.";
	public static final String PROMPT_MESSAGE_NOT_LOG_IN = "you have not logged in yet";
	public static final String PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY = "Successfully logged out\n\n\n";
	public static final String PROMPT_MESSAGE_NEED_USERNAME = "Please enter your username: ";
	public static final String PROMPT_MESSAGE_NEED_PASSWORD = "Please enter your password: ";
	public static final String PROMPT_MESSAGE_NEED_ENTER_AGAIN = "Please re-enter your password: ";
	public static final String PROMPT_MESSAGE_PASSWORD_INCORRECT = "Incorrect password! Please try again.";
	public static final String PROMPT_MESSAGE_ACCOUNT_NOT_EXIST = "The account doesn't exist! Do you want to enter the name again? (Y/N)";
	public static final String PROMPT_MESSAGE_ACCOUNT_EXIST = "The account has existed already. Do you want to change a name? (Y/N)";
	public static final String PROMPT_MESSAGE_ACCOUNT_CREATED =  "Account Created";
	public static final String PROMPT_MESSAGE_ACCOUNT_NOT_CREATED =  "Fail to create account: Please check again";
	public static final String PROMPT_MESSAGE_SESSION_END =  "Session end!";
	
	public static final String RETURN_VALUE_LOG_IN_CANCELLED =  "user cancelled logging in";
	public static final String RETURN_VALUE_AUTHENTICATION_FAILED =  "authentication failed.";
	public static final String RETURN_VALUE_LOGGED_OUT =  "log out";

	
	//parser
	public static final String KEY_TIME = "time";
	public static final String KEY_CATEGORY = "categroy";
	public static final String KEY_PRIORITY = "priority";
	public static final String KEY_REPEATED_PERIOD = "repeat";
	public static final String KEY_TAG = "tag";
	
	public static final String COMMAND_STRING_LOG_IN = "log in";
	public static final String COMMAND_STRING_LOG_OUT = "log out";
	public static final String COMMAND_STRING_CREATE_ACCOUNT = "create account";
	public static final String COMMAND_STRING_DELETE_ACCOUNT = "delete account";
	public static final String COMMAND_STRING_HELP = "help";
	public static final String COMMAND_STRING_EXIT = "exit";
	public static final String COMMAND_STRING_ADD = "add";
	public static final String COMMAND_STRING_UPDATE = "update";
	public static final String COMMAND_STRING_DELETE = "delete";
	public static final String COMMAND_STRING_SEARCH = "search";
	public static final String COMMAND_STRING_REDO = "redo";
	public static final String COMMAND_STRING_UNDO = "undo";
	public static final String COMMAND_STRING_CLEAR = "clear";
	public static final String COMMAND_STRING_DISPLAY = "display";
	
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
	
	//DataStore
	public static final int ATTRIBUTE_END_POSITION = 1;
	public static final int NO_TAG_ENDINDEX = 0;
	public static final int NO_STARTDATE_ENDINDEX = 0;
	public static final int LAST_TAG_COMMAINDEX = -1;
	
	//Logic
	public static final String TRASHED_TAG = "trashed";
	public static final String NO_REDOABLE_ERROR_MESSAGE = "nothing available for redoing";
	public static final String NO_UNDOABLE_ERROR_MESSAGE = "nothing available for undoing";
	public static final String INVALID_INDEX_ERROR_MESSAGE = "invalid task index %1$d";
	public static final String INVALID_UPDATE_MESSAGE = "invalid task attributes";
	public static final int MAXIMUM_UNDO_TIMES = 10;
	public static final int MAXIMUM_REDO_TIMES = 10;
	
	public static final String DUPLICATED_TAG_ERROR_MESSAGE = "tag %1$s already exists.";
	
	//command type
	public static enum COMMAND_TYPE {
		LOG_IN, LOG_OUT, CREATE_ACCOUNT, DELETE_ACCOUNT, HELP, EXIT, ADD, UPDATE, DELETE, DISPLAY, SEARCH, REDO, UNDO, CLEAR
	}
	
	public static final String DEFAULT_CATEGORY = "default";
	
}
