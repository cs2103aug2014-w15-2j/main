//@author A0113029U

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

import modal.Task;
import modal.TaskBox;

import org.json.simple.JSONArray;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

public abstract class DataStore {
	
	private static final String DATA_FILEPATH = 
								"List-of-Xiao-Ming/task-list.xiaoming";
	
	public static TaskBox loadFileData() throws Exception {
		if(!isFileExisting()) {
			createTaskFile();
		}
		File fileData = new File(DATA_FILEPATH);
		return getCurrentTasks(fileData);
	}
	
	/**
	 * save the changes, write all tasks into the account data 
	 * @return true if succeed, false otherwise
	 */
	@SuppressWarnings("rawtypes")
	public static boolean save(TaskBox tasks) {
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
	 * create a new data file
	 * @return true if succeed, false otherwise
	 */
	private static boolean createTaskFile() {
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
	 * read file and get user current tasks
	 * 
	 * @param file
	 * @return user current tasks
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private static TaskBox getCurrentTasks(File userFile)
													throws Exception {
		FileReader user = new FileReader(userFile);
		TaskBox tasksList = new TaskBox();
		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = setOrderedKeyFactory();
		ArrayList allTasks = (ArrayList) parser.parse(user, orderedKeyFactory);
		
		LinkedHashMap task;
		if(allTasks != null) {
			for(int i=0; i<allTasks.size(); i++) {
				task = (LinkedHashMap) allTasks.get(i);
				Task newTask = Converter.convertMapToTask(task);
				if(newTask.isDone()) {
					tasksList.getFinishedTasks().add(newTask);
				} else if(newTask.isTrashed()) {
					tasksList.getTrashedTasks().add(newTask);
				} else {
					//default: ongoing to do tasks
					tasksList.getNormalTasks().add(newTask);
				}
			}
		}
		
		user.close();
		return tasksList;
	}
	
	/**
	 * return the content of the file to be written(password and list of tasks)
	 * @param password
	 * @param tasks
	 * @return
	 */

	@SuppressWarnings({ "rawtypes" })
	private static ArrayList getContent(TaskBox tasks) {
		//list all tasks
		ArrayList<LinkedHashMap> tasksList = new ArrayList<LinkedHashMap>();
		if( tasks != null) {
			for(int i = 0; i < tasks.getNormalTasks().size(); i++) {
				LinkedHashMap task = Converter.convertTaskToMap
									(tasks.getNormalTasks().get(i));
				tasksList.add(task);
			}
			for(int i = 0; i < tasks.getFinishedTasks().size(); i++) {
				LinkedHashMap task = Converter.convertTaskToMap
									(tasks.getFinishedTasks().get(i));
				tasksList.add(task);
			}
			for(int i = 0; i < tasks.getTrashedTasks().size(); i++) {
				LinkedHashMap task = Converter.convertTaskToMap
									(tasks.getTrashedTasks().get(i));
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