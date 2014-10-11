package dataStore;

import infrastructure.Constant;
import dataStructure.*;
import infrastructure.Parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import reference.TimeInterval;

public class JSONtest {

	public static void save(String username, String password, ArrayList<Task> tasks) throws IOException, JSONException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(username + ".json"));
			
		for (int i = 0; i < tasks.size(); i++) {
			JSONObject task = representTask(tasks.get(i));
			bw.write(task.toString());
			bw.newLine();
		}

		bw.close();
	}
	
	private static JSONObject representTask(Task task) throws JSONException {
		JSONObject taskObj = new JSONObject();
		
		taskObj.put("task-id", task.getTaskId());
		taskObj.put("description", task.getDescription());
		taskObj.put("category", task.getCategory());
		
		JSONArray tags = new JSONArray();
		for(int i=0; i<task.getTag().size(); i++) {
			tags.put(task.getTag().get(i));
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
		
		JSONObject task;
		
		for(int i=0; i<allTasks.length(); i++) {
			task = (JSONObject) allTasks.getJSONObject(i);
			Task newTask = getTask(task);
			tasks.add(newTask);
		}
		
		return tasks;
	}
	
	private static Task getTask(JSONObject task) throws Exception {
		
		String task_id = (String) task.get("task-id");
		String description = (String) task.get("description");
		String category = (String) task.get("category");
		int priority = (int) task.get("priority");
		int repeated_period = (int) task.get("repeated-period");
		
		JSONArray tags = (JSONArray) task.get("tags");
		ArrayList<String> tag = new ArrayList<String>();
		for(int i=0; i<tags.length(); i++) { 
			tag.add(tags.getString(i));
		}
		
		TimeInterval interval = Parser.parseTimeInterval((String) task.get("interval"));
		
		return new Task(task_id, description, category, priority,
				repeated_period, tag, interval);
	}
	
}
