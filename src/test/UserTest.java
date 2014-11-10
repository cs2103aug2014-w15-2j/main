package test;

import infrastructure.Constant;

import java.util.ArrayList;

import model.Task;
import model.TimeInterval;
import model.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {
	
	/**
	 * test for undo method in User
	 * @throws Exception
	 */
	@Test
	//author A0119444E
	public void testUndo() throws Exception {
		User user = new User();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();
		Task task = new Task("testtask", 2, tag, interval);
		ArrayList<Task> normalTasks = user.getOngoingTaskList();
		System.out.println(normalTasks.size());
		
		//test add
		user.add(task);
		user.undo();
		System.out.println(normalTasks.size());
		testUndo("test add", normalTasks, user.getOngoingTaskList());
		
		//test delete
		if (!user.getOngoingTaskList().isEmpty()){
			user.delete(0, Constant.TASK_LIST_TODO);
		}
		user.undo();
		testUndo("test delete", normalTasks, user.getOngoingTaskList());
		
		//test deleteAll
		if (!user.getOngoingTaskList().isEmpty()){
			user.deleteAll(Constant.TASK_LIST_TODO);
		}
		user.undo();
		testUndo("test deleteAll", normalTasks, user.getOngoingTaskList());
		
		System.out.println("all undo tests are passed");
	}
	
//	/**
//	 * test for add method in User
//	 * @throws Exception
//	 */
//	//@author A0119444E
//	@Test
//	public void testAdd() throws Exception {
//		int testSize = 10;
//
//		User user = new User();
//		int initialSize = user.getNormalTaskList().size();
//		System.out.println("user normal task size: " + initialSize);
//
//		ArrayList<String> tag = new ArrayList<String>();
//		TimeInterval interval = new TimeInterval();
//
//		for (int i = 1; i < testSize + 1; i++) {
//			Task task = new Task("task" + i, 2, tag, interval);
//			user.add(task);
//		}
//		int currentSize = user.getNormalTaskList().size();
//		int p = 1;
//		for (int i = currentSize - testSize; i < currentSize - 1; i++) {
//			testAdd("test add for task: " + i, "task" + p++, user, i);
//		}
//		for (int i = currentSize - testSize; i < currentSize; i++) {
//			user.delete(currentSize - testSize, Constant.TASK_LIST_TODO);
//		}
//		System.out.println("all add tests are passed");
//	}
//	
//	/**
//	 * test for delete method in User
//	 * @throws Exception
//	 */
//	@Test
//	//author A0119444E
//	public void testDelete() throws Exception {
//		int testSize = 10;
//
//		User user = new User();
//		ArrayList<String> tag = new ArrayList<String>();
//		TimeInterval interval = new TimeInterval();
//
//		for (int i = 1; i < testSize + 1; i++) {
//			Task task = new Task("task" + i, 2, tag, interval);
//			user.add(task);
//		}
//		int currentSize = user.getNormalTaskList().size();
//		int p = 1;
//		for (int i = currentSize - testSize; i < currentSize; i++) {
//			testDelete("test delete for task: " + i, "task" + p++, user, currentSize - testSize);
//			user.delete(currentSize - testSize, Constant.TASK_LIST_TODO);
//		}
//		System.out.println("all delete tests are passed");		
//	}
//	
//	/**
//	 * test for deleteAll method in User
//	 * @throws Exception
//	 */
//	@Test
//	//author A0119444E
//	public void testDeleteAll() throws Exception {
//		User user = new User();
//		user.deleteAll(Constant.TASK_LIST_TODO);
//		testDeleteAll("test deleteAll method", 0, user.getNormalTaskList().size());
//		user.undo();
//		System.out.println("all deleteAll tests are passed");
//	}
//	
	/**
	 * test method for testing undo method
	 * @param description
	 * @param expected
	 * @param actual
	 */
	//author A0119444E
	private void testUndo(String description, ArrayList<Task> expected, ArrayList<Task> actual) {
		try {
			assertEquals(description, expected, actual);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * test method for testing add method
	 * @param description
	 * @param expected
	 * @param user
	 * @param i
	 */
	//author A0119444E
	private void testAdd(String description, String expected, User user, int i) {
		try {
			assertEquals(description, expected, user.retrieveFromNormalList(i)
					.getDescription());
		} catch (Exception e) {

		}
	}
	
	/**
	 * test method for testing delete method
	 * @param description
	 * @param expected
	 * @param user
	 * @param i
	 */
	//author A0119444E
	private void testDelete(String description, String expected, User user,
			int i) {
		try {
			assertEquals(description, expected, user.retrieveFromNormalList(i)
					.getDescription());
		} catch (Exception e) {

		}
	}
	
	/**
	 * test method for testing deleteAll method
	 * @param description
	 * @param expected
	 * @param actualString
	 */
	//author A0119444E
	private void testDeleteAll(String description, int expected, int actualString) {
		try {
			assertEquals(description, expected, actualString);
		} catch (Exception e) {
			
		}
	}
}
