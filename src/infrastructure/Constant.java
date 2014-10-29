package infrastructure;

import java.util.Date;
import java.util.logging.Logger;

public abstract class Constant {
	
	//logger
	public static Logger logger = Logger.getLogger("ListOfXiaoMing");
	
	
	//prompt messages that will be printed for users
	public static final String XML_TAG_TIME = "DATE";
	public static final String XML_TAG_DESCRIPTION = "DESCRIPTION";
	
	public static final String PROMPT_MESSAGE_WELCOME = "\n\nWelcome to 小鸣的清单(List of Xiao Ming)";
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
	public static final String PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY = "Task added!";
	public static final String PROMPT_MESSAGE_ADD_TASK_FAILED = "Failed to add task!";
	public static final String PROMPT_MESSAGE_DELETE_TASK_SUCCESSFULLY = "Task deleted!";
	public static final String PROMPT_MESSAGE_DELETE_TASK_FAILED = "Failed to delete task!";
	public static final String PROMPT_MESSAGE_UPDATE_TASK_SUCCESSFULLY = "Task updated!";
	public static final String PROMPT_MESSAGE_UPDATE_TASK_FAILED = "Failed to update task!";
	public static final String PROMPT_MESSAGE_DISPLAY_EMPTY_TASK = "--- no task in the list ---      _(:з」∠)_ ";
	public static final String PROMPT_MESSAGE_DISPLAY_ERROR = "display error";
	public static final String PROMPT_MESSAGE_CLEAR_CACHE_FAILED = "Failed to clear cache!";
	public static final String PROMPT_MESSAGE_UNDO_SUCCESSFULLY = "Undo successfully!";
	public static final String PROMPT_MESSAGE_UNDO_FAILED = "Unable to undo!";
	public static final String PROMPT_MESSAGE_REDO_SUCCESSFULLY = "Redo successfully!";
	public static final String PROMPT_MESSAGE_REDO_FAILED = "Unable to redo!";
	public static final String PROMPT_MESSAGE_SEARCH_TIME_INTERVAL = "Searching for tasks within time Interval: %1$s";
	public static final String PROMPT_MESSAGE_SEARCH_KEYWORD = "Searching for tasks containing keywords: %1$s";
	public static final String PROMPT_MESSAGE_NO_TASK_FOUNDED = "--- no task found ---";
	
	
	public static final String RETURN_VALUE_LOG_IN_CANCELLED =  "user cancelled logging in";
	public static final String RETURN_VALUE_AUTHENTICATION_FAILED =  "authentication failed.";
	
	public static final String LOG_MESSAGE_READING_CACHE = "reading from cache: %1$s";
	public static final String LOG_MESSAGE_NO_CACHE_FOUND = "no cache found";
	public static final String LOG_MESSAGE_USER_CACHED = "user: %1$s -- has been cached";
	public static final String LOG_MESSAGE_USER_LOG_OUT = "user logs out";
	public static final String LOG_MESSAGE_INITIATE_LIST = "List initiated successfully";
	public static final String LOG_MESSAGE_USER_TASKS_DISPLAYED = "User tasks displayed";
	
	//parser
	public static final String KEY_TIME = "time";
	public static final String KEY_CATEGORY = "categroy";
	public static final String KEY_PRIORITY = "priority";
	public static final String KEY_REPEATED_PERIOD = "repeat";
	public static final String KEY_TAG = "tag";
	
	public static final String COMMAND_STRING_LOG_IN = "log in";
	public static final String COMMAND_STRING_LOG_IN_ALT = "login";
	public static final String COMMAND_STRING_LOG_OUT = "log out";
	public static final String COMMAND_STRING_LOG_OUT_ALT = "logout";
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
	public static final String COMMAND_STRING_NLP = "nlp";
	
	public static final int PRIORITY_DEFAULT = 2;
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
	
	public static final int CALENDAR_WEEK_IN_SECOND = 37;
	
	//DataStore
	public static final int ATTRIBUTE_END_POSITION = 1;
	public static final int NO_TAG_ENDINDEX = 0;
	public static final int NO_STARTDATE_ENDINDEX = 0;
	public static final int LAST_TAG_COMMAINDEX = -1;
	public final static String SPLIT_SECTION = "**********";
	
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
		LOG_IN, LOG_OUT, CREATE_ACCOUNT, DELETE_ACCOUNT, HELP, EXIT, ADD, UPDATE, DELETE, DISPLAY, SEARCH, REDO, UNDO, CLEAR, NLP
	}
	
	//category
	public static final String DEFAULT_CATEGORY = "default";
	
	//TimeInterval
	public static final Date FLOATING_START_DATE = new Date(0);
	public static final Date DEADLINE_START_DATE = new Date(0);
	public static final Date FLOATING_END_DATE = new Date(9999);
}
