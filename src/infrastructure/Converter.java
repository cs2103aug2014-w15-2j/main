//@author A0113029U

package infrastructure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.LinkedHashMap;

import model.CommandFailedException;
import model.Task;
import model.TimeInterval;

public class Converter {

	/**
	 * convert a Task to LinkedHashMap needed for JSON format
	 * 
	 * @param task - a task
	 * @return LinkedHashMap of a task with the properties
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedHashMap convertTaskToMap(Task task) {
		assert(task != null);

		LinkedHashMap taskMap = new LinkedHashMap();
		
		if(task == null) {
			return taskMap;
		}

		taskMap.put(Constant.SAVE_DESCRIPTION, task.getDescription());
		taskMap.put(Constant.SAVE_STATUS, task.getStatus());

		ArrayList<String> tags = new ArrayList<String>();
		for (int i = 0; i < task.getTag().size(); i++) {
			tags.add(task.getTag().get(i));
		}
		taskMap.put(Constant.SAVE_TAGS, tags);

		taskMap.put(Constant.SAVE_PRIORITY,
				convertPriorityIntToString(task.getPriority()));
		taskMap.put(Constant.SAVE_TIME_INTERVAL,
				convertTimeIntervalToString(task.getInterval()));

		return taskMap;
	}

	/**
	 * convert map to task specify all the information into corresponding
	 * properties
	 * 
	 * @param task - Map contains all the properties of a specified task
	 * @return Task object with the specified properties
	 * @throws Exception - failed in parsing time intervals
	 */
	@SuppressWarnings("rawtypes")
	public static Task convertMapToTask(LinkedHashMap task) throws Exception {
		assert(task != null);
		if(task == null) {
			return null;
		}
		
		String description = (String) task.get(Constant.SAVE_DESCRIPTION);
		String status = (String) task.get(Constant.SAVE_STATUS);
		int priority = convertPriorityStringToInt((String) task
				.get(Constant.SAVE_PRIORITY));

		ArrayList tags = (ArrayList) task.get(Constant.SAVE_TAGS);
		ArrayList<String> tag = new ArrayList<String>();
		for (int i = 0; i < tags.size(); i++) {
			tag.add((String) tags.get(i));
		}

		LinkedHashMap intervalObj = (LinkedHashMap) task
				.get(Constant.SAVE_TIME_INTERVAL);
		TimeInterval interval = convertStringToTimeInterval(intervalObj);

		Task newTask = new Task(description, priority, tag, interval);
		newTask.setStatus(status);

		return newTask;
	}

	private static int convertPriorityStringToInt(String priorityString) {
		priorityString = priorityString.toLowerCase().trim();
		int priority = Constant.PRIORITY_DEFAULT;
		if (priorityString.equals(Constant.PRIORITY_STRING_HIGH)) {
			priority = Constant.PRIORITY_HIGH;
		} else if (priorityString.equals(Constant.PRIORITY_STRING_MEDIUM)) {
			priority = Constant.PRIORITY_MEDIUM;
		} else if (priorityString.equals(Constant.PRIORITY_STRING_LOW)) {
			priority = Constant.PRIORITY_LOW;
		}
		return priority;
	}

	private static String convertPriorityIntToString(int priorityInt) {
		String priority;
		switch (priorityInt) {
		case Constant.PRIORITY_HIGH:
			priority = Constant.PRIORITY_STRING_HIGH;
			break;
		case Constant.PRIORITY_MEDIUM:
			priority = Constant.PRIORITY_STRING_MEDIUM;
			break;
		case Constant.PRIORITY_LOW:
			priority = Constant.PRIORITY_STRING_LOW;
			break;
		default:
			priority = Constant.PRIORITY_STRING_MEDIUM;
			break;
		}
		return priority;
	}

	/**
	 * convert the given string into a time interval
	 * 
	 * @param intervalObj - containing the time information
	 * @return a time interval with start and end date
	 * @throws ParseException - failed in parsing dates
	 * @throws CommandFailedException - invalid time interval
	 */
	@SuppressWarnings("rawtypes")
	private static TimeInterval convertStringToTimeInterval(
			LinkedHashMap intervalObj) throws ParseException,
			CommandFailedException {
		Date startDate = convertDateStringToDate((String) intervalObj
				.get(Constant.SAVE_STARTDATE));
		Date endDate = convertDateStringToDate((String) intervalObj
				.get(Constant.SAVE_ENDDATE));
		return new TimeInterval(startDate, endDate);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LinkedHashMap convertTimeIntervalToString(TimeInterval time) {
		LinkedHashMap timeInterval = new LinkedHashMap();

		String startDate = convertDateToString(time.getStartDate());
		timeInterval.put(Constant.SAVE_STARTDATE, startDate);
		String endDate = convertDateToString(time.getEndDate());
		timeInterval.put(Constant.SAVE_ENDDATE, endDate);

		return timeInterval;
	}

	private static String convertDateToString(Date date) {
		String dateString;
		if (date.equals(Constant.FLOATING_START_DATE)
				|| date.equals(Constant.FLOATING_END_DATE)
				|| date.equals(Constant.DEADLINE_START_DATE)) {
			dateString = Constant.SAVE_FORMAT_NO_DATE;
		} else {
			dateString = new SimpleDateFormat(Constant.FORMAT_DATE,
					Locale.ENGLISH).format(date);
		}
		return dateString;
	}

	/**
	 * convert a string contains date information to a Date object
	 * 
	 * @param dateString - a string with the specified date format
	 * @return Date corresponding to the string
	 * @throws ParseException - failed in parsing dates
	 */
	private static Date convertDateStringToDate(String dateString)
			throws ParseException {
		if (dateString.trim().equals(Constant.SAVE_FORMAT_NO_DATE)) {
			return null;
		}
		return new SimpleDateFormat(Constant.FORMAT_DATE, Locale.ENGLISH)
				.parse(dateString);
	}
	
	/*
	 * Unused
	 * 
	@SuppressWarnings("rawtypes") 
	public static int convertRepeatedPeriodStringToInt(LinkedHashMap task) {
		int repeated_period = Constant.REPEATED_PERIOD_DEFAULT;
		if (task.get("repeated-period").equals("none")) {
			repeated_period = Constant.REPEATED_PERIOD_NONE;
		} else if (task.get("repeated-period").equals("none")) {
			repeated_period = Constant.REPEATED_PERIOD_DAILY;
		} else if (task.get("repeated-period").equals("none")) {
			repeated_period =  Constant.REPEATED_PERIOD_WEEKLY;
		} else if (task.get("repeated-period").equals("none")) {
			repeated_period = Constant.REPEATED_PERIOD_MONTHLY;
		} else {
			repeated_period = Constant.REPEATED_PERIOD_INVALID;
		}
		return repeated_period;
	}
	
	public static String convertRepeatedPeriodIntToString(Task task) {
		String repeatedPeriod = "none";
		switch (task.getRepeatedPeriod()) {
			case Constant.REPEATED_PERIOD_NONE:
				repeatedPeriod = "none";
				break;
			case Constant.REPEATED_PERIOD_DAILY:
				repeatedPeriod = "daily";
				break;
			case Constant.REPEATED_PERIOD_WEEKLY:
				repeatedPeriod = "weekly";
				break;
			case Constant.REPEATED_PERIOD_MONTHLY:
				repeatedPeriod = "monthly";
				break;
			default:
				repeatedPeriod = "invalid";
		}
		return repeatedPeriod;
	}
	*/
}
