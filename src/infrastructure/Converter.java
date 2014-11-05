//@author A0113029U

package infrastructure;

import dataStructure.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.LinkedHashMap;
import reference.CommandFailedException;
import reference.TimeInterval;

public class Converter {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedHashMap convertTaskToMap(Task task) {
		LinkedHashMap taskMap = new LinkedHashMap();
		
		taskMap.put("task-id", task.getTaskId());
		taskMap.put("description", task.getDescription());
		taskMap.put("status", task.getStatus());
		
		ArrayList<String> tags = new ArrayList<String>();
		for(int i = 0; i < task.getTag().size(); i++) {
			tags.add(task.getTag().get(i));
		}
		taskMap.put("tags", tags);
	
		taskMap.put("priority", convertPriorityIntToString(task));
		taskMap.put("time-interval", convertTimeIntervalToString(task));
		
		return taskMap;
	}
	
	/**
	 * convert map to task
	 * @param task
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes") 
	public static Task getTask(LinkedHashMap task) throws Exception {
		String description = (String) task.get("description");
		String status = (String) task.get("status");
		int priority = convertPriorityStringToInt(task);
		
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

	@SuppressWarnings("rawtypes") 
	public static int convertPriorityStringToInt(LinkedHashMap task) {
		int priority = Constant.PRIORITY_DEFAULT;
		if(task.get("priority").equals("high")) {
			priority = Constant.PRIORITY_HIGH;
		} else if(task.get("priority").equals("medium")) {
			priority = Constant.PRIORITY_MEDIUM;
		} else if(task.get("priority").equals("low")) {
			priority = Constant.PRIORITY_LOW;
		}
		return priority;
	}
	
	public static String convertPriorityIntToString(Task task) {
		String priority = "medium";
		switch(task.getPriority()) {
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
		}
		return priority;
	}

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

	@SuppressWarnings("rawtypes")
	public static TimeInterval convertStringToTimeInterval
								(LinkedHashMap intervalObj) 
								throws ParseException, CommandFailedException {
		Date startDate = null;
		if(!intervalObj.get("startDate").equals("-")) {
			startDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
				Locale.ENGLISH).parse((String) intervalObj.get("startDate"));
		}
		Date endDate = null;
		if(!intervalObj.get("endDate").equals("-")) {
			endDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
				Locale.ENGLISH).parse((String) intervalObj.get("endDate"));
		}
		return new TimeInterval(startDate, endDate);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedHashMap convertTimeIntervalToString(Task task) {
		LinkedHashMap timeInterval = new LinkedHashMap();
		
		if(task.getInterval().getStartDate() == Constant.FLOATING_START_DATE &&
			task.getInterval().getEndDate() == Constant.FLOATING_END_DATE) {
			timeInterval.put("startDate", "-");
			timeInterval.put("endDate", "-");
		} else if(task.getInterval().getStartDate() == Constant.DEADLINE_START_DATE) {
			String endDate = Converter.convertDateToString
							(task.getInterval().getEndDate());
			timeInterval.put("startDate", "-");
			timeInterval.put("endDate", endDate);
		} else {
			String startDate = Converter.convertDateToString(task.getInterval()
								.getStartDate());
			String endDate = Converter.convertDateToString(task.getInterval()
								.getEndDate());
			timeInterval.put("startDate", startDate);
			timeInterval.put("endDate", endDate);
		}
		
		return timeInterval;
	}
	
	public static String convertDateToString(Date date) {
			String dateString = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
					Locale.ENGLISH).format(date);
		
		return dateString;
	}
	
}
