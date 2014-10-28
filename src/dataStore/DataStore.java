package dataStore;

import infrastructure.Constant;
import dataStore.Converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

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
			save(username, null);

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
		File accountJSON = new File(username + ".json");
		account.delete();
		accountJSON.delete();
		DataStore.clearCache();
		return true;
	}
	
	/**
	 * save the changes, write all tasks into the account data
	 * 
	 * @param username
	 * @param tasks
	 * @return true if succeed, false otherwise
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean save(String username, ArrayList<Task> tasks) throws IOException {
		if (!isAccountExisting(username)) {
			return false;
		}
		
		String password = getPassword(username);
		
		FileWriter fw = new FileWriter(username);
	
		//write password
		LinkedHashMap account = new LinkedHashMap();
		account.put("password", password);
		
		//list all tasks
		ArrayList<LinkedHashMap> tasksList = new ArrayList<LinkedHashMap>();
		if( tasks != null) {
			for(int i = 0; i<tasks.size(); i++) {
				LinkedHashMap task = Converter.convertTaskToMap(tasks.get(i));
				tasksList.add(task);
			}
		}
		account.put("tasks", tasksList);
		
		//write to file
		Writer writer = new JSonWriter();
		JSONObject.writeJSONString(account, writer);
		fw.write(writer.toString());
		fw.close();
		
		return true;
	}

	/**
	 * read file and get user current tasks
	 * 
	 * @param file
	 * @return user current tasks
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<Task> getCurrentTask(File file) throws Exception {
		ArrayList<Task> tasks = new ArrayList<Task>();
		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = setOrderedKeyFactory();
		LinkedHashMap account = (LinkedHashMap) parser.parse(new FileReader(file), orderedKeyFactory);
		
		LinkedList allTasks = (LinkedList) account.get("tasks");
		LinkedHashMap task;
		if(allTasks != null) {
			for(int i=0; i<allTasks.size(); i++) {
				task = (LinkedHashMap) allTasks.get(i);
				Task newTask = Converter.getTask(task);
				tasks.add(newTask);
			}
		}
		
		return tasks;
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