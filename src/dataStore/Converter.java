package dataStore;

import dataStructure.*;
import infrastructure.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ContainerFactory;

import reference.CommandFailedException;
import reference.TimeInterval;

public class Converter {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void save(String username, String password, ArrayList<Task> tasks) throws IOException {
		FileWriter fw = new FileWriter(username + ".json");
		
		//write password
		LinkedHashMap account = new LinkedHashMap();
		account.put("password", password);
		
		//list all tasks
		ArrayList<LinkedHashMap> tasksList = new ArrayList<LinkedHashMap>();
		if( tasks != null) {
			for(int i = 0; i<tasks.size(); i++) {
				LinkedHashMap task = convertTaskToMap(tasks.get(i));
				tasksList.add(task);
			}
		}
		account.put("tasks", tasksList);
		
		//write to file
		Writer writer = new JSonWriter();
		JSONObject.writeJSONString(account, writer);
		fw.write(writer.toString());
		fw.close();
	}
	
	@SuppressWarnings("rawtypes")
	public static ArrayList<Task> getCurrentTask(String username) throws Exception {
		ArrayList<Task> tasks = new ArrayList<Task>();
		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = setOrderedKeyFactory();
		LinkedHashMap account = (LinkedHashMap) parser.parse(new FileReader(username + ".json"), orderedKeyFactory);
		
		LinkedList allTasks = (LinkedList) account.get("tasks");
		LinkedHashMap task;
		if(allTasks != null) {
			for(int i=0; i<allTasks.size(); i++) {
				task = (LinkedHashMap) allTasks.get(i);
				Task newTask = getTask(task);
				tasks.add(newTask);
			}
		}
		
		return tasks;
	}
	
	@SuppressWarnings("rawtypes")
	private static ContainerFactory setOrderedKeyFactory() {
		ContainerFactory orderedKeyFactory = new ContainerFactory()
		{
			@Override
		    public List creatArrayContainer() {
		      return new LinkedList();
		    }

			public Map createObjectContainer() {
		      return new LinkedHashMap();
		    }
		};
		return orderedKeyFactory;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedHashMap convertTaskToMap(Task task) {
		LinkedHashMap taskMap = new LinkedHashMap();
		
		taskMap.put("task-id", task.getTaskId());
		taskMap.put("description", task.getDescription());
		taskMap.put("category", task.getCategory());
		
		ArrayList<String> tags = new ArrayList<String>();
		for(int i=0; i<task.getTag().size(); i++) {
			tags.add(task.getTag().get(i));
		}
		taskMap.put("tags", tags);
		
		taskMap.put("repeated-period", convertRepeatedPeriodIntToString(task));
		taskMap.put("priority", convertPriorityIntToString(task));
		taskMap.put("time-interval", convertTimeIntervalToString(task));
		
		return taskMap;
	}
	
	@SuppressWarnings("rawtypes") 
	public static Task getTask(LinkedHashMap task) throws Exception {
		String task_id = (String) task.get("task-id");
		String description = (String) task.get("description");
		String category = (String) task.get("category");
		int priority = convertPriorityStringToInt(task);
		int repeated_period = convertRepeatedPeriodStringToInt(task);
		
		LinkedList tags = (LinkedList) task.get("tags");
		ArrayList<String> tag = new ArrayList<String>();
		for(int i=0; i<tags.size(); i++) { 
			tag.add((String)tags.get(i));
		}
		
		LinkedHashMap intervalObj = (LinkedHashMap) task.get("time-interval");
		TimeInterval interval = convertStringToTimeInterval(intervalObj);
		
		return new Task(task_id, description, category, priority,
				repeated_period, tag, interval);
	}

	@SuppressWarnings("rawtypes") 
	public static int convertPriorityStringToInt(LinkedHashMap task) {
		int priority = 0;
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
		int repeated_period = 0;
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
	public static TimeInterval convertStringToTimeInterval(
			LinkedHashMap intervalObj) throws ParseException,
			CommandFailedException {
		Date startDate = null;
		if(!intervalObj.get("startDate").equals("-")) {
			startDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.ENGLISH).
					parse((String) intervalObj.get("startDate"));
		}
		Date endDate = null;
		if(!intervalObj.get("endDate").equals("-")) {
			endDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.ENGLISH).
					parse((String) intervalObj.get("endDate"));
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
			String endDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm").
					format(task.getInterval().getEndDate());
			timeInterval.put("startDate", "-");
			timeInterval.put("endDate", endDate);
		} else {
			String startDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm").
					format(task.getInterval().getStartDate());
			String endDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm").
					format(task.getInterval().getEndDate());
			timeInterval.put("startDate", startDate);
			timeInterval.put("endDate", endDate);
		}
		
		return timeInterval;
	}
	
}
