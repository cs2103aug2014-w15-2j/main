package data;

import includes.DeadlineTask;
import includes.FloatingTask;
import includes.Task;
import includes.TimedTask;
import includes.Task.TaskType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class DataStore {
	
	protected final static int ATTRIBUTE_END_POSITION = 1;

	public static boolean isAccountExisting(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean authenticate(String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void createAccount(String username, String passwordInput1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * read file and get user current tasks
	 * 
	 * @param file
	 * @throws IOException
	 * @return user current tasks
	 */
	public static ArrayList<Task> getCurrentTasks(File file) throws IOException {
		ArrayList<Task> currentTasks = new ArrayList<Task>();
		Task task;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		reader.readLine();
		reader.readLine();
		reader.readLine();
		String nextTask = reader.readLine();
		while (nextTask != null) {
			task = parseTask(nextTask);
			currentTasks.add(task);
			nextTask = reader.readLine();
		}
		reader.close();
		return currentTasks;
	}

	private static Task parseTask(String taskDescription) {
		Task task;
		String type;
		int typeEndIndex;

		typeEndIndex = taskDescription.indexOf("`");
		type = taskDescription.substring(0, typeEndIndex
				- ATTRIBUTE_END_POSITION);
		taskDescription = taskDescription.substring(typeEndIndex
				+ ATTRIBUTE_END_POSITION);

		if (type.equals(TaskType.FLOATING)) {
			task = parseFloatingTask(taskDescription);
		} else if (type.equals(TaskType.TIMED)) {
			task = parseTimedTask(taskDescription);
		} else {
			task = parseDeadlineTask(taskDescription);
		}
		return task;
	}

	private static FloatingTask parseFloatingTask(String taskDescription) {
		FloatingTask task;
		String description; 
		String category;
		int priority;
		String task_id; 
		int repeated_period; 
		ArrayList<String> tag;
		
		int endIndex;
		
		endIndex = taskDescription.indexOf("`");
		task_id = taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION);
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		endIndex = taskDescription.indexOf("`");
		description = taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION);
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		endIndex = taskDescription.indexOf("`");
		category = taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION);
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		tag = getTaskTags(taskDescription);
		endIndex = taskDescription.indexOf("`");
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		endIndex = taskDescription.indexOf("`");
		repeated_period = Integer.parseInt(taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION));
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		endIndex = taskDescription.indexOf("`");
		priority = Integer.parseInt(taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION));
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		task = new FloatingTask(description, category, priority, task_id, repeated_period, tag);
		return task;
	}

	private static TimedTask parseTimedTask(String taskDescription) {
		TimedTask task;
		String description;
		String category;
		int priority;
		String task_id;
		int repeated_period;
		ArrayList<String> tag;
		Date startDate;
		Date endDate;
		
		return task;
	}

	private static DeadlineTask parseDeadlineTask(String taskDescription) {
		DeadlineTask task;
		String description; 
		String category;
		int priority;
		String task_id; 
		int repeated_period; 
		ArrayList<String> tag;
		Date deadline;
		
		return task;
	}
	
	private static ArrayList<String> getTaskTags(String taskDescription){
		ArrayList<String> tag = new ArrayList<String>();
		int endIndex;
		int commaIndex;
		endIndex = taskDescription.indexOf("`");
		commaIndex = taskDescription.indexOf(",");
		while(commaIndex != -1){
			tag.add(taskDescription.substring(0, commaIndex
				- ATTRIBUTE_END_POSITION));
			taskDescription = taskDescription.substring(commaIndex
					+ ATTRIBUTE_END_POSITION);
		}	
		tag.add(taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION));
		return tag;
	}
	
	
	
}
