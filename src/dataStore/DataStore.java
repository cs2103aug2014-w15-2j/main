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

import org.json.simple.JSONArray;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import dataStructure.Task;

public abstract class DataStore {
	
	private static final String DATA_FILEPATH = "List-of-Xiao-Ming/task-list.xiaoming";
	
	public static ArrayList<Task> loadFileData() throws Exception {
		if(!isFileExisting()) {
			createTaskFile();
		}
		File fileData = new File(DATA_FILEPATH);
		return getCurrentTasks(fileData);
	}

	/**
	 * create a new data file
	 * @return true if succeed, false otherwise
	 */
	public static boolean createTaskFile() {
		if (isFileExisting()) {
			return false;
		}
		try {
			File fileData = new File(DATA_FILEPATH);
			fileData.createNewFile();
			save(null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * save the changes, write all tasks into the account data
	 * 
	 * @return true if succeed, false otherwise
	 */
	@SuppressWarnings("rawtypes")
	public static boolean save(ArrayList<Task> tasks) {
		if (!isFileExisting()) {
			return false;
		}
		try {
			FileWriter fw = new FileWriter(DATA_FILEPATH);
			ArrayList tasksList = getContent(tasks);
			Writer writer = new JSonWriter();
			JSONArray.writeJSONString(tasksList, writer);
			fw.write(writer.toString());
			writer.close();
			fw.close();
			return true;
		} catch (IOException e) {
			return false;
		}				
	}
	
	/**
	 * check whether the task-list exists
	 * @return true if exists, no otherwise
	 */
	private static boolean isFileExisting() {
		File fileData = new File(DATA_FILEPATH);
		if (fileData.exists()) {
			return true;
		}
		return false;
	}
	
	/**
	 * read file and get user current tasks
	 * 
	 * @param file
	 * @return user current tasks
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private static ArrayList<Task> getCurrentTasks(File userFile)
													throws Exception {
		FileReader user = new FileReader(userFile);
		ArrayList<Task> currentTasks = new ArrayList<Task>();
		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = setOrderedKeyFactory();
		ArrayList allTasks = (ArrayList) parser.parse
								(user, orderedKeyFactory);
		
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
	 * return the content of the file to be written(password and list of tasks)
	 * @param password
	 * @param tasks
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	private static ArrayList getContent(ArrayList<Task> tasks) {
		//list all tasks
		ArrayList<LinkedHashMap> tasksList = new ArrayList<LinkedHashMap>();
		if( tasks != null) {
			for(int i = 0; i < tasks.size(); i++) {
				LinkedHashMap task = Converter.convertTaskToMap(tasks.get(i));
				tasksList.add(task);
			}
		}
		
		return tasksList;
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