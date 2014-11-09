//@author A0113029U

package test;

import static org.junit.Assert.*;
import modal.CommandFailedException;
import modal.Task;
import modal.TaskBox;
import modal.TimeInterval;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataStore.DataStore;
import infrastructure.Constant;

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
	
	private static final String FORMAT_OPEN_ARRAY = "[";
	private static final String FORMAT_CLOSE_ARRAY = "]";
	private static final String FORMAT_OPEN_OBJECT = "{";
	private static final String FORMAT_CLOSE_OBJECT = "}";
	private static final String FORMAT_DESCRIPTION = "\"" + Constant.SAVE_DESCRIPTION + "\":\"%s\",";
	private static final String FORMAT_STATUS = "\"" + Constant.SAVE_STATUS + "\":\"%s\",";
	private static final String FORMAT_TAGS = "\"" + Constant.SAVE_TAGS + "\":[";
	private static final String FORMAT_PRIORITY = "\"priority\":\"%s\","; 
	private static final String FORMAT_TIME_INTERVAL = "\"time-interval\":{";
	private static final String FORMAT_STARTDATE = "\"startDate\":\"%s\",";
	private static final String FORMAT_ENDDATE = "\"endDate\":\"%s\"";
	private static final String FORMAT_DATE = "dd-MMMM-yyyy HH:mm";
	
	private static final String MESSAGE_FAILED_TESTING = "failed in testing";
	
	private static final String DATA_FILEPATH = 
								"List-of-Xiao-Ming/task-list.xiaoming";
	private static final String DATA_FILEPATH_TEMP= 
								"List-of-Xiao-Ming/task-list-temp.xiaoming";
	
	static File fileData = new File(DATA_FILEPATH);
	static File fileTemp = new File(DATA_FILEPATH_TEMP);
	
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
	
	@After
	public void setBackOriginalData() {
		fileData.delete();
		fileTemp.renameTo(fileData);
	}
	
	@Test
	public void testSaveNull() {
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
			ArrayList<String> tags = new ArrayList<String>();
			tags.add("tag1");
			tags.add("tag2");
			Date startDate = new SimpleDateFormat(FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 10:00");
			Date endDate = new SimpleDateFormat(FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 12:00");
			TimeInterval time = new TimeInterval(startDate, endDate);
			Task task1 = new Task("task1", Constant.PRIORITY_HIGH, tags, time);
			task1.setStatus(Constant.TASK_STATUS_NORMAL);
			Task task2 = new Task("task2", Constant.PRIORITY_LOW, tags, time);
			task2.setStatus(Constant.TASK_STATUS_DONE);
			Task task3 = new Task("task3", Constant.PRIORITY_MEDIUM, tags, time);
			task3.setStatus(Constant.TASK_STATUS_TRASHED);
			
			TaskBox tasks = new TaskBox();
			tasks.getNormalTasks().add(task1);
			tasks.getFinishedTasks().add(task2);
			tasks.getTrashedTasks().add(task3);
			
			assertTrue(DataStore.save(tasks));
			checkDataFileTC2();
		} catch (ParseException | CommandFailedException | IOException e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}
	
	@Test
	public void testLoadFileNotExist() {
		fileData.delete();
		try {
			TaskBox tasks = new TaskBox();
			assertEquals(tasks.getNormalTasks(), DataStore.loadFileData().getNormalTasks());
		} catch (Exception e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}
	
	@Test
	public void testLoadFile() {
		try {
			writeDataTC1();
			TaskBox tasks = DataStore.loadFileData();
			
			Date startDate = new SimpleDateFormat(FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 10:00");
			Date endDate = new SimpleDateFormat(FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 12:00");
			
			assertEquals("task1", tasks.getNormalTasks().get(0).getDescription());
			assertEquals(Constant.TASK_STATUS_NORMAL, tasks.getNormalTasks().get(0).getStatus());
			assertEquals("tag1", tasks.getNormalTasks().get(0).getTag().get(0));
			assertEquals("tag2", tasks.getNormalTasks().get(0).getTag().get(1));
			assertEquals(Constant.PRIORITY_HIGH, tasks.getNormalTasks().get(0).getPriority());
			assertEquals(startDate, tasks.getNormalTasks().get(0).getInterval().getStartDate());
			assertEquals(endDate, tasks.getNormalTasks().get(0).getInterval().getEndDate());
			
			assertEquals("task2", tasks.getFinishedTasks().get(0).getDescription());
			assertEquals(Constant.TASK_STATUS_DONE, tasks.getFinishedTasks().get(0).getStatus());
			assertEquals("tag1", tasks.getFinishedTasks().get(0).getTag().get(0));
			assertEquals("tag2", tasks.getFinishedTasks().get(0).getTag().get(1));
			assertEquals(Constant.PRIORITY_LOW, tasks.getFinishedTasks().get(0).getPriority());
			assertEquals(startDate, tasks.getFinishedTasks().get(0).getInterval().getStartDate());
			assertEquals(endDate, tasks.getFinishedTasks().get(0).getInterval().getEndDate());
			
			assertEquals("task3", tasks.getTrashedTasks().get(0).getDescription());
			assertEquals(Constant.TASK_STATUS_TRASHED, tasks.getTrashedTasks().get(0).getStatus());
			assertEquals("tag1", tasks.getTrashedTasks().get(0).getTag().get(0));
			assertEquals("tag2", tasks.getTrashedTasks().get(0).getTag().get(1));
			assertEquals(Constant.PRIORITY_MEDIUM, tasks.getTrashedTasks().get(0).getPriority());
			assertEquals(startDate, tasks.getTrashedTasks().get(0).getInterval().getStartDate());
			assertEquals(endDate, tasks.getTrashedTasks().get(0).getInterval().getEndDate());
			
		} catch (Exception e) {
			System.out.println(MESSAGE_FAILED_TESTING);
		}
	}

	private void writeDataTC1() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileData));
		bw.write(FORMAT_OPEN_ARRAY + "\n");
		
		bw.write(FORMAT_OPEN_OBJECT + "\n");
		bw.write(String.format(FORMAT_DESCRIPTION, "task1") + "\n");
		bw.write(String.format(FORMAT_STATUS, Constant.TASK_STATUS_NORMAL) + "\n");
		bw.write(FORMAT_TAGS + "\n");
		bw.write("\"tag1\",\n");
		bw.write("\"tag2\"\n");
		bw.write(FORMAT_CLOSE_ARRAY + ",\n");
		bw.write(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_HIGH) + "\n");
		bw.write(FORMAT_TIME_INTERVAL + "\n");
		bw.write(String.format(FORMAT_STARTDATE, "29-November-2014 10:00") + "\n");
		bw.write(String.format(FORMAT_ENDDATE, "29-November-2014 12:00") + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + ",\n");
		
		bw.write(FORMAT_OPEN_OBJECT + "\n");
		bw.write(String.format(FORMAT_DESCRIPTION, "task2") + "\n");
		bw.write(String.format(FORMAT_STATUS, Constant.TASK_STATUS_DONE) + "\n");
		bw.write(FORMAT_TAGS + "\n");
		bw.write("\"tag1\",\n");
		bw.write("\"tag2\"\n");
		bw.write(FORMAT_CLOSE_ARRAY + ",\n");
		bw.write(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_LOW) + "\n");
		bw.write(FORMAT_TIME_INTERVAL + "\n");
		bw.write(String.format(FORMAT_STARTDATE, "29-November-2014 10:00") + "\n");
		bw.write(String.format(FORMAT_ENDDATE, "29-November-2014 12:00") + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + ",\n");
		
		bw.write(FORMAT_OPEN_OBJECT + "\n");
		bw.write(String.format(FORMAT_DESCRIPTION, "task3") + "\n");
		bw.write(String.format(FORMAT_STATUS, Constant.TASK_STATUS_TRASHED) + "\n");
		bw.write(FORMAT_TAGS +"\n");
		bw.write("\"tag1\",\n");
		bw.write("\"tag2\"\n");
		bw.write(FORMAT_CLOSE_ARRAY + ",\n");
		bw.write(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_MEDIUM) + "\n");
		bw.write(FORMAT_TIME_INTERVAL + "\n");
		bw.write(String.format(FORMAT_STARTDATE, "29-November-2014 10:00") + "\n");
		bw.write(String.format(FORMAT_ENDDATE, "29-November-2014 12:00") + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		bw.write(FORMAT_CLOSE_OBJECT + "\n");
		
		bw.write(FORMAT_CLOSE_ARRAY);
		bw.close();
	}
	
	private void checkDataFileTC2()
			throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileData));
		
		assertEquals(FORMAT_OPEN_ARRAY, br.readLine());
		assertEquals(FORMAT_OPEN_OBJECT, br.readLine().trim());
		assertEquals(String.format(FORMAT_DESCRIPTION, "task1"), br.readLine().trim());
		assertEquals(String.format(FORMAT_STATUS, Constant.TASK_STATUS_NORMAL), br.readLine().trim());
		assertEquals(FORMAT_TAGS, br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY + ",", br.readLine().trim());
		assertEquals(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_HIGH), br.readLine().trim());
		assertEquals(FORMAT_TIME_INTERVAL, br.readLine().trim());
		assertEquals(String.format(FORMAT_STARTDATE, "29-November-2014 10:00"), br.readLine().trim());
		assertEquals(String.format(FORMAT_ENDDATE, "29-November-2014 12:00"), br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT + ",", br.readLine().trim());
		
		assertEquals(FORMAT_OPEN_OBJECT, br.readLine().trim());
		assertEquals(String.format(FORMAT_DESCRIPTION, "task2"), br.readLine().trim());
		assertEquals(String.format(FORMAT_STATUS, Constant.TASK_STATUS_DONE), br.readLine().trim());
		assertEquals(FORMAT_TAGS, br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY + ",", br.readLine().trim());
		assertEquals(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_LOW), br.readLine().trim());
		assertEquals(FORMAT_TIME_INTERVAL, br.readLine().trim());
		assertEquals(String.format(FORMAT_STARTDATE, "29-November-2014 10:00"), br.readLine().trim());
		assertEquals(String.format(FORMAT_ENDDATE, "29-November-2014 12:00"), br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT + ",", br.readLine().trim());

		assertEquals(FORMAT_OPEN_OBJECT, br.readLine().trim());
		assertEquals(String.format(FORMAT_DESCRIPTION, "task3"), br.readLine().trim());
		assertEquals(String.format(FORMAT_STATUS, Constant.TASK_STATUS_TRASHED), br.readLine().trim());
		assertEquals(FORMAT_TAGS, br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY + ",", br.readLine().trim());
		assertEquals(String.format(FORMAT_PRIORITY, Constant.PRIORITY_STRING_MEDIUM), br.readLine().trim());
		assertEquals(FORMAT_TIME_INTERVAL, br.readLine().trim());
		assertEquals(String.format(FORMAT_STARTDATE, "29-November-2014 10:00"), br.readLine().trim());
		assertEquals(String.format(FORMAT_ENDDATE, "29-November-2014 12:00"), br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_OBJECT, br.readLine().trim());
		assertEquals(FORMAT_CLOSE_ARRAY, br.readLine());
		
		br.close();
	}
}
