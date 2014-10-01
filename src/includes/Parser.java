package includes;

import includes.Constant.COMMAND_TYPE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class Parser {
	
	public static COMMAND_TYPE determineCommandType(String commandTypeString) {
		switch (commandTypeString) {
			case Constant.COMMAND_STRING_LOG_IN:
				return COMMAND_TYPE.LOG_IN;
			
			case Constant.COMMAND_STRING_LOG_OUT:
				return COMMAND_TYPE.LOG_OUT;
				
			case Constant.COMMAND_STRING_CREATE_ACCOUNT:
				return COMMAND_TYPE.CREATE_ACCOUNT;
				
			case Constant.COMMAND_STRING_HELP:
				return COMMAND_TYPE.HELP;
				
			case Constant.COMMAND_STRING_EXIT:
				return COMMAND_TYPE.EXIT;
				
			case Constant.COMMAND_STRING_ADD:
				return COMMAND_TYPE.ADD;
				
			case Constant.COMMAND_STRING_UPDATE:
				return COMMAND_TYPE.UPDATE;
				
			case Constant.COMMAND_STRING_DELETE:
				return COMMAND_TYPE.DELETE;

			case Constant.COMMAND_STRING_SEARCH:
				return COMMAND_TYPE.SEARCH;
				
			case Constant.COMMAND_STRING_UNDO:
				return COMMAND_TYPE.UNDO;
				
			case Constant.COMMAND_STRING_REDO:
				return COMMAND_TYPE.REDO;
	
			case Constant.COMMAND_STRING_CLEAR:
				return COMMAND_TYPE.CLEAR;	
		
			default:
				return COMMAND_TYPE.HELP;
		}
	}
	
	public static Task getTaskFromParameterList(ArrayList<String> parameterList) {
		TimeInterval timeInterval = null;
		String category = null; 
		int priority = Constant.PRIORITY_DEFAULT;
		int repeatedPeriod = Constant.REPEATED_PERIOD_DEFAULT; 
		ArrayList<String> tag = new ArrayList<String>();
		
		String description = parameterList.get(0);
		parameterList.remove(0);
		
		boolean hasTime = false;
		boolean hasCategory = false;
		boolean hasPriority = false;
		boolean hasRepeatedPeriod = false;
		
		for (String parameter: parameterList) {
			String key = Toolbox.getFirstWord(parameter);
			String value = Toolbox.removeFirstWord(parameter);
			switch(key) {
				case Constant.KEY_TIME:
					try {
						if (hasTime) {
							Toolbox.showToUser("You can only assign one time for a task");
						} else {
							timeInterval = parseTimeInterval(value);
							if (timeInterval == null) {
								Toolbox.showToUser("invalid time format: the correct format should be...");
							} else {
								hasTime = true;
							}
						}
					} catch (Exception e) {
						Toolbox.showToUser("start time should be earlier than end time");
					}
					break;
				
				case Constant.KEY_CATEGORY:
					
					//
					//
					//
					//
					
					break;
				
				case Constant.KEY_PRIORITY:
					if (hasPriority) {
						Toolbox.showToUser("You can only assign one priority to a task");
					} else {
						int tempPriority = parsePriority(value);
						if (tempPriority == Constant.PRIORITY_INVALID) {
							Toolbox.showToUser("invalid priority format: it should be 'priority none/high/medium/low'");
						} else {
							priority = tempPriority;
							hasPriority = true;
						}

					}
					break;
				
				case Constant.KEY_REPEATED_PERIOD:
					if (hasRepeatedPeriod) {
						Toolbox.showToUser("You can only assign one repeated period to a task");
					} else {
						int tempRepeatedPeriod = parseRepeatedPeriod(value);
						if (tempRepeatedPeriod == Constant.REPEATED_PERIOD_INVALID) {
							Toolbox.showToUser("invalid repeat format: it should be 'repeat daily/weekly/monthly'");
						} else {
							repeatedPeriod = tempRepeatedPeriod;
							hasRepeatedPeriod = true;
						}
					}
					break;
					
				case Constant.KEY_TAG:
					if (!tag.contains(value)) {
						tag.add(value);
					}
					break;
					
				default:
					Toolbox.showToUser("Unrecognized parameter!");
					break;
			}
		}
		
		
		return new Task(description, category, priority, repeatedPeriod, tag, timeInterval);
	}

	
	public static Pair parse(String userInput) {
		ArrayList<String> parameterList = new ArrayList<String>(Arrays.asList(userInput.trim().split("@")));
		COMMAND_TYPE thisCommand = determineCommandType(parameterList.get(0).trim());
		parameterList.remove(0);
		return new Pair(thisCommand, parameterList);
	}
	
	public static TimeInterval parseTimeInterval(String parameter) throws Exception {
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
	
	public static Date parseDateString (String dateString) {
		try {
			//e.g. "20:00 04 Jan 2014"
			Date date = new SimpleDateFormat("hh:mm dd MMMM yyyy", Locale.ENGLISH).parse(dateString);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static int parsePriority(String parameter) {
		if (parameter.equalsIgnoreCase("high")) {
			return Constant.PRIORITY_HIGH;
		} else if (parameter.equalsIgnoreCase("medium")) {
			return Constant.PRIORITY_MEDIUM;
		} else if (parameter.equalsIgnoreCase("low")) {
			return Constant.PRIORITY_LOW;
		} else {
			return Constant.PRIORITY_INVALID;
		}
	}

	public static int parseRepeatedPeriod(String parameter) {
		if (parameter.equalsIgnoreCase("none")) {
			return Constant.REPEATED_PERIOD_NONE;
		} else if (parameter.equalsIgnoreCase("daily")) {
			return Constant.REPEATED_PERIOD_DAILY;
		} else if (parameter.equalsIgnoreCase("weekly")) {
			return Constant.REPEATED_PERIOD_WEEKLY;
		} else if (parameter.equalsIgnoreCase("monthly")) {
			return Constant.REPEATED_PERIOD_MONTHLY;
		} else {
			return Constant.REPEATED_PERIOD_INVALID;
		}
	}
}
