//@author A0119379R-unused
//This class was used when we didn't add in the natural language processor.
//All functionalities covered by this class is cover by NerParser class right now.
//

package infrastructure;

//import infrastructure.Constant.COMMAND_TYPE;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//
//import model.CommandFailedException;
//import model.Pair;
//import model.Task;
//import model.TimeInterval;



public class Parser {
//	
//	private static final String KEY_NER_MAP_DATE = "DATE";
//	private static final String MESSAGE_INVALID_ARGUMENT_MULTIPLE_PRIORITY = "You can only assign one priority to a task";
//	private static final String MESSAGE_INVALID_ARGUMENT_TWISTED_INTERVAL = "start time should be earlier than end time";
//	private static final String MESSAGE_INVALID_ARGUMENT_TIME_FORMAT = "invalid time format: the correct format should be...";
//	private static final String MESSAGE_INVALID_ARGUMENT_MULTIPLE_TIME = "You can only assign one time for a task";
//	private static final String KEY_STRING_DESCRIPTION = "description";
//	private static final String MESSAGE_INVALID_ARGUMENT_REPEATED_PERIOD = "invalid argument for repeated period";
//	private static final String MESSAGE_INVALID_ARGUMENT_PRIORITY = "invalid argument for priority";
//	private static final String PRIORITY_STRING_LOW = "low";
//	private static final String PRIORITY_STRING_MEDIUM = "medium";
//	private static final String PRIORITY_STRING_HIGH = "high";
//	private static final String KEY_STRING_TAG = "tag";
//	private static final String KEY_STRING_REPEAT = "repeat";
//	private static final String KEY_STRING_PRIORITY = "priority";
//	private static final String KEY_STRING_TIME = "time";
//	private static final String KEY_STRING_CATEGORY = "category";
//	
//	public NerParser nerParser;
//	
//	/**
//	 * constructor: create a new NerParser to process the date
//	 */
//	public Parser() {
//		nerParser = new NerParser();
//	}
//	
//	/**
//	 * translate a string to a integer indicates the priority
//	 * 
//	 * @param parameter		a string represents the priority, valid formats include "high", "medium", "low", case insensitive
//	 * @return				an integer representation of the priority, returns PRIORITY_INVALID is system can't interpret the input
//	 */
//	public static int parsePriority(String parameter) {
//		if (parameter.equalsIgnoreCase(PRIORITY_STRING_HIGH)) {
//			return Constant.PRIORITY_HIGH;
//		} else if (parameter.equalsIgnoreCase(PRIORITY_STRING_MEDIUM)) {
//			return Constant.PRIORITY_MEDIUM;
//		} else if (parameter.equalsIgnoreCase(PRIORITY_STRING_LOW)) {
//			return Constant.PRIORITY_LOW;
//		} else {
//			return Constant.PRIORITY_INVALID;
//		}
//	}
//	
//	/**
//	 * determine the command type with the given string
//	 * 
//	 * @param commandTypeString		a string that might contain the user command
//	 * @return						an COMMAND_TYPE enumeration representing the corresponding command
//	 */
//	public static COMMAND_TYPE determineCommandType(String commandTypeString) {
//		switch (commandTypeString) {
//			case Constant.COMMAND_STRING_LOG_IN:
//				return COMMAND_TYPE.LOG_IN;
//			
//			case Constant.COMMAND_STRING_LOG_OUT:
//				return COMMAND_TYPE.LOG_OUT;
//				
//			case Constant.COMMAND_STRING_CREATE_ACCOUNT:
//				return COMMAND_TYPE.CREATE_ACCOUNT;
//				
//			case Constant.COMMAND_STRING_DELETE_ACCOUNT:
//				return COMMAND_TYPE.DELETE_ACCOUNT;
//				
//			case Constant.COMMAND_STRING_HELP:
//				return COMMAND_TYPE.HELP;
//				
//			case Constant.COMMAND_STRING_EXIT:
//				return COMMAND_TYPE.EXIT;
//				
//			case Constant.COMMAND_STRING_ADD:
//				return COMMAND_TYPE.ADD;
//				
//			case Constant.COMMAND_STRING_UPDATE:
//				return COMMAND_TYPE.UPDATE;
//				
//			case Constant.COMMAND_STRING_DELETE:
//				return COMMAND_TYPE.DELETE;
//				
//			case Constant.COMMAND_STRING_DISPLAY:
//				return COMMAND_TYPE.DISPLAY;
//
//			case Constant.COMMAND_STRING_SEARCH:
//				return COMMAND_TYPE.SEARCH;
//				
//			case Constant.COMMAND_STRING_UNDO:
//				return COMMAND_TYPE.UNDO;
//				
//			case Constant.COMMAND_STRING_REDO:
//				return COMMAND_TYPE.REDO;
//	
//			case Constant.COMMAND_STRING_CLEAR:
//				return COMMAND_TYPE.CLEAR;	
//	
//			case Constant.COMMAND_STRING_LOG_IN_ALT:
//				return COMMAND_TYPE.LOG_IN;
//				
//			case Constant.COMMAND_STRING_LOG_OUT_ALT:
//				return COMMAND_TYPE.LOG_OUT;
//				
//			case Constant.COMMAND_STRING_NLP:
//				return COMMAND_TYPE.NLP;
//				
//			case Constant.COMMAND_STRING_EMPTY_TRASH:
//				return COMMAND_TYPE.EMPTY_TRASH;
//				
//			case Constant.COMMAND_STRING_RELOAD:
//				return COMMAND_TYPE.RELOAD;
//				
//			case Constant.COMMAND_STRING_DONE:
//				return COMMAND_TYPE.DONE;
//				
//			case Constant.COMMAND_STRING_RECOVER:
//				return COMMAND_TYPE.RECOVER;
//				
//			default:
//				return COMMAND_TYPE.HELP;
//		}
//	}
//	
//	
//	/**
//	 * get a key-value map from the given parameter list
//	 * this method will be called when performing an update operation
//	 * 
//	 * @param parameterList		A list of String, in which each element represents a parameter of the task
//	 * @return					A map that maps the task property key and value
//	 */
//	public HashMap<String, Object> getTaskMap(ArrayList<String> parameterList) {
//		HashMap <String, Object> updateAttributes = new HashMap<String, Object> ();
//		
//		for (String parameter: parameterList) {
//			String key = UtilityMethod.getFirstWord(parameter);
//			String value = UtilityMethod.removeFirstWord(parameter);
//			switch (key) {
//			case KEY_STRING_CATEGORY:
//				updateAttributes.put(KEY_STRING_CATEGORY, value);
//				break;
//				
//			case KEY_STRING_TIME:
//				try {
//					TimeInterval t = parseTimeInterval(value);
//					updateAttributes.put(KEY_STRING_TIME, t);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//				
//			case KEY_STRING_PRIORITY:
//				try {
//					Integer p = Parser.parsePriority(value);
//					updateAttributes.put(KEY_STRING_PRIORITY, p);
//				} catch (Exception e) {
//					UtilityMethod.showToUser(MESSAGE_INVALID_ARGUMENT_PRIORITY);
//				}
//				
//				break;
//				
//			case KEY_STRING_REPEAT:
//				try {
//					Integer r = Integer.parseInt(value);
//					updateAttributes.put(KEY_STRING_REPEAT, r);
//				} catch (Exception e) {
//					UtilityMethod.showToUser(MESSAGE_INVALID_ARGUMENT_REPEATED_PERIOD);
//				}
//				break;
//				
//			case KEY_STRING_TAG:
//				ArrayList<String> tags = new ArrayList<String>();
//				tags.add(value);
//				updateAttributes.put(KEY_STRING_TAG, tags);
//				break;
//				
//			default:
//				updateAttributes.put(KEY_STRING_DESCRIPTION, parameter);
//				break;
//			}
//		}
//		
//		return updateAttributes;
//	}
//	
//	
//	/**
//	 * Get a Task object from the parameter list
//	 * This method will be called when performing an add operation
//	 * 
//	 * @param parameterList					A list of String, in which each element represents a parameter of the task
//	 * @return								A task object, constructed with the parameter list
//	 * @throws CommandFailedException		CommandFailedException thrown when one or more subsequent parsing goes wrong.
//	 */
//	public Task getTaskFromParameterList(ArrayList<String> parameterList) throws CommandFailedException {
//		TimeInterval timeInterval = new TimeInterval();
//		int priority = Constant.PRIORITY_DEFAULT;
//		ArrayList<String> tag = new ArrayList<String>();
//		String description = null;
//		
//		boolean hasTime = false;
//		boolean hasPriority = false;
//		
//		for (String parameter: parameterList) {
//			String key = UtilityMethod.getFirstWord(parameter);
//			String value = UtilityMethod.removeFirstWord(parameter);
//			switch(key) {
//				case Constant.KEY_TIME:
//					try {
//						if (hasTime) {
//							UtilityMethod.showToUser(MESSAGE_INVALID_ARGUMENT_MULTIPLE_TIME);
//						} else {
//							TimeInterval parsedTimeInterval = parseTimeInterval(value);
//							if (parsedTimeInterval == null) {
//								UtilityMethod.showToUser(MESSAGE_INVALID_ARGUMENT_TIME_FORMAT);
//							} else {
//								timeInterval = parsedTimeInterval;
//								hasTime = true;
//							}
//						}
//					} catch (Exception e) {
//						UtilityMethod.showToUser(MESSAGE_INVALID_ARGUMENT_TWISTED_INTERVAL);
//					}
//					break;
//		
//				case Constant.KEY_PRIORITY:
//					if (hasPriority) {
//						UtilityMethod.showToUser(MESSAGE_INVALID_ARGUMENT_MULTIPLE_PRIORITY);
//					} else {
//						int tempPriority = parsePriority(value);
//						if (tempPriority == Constant.PRIORITY_INVALID) {
//							UtilityMethod.showToUser(MESSAGE_INVALID_ARGUMENT_PRIORITY);
//						} else {
//							priority = tempPriority;
//							hasPriority = true;
//						}
//
//					}
//					break;
//		
//				case Constant.KEY_TAG:
//					if (!tag.contains(value)) {
//						tag.add(value);
//					}
//					break;
//					
//				default:
//					description = parameter;
//					break;
//			}
//		}
//		
//		
//		return new Task(description, /*category,*/ priority, /*repeatedPeriod,*/ tag, timeInterval);
//	}
//
//	/**
//	 * The method breaks an userInput String, returns a COMMAND_TYPE and a list of parameters
//	 * This method will be called immediately after the system received the user input
//	 * 
//	 * @param userInput		the String that user input
//	 * @return				a Pair object, of which the head is a COMMAND_TYPE, the tail is a list of task parameters 
//	 */
//	public static Pair<COMMAND_TYPE, ArrayList<String>> parseCommandPair(String userInput) {
//		ArrayList<String> parameterList = new ArrayList<String>(Arrays.asList(userInput.trim().split("@")));
//		COMMAND_TYPE thisCommand = determineCommandType(parameterList.get(0).trim());
//		parameterList.remove(0);
//		return new Pair<COMMAND_TYPE, ArrayList<String>>(thisCommand, parameterList);
//	}
//	
//	/**
//	 * Date parsing
//	 * This method parse a string to a TimeInterval object
//	 * 
//	 * @param parameter						the specific string that might contains the date/time of the task 
//	 * @return								a TimeInterval object representing the time of the task
//	 * @throws CommandFailedException 		CommandFailedExcpetion thrown if the subsequent NerParsing goes wrong.
//	 */
//	public TimeInterval parseTimeInterval(String parameter) throws CommandFailedException {
//		HashMap<String, ArrayList<String>> map = NerParser.parseToMap(nerParser.parseTimeToXML(parameter));
//		ArrayList<String> dateList = map.get(KEY_NER_MAP_DATE);
//		return nerParser.parseTimeInterval(dateList);
//	}
}