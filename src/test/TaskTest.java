package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import dataStructure.Task;

public class TaskTest {
	/**
	 * test for task methods
	 * @author linfanshi
	 */
	@Test
	public void test() {		
		ArrayList<String> tag = new ArrayList<String>();
		tag.add("cs2102");
		tag.add("cs2103");
		Task task1 = new Task("task1", "default", 0, 0, tag, null);
		
		testOneCommand("task1 toString", 
					   "cs2102,cs2103", 
					   task1);
		
		System.out.println("all tests are passed");
	}
	

	private void testOneCommand(String description, String expected, Task task) {
		try {
			assertEquals(description, expected, task.tagToString()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
