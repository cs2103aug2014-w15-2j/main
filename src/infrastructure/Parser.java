package infrastructure;

import infrastructure.Constant.COMMAND_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import reference.CommandFailedException;
import reference.Pair;
import reference.TimeInterval;
import dataStructure.Task;



public class Parser {
	
	public NERParser nerParser;
	
	public Parser() {
		nerParser = new NERParser();
	}
	
	public static COMMAND_TYPE determineCommandType(String commandTypeString) {
		switch (commandTypeString) {
			case Constant.COMMAND_STRING_LOG_IN:
				return COMMAND_TYPE.LOG_IN;
			
			case Constant.COMMAND_STRING_LOG_OUT:
				return COMMAND_TYPE.LOG_OUT;
				
			case Constant.COMMAND_STRING_CREATE_ACCOUNT:
				return COMMAND_TYPE.CREATE_ACCOUNT;
				
			case Constant.COMMAND_STRING_DELETE_ACCOUNT:
				return COMMAND_TYPE.DELETE_ACCOUNT;
				
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
				
			case Constant.COMMAND_STRING_DISPLAY:
				return COMMAND_TYPE.DISPLAY;

			case Constant.COMMAND_STRING_SEARCH:
				return COMMAND_TYPE.SEARCH;
				
			case Constant.COMMAND_STRING_UNDO:
				return COMMAND_TYPE.UNDO;
				
			case Constant.COMMAND_STRING_REDO:
				return COMMAND_TYPE.REDO;
	
			case Constant.COMMAND_STRING_CLEAR:
				return COMMAND_TYPE.CLEAR;	
	
			case Constant.COMMAND_STRING_LOG_IN_ALT:
				return COMMAND_TYPE.LOG_IN;
				
			case Constant.COMMAND_STRING_LOG_OUT_ALT:
				return COMMAND_TYPE.LOG_OUT;
				
			case Constant.COMMAND_STRING_NLP:
				return COMMAND_TYPE.NLP;
				
			default:
				return COMMAND_TYPE.HELP;
		}
	}
	
	
	public HashMap<String, Object> getTaskMap(ArrayList<String> parameterList) {
		HashMap <String, Object> updateAttributes = new HashMap<String, Object> ();
		
		for (String parameter: parameterList) {
			String key = UtilityMethod.getFirstWord(parameter);
			String value = UtilityMethod.removeFirstWord(parameter);
			switch (key) {
			case "category":
				updateAttributes.put("category", value);
				break;
				
			case "time":
				try {
					TimeInterval t = parseTimeInterval(value);
					updateAttributes.put("time_interval", t);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case "priority":
				try {
					Integer p = Parser.parsePriority(value);
					updateAttributes.put("priority", p);
				} catch (Exception e) {
					UtilityMethod.showToUser("invalid argument for priority");
				}
				
				break;
				
			case "repeat":
				try {
					Integer r = Integer.parseInt(value);
					updateAttributes.put("repeated_period", r);
				} catch (Exception e) {
					UtilityMethod.showToUser("invalid argument for repeated period");
				}
				break;
				
			case "tag":
				ArrayList<String> tags = new ArrayList<String>();
				tags.add(value);
				updateAttributes.put("tag", tags);
				break;
				
			default:
				updateAttributes.put("description", parameter);
				break;
			}
		}
		
		return updateAttributes;
	}
	
	public Task getTaskFromParameterList(ArrayList<String> parameterList) throws CommandFailedException {
		TimeInterval timeInterval = new TimeInterval();
		String category = null; 
		int priority = Constant.PRIORITY_DEFAULT;
		int repeatedPeriod = Constant.REPEATED_PERIOD_DEFAULT; 
		ArrayList<String> tag = new ArrayList<String>();
		String description = null;
//		String description = parameterList.get(0);
//		parameterList.remove(0);
		
		boolean hasTime = false;
		boolean hasCategory = false;
		boolean hasPriority = false;
		boolean hasRepeatedPeriod = false;
		
		for (String parameter: parameterList) {
			String key = UtilityMethod.getFirstWord(parameter);
			String value = UtilityMethod.removeFirstWord(parameter);
			switch(key) {
				case Constant.KEY_TIME:
					try {
						if (hasTime) {
							UtilityMethod.showToUser("You can only assign one time for a task");
						} else {
							TimeInterval parsedTimeInterval = parseTimeInterval(value);
							if (parsedTimeInterval == null) {
								UtilityMethod.showToUser("invalid time format: the correct format should be...");
							} else {
								timeInterval = parsedTimeInterval;
								hasTime = true;
							}
						}
					} catch (Exception e) {
						UtilityMethod.showToUser("start time should be earlier than end time");
					}
					break;
				
				case Constant.KEY_CATEGORY:
					if (hasCategory) {
						UtilityMethod.showToUser("You can only assign one category for a task");
					} else {
						category = value;
					}
					break;
				
				case Constant.KEY_PRIORITY:
					if (hasPriority) {
						UtilityMethod.showToUser("You can only assign one priority to a task");
					} else {
						int tempPriority = parsePriority(value);
						if (tempPriority == Constant.PRIORITY_INVALID) {
							UtilityMethod.showToUser("invalid priority format: it should be 'priority none/high/medium/low'");
						} else {
							priority = tempPriority;
							hasPriority = true;
						}

					}
					break;
				
				case Constant.KEY_REPEATED_PERIOD:
					if (hasRepeatedPeriod) {
						UtilityMethod.showToUser("You can only assign one repeated period to a task");
					} else {
						int tempRepeatedPeriod = parseRepeatedPeriod(value);
						if (tempRepeatedPeriod == Constant.REPEATED_PERIOD_INVALID) {
							throw new CommandFailedException("invalid repeat format: it should be 'repeat daily/weekly/monthly'");
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
					description = parameter;
					break;
			}
		}
		
		
		return new Task(description, category, priority, repeatedPeriod, tag, timeInterval);
	}

	
	public static Pair<COMMAND_TYPE, ArrayList<String>> parseCommandPair(String userInput) {
		ArrayList<String> parameterList = new ArrayList<String>(Arrays.asList(userInput.trim().split("@")));
		COMMAND_TYPE thisCommand = determineCommandType(parameterList.get(0).trim());
		parameterList.remove(0);
		return new Pair<COMMAND_TYPE, ArrayList<String>>(thisCommand, parameterList);
	}
	
	public TimeInterval parseTimeInterval(String parameter) throws Exception {
		HashMap<String, ArrayList<String>> map = NERParser.parseToMap(nerParser.pasreTimeToXML(parameter));
		ArrayList<String> dateList = map.get("DATE");
		return nerParser.parseTimeInterval(dateList);
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

	private static int parseRepeatedPeriod(String parameter) {
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
	
	/**
	 * parse String to task
	 * 
	 * @param taskDescription
	 * @return task
	 * @throws Exception
	 */
	public static Task parseTaskFromRecords(String taskDescription) throws Exception {
		Task task;
		String description;
		String category;
		int priority;
		String task_id;
		int repeated_period;
		ArrayList<String> tag;
		Date startDate;
		Date endDate;
		TimeInterval interval;

		int endIndex;

		endIndex = taskDescription.indexOf("`");
		task_id = taskDescription.substring(0, endIndex);
		taskDescription = taskDescription.substring(endIndex
				+ Constant.ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		description = taskDescription.substring(0, endIndex);
		taskDescription = taskDescription.substring(endIndex
				+ Constant.ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		category = taskDescription.substring(0, endIndex);
		taskDescription = taskDescription.substring(endIndex
				+ Constant.ATTRIBUTE_END_POSITION);

		tag = getTaskTags(taskDescription);
		endIndex = taskDescription.indexOf("`");
		taskDescription = taskDescription.substring(endIndex
				+ Constant.ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		repeated_period = Integer.parseInt(taskDescription.substring(0,
				endIndex));
		taskDescription = taskDescription.substring(endIndex
				+ Constant.ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		priority = Integer.parseInt(taskDescription.substring(0, endIndex
				));
		taskDescription = taskDescription.substring(endIndex
				+ Constant.ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		// no startDate
		if (endIndex == Constant.NO_STARTDATE_ENDINDEX) {
			startDate = null;
		} else {
			startDate = new Date(Long.parseLong(taskDescription.substring(0,
					endIndex)));
		}

		// no endDate
		if (endIndex == taskDescription.length() - Constant.ATTRIBUTE_END_POSITION) {
			endDate = null;
		} else {
			taskDescription = taskDescription.substring(endIndex
					+ Constant.ATTRIBUTE_END_POSITION);
			endDate = new Date(Long.parseLong(taskDescription));
		}
		
		interval = new TimeInterval(startDate, endDate);

		task = new Task(task_id, description, category, priority,
				repeated_period, tag, interval);
		return task;
	}

	/**
	 * get the tags of a task
	 * 
	 * @param taskDescription
	 * @return list of tags
	 */
	private static ArrayList<String> getTaskTags(String taskDescription) {
		ArrayList<String> tag = new ArrayList<String>();
		int endIndex;
		int commaIndex;
		endIndex = taskDescription.indexOf("`");
		commaIndex = taskDescription.indexOf(",");
		// no tag
		if (endIndex == Constant.NO_TAG_ENDINDEX) {
			return tag;
		}
		while (commaIndex != Constant.LAST_TAG_COMMAINDEX) {
			tag.add(taskDescription.substring(0, commaIndex));
			taskDescription = taskDescription.substring(commaIndex
					+ Constant.ATTRIBUTE_END_POSITION);
			endIndex = taskDescription.indexOf("`");
			commaIndex = taskDescription.indexOf(",");
		}
		// add last tag
		tag.add(taskDescription.substring(0, endIndex));
		return tag;
	}
}
