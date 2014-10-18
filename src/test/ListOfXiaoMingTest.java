package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dataStore.DataStore;
import userInterface.ListOfXiaoMing;

public class ListOfXiaoMingTest {
	
	boolean bool1 = DataStore.destroy("test", "test");
	boolean bool2 = DataStore.createAccount("test", "test");
	ListOfXiaoMing xiaoming = new ListOfXiaoMing("test");
	
	@Test
	public void test() {
		testOneCommand("TC01-Add a task", 
					   "Task added!", 
					   "add @sampleTask");
		
		testOneCommand("TC02a-Undo", 
					   "Undo successfully!", 
					   "undo");
		
		testOneCommand("TC02b-Undo", 
					   "--- no task in the list ---      _(:з」∠)_ ", 
					   "display");
		
		System.out.println("all search tests are passed");
	}
	

	private void testOneCommand(String description, String expected, String command) {
		try {
			assertEquals(description, expected, xiaoming.execute(command)); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
