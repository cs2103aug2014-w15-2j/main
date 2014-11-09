//@author A0113029U

package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import model.CommandFailedException;
import model.Task;
import model.TaskBox;
import model.TimeInterval;
import dataStore.DataStore;
import infrastructure.Constant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DataStoreTest {
	
	//Formatting in the saved file
	private static final String FORMAT_OPEN_ARRAY = "[";
	private static final String FORMAT_CLOSE_ARRAY = "]";
	private static final String FORMAT_OPEN_OBJECT = "{";
	private static final String FORMAT_CLOSE_OBJECT = "}";
	private static final String FORMAT_DESCRIPTION = "\"" +
								Constant.SAVE_DESCRIPTION + "\":\"%s\",";
	private static final String FORMAT_STATUS = "\"" +
								Constant.SAVE_STATUS + "\":\"%s\",";
	private static final String FORMAT_TAGS = "\"" +
								Constant.SAVE_TAGS + "\":[";
	private static final String FORMAT_PRIORITY = "\"" +
								Constant.SAVE_PRIORITY + "\":\"%s\","; 
	private static final String FORMAT_TIME_INTERVAL = "\"" +
								Constant.SAVE_TIME_INTERVAL + "\":{";
	private static final String FORMAT_STARTDATE = "\"" +
								Constant.SAVE_STARTDATE + "\":\"%s\",";
	private static final String FORMAT_ENDDATE = "\"" +
								Constant.SAVE_ENDDATE + "\":\"%s\"";
	
	//error message
	private static final String MESSAGE_FAILED_TESTING = "failed in testing";
	
	//file path and data for data-testing and original data
	private static final String DATA_FILEPATH = 
								"List-of-Xiao-Ming/task-list.xiaoming";
	private static final String DATA_FILEPATH_TEMP= 
								"List-of-Xiao-Ming/task-list-temp.xiaoming";
	
	static File fileData = new File(DATA_FILEPATH);
	static File fileTemp = new File(DATA_FILEPATH_TEMP);
	
	/**
	 * move the original data to temporary file
	 * create a new file for testing
	 */
	@Before
	public void setTestEnvironment() { 
		if(fileData.exists()) {
			fileData.renameTo(fileTemp);
		}
		try {
			fileData.createNewFile();
		} catch (IOException e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}
	
	/**
	 * delete testing file
	 * move back the original data
	 */
	@After
	public void setBackOriginalData() {
		fileData.delete();
		fileTemp.renameTo(fileData);
	}
	
	/**
	 * test save an empty tasks-list
	 */
	@Test
	public void testSaveNull() {
		//failed saving if file does not exist
		if(fileData.exists()) {
			fileData.delete();
		}
		assertFalse(DataStore.save(null));
		
		try {
			fileData.createNewFile();
			assertTrue(DataStore.save(null));
			
			BufferedReader br = new BufferedReader(new FileReader(fileData));
			assertEquals(FORMAT_OPEN_ARRAY, br.readLine().trim());
			br.readLine();
			assertEquals(FORMAT_CLOSE_ARRAY, br.readLine().trim());
			br.close();
		} catch (IOException e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}

	@Test
	public void testSave() {
		try {
			TaskBox tasks = getTasksTC1();
			assertTrue(DataStore.save(tasks));
			checkDataFileTC1();
		} catch (ParseException | CommandFailedException | IOException e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}

	@Test
	public void testLoadFileNotExist() {
		fileData.delete();
		try {
			TaskBox tasks = new TaskBox();
			assertEquals(tasks, DataStore.loadFileData());
		} catch (Exception e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}

	@Test
	public void testLoadFile() {
		try {
			writeDataFileTC1();
			assertEquals(getTasksTC1(), DataStore.loadFileData());
		} catch (Exception e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}
	
	private TaskBox getTasksTC1() throws ParseException,
										CommandFailedException {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("tag1");
		tags.add("tag2");
		Date startDate = new SimpleDateFormat(Constant.FORMAT_DATE,
				Locale.ENGLISH).parse("29-Nov-2014 10:00");
		Date endDate = new SimpleDateFormat(Constant.FORMAT_DATE,
				Locale.ENGLISH).parse("29-Nov-2014 12:00");
		
		//priority - high, period task, status - normal
		Task task1 = new Task("task1", Constant.PRIORITY_HIGH, tags,
							new TimeInterval(startDate, endDate));
		task1.setStatus(Constant.TASK_STATUS_NORMAL);
		
		//priority - low, deadline task, status - done 
		Task task2 = new Task("task2", Constant.PRIORITY_LOW, tags,
							new TimeInterval(null, endDate));
		task2.setStatus(Constant.TASK_STATUS_DONE);
		
		//priority - medium, floating task, status - trashed
		Task task3 = new Task("task3", Constant.PRIORITY_MEDIUM,
							tags, new TimeInterval(null, null));
		task3.setStatus(Constant.TASK_STATUS_TRASHED);
		
		TaskBox tasks = new TaskBox();
		tasks.getNormalTasks().add(task1);
		tasks.getFinishedTasks().add(task2);
		tasks.getTrashedTasks().add(task3);
		return tasks;
	}
	
	private void writeDataFileTC1() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileData));
		bw.write(FORMAT_OPEN_ARRAY + "\n");
		
		//priority - high, period task, status - normal
		bw.write(FORMAT_OPEN_OBJECT + "\n");
		bw.write(String.format(FORMAT_DESCRIPTION, "task1") + "\n");
		bw.write(String.format(FORMAT_STATUS, Constant.TASK_STATUS_NORMAL)
				+ "\n");
		bw.write(FORMAT_TAGS + "\n");
		bw.write("\"tag1\",\n");
		bw.write("\"tag2\"\n");
		bw.write(FORMAT_CLOSE_ARRAY + ",\n");
		bw.write(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_HIGH)
				+ "\n");
		bw.write(FORMAT_TIME_INTERVAL + "\n");
		bw.write(String.format(FORMAT_STARTDATE, "29-November-2014 10:00")
				+ "\n");
		bw.write(String.format(FORMAT_ENDDATE, "29-November-2014 12:00")
				+ "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + ",\n");
		
		//priority - low, deadline task, status - done 
		bw.write(FORMAT_OPEN_OBJECT + "\n");
		bw.write(String.format(FORMAT_DESCRIPTION, "task2") + "\n");
		bw.write(String.format(FORMAT_STATUS, Constant.TASK_STATUS_DONE)
				+ "\n");
		bw.write(FORMAT_TAGS + "\n");
		bw.write("\"tag1\",\n");
		bw.write("\"tag2\"\n");
		bw.write(FORMAT_CLOSE_ARRAY + ",\n");
		bw.write(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_LOW)
				+ "\n");
		bw.write(FORMAT_TIME_INTERVAL + "\n");
		bw.write(String.format(FORMAT_STARTDATE, Constant.SAVE_FORMAT_NO_DATE)
				+ "\n");
		bw.write(String.format(FORMAT_ENDDATE, "29-November-2014 12:00")
				+ "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + ",\n");
		
		//priority - medium, floating task, status - trashed
		bw.write(FORMAT_OPEN_OBJECT + "\n");
		bw.write(String.format(FORMAT_DESCRIPTION, "task3") + "\n");
		bw.write(String.format(FORMAT_STATUS, Constant.TASK_STATUS_TRASHED)
				+ "\n");
		bw.write(FORMAT_TAGS +"\n");
		bw.write("\"tag1\",\n");
		bw.write("\"tag2\"\n");
		bw.write(FORMAT_CLOSE_ARRAY + ",\n");
		bw.write(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_MEDIUM)
				+ "\n");
		bw.write(FORMAT_TIME_INTERVAL + "\n");
		bw.write(String.format(FORMAT_STARTDATE, Constant.SAVE_FORMAT_NO_DATE)
				+ "\n");
		bw.write(String.format(FORMAT_ENDDATE, Constant.SAVE_FORMAT_NO_DATE)
				+ "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		
		bw.write(FORMAT_CLOSE_ARRAY);
		bw.close();
	}
	
	private void checkDataFileTC1()
			throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileData));
		
		//priority - high, period task, status - normal
		assertEquals(FORMAT_OPEN_ARRAY, br.readLine());
		assertEquals(FORMAT_OPEN_OBJECT, br.readLine().trim());
		assertEquals(String.format(FORMAT_DESCRIPTION, "task1"),
					br.readLine().trim());
		assertEquals(String.format(FORMAT_STATUS, Constant.TASK_STATUS_NORMAL),
					br.readLine().trim());
		assertEquals(FORMAT_TAGS, br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY + ",", br.readLine().trim());
		assertEquals(String.format(FORMAT_PRIORITY,
					Constant.PRIORITY_STRING_HIGH), br.readLine().trim());
		assertEquals(FORMAT_TIME_INTERVAL, br.readLine().trim());
		assertEquals(String.format(FORMAT_STARTDATE, "29-November-2014 10:00"),
					br.readLine().trim());
		assertEquals(String.format(FORMAT_ENDDATE, "29-November-2014 12:00"),
					br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT + ",", br.readLine().trim());
		
		//priority - low, deadline task, status - done 
		assertEquals(FORMAT_OPEN_OBJECT, br.readLine().trim());
		assertEquals(String.format(FORMAT_DESCRIPTION, "task2"),
					br.readLine().trim());
		assertEquals(String.format(FORMAT_STATUS, Constant.TASK_STATUS_DONE),
					br.readLine().trim());
		assertEquals(FORMAT_TAGS, br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY + ",", br.readLine().trim());
		assertEquals(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_LOW),
					br.readLine().trim());
		assertEquals(FORMAT_TIME_INTERVAL, br.readLine().trim());
		assertEquals(String.format(FORMAT_STARTDATE, Constant.SAVE_FORMAT_NO_DATE),
					br.readLine().trim());
		assertEquals(String.format(FORMAT_ENDDATE, "29-November-2014 12:00"),
					br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT + ",", br.readLine().trim());

		//priority - medium, floating task, status - trashed
		assertEquals(FORMAT_OPEN_OBJECT, br.readLine().trim());
		assertEquals(String.format(FORMAT_DESCRIPTION, "task3"),
					br.readLine().trim());
		assertEquals(String.format(FORMAT_STATUS, Constant.TASK_STATUS_TRASHED),
					br.readLine().trim());
		assertEquals(FORMAT_TAGS, br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY + ",", br.readLine().trim());
		assertEquals(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_MEDIUM),
					br.readLine().trim());
		assertEquals(FORMAT_TIME_INTERVAL, br.readLine().trim());
		assertEquals(String.format(FORMAT_STARTDATE, Constant.SAVE_FORMAT_NO_DATE),
					br.readLine().trim());
		assertEquals(String.format(FORMAT_ENDDATE, Constant.SAVE_FORMAT_NO_DATE),
					br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY, br.readLine());
		
		br.close();
	}
}
