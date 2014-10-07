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
		testOneCommand("Add a task", 
					   "task added", 
					   "add @sampleTask");
		
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
