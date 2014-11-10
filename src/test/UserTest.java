package test;

import infrastructure.Constant;

import java.util.ArrayList;

import model.Task;
import model.TimeInterval;
import model.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

	//@author A0119444E
	/**
	 * test for redo method in User
	 * @throws Exception
	 */
	@Test
	public void testRedo() throws Exception {
		User user = new User();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();
		Task task = new Task("testtask", 2, tag, interval);
		ArrayList<Task> normalTasks;
		
		//test add
		user.add(task);
		normalTasks = user.getOngoingTaskList();
		user.undo();
		user.redo();
		testRedo("test add", normalTasks, user.getOngoingTaskList());
		user.undo();
		
		//test delete
		if (!user.getOngoingTaskList().isEmpty()){
			user.delete(0, Constant.TASK_LIST_ONGOING);
			normalTasks = user.getOngoingTaskList();
			user.undo();
			user.redo();
			testRedo("test delete", normalTasks, user.getOngoingTaskList());
			user.undo();
		} else {
			user.add(task);
			user.delete(0, Constant.TASK_LIST_ONGOING);
			normalTasks = user.getOngoingTaskList();
			user.undo();
			user.redo();
			testRedo("test delete", normalTasks, user.getOngoingTaskList());
			user.undo();
			user.undo();
		}
		
		//test deleteAll
		if (!user.getOngoingTaskList().isEmpty()){
			user.delete(0, Constant.TASK_LIST_ONGOING);
			normalTasks = user.getOngoingTaskList();
			user.undo();
			user.redo();
			testRedo("test delete", normalTasks, user.getOngoingTaskList());
			user.undo();
		} else {
			user.add(task);
			user.add(task);
			user.delete(0, Constant.TASK_LIST_ONGOING);
			normalTasks = user.getOngoingTaskList();
			user.undo();
			user.redo();
			testRedo("test delete", normalTasks, user.getOngoingTaskList());
			user.undo();
			user.undo();
			user.undo();
		}
		System.out.println("all redo tests are passed");
	}

	//@author A0119444E
	/**
	 * test for undo method in User
	 * @throws Exception
	 */
	@Test
	public void testUndo() throws Exception {
		User user = new User();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();
		Task task = new Task("testtask", 2, tag, interval);
		ArrayList<Task> normalTasks = user.getOngoingTaskList();
		
		//test add
		user.add(task);
		user.undo();
		testUndo("test add", normalTasks, user.getOngoingTaskList());
		
		//test delete
		if (!user.getOngoingTaskList().isEmpty()){
			user.delete(0, Constant.TASK_LIST_ONGOING);
			user.undo();
			testUndo("test delete", normalTasks, user.getOngoingTaskList());
		} else {
			user.add(task);
			normalTasks = user.getOngoingTaskList();
			user.delete(0, Constant.TASK_LIST_ONGOING);
			user.undo();
			testUndo("test delete", normalTasks, user.getOngoingTaskList());
			user.undo();
		}	
		
		//test deleteAll
		if (!user.getOngoingTaskList().isEmpty()){
			user.deleteAll(Constant.TASK_LIST_ONGOING);
			user.undo();
			testUndo("test deleteAll", normalTasks, user.getOngoingTaskList());
		} else {
			user.add(task);
			user.add(task);
			user.add(task);
			normalTasks = user.getOngoingTaskList();
			user.deleteAll(Constant.TASK_LIST_ONGOING);
			user.undo();
			testUndo("test deleteAll", normalTasks, user.getOngoingTaskList());
			user.undo();
			user.undo();
			user.undo();
		}		
		
		System.out.println("all undo tests are passed");
	}
	
	//@author A0119444E
	/**
	 * test for add method in User
	 * @throws Exception
	 */
	@Test
	public void testAdd() throws Exception {
		int testSize = 10;

		User user = new User();
		int initialSize = user.getOngoingTaskList().size();
		System.out.println("user normal task size: " + initialSize);

		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();

		for (int i = 1; i < testSize + 1; i++) {
			Task task = new Task("task" + i, 2, tag, interval);
			user.add(task);
		}
		int currentSize = user.getOngoingTaskList().size();
		int p = 1;
		for (int i = currentSize - testSize; i < currentSize - 1; i++) {
			testAdd("test add for task: " + i, "task" + p++, user, i);
		}
		for (int i = currentSize - testSize; i < currentSize; i++) {
			user.delete(currentSize - testSize, Constant.TASK_LIST_ONGOING);
		}
		System.out.println("all add tests are passed");
	}
	
	//@author A0119444E
	/**
	 * test for delete method in User
	 * @throws Exception
	 */
	@Test	
	public void testDelete() throws Exception {
		int testSize = 10;

		User user = new User();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();

		for (int i = 1; i < testSize + 1; i++) {
			Task task = new Task("task" + i, 2, tag, interval);
			user.add(task);
		}
		int currentSize = user.getOngoingTaskList().size();
		int p = 1;
		for (int i = currentSize - testSize; i < currentSize; i++) {
			testDelete("test delete for task: " + i, "task" + p++, user, currentSize - testSize);
			user.delete(currentSize - testSize, Constant.TASK_LIST_ONGOING);
		}
		System.out.println("all delete tests are passed");		
	}
	
	/**
	 * test for deleteAll method in User
	 * @throws Exception
	 */
	@Test
	//@author A0119444E
	public void testDeleteAll() throws Exception {
		User user = new User();
		user.deleteAll(Constant.TASK_LIST_ONGOING);
		testDeleteAll("test deleteAll method", 0, user.getOngoingTaskList().size());
		user.undo();
		System.out.println("all deleteAll tests are passed");
	}

	//@author A0119444E
	/**
	 * test for putBack method in User
	 * @throws Exception
	 */
	@Test
	public void testPutBack() throws Exception {
		User user = new User();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();
		Task task = new Task("testtask", 2, tag, interval);
		
		ArrayList<Task> tasks = user.getOngoingTaskList();
		if (!tasks.isEmpty()){
			user.delete(0, Constant.TASK_LIST_ONGOING);
			user.putBack(user.getTrashedTaskList().size() - 1);
			testPutBack("test putBack", tasks, user.getOngoingTaskList());
			user.undo();
		} else {
			user.add(task);
			tasks = user.getOngoingTaskList();
			user.delete(0, Constant.TASK_LIST_ONGOING);
			user.putBack(0);
			testPutBack("test putBack", tasks, user.getOngoingTaskList());
			user.undo();
		}
		System.out.println("all putBack tests are passed");
	}

	//@author A0119444E
	/**
	 * test for done method in User
	 * @throws Exception
	 */
	@Test
	public void testDone() throws Exception {
		User user = new User();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();
		Task task = new Task("testtask", 2, tag, interval);
		ArrayList<Task> tasks = user.getOngoingTaskList();
		
		if (!tasks.isEmpty()) {
			String description = user.getOngoingTaskList().get(0).getDescription();
			user.done(0);
			testDone("test done", description, user.getFinishedTaskList().get(user.getFinishedTaskList().size() - 1).getDescription());
			user.undo();
		} else {
			user.add(task);
			String description = user.getOngoingTaskList().get(0).getDescription();
			user.done(0);
			testDone("test done", description, user.getFinishedTaskList().get(user.getFinishedTaskList().size() - 1).getDescription());
			user.undo();
			user.undo();
		}
		System.out.println("all done tests are passed");
	}
	
	//@author A0119447Y
	@Test
	public void testUndone() throws Exception {
		User user = new User();
		ArrayList<String> tag = new ArrayList<String>();
		TimeInterval interval = new TimeInterval();
		Task task = new Task("another test task", 2, tag, interval);
		
		ArrayList<Task> tasks = user.getOngoingTaskList();
		if (!tasks.isEmpty()){
			user.done(0);
			user.unDone(user.getFinishedTaskList().size() - 1);
			testUndone("test undone", tasks, user.getOngoingTaskList());
		} else {
			user.add(task);
			tasks = user.getOngoingTaskList();
			user.done(0);
			user.unDone(user.getFinishedTaskList().size() - 1);
			testUndone("test putBack", tasks, user.getOngoingTaskList());
		}
		System.out.println("all undone tests are passed");
	}

	//@author A0119444E
	/**
	 * test method for testing undo method
	 * @param description
	 * @param expected
	 * @param actual
	 */
	private void testUndo(String description, ArrayList<Task> expected, ArrayList<Task> actual) {
		try {
			assert(expected.equals(actual));
		} catch (Exception e) {
			
		}
	}

	//@author A0119444E
	/**
	 * test method for testing redo method
	 * @param description
	 * @param expected
	 * @param actual
	 */
	private void testRedo(String description, ArrayList<Task> expected, ArrayList<Task> actual) {
		try {
			assert(expected.equals(actual));
		} catch (Exception e) {
			
		}
	}

	//@author A0119444E
	/**
	 * test method for testing add method
	 * @param description
	 * @param expected
	 * @param user
	 * @param i
	 */
	private void testAdd(String description, String expected, User user, int i) {
		try {
			assertEquals(description, expected, user.retrieveFromNormalList(i)
					.getDescription());
		} catch (Exception e) {

		}
	}	

	//@author A0119444E
	/**
	 * test method for testing delete method
	 * @param description
	 * @param expected
	 * @param user
	 * @param i
	 */
	private void testDelete(String description, String expected, User user,
			int i) {
		try {
			assertEquals(description, expected, user.retrieveFromNormalList(i)
					.getDescription());
		} catch (Exception e) {

		}
	}
	
	//@author A0119444E
	/**
	 * test method for testing deleteAll method
	 * @param description
	 * @param expected
	 * @param actualString
	 */
	private void testDeleteAll(String description, int expected, int actualString) {
		try {
			assertEquals(description, expected, actualString);
		} catch (Exception e) {
			
		}
	}
	
	//@author A0119444E
	/**
	 * test method for testing putBack method
	 * @param description
	 * @param expected
	 * @param actualString
	 */
	private void testPutBack(String description, ArrayList<Task> expected, ArrayList<Task> actual) {
		try {
			assert(expected.equals(actual));
		} catch (Exception e) {
			
		}
	}
	
	//@author A0119444E
	/**
	 * test method for testing done method
	 * @param description
	 * @param expected
	 * @param actualString
	 */	
	private void testDone(String description, String expected, String actualString) {
		try {
			assertEquals(description, expected, actualString);
		} catch (Exception e) {
			
		}
	}
	
	//@author A0119447Y
	/**
	 * test method for testing unDone method
	 */	
	private void testUndone(String description, ArrayList<Task> expected, ArrayList<Task> actual) {
		try {
			assert(expected.equals(actual));
		} catch (Exception e) {
			
		}
	}
}
