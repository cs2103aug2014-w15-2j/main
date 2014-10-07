package dataStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import dataStructure.Task;
import dataStructure.TimeInterval;

public class DataStore {

	protected final static int ATTRIBUTE_END_POSITION = 1;
	private final static String SPLIT_SECTION = "**********";
	private final static int NO_TAG_ENDINDEX = 0;
	private final static int NO_STARTDATE_ENDINDEX = -1;
	private final static int NO_ENDDATE_ENDINDEX = -1;
	private final static int LAST_TAG_COMMAINDEX = -1;

	/**
	 * check whether an account exists
	 * 
	 * @param username
	 * @return true if exists, no otherwise
	 */
	public static boolean isAccountExisting(String username) {
		File account = new File(username);
		if (account.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * check if it is a valid account (valid username and correct password)
	 * 
	 * @param username
	 * @param password
	 * @return true if valid, false if account not exists or wrong password
	 */
	public static boolean authenticate(String username, String password) {
		if (!isAccountExisting(username)) {
			return false;
		}
		try {
			String realPassword = getPassword(username);
			return password.equals(realPassword);
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * create a new account (can not create an existing account)
	 * 
	 * @param username
	 * @param passwordInput
	 * @return true if succeed, false otherwise
	 */
	public static boolean createAccount(String username, String passwordInput) {
		if (isAccountExisting(username)) {
			return false;
		}
		try {
			File account = new File(username);
			account.createNewFile();

			BufferedWriter bw = new BufferedWriter(new FileWriter(username));
			bw.write(passwordInput);
			bw.newLine();
			bw.write(SPLIT_SECTION);
			bw.newLine();
			bw.close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * delete an account (can not be undone)
	 * 
	 * @param username
	 * @param password
	 * @return true if succeed, false otherwise
	 */
	public static boolean destroy(String username, String password) {
		// check whether it is a valid account
		if (!authenticate(username, password)) {
			return false;
		}
		File account = new File(username);
		return account.delete();
	}

	/**
	 * save the changes, write all tasks into the account data
	 * 
	 * @param username
	 * @param tasks
	 * @return true if succeed, false otherwise
	 */
	public static boolean save(String username, ArrayList<Task> tasks) {
		// check if it is an existing account
		if (!isAccountExisting(username)) {
			return false;
		}
		try {
			String password = getPassword(username);
			BufferedWriter bw = new BufferedWriter(new FileWriter(username));

			bw.write(password);
			bw.newLine();
			bw.write(SPLIT_SECTION);
			bw.newLine();
			bw.flush();
			for (int i = 0; i < tasks.size(); i++) {
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
		// tasks start from line 3
		reader.readLine();
		reader.readLine();
		String nextTask = reader.readLine();
		while (nextTask!=null) {
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
		task_id = taskDescription.substring(0, endIndex);
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		description = taskDescription.substring(0, endIndex);
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		category = taskDescription.substring(0, endIndex);
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);

		tag = getTaskTags(taskDescription);
		endIndex = taskDescription.indexOf("`");
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		repeated_period = Integer.parseInt(taskDescription.substring(0,
				endIndex));
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		priority = Integer.parseInt(taskDescription.substring(0, endIndex));
		taskDescription = taskDescription.substring(endIndex
				+ ATTRIBUTE_END_POSITION);

		endIndex = taskDescription.indexOf("`");
		// no startDate
		if (endIndex == NO_STARTDATE_ENDINDEX) {
			startDate = null;
		} else {
			startDate = new Date(Long.parseLong(taskDescription.substring(0,
					endIndex)));
		}

		endIndex = taskDescription.indexOf("`");
		// no endDate
		if (endIndex == NO_ENDDATE_ENDINDEX) {
			endDate = null;
		} else {
			taskDescription = taskDescription.substring(endIndex);
			endDate = new Date(Long.parseLong(taskDescription));
		}
		
		interval = new TimeInterval(startDate, endDate);

		task = new Task(task_id, description, category, priority,
				repeated_period, tag, interval);

		return task;
	}

	/**
	 * get the tags of a task
	 * 
	 * @param taskDescription
	 * @return list of tags
	 */
	private static ArrayList<String> getTaskTags(String taskDescription) {
		ArrayList<String> tag = new ArrayList<String>();
		int endIndex;
		int commaIndex;
		
		endIndex = taskDescription.indexOf("`");
		commaIndex = taskDescription.indexOf(",");
		
		if(endIndex == NO_TAG_ENDINDEX) {
			return tag;
		}
		while (commaIndex != LAST_TAG_COMMAINDEX) {
			tag.add(taskDescription.substring(0, commaIndex));
			taskDescription = taskDescription.substring(commaIndex
					+ ATTRIBUTE_END_POSITION);
			commaIndex = taskDescription.indexOf(",");
		}
		
		// add last tag
		endIndex = taskDescription.indexOf("`");
		tag.add(taskDescription.substring(0, endIndex));
		
		return tag;
	}

	/**
	 * Read the password of the account and return it
	 * 
	 * @param username
	 * @return password
	 * @throws IOException
	 */
	private static String getPassword(String username) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(username));
		String password = br.readLine();
		br.close();
		return password;
	}

}
