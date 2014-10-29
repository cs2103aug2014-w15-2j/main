package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dataStore.DataStore;
import userInterface.ListOfXiaoMing;
import infrastructure.*;

public class ListOfXiaoMingTest {
	
	boolean bool1 = DataStore.destroy("test", "test");
	boolean bool2 = DataStore.createAccount("test", "test");
	ListOfXiaoMing xiaoming = new ListOfXiaoMing("test");
	
	@Test
	public void test() {		
		testOneCommand("TC01-Add a task", 
					   Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY, 
					   "add sampleTask");
		
		testOneCommand("TC02a-Undo", 
					   Constant.PROMPT_MESSAGE_UNDO_SUCCESSFULLY, 
					   "undo");
		
		testOneCommand("TC02b-Undo", 
					   Constant.PROMPT_MESSAGE_DISPLAY_EMPTY_TASK,
					   "display");
		
		testOneCommand("TC03a-Add a timed task",
					   Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY, 
				   	   "add sampleTask today");
		
		testOneCommand("TC03b-Search timed task",
					   "1. sampleTask\n\t priority: medium;\n\t no specific time requirements;",
					   "search from yesterday to tomorrow");
		
		System.out.println("all search tests are passed");
	}
	

	private void testOneCommand(String description, String expected, String command) {
		try {
			assertEquals(description, expected, xiaoming.executeNLP(command)); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
