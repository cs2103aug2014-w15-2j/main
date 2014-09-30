package data;

import includes.Task;
import includes.TimeInterval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

public class DataStore {

	protected final static int ATTRIBUTE_END_POSITION = 1;
	private final static String SPLIT_SECTION = "**********";

	public static boolean isAccountExisting(String username) {
		File account = new File(username + ".txt");
		if(account.exists()){
			return true;
		}
		return false;
	}

	public static boolean authenticate(String username, String password) {
		if(!isAccountExisting(username)){
			return false;
		}
		
		try {
			String realPassword = getPassword(username);
			return password.equals(realPassword);
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e){
			return false;
		}
	}

	public static boolean createAccount(String username, String passwordInput1) {
		if(isAccountExisting(username)){
			return false;
		}
		
		try {
			File account = new File(username + ".txt");
			account.createNewFile();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(username + ".txt"));
			bw.write(passwordInput1);
			bw.newLine();
			bw.write(SPLIT_SECTION);
			bw.newLine();
			bw.close();
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean destroy(String username, String password) {
		//check whether it is a valid account
		if(!authenticate(username, password)){
			return false;
		}
		File account = new File(username + ".txt");
		return account.delete();
	}
	
	public static boolean save(String username, ArrayList<Task> tasks) {
		if(!isAccountExisting(username)){
			return false;
		}
		
		try {
			String password = getPassword(username);
			BufferedWriter bw = new BufferedWriter(new FileWriter(username + ".txt"));
			
			bw.write(password);
			bw.newLine();
			bw.write(SPLIT_SECTION);
			bw.newLine();
			
			for(int i=0; i<tasks.size(); i++){
				bw.write(tasks.get(i).toString());
				bw.newLine();
			}
			
			bw.close();
			return true;
		} catch (IOException e) {
			return false;
		}
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

	/**
	 * parse String to task
	 * 
	 * @param taskDescription
	 * @return task
	 * @throws Exception
	 */
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
		TimeInterval interval;
		
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

		interval = new TimeInterval(startDate, endDate);

		task = new Task(task_id, description, category, priority,
				repeated_period, tag, interval);

		return task;
	}

	/**
	 * get the tags of a task
	 * @param taskDescription
	 * @return list of tags
	 */
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
	
	private static String getPassword(String username) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(username + ".txt"));
		String password = br.readLine();
		br.close();
		return password;
	}

}
