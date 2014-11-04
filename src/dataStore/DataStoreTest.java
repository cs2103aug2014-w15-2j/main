package dataStore;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataStructure.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
	public void testLoadFileNotExist() {
		fileData.delete();
		try {
			ArrayList<Task> tasks = new ArrayList<Task>();
			assertEquals(tasks, DataStore.loadFileData());
		} catch (Exception e) {
			System.out.println("failed in loading data");
		}
	}
	
}
