package test;

import java.util.ArrayList;

import modal.Task;
import modal.TimeInterval;
import modal.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

	// @author A0119444E

	@Test
	public void testAdd() throws Exception {
		int testSize = 10;

		User user = new User();
		int initialSize = user.getNormalTaskList().size();
		System.out.println("user normal task size: " + initialSize);

		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();

		for (int i = 1; i < testSize + 1; i++) {
			Task task = new Task("task" + i, 2, tag, interval);
			user.add(task);
		}
		int currentSize = user.getNormalTaskList().size();
		int p = 1;
		for (int i = currentSize - testSize; i < currentSize - 1; i++) {
			testAdd("test add for task: " + i, "task" + p++, user, i);
		}
		for (int i = currentSize - testSize; i < currentSize; i++) {
			user.delete(currentSize - testSize, true);
		}
	}
	
	@Test
	
	public void testDelete() throws Exception {
		int testSize = 10;

		User user = new User();
		int initialSize = user.getNormalTaskList().size();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();

		for (int i = 1; i < testSize + 1; i++) {
			Task task = new Task("task" + i, 2, tag, interval);
			user.add(task);
		}
		int currentSize = user.getNormalTaskList().size();
		int p = 1;
		for (int i = currentSize - testSize; i < currentSize; i++) {
			testDelete("test delete for task: " + i, "task" + p++, user, currentSize - testSize);
			user.delete(currentSize - testSize, true);
		}
		System.out.println("all delete tests are passed");		
	}
	
	private void testAdd(String description, String expected, User user, int i) {
		try {
			assertEquals(description, expected, user.retrieveFromNormalList(i)
					.getDescription());
		} catch (Exception e) {

		}
	}

	private void testDelete(String description, String expected, User user,
			int i) {
		try {
			assertEquals(description, expected, user.retrieveFromNormalList(i)
					.getDescription());
		} catch (Exception e) {

		}
	}
}
