package test;

import static org.junit.Assert.assertEquals;
import infrastructure.Constant;

import java.util.ArrayList;

import modal.Task;
import modal.TimeInterval;

import org.junit.Test;

public class TaskTest {
	/**
	 * 
	 * test for task methods
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
				task.setStatus(Constant.TASK_STATUS_NORMAL);
			} else {
				task.setStatus(Constant.TASK_STATUS_TRASHED);
			}
			tasks.add(task);
		}
		
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0){
				testStatus("test task" + i, Constant.TASK_STATUS_DONE, tasks.get(i));
			} else if (i % 3 == 1) {
				testStatus("test task" + i, Constant.TASK_STATUS_NORMAL, tasks.get(i));
			} else {
				testStatus("test task" + i, Constant.TASK_STATUS_TRASHED, tasks.get(i));
			}
		}	
		
		System.out.println("all status tests are passed");
	}	

	private void testTagToString(String description, String expected, Task task) {
		try {
			assertEquals(description, expected, task.tagToString()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testStatus(String description, String expected, Task task) {
		try {
			assertEquals(description, expected, task.getStatus());
		} catch (Exception e) {
			
		}
	}
}
