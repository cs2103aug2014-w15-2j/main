package includes;

public class Constant {
	
	//prompt messages that will be printed for users
	public static final String PROMPT_MESSAGE_WELCOME = "Welcome to 小鸣的清单(List of Xiao Ming), you can log in or create a new accout.";
	public static final String PROMPT_MESSAGE_INSTRUCTION = "You can type in 'log in', 'create account', 'help' or 'exit'.";
	public static final String PROMPT_MESSAGE_LOG_IN_CANCELLED = "user cancelled logging in";
	public static final String PROMPT_MESSAGE_NOT_LOG_IN = "you have not logged in yet";
	
	
	//parser
	public static final String KEY_TIME = "time";
	public static final String KEY_CATEGORY = "categroy";
	public static final String KEY_PRIORITY = "priority";
	public static final String KEY_REPEATED_PERIOD = "repeat";
	public static final String KEY_TAG = "tag";
	
	public static final String COMMAND_STRING_LOG_IN = "log in";
	public static final String COMMAND_STRING_LOG_OUT = "log out";
	public static final String COMMAND_STRING_CREATE_ACCOUNT = "create account";
	public static final String COMMAND_STRING_HELP = "help";
	public static final String COMMAND_STRING_EXIT = "exit";
	public static final String COMMAND_STRING_ADD = "add";
	public static final String COMMAND_STRING_UPDATE = "update";
	public static final String COMMAND_STRING_DELETE = "delete";
	public static final String COMMAND_STRING_SEARCH = "search";
	public static final String COMMAND_STRING_REDO = "redo";
	public static final String COMMAND_STRING_UNDO = "undo";
	public static final String COMMAND_STRING_CLEAR = "clear";
	
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
	
	
	//command type
	public static enum COMMAND_TYPE {
		LOG_IN, LOG_OUT, CREATE_ACCOUNT, HELP, EXIT, ADD, UPDATE, DELETE, SEARCH, REDO, UNDO, CLEAR
	}
	
}
