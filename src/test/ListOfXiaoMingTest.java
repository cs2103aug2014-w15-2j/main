package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dataStore.DataStore;
import userInterface.ListOfXiaoMing;
import infrastructure.*;

public class ListOfXiaoMingTest {
	
//	boolean bool1 = DataStore.destroy("test", "test");
//	boolean bool2 = DataStore.createAccount("test", "test");
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
	
//	public void testAdd() {
//		testOneCommand("TO-01-1 add a task", Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY, "add attend cs2103 lecture");
//		testOneCommand("", "1. attend cs2103 lecture\n\t priority: medium;\n\t no specific time requirements;", "display");
//		
//		testOneCommand("TO-01-2 add a task", Constant.PROMPT_MESSAGE_ADD_TASK_SUCCESSFULLY, "add attend cs2103 tutorial today, 3pm to 4pm");
//		testOneCommand("", "1. attend cs2103 lecture\n\t priority: medium;\n\t no specific time requirements;\n\n2. attend cs2103 tutorial\n\t priority: medium;\n\t from 29/October/2014 15:00 to 29/October/2014 16:00;", "display");
//
//		testOneCommand("TO-02-1 search with time interval", "1. attend cs2103 tutorial\n\t priority: medium;\n\t from 29/October/2014 15:00 to 29/October/2014 16:00;", "search this week");
//	}
	
	private void testOneCommand(String description, String expected, String command) {
		try {

			assertEquals(description, expected, xiaoming.executeNLP(command)); 

			String result = xiaoming.executeNLP(command);
			System.out.println(description);
			System.out.println("INPUT:\t" + command);
			System.out.println("EXPECT:\t" + expected);
			System.out.println("RESULT:\t" + result);
			assertEquals(expected, result); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
