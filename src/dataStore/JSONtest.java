package dataStore;

import dataStructure.*;
import infrastructure.Parser;

import java.util.ArrayList;
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

	public static void save(String username, String password, ArrayList<Task> tasks) throws IOException, JSONException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(username + ".json"));
		
		JSONArray taskList = new JSONArray();
			
		for (int i = 0; i < tasks.size(); i++) {
			JSONObject task = representTask(tasks.get(i));
			taskList.add(task);
		}
		
		bw.write(taskList.toString());
		bw.close();
	}
	
	private static JSONObject representTask(Task task) throws JSONException {
		JSONObject taskObj = new JSONObject();
		
		taskObj.put("task-id", task.getTaskId());
		taskObj.put("description", task.getDescription());
		taskObj.put("category", task.getCategory());
		
		JSONArray tags = new JSONArray();
		for(int i=0; i<task.getTag().size(); i++) {
			tags.add(task.getTag().get(i));
		}
		taskObj.put("tags", tags);
		
		taskObj.put("repeated-period", task.getRepeatedPeriod());
		taskObj.put("priority", task.getPriority());
		taskObj.put("time-interval", task.getInterval().toString());
		
		return taskObj;
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
