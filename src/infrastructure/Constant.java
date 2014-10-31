package infrastructure;

import java.util.Date;
import java.util.logging.Logger;

public abstract class Constant {
	
	//logger
	public static Logger logger = Logger.getLogger("ListOfXiaoMing");

	/**
	 *training data path 
	 */
	//directory
	public static final String FILE_PATH_ROOT 		= "List-of-Xiao-Ming";
	public static final String FILE_PATH_NLP_ROOT 	= "List-of-Xiao-Ming/NLP-Data";
	public static final String FILE_PATH_NLP_SRC 	= "src/NLPTraining";
	
	//tsv files
	public static final String FILE_PATH_TIME_PICKER 		= "/time-picker-training-data.tsv";
	public static final String FILE_PATH_DESCRIPTION_PICKER = "/description-picker-training-data.tsv";
	public static final String FILE_PATH_TAG_PICKER 		= "/tag-picker-training-data.tsv";
	public static final String FILE_PATH_PRIORITY_PICKER 	= "/priority-picker-training-data.tsv";
	public static final String FILE_PATH_INDEX_PICKER 		= "/index-picker-training-data.tsv";
	public static final String FILE_PATH_COMMAND_PICKER 	= "/command-picker-training-data.tsv";
	
	public static final String FILE_PATH_TIME_PICKER_USER 			= FILE_PATH_NLP_ROOT + FILE_PATH_TIME_PICKER;
	public static final String FILE_PATH_DESCRIPTION_PICKER_USER 	= FILE_PATH_NLP_ROOT + FILE_PATH_DESCRIPTION_PICKER;
	public static final String FILE_PATH_TAG_PICKER_USER 			= FILE_PATH_NLP_ROOT + FILE_PATH_TAG_PICKER;
	public static final String FILE_PATH_PRIORITY_PICKER_USER 		= FILE_PATH_NLP_ROOT + FILE_PATH_PRIORITY_PICKER;
	public static final String FILE_PATH_INDEX_PICKER_USER 			= FILE_PATH_NLP_ROOT + FILE_PATH_INDEX_PICKER;
	public static final String FILE_PATH_COMMAND_PICKER_USER 		= FILE_PATH_NLP_ROOT + FILE_PATH_COMMAND_PICKER;
	
			
	public static final String FILE_PATH_TIME_PICKER_SOURCE 		= FILE_PATH_NLP_SRC + FILE_PATH_TIME_PICKER;
	public static final String FILE_PATH_DESCRIPTION_PICKER_SOURCE 	= FILE_PATH_NLP_SRC + FILE_PATH_DESCRIPTION_PICKER;
	public static final String FILE_PATH_TAG_PICKER_SOURCE 			= FILE_PATH_NLP_SRC + FILE_PATH_TAG_PICKER;
	public static final String FILE_PATH_PRIORITY_PICKER_SOURCE 	= FILE_PATH_NLP_SRC + FILE_PATH_PRIORITY_PICKER;
	public static final String FILE_PATH_INDEX_PICKER_SOURCE 		= FILE_PATH_NLP_SRC + FILE_PATH_INDEX_PICKER;
	public static final String FILE_PATH_COMMAND_PICKER_SOURCE		 = FILE_PATH_NLP_SRC + FILE_PATH_COMMAND_PICKER;
	
	//prop files
	public static final String FILE_PATH_TIME_PICKER_PROP 			= "/time_picker.prop";
	public static final String FILE_PATH_DESCRIPTION_PICKER_PROP 	= "/description_picker.prop";
	public static final String FILE_PATH_TAG_PICKER_PROP 			= "/tag_picker.prop";
	public static final String FILE_PATH_PRIORITY_PICKER_PROP		= "/priority_picker.prop";
	public static final String FILE_PATH_INDEX_PICKER_PROP			= "/index_picker.prop";
	public static final String FILE_PATH_COMMAND_PICKER_PROP 		= "/command_picker.prop";
	
	public static final String FILE_PATH_TIME_PICKER_PROP_USER 			= FILE_PATH_NLP_ROOT + FILE_PATH_TIME_PICKER_PROP;
	public static final String FILE_PATH_DESCRIPTION_PICKER_PROP_USER 	= FILE_PATH_NLP_ROOT + FILE_PATH_DESCRIPTION_PICKER_PROP;
	public static final String FILE_PATH_TAG_PICKER_PROP_USER 			= FILE_PATH_NLP_ROOT + FILE_PATH_TAG_PICKER_PROP;
	public static final String FILE_PATH_PRIORITY_PICKER_PROP_USER 		= FILE_PATH_NLP_ROOT + FILE_PATH_PRIORITY_PICKER_PROP;
	public static final String FILE_PATH_INDEX_PICKER_PROP_USER 		= FILE_PATH_NLP_ROOT + FILE_PATH_INDEX_PICKER_PROP;
	public static final String FILE_PATH_COMMAND_PICKER_PROP_USER 		= FILE_PATH_NLP_ROOT + FILE_PATH_COMMAND_PICKER_PROP;
	
	public static final String FILE_PATH_TIME_PICKER_PROP_SOURCE 		= FILE_PATH_NLP_SRC + FILE_PATH_TIME_PICKER_PROP;
	public static final String FILE_PATH_DESCRIPTION_PICKER_PROP_SOURCE = FILE_PATH_NLP_SRC + FILE_PATH_DESCRIPTION_PICKER_PROP;
	public static final String FILE_PATH_TAG_PICKER_PROP_SOURCE 		= FILE_PATH_NLP_SRC + FILE_PATH_TAG_PICKER_PROP;
	public static final String FILE_PATH_PRIORITY_PICKER_PROP_SOURCE	= FILE_PATH_NLP_SRC + FILE_PATH_PRIORITY_PICKER_PROP;
	public static final String FILE_PATH_INDEX_PICKER_PROP_SOURCE 		= FILE_PATH_NLP_SRC + FILE_PATH_INDEX_PICKER_PROP;
	public static final String FILE_PATH_COMMAND_PICKER_PROP_SOURCE 	= FILE_PATH_NLP_SRC + FILE_PATH_COMMAND_PICKER_PROP;
	
	//gz files
	public static final String FILE_PATH_TIME_PICKER_GZ 		= "/time-picker-ner-model.ser.gz";
	public static final String FILE_PATH_TAG_PICKER_GZ 			= "/tag-picker-ner-model.ser.gz";
	public static final String FILE_PATH_DESCRIPTION_PICKER_GZ 	= "/description-picker-ner-model.ser.gz";
	public static final String FILE_PATH_INDEX_PICKER_GZ 		= "/index-picker-ner-model.ser.gz";
	public static final String FILE_PATH_PRIORITY_PICKER_GZ 	= "/priority-picker-ner-model.ser.gz";
	public static final String FILE_PATH_COMMAND_PICKER_GZ 		= "/command-picker-ner-model.ser.gz";
	
	public static final String FILE_PATH_TIME_PICKER_GZ_USER 		= FILE_PATH_NLP_ROOT + "/time-picker-ner-model.ser.gz";
	public static final String FILE_PATH_TAG_PICKER_GZ_USER 		= FILE_PATH_NLP_ROOT + "/tag-picker-ner-model.ser.gz";
	public static final String FILE_PATH_DESCRIPTION_PICKER_GZ_USER = FILE_PATH_NLP_ROOT + "/description-picker-ner-model.ser.gz";
	public static final String FILE_PATH_INDEX_PICKER_GZ_USER 		= FILE_PATH_NLP_ROOT + "/index-picker-ner-model.ser.gz";
	public static final String FILE_PATH_PRIORITY_PICKER_GZ_USER 	= FILE_PATH_NLP_ROOT + "/priority-picker-ner-model.ser.gz";
	public static final String FILE_PATH_COMMAND_PICKER_GZ_USER 	= FILE_PATH_NLP_ROOT + "/command-picker-ner-model.ser.gz";
	
	public static final String FILE_PATH_TIME_PICKER_GZ_SOURCE 			= FILE_PATH_NLP_SRC + "/time-picker-ner-model.ser.gz";
	public static final String FILE_PATH_TAG_PICKER_GZ_SOURCE 			= FILE_PATH_NLP_SRC + "/tag-picker-ner-model.ser.gz";
	public static final String FILE_PATH_DESCRIPTION_PICKER_GZ_SOURCE 	= FILE_PATH_NLP_SRC + "/description-picker-ner-model.ser.gz";
	public static final String FILE_PATH_INDEX_PICKER_GZ_SOURCE 		= FILE_PATH_NLP_SRC + "/index-picker-ner-model.ser.gz";
	public static final String FILE_PATH_PRIORITY_PICKER_GZ_SOURCE 		= FILE_PATH_NLP_SRC + "/priority-picker-ner-model.ser.gz";
	public static final String FILE_PATH_COMMAND_PICKER_GZ_SOURCE 		= FILE_PATH_NLP_SRC + "/command-picker-ner-model.ser.gz";
	
	
	
	
	
	
	//XML tags
	public static final String XML_TAG_DEFAULT = "O";
	public static final String XML_TAG_TIME = "DATE";
	public static final String XML_TAG_DESCRIPTION = "DESCRIPTION";
	public static final String XML_TAG_INDEX = "INDEX";
	public static final String XML_TAG_TAG = "TAG";
	public static final String XML_TAG_PRIORITY = "PRIORITY";
	public static final String XML_TAG_COMMAND = "COMMAND";
	

	//GUI messages that will be shown to users
	public static final String GUI_MESSAGE_WELCOME = "Welcome to 小鸣的清单(List of Xiao Ming)";
	public static final String GUI_MESSAGE_SHORTCUT_INSTRUCTION = "ALT + A:\t insert <DATE> \t\tor </DATE>\n"
																+ "ALT + C:\t insert <COMMAND> \tor </COMMAND>\n"
																+ "ALT + D:\t insert <DESCRIPTION> \tor </DESCRIPTION>\n"
																+ "ALT + I:\t insert <INDEX> \t\tor </INDEX>\n"
																+ "ALT + P:\t insert <PRIORITY> \t\tor </PRIORITY>\n"
																+ "ALT + T:\t insert <TAG> \t\t\tor </TAG>\n\n"
																+ "CTRL + C:\t insert add\n"
																+ "CTRL + R:\t insert display\n"
																+ "CTRL + U:\t insert update\n"
																+ "CTRL + D:\t insert delete\n"
																+ "CTRL + F:\t insert search\n";
	public static final String GUI_PREVIEW_MESSAGE_DELETE = "Command: delete \n\n" + "No Task Specified";
	
	//prompt messages that will be printed for users	
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
	public static final String COMMAND_STRING_EMPTY_TRASH = "empty_trash";
	
	
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
		LOG_IN, LOG_OUT, CREATE_ACCOUNT, DELETE_ACCOUNT, HELP, EXIT, ADD, UPDATE, DELETE, DISPLAY, SEARCH, REDO, UNDO, CLEAR, NLP, EMPTY_TRASH
	}
	
	//category
	public static final String DEFAULT_CATEGORY = "default";
	
	//TimeInterval
	public static final Date FLOATING_START_DATE = new Date(0);
	public static final Date DEADLINE_START_DATE = new Date(1);
	public static final Date FLOATING_END_DATE = new Date(9999);
	
	//user interface mode
	public static enum UI_MODE {
		GUI, CLI
	}
}
