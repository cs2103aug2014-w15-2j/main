package dataStore;

import dataStructure.*;
import infrastructure.Constant;
import infrastructure.Parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import reference.TimeInterval;

public class JSONtest {
	
	public static void save(String username, String password, ArrayList<Task> tasks) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(username + ".json"));
		ArrayList<LinkedHashMap> tasksList = new ArrayList<LinkedHashMap>();
		
		for(int i=0; i<tasks.size(); i++) {
			LinkedHashMap task = convertTaskToMap(tasks.get(i));
			tasksList.add(task);
		}
		JSONArray.writeJSONString(tasksList, bw);
		
		bw.close();
	}
	
	private static LinkedHashMap convertTaskToMap(Task task) {
		LinkedHashMap taskMap = new LinkedHashMap();
		
		taskMap.put("task-id", task.getTaskId());
		taskMap.put("description", task.getDescription());
		taskMap.put("category", task.getCategory());
		
		ArrayList<String> tags = new ArrayList<String>();
		for(int i=0; i<task.getTag().size(); i++) {
			tags.add(task.getTag().get(i));
		}
		taskMap.put("tags", tags);
		
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
		taskMap.put("repeated-period", repeatedPeriod);
		
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
		taskMap.put("priority", priority);
		
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
		
		taskMap.put("time-interval", timeInterval);
		
		return taskMap;
	}
	
	public static ArrayList<Task> getCurrentTask(String username) throws Exception {
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		JSONParser parser = new JSONParser();
		JSONArray allTasks = (JSONArray) parser.parse(new FileReader(username + ".json"));
		
		if(allTasks == null) {
			return tasks;
		}
		
		JSONObject task;
		
		for(int i=0; i<allTasks.size(); i++) {
			task = (JSONObject) allTasks.get(i);
			Task newTask = getTask(task);
			tasks.add(newTask);
		}
		
		return tasks;
	}
	
	private static Task getTask(JSONObject task) throws Exception {
		
		String task_id = (String) task.get("task-id");
		String description = (String) task.get("description");
		String category = (String) task.get("category");
		int priority = ((Long) task.get("priority")).intValue();
		int repeated_period = ((Long) task.get("repeated-period")).intValue();
		
		JSONArray tags = (JSONArray) task.get("tags");
		ArrayList<String> tag = new ArrayList<String>();
		for(int i=0; i<tags.size(); i++) { 
			tag.add((String)tags.get(i));
		}
		
		String intervalString = (String) task.get("interval");
		
		TimeInterval interval = (intervalString!=null)?Parser.parseTimeInterval((String) task.get("interval")):
									new TimeInterval(null, null);
		
		return new Task(task_id, description, category, priority,
				repeated_period, tag, interval);
	}
	
}
