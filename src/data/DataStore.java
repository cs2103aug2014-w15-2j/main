package data;

import includes.Task;

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
	 * @return user current tasks
	 * @throws Exception 
	 */
	public static ArrayList<Task> getCurrentTasks(File file) throws Exception {
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

	private static Task parseTask(String taskDescription) throws Exception {
		Task task;
		String description;
		String category;
		int priority;
		String task_id;
		int repeated_period;
		ArrayList<String> tag;
		Date startDate;
		Date endDate;
		
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
		repeated_period = Integer.parseInt(taskDescription.substring(0,
				endIndex - ATTRIBUTE_END_POSITION));
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		priority = Integer.parseInt(taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION));
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		endIndex = taskDescription.indexOf("`");
		startDate = new Date(Long.parseLong(taskDescription.substring(0, endIndex
				- ATTRIBUTE_END_POSITION)));
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);
		
		endDate = new Date(Long.parseLong(taskDescription));

		task = new Task(task_id, description, category, priority,
				repeated_period, tag, startDate, endDate);

		return task;
	}

	private static ArrayList<String> getTaskTags(String taskDescription) {
		ArrayList<String> tag = new ArrayList<String>();
		int endIndex;
		int commaIndex;
		endIndex = taskDescription.indexOf("`");
		commaIndex = taskDescription.indexOf(",");
		while (commaIndex != -1) {
			tag.add(taskDescription.substring(0, commaIndex
					- ATTRIBUTE_END_POSITION));
			taskDescription = taskDescription.substring(commaIndex
					+ ATTRIBUTE_END_POSITION);
		}
		tag.add(taskDescription.substring(0, endIndex - ATTRIBUTE_END_POSITION));
		return tag;
	}

}
