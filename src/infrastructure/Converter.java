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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedHashMap convertTaskToMap(Task task) {
		LinkedHashMap taskMap = new LinkedHashMap();
		
		taskMap.put("description", task.getDescription());
		taskMap.put("status", task.getStatus());
		
		ArrayList<String> tags = new ArrayList<String>();
		for(int i = 0; i < task.getTag().size(); i++) {
			tags.add(task.getTag().get(i));
		}
		taskMap.put("tags", tags);
	
		taskMap.put("priority", 
					convertPriorityIntToString(task.getPriority()));
		taskMap.put("time-interval",
					convertTimeIntervalToString(task.getInterval()));
		
		return taskMap;
	}
	
	/**
	 * convert map to task
	 * @param task
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes") 
	public static Task convertMapToTask(LinkedHashMap task) throws Exception {
		String description = (String) task.get("description");
		String status = (String) task.get("status");
		int priority = convertPriorityStringToInt
						((String) task.get("priority"));
		
		ArrayList tags = (ArrayList) task.get("tags");
		ArrayList<String> tag = new ArrayList<String>();
		for(int i = 0; i < tags.size(); i++) { 
			tag.add((String)tags.get(i));
		}
		
		LinkedHashMap intervalObj = (LinkedHashMap) task.get("time-interval");
		TimeInterval interval = convertStringToTimeInterval(intervalObj);
		
		Task newTask = new Task(description, priority, tag, interval);
		newTask.setStatus(status);
		
		return newTask;
	}

	private static int convertPriorityStringToInt(String priorityString) {
		priorityString = priorityString.toLowerCase().trim();
		int priority = Constant.PRIORITY_DEFAULT;
		if(priorityString.equals("high")) {
			priority = Constant.PRIORITY_HIGH;
		} else if(priorityString.equals("medium")) {
			priority = Constant.PRIORITY_MEDIUM;
		} else if(priorityString.equals("low")) {
			priority = Constant.PRIORITY_LOW;
		}
		return priority;
	}
	
	private static String convertPriorityIntToString(int priorityInt) {
		String priority = "medium";
		switch(priorityInt) {
			case Constant.PRIORITY_HIGH:
				priority = "high";
				break;
			case Constant.PRIORITY_MEDIUM:
				priority = "medium";
				break;
			case Constant.PRIORITY_LOW:
				priority = "low";
				break;
			default:
				priority = "invalid";
				break;
		}
		return priority;
	}

	@SuppressWarnings("rawtypes")
	private static TimeInterval convertStringToTimeInterval
								(LinkedHashMap intervalObj) 
								throws ParseException, CommandFailedException {
		Date startDate = convertDateStringToDate
						((String)intervalObj.get("startDate")); 
		Date endDate = convertDateStringToDate
						((String) intervalObj.get("endDate"));
		return new TimeInterval(startDate, endDate);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LinkedHashMap convertTimeIntervalToString
											(TimeInterval time) {
		LinkedHashMap timeInterval = new LinkedHashMap();
		
		String startDate = convertDateToString(time.getStartDate());
		timeInterval.put("startDate", startDate);
		String endDate = convertDateToString(time.getEndDate());
		timeInterval.put("endDate", endDate);
		
		return timeInterval;
	}
	
	private static String convertDateToString(Date date) {
		String dateString;
		if(date.equals(Constant.FLOATING_START_DATE)||
			date.equals(Constant.FLOATING_END_DATE) ||
			date.equals(Constant.DEADLINE_START_DATE)) {
			dateString = "-"; 
		} else {
			dateString = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
					Locale.ENGLISH).format(date);	
		}
		return dateString;
	}
	
	private static Date convertDateStringToDate(String dateString)
												throws ParseException {
		if(dateString.trim().equals("-")) {
			return null;
		}
		return new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
				Locale.ENGLISH).parse(dateString);
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
