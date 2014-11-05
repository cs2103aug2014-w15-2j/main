//@author A0113029U

package dataStore;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import reference.CommandFailedException;
import reference.TaskBox;
import reference.TimeInterval;
import dataStructure.Task;
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
			System.out.println("failed to create a test-file");
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
			assertEquals("[", br.readLine().trim());
			br.readLine();
			assertEquals("]", br.readLine().trim());
			br.close();
		} catch (IOException e) {
			System.out.println("failed in generating the test-save-file");
		}
	}
	
	@Test
	public void testSave() {
		try {
			ArrayList<String> tags = new ArrayList<String>();
			tags.add("tag1");
			tags.add("tag2");
			Date startDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
					Locale.ENGLISH).parse("29-Nov-2014 10:00");
			Date endDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
					Locale.ENGLISH).parse("29-Nov-2014 12:00");
			TimeInterval time = new TimeInterval(startDate, endDate);
			Task task1 = new Task("task1", Constant.PRIORITY_HIGH, tags, time);
			task1.setStatus("normal");
			Task task2 = new Task("task2", Constant.PRIORITY_LOW, tags, time);
			task2.setStatus("done");
			Task task3 = new Task("task3", Constant.PRIORITY_MEDIUM, tags, time);
			task3.setStatus("trashed");
			
			TaskBox tasks = new TaskBox();
			tasks.getNormalTasks().add(task1);
			tasks.getFinishedTasks().add(task2);
			tasks.getTrashedTasks().add(task3);
			
			assertTrue(DataStore.save(tasks));
			checkDataFileTC2();
		} catch (ParseException | CommandFailedException | IOException e) {
			System.out.println("failed in testing");
		}
	}

	@Test
	public void testLoadFileNotExist() {
		fileData.delete();
		try {
			TaskBox tasks = new TaskBox();
			assertEquals(tasks.getNormalTasks(), DataStore.loadFileData().getNormalTasks());
		} catch (Exception e) {
			System.out.println("failed in loading data");
		}
	}
	
	@Test
	public void testLoadFile() {
		try {
			writeDataTC1();
			TaskBox tasks = DataStore.loadFileData();
			
			Date startDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
					Locale.ENGLISH).parse("29-Nov-2014 10:00");
			Date endDate = new SimpleDateFormat("dd-MMMM-yyyy HH:mm",
					Locale.ENGLISH).parse("29-Nov-2014 12:00");
			
			assertEquals("task1", tasks.getNormalTasks().get(0).getDescription());
			assertEquals(Constant.TASK_STATUS.NORMAL, tasks.getNormalTasks().get(0).getStatus());
			assertEquals("tag1", tasks.getNormalTasks().get(0).getTag().get(0));
			assertEquals("tag2", tasks.getNormalTasks().get(0).getTag().get(1));
			assertEquals(Constant.PRIORITY_HIGH, tasks.getNormalTasks().get(0).getPriority());
			assertEquals(startDate, tasks.getNormalTasks().get(0).getInterval().getStartDate());
			assertEquals(endDate, tasks.getNormalTasks().get(0).getInterval().getEndDate());
			
			assertEquals("task2", tasks.getFinishedTasks().get(0).getDescription());
			assertEquals(Constant.TASK_STATUS.DONE, tasks.getFinishedTasks().get(0).getStatus());
			assertEquals("tag1", tasks.getFinishedTasks().get(0).getTag().get(0));
			assertEquals("tag2", tasks.getFinishedTasks().get(0).getTag().get(1));
			assertEquals(Constant.PRIORITY_LOW, tasks.getFinishedTasks().get(0).getPriority());
			assertEquals(startDate, tasks.getFinishedTasks().get(0).getInterval().getStartDate());
			assertEquals(endDate, tasks.getFinishedTasks().get(0).getInterval().getEndDate());
			
			assertEquals("task3", tasks.getTrashedTasks().get(0).getDescription());
			assertEquals(Constant.TASK_STATUS.TRASHED, tasks.getTrashedTasks().get(0).getStatus());
			assertEquals("tag1", tasks.getTrashedTasks().get(0).getTag().get(0));
			assertEquals("tag2", tasks.getTrashedTasks().get(0).getTag().get(1));
			assertEquals(Constant.PRIORITY_MEDIUM, tasks.getTrashedTasks().get(0).getPriority());
			assertEquals(startDate, tasks.getTrashedTasks().get(0).getInterval().getStartDate());
			assertEquals(endDate, tasks.getTrashedTasks().get(0).getInterval().getEndDate());
			
		} catch (Exception e) {
			System.out.println("failed in testing");
		}
	}

	private void writeDataTC1() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileData));
		bw.write("[");
		bw.newLine();
		bw.write("{");
		bw.newLine();
		bw.write("\"description\":\"task1\",");
		bw.newLine();
		bw.write("\"status\":\"normal\",");
		bw.newLine();
		bw.write("\"tags\":[");
		bw.newLine();
		bw.write("\"tag1\",");
		bw.newLine();
		bw.write("\"tag2\"");
		bw.newLine();
		bw.write("],");
		bw.newLine();
		bw.write("\"priority\":\"high\",");
		bw.newLine();
		bw.write("\"time-interval\":{");
		bw.newLine();
		bw.write("\"startDate\":\"29-November-2014 10:00\",");
		bw.newLine();
		bw.write("\"endDate\":\"29-November-2014 12:00\"");
		bw.newLine();
		bw.write("}");
		bw.newLine();
		bw.write("},");
		bw.newLine();
		
		bw.write("{");
		bw.newLine();
		bw.write("\"description\":\"task2\",");
		bw.newLine();
		bw.write("\"status\":\"done\",");
		bw.newLine();
		bw.write("\"tags\":[");
		bw.newLine();
		bw.write("\"tag1\",");
		bw.newLine();
		bw.write("\"tag2\"");
		bw.newLine();
		bw.write("],");
		bw.newLine();
		bw.write("\"priority\":\"low\",");
		bw.newLine();
		bw.write("\"time-interval\":{");
		bw.newLine();
		bw.write("\"startDate\":\"29-November-2014 10:00\",");
		bw.newLine();
		bw.write("\"endDate\":\"29-November-2014 12:00\"");
		bw.newLine();
		bw.write("}");
		bw.newLine();
		bw.write("},");
		bw.newLine();
		
		bw.write("{");
		bw.newLine();
		bw.write("\"description\":\"task3\",");
		bw.newLine();
		bw.write("\"status\":\"trashed\",");
		bw.newLine();
		bw.write("\"tags\":[");
		bw.newLine();
		bw.write("\"tag1\",");
		bw.newLine();
		bw.write("\"tag2\"");
		bw.newLine();
		bw.write("],");
		bw.newLine();
		bw.write("\"priority\":\"medium\",");
		bw.newLine();
		bw.write("\"time-interval\":{");
		bw.newLine();
		bw.write("\"startDate\":\"29-November-2014 10:00\",");
		bw.newLine();
		bw.write("\"endDate\":\"29-November-2014 12:00\"");
		bw.newLine();
		bw.write("}");
		bw.newLine();
		bw.write("}");
		bw.newLine();
		
		bw.write("]");
		bw.close();
	}
	
	private void checkDataFileTC2()
			throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileData));
		assertEquals("[", br.readLine());
		assertEquals("{", br.readLine().trim());
		assertEquals("\"description\":\"task1\",", br.readLine().trim());
		assertEquals("\"status\":NORMAL,", br.readLine().trim());
		assertEquals("\"tags\":[", br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals("],", br.readLine().trim());
		assertEquals("\"priority\":\"high\",", br.readLine().trim());
		assertEquals("\"time-interval\":{", br.readLine().trim());
		assertEquals("\"startDate\":\"29-November-2014 10:00\",", br.readLine().trim());
		assertEquals("\"endDate\":\"29-November-2014 12:00\"", br.readLine().trim());
		assertEquals("}", br.readLine().trim());
		assertEquals("},", br.readLine().trim());
		
		assertEquals("{", br.readLine().trim());
		assertEquals("\"description\":\"task2\",", br.readLine().trim());
		assertEquals("\"status\":DONE,", br.readLine().trim());
		assertEquals("\"tags\":[", br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals("],", br.readLine().trim());
		assertEquals("\"priority\":\"low\",", br.readLine().trim());
		assertEquals("\"time-interval\":{", br.readLine().trim());
		assertEquals("\"startDate\":\"29-November-2014 10:00\",", br.readLine().trim());
		assertEquals("\"endDate\":\"29-November-2014 12:00\"", br.readLine().trim());
		assertEquals("}", br.readLine().trim());
		assertEquals("},", br.readLine().trim());

		assertEquals("{", br.readLine().trim());
		assertEquals("\"description\":\"task3\",", br.readLine().trim());
		assertEquals("\"status\":TRASHED,", br.readLine().trim());
		assertEquals("\"tags\":[", br.readLine().trim());
		assertEquals("\"tag1\",", br.readLine().trim());
		assertEquals("\"tag2\"", br.readLine().trim());
		assertEquals("],", br.readLine().trim());
		assertEquals("\"priority\":\"medium\",", br.readLine().trim());
		assertEquals("\"time-interval\":{", br.readLine().trim());
		assertEquals("\"startDate\":\"29-November-2014 10:00\",", br.readLine().trim());
		assertEquals("\"endDate\":\"29-November-2014 12:00\"", br.readLine().trim());
		assertEquals("}", br.readLine().trim());
		assertEquals("}", br.readLine().trim());
		assertEquals("]", br.readLine());
		br.close();
	}
}
