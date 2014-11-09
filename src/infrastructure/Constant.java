package infrastructure;

import java.util.Date;
import java.util.logging.Logger;

public abstract class Constant {

	//@author A0119379R
	// logger
	public static Logger logger = Logger.getLogger("ListOfXiaoMing");
	public final static boolean ERROR_PRINT_ON = true;

	/**
	 * training data path
	 */

	// directory
	public static final String FILE_PATH_ROOT = "List-of-Xiao-Ming";
	public static final String FILE_PATH_NLP_ROOT = "List-of-Xiao-Ming/NLP-Data";
	public static final String FILE_PATH_NLP_SRC = "/NLPTraining";

	// tsv files
	public static final String FILE_PATH_TIME_PICKER = "/time-picker-training-data.tsv";
	public static final String FILE_PATH_DESCRIPTION_PICKER = "/description-picker-training-data.tsv";
	public static final String FILE_PATH_TAG_PICKER = "/tag-picker-training-data.tsv";
	public static final String FILE_PATH_PRIORITY_PICKER = "/priority-picker-training-data.tsv";
	public static final String FILE_PATH_INDEX_PICKER = "/index-picker-training-data.tsv";
	public static final String FILE_PATH_COMMAND_PICKER = "/command-picker-training-data.tsv";
	public static final String FILE_PATH_COMMAND = "/command-training-data.tsv";
	public static final String FILE_PATH_INDEX = "/index-training-data.tsv";
	public static final String FILE_PATH_PRIORITY = "/priority-training-data.tsv";
	public static final String FILE_PATH_TAG = "/tag-training-data.tsv";
	public static final String FILE_PATH_TIME = "/time-training-data.tsv";

	public static final String FILE_PATH_TIME_PICKER_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TIME_PICKER;
	public static final String FILE_PATH_DESCRIPTION_PICKER_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_DESCRIPTION_PICKER;
	public static final String FILE_PATH_TAG_PICKER_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TAG_PICKER;
	public static final String FILE_PATH_PRIORITY_PICKER_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_PRIORITY_PICKER;
	public static final String FILE_PATH_INDEX_PICKER_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_INDEX_PICKER;
	public static final String FILE_PATH_COMMAND_PICKER_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_COMMAND_PICKER;
	public static final String FILE_PATH_COMMAND_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_COMMAND;
	public static final String FILE_PATH_INDEX_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_INDEX;
	public static final String FILE_PATH_PRIORITY_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_PRIORITY;
	public static final String FILE_PATH_TAG_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TAG;
	public static final String FILE_PATH_TIME_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TIME;

	public static final String[] TSVS_USER = { FILE_PATH_TIME_PICKER_USER,
			FILE_PATH_DESCRIPTION_PICKER_USER, FILE_PATH_TAG_PICKER_USER,
			FILE_PATH_PRIORITY_PICKER_USER, FILE_PATH_INDEX_PICKER_USER,
			FILE_PATH_COMMAND_PICKER_USER, FILE_PATH_COMMAND_USER,
			FILE_PATH_INDEX_USER, FILE_PATH_PRIORITY_USER, FILE_PATH_TAG_USER,
			FILE_PATH_TIME_USER };

	public static final String FILE_PATH_TIME_PICKER_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TIME_PICKER;
	public static final String FILE_PATH_DESCRIPTION_PICKER_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_DESCRIPTION_PICKER;
	public static final String FILE_PATH_TAG_PICKER_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TAG_PICKER;
	public static final String FILE_PATH_PRIORITY_PICKER_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_PRIORITY_PICKER;
	public static final String FILE_PATH_INDEX_PICKER_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_INDEX_PICKER;
	public static final String FILE_PATH_COMMAND_PICKER_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_COMMAND_PICKER;
	public static final String FILE_PATH_COMMAND_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_COMMAND;
	public static final String FILE_PATH_INDEX_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_INDEX;
	public static final String FILE_PATH_PRIORITY_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_PRIORITY;
	public static final String FILE_PATH_TAG_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TAG;
	public static final String FILE_PATH_TIME_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TIME;

	public static final String[] TSVS_SOURCE = { FILE_PATH_TIME_PICKER_SOURCE,
			FILE_PATH_DESCRIPTION_PICKER_SOURCE, FILE_PATH_TAG_PICKER_SOURCE,
			FILE_PATH_PRIORITY_PICKER_SOURCE, FILE_PATH_INDEX_PICKER_SOURCE,
			FILE_PATH_COMMAND_PICKER_SOURCE, FILE_PATH_COMMAND_SOURCE,
			FILE_PATH_INDEX_SOURCE, FILE_PATH_PRIORITY_SOURCE,
			FILE_PATH_TAG_SOURCE, FILE_PATH_TIME_SOURCE };

	// prop files
	public static final String FILE_PATH_TIME_PICKER_PROP = "/time_picker.prop";
	public static final String FILE_PATH_DESCRIPTION_PICKER_PROP = "/description_picker.prop";
	public static final String FILE_PATH_TAG_PICKER_PROP = "/tag_picker.prop";
	public static final String FILE_PATH_PRIORITY_PICKER_PROP = "/priority_picker.prop";
	public static final String FILE_PATH_INDEX_PICKER_PROP = "/index_picker.prop";
	public static final String FILE_PATH_COMMAND_PICKER_PROP = "/command_picker.prop";
	public static final String FILE_PATH_TIME_PROP = "/time.prop";
	public static final String FILE_PATH_TAG_PROP = "/tag.prop";
	public static final String FILE_PATH_PRIORITY_PROP = "/priority.prop";
	public static final String FILE_PATH_INDEX_PROP = "/index.prop";
	public static final String FILE_PATH_COMMAND_PROP = "/command.prop";

	public static final String FILE_PATH_TIME_PICKER_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TIME_PICKER_PROP;
	public static final String FILE_PATH_DESCRIPTION_PICKER_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_DESCRIPTION_PICKER_PROP;
	public static final String FILE_PATH_TAG_PICKER_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TAG_PICKER_PROP;
	public static final String FILE_PATH_PRIORITY_PICKER_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_PRIORITY_PICKER_PROP;
	public static final String FILE_PATH_INDEX_PICKER_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_INDEX_PICKER_PROP;
	public static final String FILE_PATH_COMMAND_PICKER_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_COMMAND_PICKER_PROP;
	public static final String FILE_PATH_TIME_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TIME_PROP;
	public static final String FILE_PATH_TAG_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_TAG_PROP;
	public static final String FILE_PATH_PRIORITY_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_PRIORITY_PROP;
	public static final String FILE_PATH_INDEX_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_INDEX_PROP;
	public static final String FILE_PATH_COMMAND_PROP_USER = FILE_PATH_NLP_ROOT
			+ FILE_PATH_COMMAND_PROP;

	public static final String[] PROPS_USER = {
			FILE_PATH_TIME_PICKER_PROP_USER,
			FILE_PATH_DESCRIPTION_PICKER_PROP_USER,
			FILE_PATH_TAG_PICKER_PROP_USER,
			FILE_PATH_PRIORITY_PICKER_PROP_USER,
			FILE_PATH_INDEX_PICKER_PROP_USER,
			FILE_PATH_COMMAND_PICKER_PROP_USER, FILE_PATH_TIME_PROP_USER,
			FILE_PATH_TAG_PROP_USER, FILE_PATH_PRIORITY_PROP_USER,
			FILE_PATH_INDEX_PROP_USER, FILE_PATH_COMMAND_PROP_USER };

	public static final String FILE_PATH_TIME_PICKER_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TIME_PICKER_PROP;
	public static final String FILE_PATH_DESCRIPTION_PICKER_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_DESCRIPTION_PICKER_PROP;
	public static final String FILE_PATH_TAG_PICKER_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TAG_PICKER_PROP;
	public static final String FILE_PATH_PRIORITY_PICKER_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_PRIORITY_PICKER_PROP;
	public static final String FILE_PATH_INDEX_PICKER_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_INDEX_PICKER_PROP;
	public static final String FILE_PATH_COMMAND_PICKER_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_COMMAND_PICKER_PROP;
	public static final String FILE_PATH_TIME_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TIME_PROP;
	public static final String FILE_PATH_TAG_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_TAG_PROP;
	public static final String FILE_PATH_PRIORITY_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_PRIORITY_PROP;
	public static final String FILE_PATH_INDEX_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_INDEX_PROP;
	public static final String FILE_PATH_COMMAND_PROP_SOURCE = FILE_PATH_NLP_SRC
			+ FILE_PATH_COMMAND_PROP;

	public static final String[] PROPS_SOURCE = {
			FILE_PATH_TIME_PICKER_PROP_SOURCE,
			FILE_PATH_DESCRIPTION_PICKER_PROP_SOURCE,
			FILE_PATH_TAG_PICKER_PROP_SOURCE,
			FILE_PATH_PRIORITY_PICKER_PROP_SOURCE,
			FILE_PATH_INDEX_PICKER_PROP_SOURCE,
			FILE_PATH_COMMAND_PICKER_PROP_SOURCE, FILE_PATH_TIME_PROP_SOURCE,
			FILE_PATH_TAG_PROP_SOURCE, FILE_PATH_PRIORITY_PROP_SOURCE,
			FILE_PATH_INDEX_PROP_SOURCE, FILE_PATH_COMMAND_PROP_SOURCE };

	// gz files
	public static final String FILE_PATH_TIME_PICKER_GZ = "/time-picker-ner-model.ser.gz";
	public static final String FILE_PATH_TAG_PICKER_GZ = "/tag-picker-ner-model.ser.gz";
	public static final String FILE_PATH_DESCRIPTION_PICKER_GZ = "/description-picker-ner-model.ser.gz";
	public static final String FILE_PATH_INDEX_PICKER_GZ = "/index-picker-ner-model.ser.gz";
	public static final String FILE_PATH_PRIORITY_PICKER_GZ = "/priority-picker-ner-model.ser.gz";
	public static final String FILE_PATH_COMMAND_PICKER_GZ = "/command-picker-ner-model.ser.gz";
	public static final String FILE_PATH_INDEX_GZ = "/index-ner-model.ser.gz";
	public static final String FILE_PATH_PRIORITY_GZ = "/priority-ner-model.ser.gz";
	public static final String FILE_PATH_TIME_GZ = "/time-ner-model.ser.gz";
	public static final String FILE_PATH_COMMAND_GZ = "/command-ner-model.ser.gz";
	public static final String FILE_PATH_TAG_GZ = "/tag-ner-model.ser.gz";

	public static final String[] GZS_USER = {
			FILE_PATH_NLP_ROOT + FILE_PATH_TIME_PICKER_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_DESCRIPTION_PICKER_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_TAG_PICKER_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_PRIORITY_PICKER_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_INDEX_PICKER_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_COMMAND_PICKER_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_TIME_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_TAG_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_PRIORITY_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_INDEX_GZ,
			FILE_PATH_NLP_ROOT + FILE_PATH_COMMAND_GZ

	};

	public static final String[] GZS_SOURCE = {
			FILE_PATH_NLP_SRC + FILE_PATH_TIME_PICKER_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_DESCRIPTION_PICKER_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_TAG_PICKER_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_PRIORITY_PICKER_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_INDEX_PICKER_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_COMMAND_PICKER_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_TIME_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_TAG_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_PRIORITY_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_INDEX_GZ,
			FILE_PATH_NLP_SRC + FILE_PATH_COMMAND_GZ

	};

	// XML tags
	public static final String XML_TAG_DEFAULT = "O";
	public static final String XML_TAG_TIME = "DATE";
	public static final String XML_TAG_DESCRIPTION = "DESCRIPTION";
	public static final String XML_TAG_INDEX = "INDEX";
	public static final String XML_TAG_TAG = "TAG";
	public static final String XML_TAG_PRIORITY = "PRIORITY";
	public static final String XML_TAG_COMMAND = "COMMAND";

	// GUI messages that will be shown to users
	public static final String GUI_MESSAGE_WELCOME = "Welcome to 小鸣的清单(List of Xiao Ming)";
	public static final String GUI_MESSAGE_SHORTCUT_INSTRUCTION = "ALT + A:\t <DATE> \t </DATE>\n"
			+ "ALT + C:\t <COMMAND> \t </COMMAND>\n"
			+ "ALT + D:\t <DESCRIPTION> \t </DESCRIPTION>\n"
			+ "ALT + I:\t <INDEX> \t </INDEX>\n"
			+ "ALT + P:\t <PRIORITY> \t </PRIORITY>\n"
			+ "ALT + T:\t <TAG> \t\t </TAG>\n\n"
			+ "CTRL + C:\t add\n"
			+ "CTRL + R:\t display\n"
			+ "CTRL + U:\t update\n"
			+ "CTRL + D:\t delete\n"
			+ "CTRL + F:\t search\n"
			+ "CTRL + M:\t reload model\n";
	public static final String GUI_PREVIEW_MESSAGE_DELETE = "Command: delete \n\n"
			+ "No Task Specified";

	// prompt messages that will be printed for users
	public static final String PROMPT_MESSAGE_WELCOME = "\n\nWelcome to 小鸣的清单(List of Xiao Ming)";
	public static final String PROMPT_MESSAGE_INSTRUCTION = "You can type in 'log in', 'create account', 'help' or 'exit'.";
	public static final String PROMPT_MESSAGE_NOT_LOG_IN = "you have not logged in yet";
	public static final String PROMPT_MESSAGE_LOG_OUT_SUCCESSFULLY = "Successfully logged out\n\n\n";
	public static final String PROMPT_MESSAGE_NEED_USERNAME = "Please enter your username: ";
	public static final String PROMPT_MESSAGE_NEED_PASSWORD = "Please enter your password: ";
	public static final String PROMPT_MESSAGE_NEED_ENTER_AGAIN = "Please re-enter your password: ";
	public static final String PROMPT_MESSAGE_PASSWORD_INCORRECT = "Incorrect password. Please try again";
	public static final String PROMPT_MESSAGE_ACCOUNT_NOT_EXIST = "The account doesn't exist! Do you want to enter the name again? (Y/N)";
	public static final String PROMPT_MESSAGE_ACCOUNT_EXIST = "The account has existed already. Do you want to change a name? (Y/N)";
	public static final String PROMPT_MESSAGE_ACCOUNT_CREATED = "Account Created";
	public static final String PROMPT_MESSAGE_ACCOUNT_NOT_CREATED = "Fail to create account: Please check again";
	public static final String PROMPT_MESSAGE_SESSION_END = "Session end";
	public static final String PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY = "Task added";
	public static final String PROMPT_MESSAGE_ADD_TASK_FAILED = "Failed to add task";
	public static final String PROMPT_MESSAGE_DELETE_TASK_SUCCESSFULLY = "Task deleted";
	public static final String PROMPT_MESSAGE_DELETE_TASK_FAILED = "Failed to delete task";
	public static final String PROMPT_MESSAGE_UPDATE_TASK_SUCCESSFULLY = "Task updated";
	public static final String PROMPT_MESSAGE_UPDATE_TASK_FAILED = "Failed to update task";
	public static final String PROMPT_MESSAGE_DISPLAY_EMPTY_TASK = "No task in the list";
	public static final String PROMPT_MESSAGE_DISPLAY_ERROR = "display error";
	public static final String PROMPT_MESSAGE_CLEAR_CACHE_FAILED = "Failed to clear cache";
	public static final String PROMPT_MESSAGE_UNDO_SUCCESSFULLY = "Undo successfully";
	public static final String PROMPT_MESSAGE_UNDO_FAILED = "Unable to undo";
	public static final String PROMPT_MESSAGE_REDO_SUCCESSFULLY = "Redo successfully";
	public static final String PROMPT_MESSAGE_REDO_FAILED = "Unable to redo";
	public static final String PROMPT_MESSAGE_SEARCH_TIME_INTERVAL = "Searching for tasks within time Interval: %1$s";
	public static final String PROMPT_MESSAGE_SEARCH_KEYWORD = "Searching for tasks containing keywords: %1$s";
	public static final String PROMPT_MESSAGE_NO_TASK_FOUNDED = "No task found";
	public static final String PROMPT_MESSAGE_DONE_TASK_SUCCESSFULLY = "Tasks are marked done";
	public static final String PROMPT_MESSAGE_DONE_TASK_FAILED = "Task is failed to finished";
	public static final String PROMPT_MESSAGE_TRASH_EMPTIED = "Trash emptied";
	public static final String PROMPT_MESSAGE_INVALID_TASK_LISE = "Invalid task list name";

	public static final String RETURN_VALUE_LOG_IN_CANCELLED = "user cancelled logging in";
	public static final String RETURN_VALUE_AUTHENTICATION_FAILED = "authentication failed.";

	public static final String LOG_MESSAGE_INITIATE_LIST = "List initiated successfully";
	public static final String LOG_MESSAGE_USER_TASKS_DISPLAYED = "User tasks displayed";

	// parser
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
	public static final String COMMAND_STRING_RELOAD = "reload";
	public static final String COMMAND_STRING_DONE = "done";
	public static final String COMMAND_STRING_RECOVER = "recover";

	public static final int PRIORITY_DEFAULT = 2;
	public static final int PRIORITY_INVALID = 0;
	public static final int PRIORITY_HIGH = 1;
	public static final int PRIORITY_MEDIUM = 2;
	public static final int PRIORITY_LOW = 3;
	
	public static final String PRIORITY_STRING_HIGH = "high";
	public static final String PRIORITY_STRING_MEDIUM = "medium";
	public static final String PRIORITY_STRING_LOW = "low";

	public static final int REPEATED_PERIOD_DEFAULT = 1;
	public static final int REPEATED_PERIOD_INVALID = 0;
	public static final int REPEATED_PERIOD_NONE = 1;
	public static final int REPEATED_PERIOD_DAILY = 2;
	public static final int REPEATED_PERIOD_WEEKLY = 3;
	public static final int REPEATED_PERIOD_MONTHLY = 4;

	public static final int CALENDAR_WEEK_IN_SECOND = 37;
	public static final int CALENDAR_MONTH_IN_SECOND = 39;
	public static final int CALENDAR_YEAR_IN_SECOND = 41;
	
	//DataStore
	public static final String FORMAT_DATE = "dd-MMMM-yyyy HH:mm";
	public static final String SAVE_DESCRIPTION = "description";
	public static final String SAVE_STATUS = "status";
	public static final String SAVE_TAGS = "tags";
	public static final String SAVE_PRIORITY = "priority";
	public static final String SAVE_TIME_INTERVAL = "time-interval";
	public static final String SAVE_STARTDATE = "startDate";
	public static final String SAVE_ENDDATE = "endDate";
	
	public static final String SAVE_FORMAT_NO_DATE = "-";
	
	// Logic
	public static final String TRASHED_TAG = "trashed";
	public static final String NO_REDOABLE_ERROR_MESSAGE = "nothing available for redoing";
	public static final String NO_UNDOABLE_ERROR_MESSAGE = "nothing available for undoing";
	public static final String INVALID_INDEX_ERROR_MESSAGE = "invalid task index %1$d";
	public static final String INVALID_UPDATE_MESSAGE = "invalid task attributes";
	public static final int MAXIMUM_UNDO_TIMES = 10;
	public static final int MAXIMUM_REDO_TIMES = 10;

	public static final String DUPLICATED_TAG_ERROR_MESSAGE = "tag %1$s already exists.";

	// command type
	public static enum COMMAND_TYPE {
		LOG_IN, LOG_OUT, CREATE_ACCOUNT, DELETE_ACCOUNT, HELP, EXIT, ADD, UPDATE, DELETE, DISPLAY, SEARCH, REDO, UNDO, CLEAR, NLP, EMPTY_TRASH, RELOAD, DONE, RECOVER
	}

	// task status
	public static final String TASK_STATUS_NORMAL = "normal";
	public static final String TASK_STATUS_DONE = "done";
	public static final String TASK_STATUS_TRASHED = "trashed";

	// category
	public static final String DEFAULT_CATEGORY = "default";

	// TimeInterval
	public static final Date FLOATING_START_DATE = new Date(0);
	public static final Date DEADLINE_START_DATE = new Date(1);
	public static final Date FLOATING_END_DATE = new Date(9999);

	public static String TIME_MESSAGE_FLOATING = "";

	/**
	 * MainViewController
	 */

	// CSS styles
	public static final String CSS_STYLE_TAG_LABEL = "-fx-font: 10px \"Akagi-SemiBold\";"
			+ "-fx-padding: 5 10 5 10;"
			+ "-fx-background-color: %s;"
			+ "-fx-background-radius: 5px;"
			+ "-fx-text-fill: white;"
			+ "-fx-margin: 0 5 0 5;"
			+ "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 )";
	public static final String CSS_STYLE_TAG_PANE = "-fx-padding: 5 0 0 0;";
	public static final String CSS_STYLE_PRIORITY_PANE = "-fx-padding: 8 8 0 8; "
			+ "-fx-background-color: %s";
	public static final String CSS_STYLE_CONTENT_PANE = "-fx-padding: 8 8 8 8;"
			+ "-fx-background-color: %s";
	public static final String CSS_STYLE_PRIVIEW_CONTENT_BOX = "-fx-font: 12px \"Akagi-SemiBold\";"
			+ "-fx-background-color: rgba(0, 200, 255, 1)";
	public static final String CSS_STYLE_PREVIEW_SCROLL_PANE = "-fx-padding:8 8 8 10;"
			+ "-fx-background-color: rgb(244, 244, 244);"
			+ "-fx-effect: innershadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 0);";
	public static final String CSS_STYLE_DEADLINE_LABEL = "-fx-font: 38px \"Ticking Timebomb BB\";"
			+ "-fx-padding:5 0 0 0;" + "-fx-text-fill: white;";
	public static final String CSS_STYLE_TIME_LABEL = "-fx-text-fill: white;"
			+ "-fx-font: 38px \"Ticking Timebomb BB\";"
			+ "-fx-padding: 5 0 0 5";
	public static final String CSS_STYLE_WEEKDAY_LABEL = "-fx-text-fill: white;"
			+ "-fx-font: 17px \"Akagi-SemiBold\";" + "-fx-padding: 2 0 0 0";
	public static final String CSS_STYLE_TO_LABEL = "-fx-font: 18px \"Akagi-SemiBold\";";
	public static final String CSS_STYLE_OVERALL_TIME_BOX = "-fx-padding: 0 0 0 0;"
			+ "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";
	public static final String CSS_STYLE_DESCRIPTION_LABEL = "-fx-font: 17px \"Akagi-SemiBold\";"
			+ "-fx-padding:0 0 5 0;";
	public static final String CSS_STYLE_TIME_BOX = "-fx-font: 12px \"Akagi-SemiBold\";"
			+ "-fx-background-color: rgba(0, 0, 0, 0.5);"
			+ "-fx-padding: 5 0 0 5;"
			+ "-fx-background-radius: 5px;"
			+ "-fx-text-fill: white;";
	public static final String CSS_STYLE_PRIORITY_LABEL = "-fx-font: 19px \"Akagi-SemiBold\";"
			+ "-fx-padding: 0 0 10 0;"
			+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"
			+ "-fx-text-fill: white;";
	public static final String CSS_STYLE_EMPTY_PANE = "-fx-background-color: rgba(0, 0, 0, 0);";
	public static final String CSS_STYLE_SHADOW = "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 0);";

	// CSS colors
	public static final String COLOR_BANNER_DEFAULT = "rgba(222, 222, 222, 1)";
	public static final String COLOR_BANNER_PRIORITY_LOW = "rgba(222, 236, 147, 1)";
	public static final String COLOR_BANNER_PRIORITY_MEDIUM = "rgba(250, 230, 155, 1)";
	public static final String COLOR_BANNER_PRIORITY_HIGH = "rgba(240, 115, 136, 1)";
	public static final String COLOR_BODY_DEFAULT = "rgba(0, 0, 0, 1)";
	public static final String COLOR_BODY_PRIORITY_LOW = "rgba(250, 255, 230, 1)";
	public static final String COLOR_BODY_PRIORITY_MEDIUM = "rgba(255, 248, 222, 1)";
	public static final String COLOR_BODY_PRIORITY_HIGH = "rgba(250, 223, 227, 1)";
	public static final String[] COLORS_TAG = { "rgba(74, 137, 220, 0.7)",
			"rgba(59, 175, 218, 0.7)", "rgba(55, 188, 155, 0.7)",
			"rgba(246, 187, 66, 0.7)", "rgba(140, 193, 82, 0.7)",
			"rgba(233, 87, 63, 0.7)", "rgba(218, 68, 84, 0.7)" };

	// fonts
	public static final String FONT_FILE_BASE = "/resource/Akagi-SB.ttf";
	public static final String FONT_FILE_BASE_BOLD = "/resource/Akagi-EB.ttf";
	public static final String FONT_FILE_TIME = "/resource/TickingTimebombBB.ttf";
	public static final String FONT_FILE_FEEDBACK = "/resource/monaco.ttf";

	// shortcuts
	public static final String HOT_KEY_ADD_DESCRIPTION_TAG = "alt D";
	public static final String HOT_KEY_ADD_DATE_TAG = "alt A";
	public static final String HOT_KEY_ADD_TAG_TAG = "alt T";
	public static final String HOT_KEY_ADD_COMMAND_TAG = "alt C";
	public static final String HOT_KEY_ADD_INDEX_TAG = "alt I";
	public static final String HOT_KEY_ADD_PRIORITY_TAG = "alt P";
	public static final String HOT_KEY_PREVIEW = "control ENTER";
	public static final String HOT_KEY_CREATE = "control C";
	public static final String HOT_KEY_READ = "control R";
	public static final String HOT_KEY_UPDATE = "control U";
	public static final String HOT_KEY_DELETE = "control D";
	public static final String HOT_KEY_SEARCH = "control F";
	public static final String HOT_KEY_RELOAD = "control M";

	public static final String HOT_KEY_TO_DO = "control 1";
	public static final String HOT_KEY_TRASHED = "control 3";
	public static final String HOT_KEY_DONE = "control 2";

	public static final String HOT_KEY_LAST_COMMAND = "UP";
	public static final String HOT_KEY_NEXT_COMMAND = "DOWN";

	public static final double GRID_ROW_HEIGHT = 30.0;

	public static final int MODIFIER_ALT = 520;

	public static final int MODIFIER_CTRL = 130;
	public static final String TASK_LIST_FINISHED = "finished";
	public static final String TASK_LIST_TRASHED = "trashed";
	public static final String TASK_LIST_TODO = "ongoing";
	public static final String TASK_LIST_SEARCH = "search";
	public static final String TASK_LIST_HELP = "help";
}
