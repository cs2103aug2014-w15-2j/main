package test;

import static org.junit.Assert.assertEquals;
import infrastructure.Constant;

import java.util.ArrayList;
import java.util.Date;

import model.CommandFailedException;
import model.Task;
import model.TimeInterval;

import org.junit.Test;

public class TaskTest {
	
	/**
	 * test for tagToString method in Task
	 */
	//@author A0119444E
	@Test
	public void testTag() {		
		ArrayList<String> tag = new ArrayList<String>();
		tag.add("cs2102");
		tag.add("cs2103");
		Task task1 = new Task("task1", 0, tag, null);
		
		testTagToString("task1 toString", 
					   "cs2102,cs2103", 
					   task1);
		
		System.out.println("all tag tests are passed");
	}
	
	/**
	 * test for status methods in Task
	 */
	//@author A0119444E
	@Test
	public void testStatus() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();
		
		for (int i = 0; i < 10; i++) {
			Task task = new Task("task" + i, 2, tag, interval);
			if (i % 3 == 0){
				task.setStatus(Constant.TASK_STATUS_DONE);
			} else if (i % 3 == 1) {
				task.setStatus(Constant.TASK_STATUS_ONGOING);
			} else {
				task.setStatus(Constant.TASK_STATUS_TRASHED);
			}
			tasks.add(task);
		}
		
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0){
				testStatus("test task" + i, Constant.TASK_STATUS_DONE, tasks.get(i));
			} else if (i % 3 == 1) {
				testStatus("test task" + i, Constant.TASK_STATUS_ONGOING, tasks.get(i));
			} else {
				testStatus("test task" + i, Constant.TASK_STATUS_TRASHED, tasks.get(i));
			}
		}	
		
		System.out.println("all status tests are passed");
	}
	
	//@author A0119447Y
	/**
	 * test for interval method in Task
	 */
	@Test
	public void testInterval() {
		try {
			TimeInterval interval = new TimeInterval(new Date(34826), new Date());
			Task task1 = new Task("task1", 0, null, interval);
			
			testInterval("task1 interval", 
						   interval, 
						   task1);
			
			System.out.println("all interval tests are passed");
		} catch (CommandFailedException e) {
			e.printStackTrace();
		}
		
	}
	
	//@author A0119444E
	/**
	 * test method for tagToString
	 * @param description
	 * @param expected
	 * @param task
	 */
	private void testTagToString(String description, String expected, Task task) {
		try {
			assertEquals(description, expected, task.tagToString()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@author A0119444E
	/**
	 * test method for status
	 * @param description
	 * @param expected
	 * @param task
	 */
	private void testStatus(String description, String expected, Task task) {
		try {
			assertEquals(description, expected, task.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@author A0119447Y
	private void testInterval(String description, TimeInterval expected, Task task) {
		try {
			assertEquals(description, expected, task.getInterval());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
