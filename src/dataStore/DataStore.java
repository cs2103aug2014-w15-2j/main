package dataStore;

import infrastructure.Converter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dataStructure.Task;

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
		} catch (IOException | ParseException e) {
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
			saveFile(username, passwordInput, null);
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
		account.delete();
		TestingCache.clearCache();
		return true;
	}
	
	/**
	 * save the changes, write all tasks into the account data
	 * 
	 * @param username
	 * @param tasks
	 * @return true if succeed, false otherwise
	 */
	public static boolean save(String username, ArrayList<Task> tasks) {
		String password;
		try {
			password = getPassword(username);
		} catch (IOException | ParseException e) {
			return false;
		}
		return saveFile(username, password, tasks);
	}
	
	/**
	 * read file and get user current tasks
	 * 
	 * @param file
	 * @return user current tasks
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<Task> getCurrentTasks(File userFile) throws Exception {
		FileReader user = new FileReader(userFile);
		ArrayList<Task> currentTasks = new ArrayList<Task>();
		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = setOrderedKeyFactory();
		LinkedHashMap account = (LinkedHashMap) parser.parse
								(user, orderedKeyFactory);
		
		ArrayList allTasks = (ArrayList) account.get("tasks");
		LinkedHashMap task;
		if(allTasks != null) {
			for(int i=0; i<allTasks.size(); i++) {
				task = (LinkedHashMap) allTasks.get(i);
				Task newTask = Converter.getTask(task);
				currentTasks.add(newTask);
			}
		}
		
		user.close();
		return currentTasks;
	}
	
	/**
	 * write to file
	 * @param username
	 * @param password
	 * @param tasks
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	private static boolean saveFile(String username, String password, ArrayList<Task> tasks) {
		if (!isAccountExisting(username)) {
			return false;
		}
		try {
			FileWriter fw = new FileWriter(username);
			LinkedHashMap account = getContent(password, tasks);
			Writer writer = new JSonWriter();
			JSONObject.writeJSONString(account, writer);
			fw.write(writer.toString());
			writer.close();
			fw.close();
			return true;
		} catch (IOException e) {
			return false;
		}				
	}
	
	/**
	 * return the content of the file to be written(password and list of tasks)
	 * @param password
	 * @param tasks
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LinkedHashMap getContent(String password, ArrayList<Task> tasks) {
		LinkedHashMap account = new LinkedHashMap();
		
		//write password
		account.put("password", password);
		
		//list all tasks
		ArrayList<LinkedHashMap> tasksList = new ArrayList<LinkedHashMap>();
		if( tasks != null) {
			for(int i = 0; i < tasks.size(); i++) {
				LinkedHashMap task = Converter.convertTaskToMap(tasks.get(i));
				tasksList.add(task);
			}
		}
		account.put("tasks", tasksList);
		
		return account;
	}

	/**
	 * Read the password of the account and return it
	 * 
	 * @param username
	 * @return password
	 * @throws IOException
	 * @throws ParseException 
	 */
	@SuppressWarnings("rawtypes")
	private static String getPassword(String username) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = setOrderedKeyFactory();
		FileReader userFile = new FileReader(username);
		LinkedHashMap account = (LinkedHashMap) parser.parse
								(userFile, orderedKeyFactory);
		String password = (String) account.get("password");
		userFile.close();
		return password;
	}
	
	/**
	 * return an ordered key factory (maintain the ordering from the file)
	 */
	@SuppressWarnings("rawtypes")
	private static ContainerFactory setOrderedKeyFactory() {
		ContainerFactory orderedKeyFactory = new ContainerFactory()
		{
			@Override
		    public List creatArrayContainer() {
		      return new ArrayList();
		    }

			@Override
			public Map createObjectContainer() {
		      return new LinkedHashMap();
		    }
		};
		return orderedKeyFactory;
	}

}