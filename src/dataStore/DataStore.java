package dataStore;

import infrastructure.Constant;
import infrastructure.Parser;
import dataStore.JSONtest;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import dataStructure.*;

public abstract class DataStore {

	
	

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
			bw.write(Constant.SPLIT_SECTION);
			bw.newLine();
			bw.close();
			
			File accountJSON = new File(username + ".json");
			accountJSON.createNewFile();

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
		DataStore.clearCache();
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
			bw.write(Constant.SPLIT_SECTION);
			bw.newLine();
			bw.flush();
			for (int i = 0; i < tasks.size(); i++) {
				bw.write(tasks.get(i).toString());
				bw.newLine();
			}

			bw.close();
			
			JSONtest.save(username, password, tasks);
			
			return true;
		} catch (IOException e) {
			return false;
		} catch (JSONException e) {
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
		/*
		ArrayList<Task> currentTasks = new ArrayList<Task>();
		Task task;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		// tasks start from line 3
		reader.readLine();
		reader.readLine();
		String nextTask = reader.readLine();
		while (nextTask != null) {
			task = Parser.parseTaskFromRecords(nextTask);
			currentTasks.add(task);
			nextTask = reader.readLine();
		}
		reader.close();
		*/
		ArrayList<Task> currentTasks = JSONtest.getCurrentTask(file.getName());
		return currentTasks;
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
	
	
	// FOR TESTING ONLY
	/**
	 * cacheAccount caching
	 * 
	 * @param username
	 * @return true if succeed, false otherwise
	 */
	public static boolean cacheAccount(String username) {
		if (!isAccountExisting(username)) {
			return false;
		}
		try {
			File cached = new File("_cache");
			if (!cached.exists()) {
				cached.createNewFile();
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter("_cache"));
			bw.write(username);
			bw.close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * getCachedAccount
	 * 
	 * @return cached account name
	 */
	public static String getCachedAccount() {
		try {
			File cached = new File("_cache");
			if (!cached.exists()) {
				cached.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader("_cache"));
			String cachedName = reader.readLine();
			
			reader.close();
			return cachedName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * clearCache
	 * 
	 * @return boolean
	 */
	public static boolean clearCache() {
		File cache = new File("_cache");
		return cache.delete();
	}

}
