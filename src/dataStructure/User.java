package dataStructure;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.rits.cloning.*;

import dataStore.DataStore;
import reference.*;

public class User {

	private ArrayList<Task> currentTasks;
	private ArrayList<String> validCategory;
	private Stack<ArrayList<Task>> undoable;
	private Stack<ArrayList<Task>> redoable;
	private int taskEndIndex = -1;
	private Cloner cloner = new Cloner();

	/**
	 * constructor
	 * 
	 * @param recordFilePath
	 * @throws Exception
	 */
	
	// author A0119447Y
	public User() throws Exception {
		currentTasks = DataStore.loadFileData();
		undoable = new Stack<ArrayList<Task>>();
		redoable = new Stack<ArrayList<Task>>();
		validCategory = getValidCategory();
	}

	/**
	 * undo resets the current task list to one step backwards.
	 * 
	 * @throws CommandFailedException
	 */
	
	// author A0119447Y
	public void undo() throws CommandFailedException {
		if (this.undoable.empty()) {
			throw new CommandFailedException(Constant.NO_UNDOABLE_ERROR_MESSAGE);
		} else {
			this.redoable.push(cloner.deepClone(this.currentTasks));

			if (this.redoable.size() > Constant.MAXIMUM_REDO_TIMES) {
				this.redoable.remove(0);
			}

			this.currentTasks = cloner.deepClone(this.undoable.pop());
		}
	}

	/**
	 * redo resets the current task list to one step forwards.
	 * 
	 * @throws CommandFailedException
	 */
	
	// author A0119447Y
	public void redo() throws CommandFailedException {
		if (this.redoable.empty()) {
			throw new CommandFailedException(Constant.NO_REDOABLE_ERROR_MESSAGE);
		} else {
			this.undoable.push(cloner.deepClone(this.currentTasks));

			if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
				this.undoable.remove(0);
			}

			this.currentTasks = cloner.deepClone(this.redoable.pop());
		}
	}

	/**
	 * updateUndoable this method should be called BEFORE every operation
	 * involving task list
	 */
	
	// author A0119447Y
	private void updateUndoable() {
		this.redoable.clear();
		this.undoable.push(cloner.deepClone(this.currentTasks));
		if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
			this.undoable.remove(0);
		}
	}

	/**
	 * add adds a task into the task list.
	 * 
	 * @param task
	 */
	
	// author A0119444E
	public boolean add(Task task) {
		this.updateUndoable();
		if (taskEndIndex == -1 || taskEndIndex == currentTasks.size() - 1) {
			this.currentTasks.add(task);
		} else {
			this.currentTasks.add(task);
			moveAddedTask(taskEndIndex);
		}
		taskEndIndex++;
		boolean isSuccessful = DataStore.save(this.currentTasks);
		return isSuccessful;
	}

	/**
	 * delete deletes the task with the index from the task list.
	 * 
	 * @param index
	 * @throws CommandFailedException
	 */
	
	// author A0119444E
	public boolean delete(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			this.updateUndoable();
			boolean isSuccessful = this.currentTasks.get(index).addTag(
					Constant.TRASHED_TAG);
			moveTrashedTask(index);
			taskEndIndex--;
			DataStore.save(this.currentTasks);
			return isSuccessful;
		}
	}

	// author A0119479R
	@SuppressWarnings("unchecked")
	public Task getUpdatePreview(int index, HashMap<String, Object> toBeUpdated)
			throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Task task = cloner.deepClone(this.currentTasks.get(index));
			if (task.isTrashed()) {
				throw new CommandFailedException("Task is trashed");
			}
			Iterator<String> attributes = toBeUpdated.keySet().iterator();
			while (attributes.hasNext()) {
				String currentAttribute = attributes.next();
				Object currentObject = toBeUpdated.get(currentAttribute);

				if (currentAttribute.equals("description")) {
					task.setDescription((String) currentObject);
				} else if (currentAttribute.equals("category")
						&& validCategory.contains((String) currentObject)) {
					task.setCategory((String) currentObject);
				} else if (currentAttribute.equals("priority")) {
					task.setPriority((int) currentObject);
				} else if (currentAttribute.equals("repeated_period")) {
					task.setRepeatedPeriod((int) currentObject);
				} else if (currentAttribute.equals("tag")) {
					task.setTag((ArrayList<String>) currentObject);
				} else if (currentAttribute.equals("time_interval")) {
					System.err.println("USER UPDATE INTERVAL: "
							+ (TimeInterval) currentObject);
					task.setInterval((TimeInterval) currentObject);
				} else {
					throw new CommandFailedException(
							Constant.INVALID_UPDATE_MESSAGE);
				}
			}
			return task;
		}
	}

	/**
	 * update updates the task with the index according to the key-value pairs
	 * in toBeUpdated.
	 * 
	 * @param index
	 * @param toBeUpdated
	 *            attributes to be updated
	 * @throws CommandFailedException
	 */
	
	// author A0119444E
	@SuppressWarnings("unchecked")
	public void update(int index, HashMap<String, Object> toBeUpdated)
			throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			this.updateUndoable();
			Task task = this.currentTasks.get(index);
			Iterator<String> attributes = toBeUpdated.keySet().iterator();
			while (attributes.hasNext()) {
				String currentAttribute = attributes.next();
				Object currentObject = toBeUpdated.get(currentAttribute);

				if (currentAttribute.equals("description")) {
					task.setDescription((String) currentObject);
				} else if (currentAttribute.equals("category")
						&& validCategory.contains((String) currentObject)) {
					task.setCategory((String) currentObject);
				} else if (currentAttribute.equals("priority")) {
					task.setPriority((int) currentObject);
				} else if (currentAttribute.equals("repeated_period")) {
					task.setRepeatedPeriod((int) currentObject);
				} else if (currentAttribute.equals("tag")) {
					task.setTag((ArrayList<String>) currentObject);
					ArrayList<String> tagList = (ArrayList<String>) currentObject;
					String tag = tagList.get(0);
					if (tag.equals("done")) {
						delete(index);
					}
				} else if (currentAttribute.equals("time_interval")) {
					System.err.println("USER UPDATE INTERVAL: "
							+ (TimeInterval) currentObject);
					task.setInterval((TimeInterval) currentObject);
				} else {
					throw new CommandFailedException(
							Constant.INVALID_UPDATE_MESSAGE);
				}
			}
		}
		DataStore.save(this.currentTasks);
	}

	/**
	 * delete all current tasks
	 * 
	 * @throws CommandFailedException
	 */
	
	// author A0119447Y
	public void deleteAll() throws CommandFailedException {
		this.updateUndoable();
		for (Task task : currentTasks) {
			if (task.isTrashed()) {

			} else {
				task.addTag(Constant.TRASHED_TAG);
			}
		}
		DataStore.save(this.currentTasks);
	}

	/**
	 * clear all current tasks
	 */
	
	// author A0119444E
	public void clear() {
		this.updateUndoable();
		ArrayList<Task> toBeCleared = new ArrayList<Task>();

		for (Task task : currentTasks) {
			if (task.isTrashed()) {
				toBeCleared.add(task);
			}
		}

		for (Task task : toBeCleared) {
			currentTasks.remove(task);
		}
		DataStore.save(this.currentTasks);
	}

	/**
	 * move new added task to correct position
	 * 
	 * @param index
	 */
	
	// author A0119444E
	private void moveAddedTask(int index) {
		Task newAddedTask = currentTasks.get(currentTasks.size() - 1);
		for (int i = currentTasks.size() - 1; i > index + 1; i--) {
			currentTasks.set(i, currentTasks.get(i - 1));
		}
		currentTasks.set(index + 1, newAddedTask);
	}

	/**
	 * move new trashed task to the end of currentTasks
	 * 
	 * @param index
	 */
	
	// author A0119444E
	private void moveTrashedTask(int index) {
		int lastTaskIndex = currentTasks.size();
		Task newTrashedTask = currentTasks.get(index);
		currentTasks.add(newTrashedTask);
		for (int i = index; i < lastTaskIndex; i++) {
			currentTasks.set(i, currentTasks.get(i + 1));
		}
		currentTasks.remove(lastTaskIndex);
	}

	/**
	 * get valid categories from current tasks' categories
	 * category is not supported now
	 * @return
	 */
	
	// author A0119444E unused
	private ArrayList<String> getValidCategory() {
		ArrayList<String> validCategory = new ArrayList<String>();
		if (currentTasks.equals(null) || currentTasks.isEmpty()) {

		} else {
			for (Task task : currentTasks) {
				validCategory.add(task.getCategory());
			}
		}
		return validCategory;
	}

	/**
	 * create a new category
	 * category is not supported now
	 * 
	 * @param category
	 * @throws CommandFailedException
	 */
	
	// author A0119444E unused
	public void createCategory(String category) throws CommandFailedException {
		if (validCategory.contains(category)) {
			throw new CommandFailedException("invalid category");
		} else {
			validCategory.add(category);
		}
	}

	/**
	 *
	 * delete a category
	 * caetgory is not supported now
	 * @param category
	 * @throws CommandFailedException
	 */
	
	// author A0119444E
	public void deleteCategory(String category) throws CommandFailedException {
		if (!validCategory.contains(category)) {
			throw new CommandFailedException("no such category");
		} else {
			for (Task task : currentTasks) {
				if (task.getCategory().equals(category)) {
					task.setCategory(Constant.DEFAULT_CATEGORY);
				}
			}
			validCategory.remove(category);
		}
	}

	/**
	 * show a joke to user
	 * joke is not supported now
	 */
	
	// author A0119444E unused
	public void showJoke() {
		System.out
				.println("How can you expect a Todo-List software to provide you a joke!");
		System.out.println("This function is actually the joke.");
		System.out.println("If you really want some, go to jokes.cc.com ");
	}

	/**
	 * getTaskIdByIndex
	 * 
	 * @param index
	 * @return
	 * @throws CommandFailedException
	 */
	
	// author A0119447Y
	public Task retrieve(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			if (this.currentTasks.get(index).isTrashed()) {
				throw new CommandFailedException(String.format(
						Constant.INVALID_INDEX_ERROR_MESSAGE, index));
			} else {
				return this.currentTasks.get(index);
			}

		}
	}

	/**
	 * find gets the list of tasks that meet the constraint.
	 * 
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	
	// author A0119447Y
	public ArrayList<Task> find(Constraint constraint) throws Exception {
		Iterator<Task> taskIterator = this.currentTasks.iterator();
		ArrayList<Task> matchedTasks = new ArrayList<Task>();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (constraint.isMeeted(task)) {
				matchedTasks.add(task);
			}
		}
		return matchedTasks;
	}

	/**
	 * isValidIndex
	 * 
	 * @param index
	 * @return
	 */
	
	// author A0119447Y
	private boolean isValidIndex(int index) {
		if ((index < 0) || (index > this.currentTasks.size())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * getTaskList gets the list of every non-trashed tasks of the user.
	 * 
	 * @return
	 */
	
	// author A0119447Y
	public ArrayList<Task> getTaskList() {
		ArrayList<Task> nonTrashedTasks = new ArrayList<Task>();
		for (Task task : this.currentTasks) {
			if (!task.isTrashed()) {
				nonTrashedTasks.add(task);
			}
		}
		return nonTrashedTasks;
	}

	// system level static methods

	/**
	 * showHelp shows the application manual.
	 * 
	 * @return
	 */
	
	// author A0119447Y unused
	public static String showHelp() {

		return "'Help' has not been implemented yet";
	}
	
	

	/**
	 * exit exits the application
	 */
	
	// author A0119447Y
	public static void exit() {
		UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_SESSION_END);
		System.exit(0);
	}

	// author A0119447Y unused
	public boolean done(int index) throws CommandFailedException {
		// TODO Auto-generated method stub
		return false;
	}
}