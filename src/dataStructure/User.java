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

	//@author A0119447Y
	private TaskBox currentTasks;
	private Stack<TaskBox> undoable;
	private Stack<TaskBox> redoable;
	private Cloner cloner = new Cloner();

	//@author A0119447Y
	/**
	 * constructor for class User
	 * 
	 * @throws Exception
	 */
	public User() throws Exception {
		currentTasks = DataStore.loadFileData();
		undoable = new Stack<TaskBox>();
		redoable = new Stack<TaskBox>();
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
			
			DataStore.save(this.currentTasks);
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
			
			DataStore.save(this.currentTasks);
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

	//@author A0119444E-unused
//	/**
//	 * add 
//	 * adds a task into the task list.
//	 * 
//	 * @param task
//	 */
//	
//	public boolean add(Task task) {
//		this.updateUndoable();
//		if (taskEndIndex == -1 || taskEndIndex == currentTasks.size() - 1) {
//			this.currentTasks.add(task);
//		} else {
//			this.currentTasks.add(task);
//			moveAddedTask(taskEndIndex);
//		}
//		taskEndIndex++;
//		boolean isSuccessful = DataStore.save(this.currentTasks);
//		return isSuccessful;
//	}
	
	//@author A0119447Y
	/**
	 * add 
	 * adds a task into the current task list.
	 * 
	 * @param task
	 */
	public boolean add(Task task) {
		this.updateUndoable();
		this.currentTasks.getNormalTasks().add(task);
		boolean isSuccessful = DataStore.save(this.currentTasks);
		return isSuccessful;
	}

	//@author A0119444E-unused
//	/**
//	 * delete deletes the task with the index from the task list.
//	 * 
//	 * @param index
//	 * @throws CommandFailedException
//	 */
//	public boolean delete(int index) throws CommandFailedException {
//		if (!this.isValidIndex(index)) {
//			throw new CommandFailedException(String.format(
//					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
//		} else {
//			this.updateUndoable();
//			boolean isSuccessful = this.currentTasks.get(index).addTag(
//					Constant.TRASHED_TAG);
//			moveTrashedTask(index);
//			taskEndIndex--;
//			DataStore.save(this.currentTasks);
//			return isSuccessful;
//		}
//	}
	
	//@author A0119447Y
	/**
	 * delete 
	 * deletes the task with the index from the task list.
	 * 
	 * @param index
	 * @param willSave
	 * @throws CommandFailedException
	 */
	public boolean delete(int index, boolean willSave) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			this.updateUndoable();
			Task removedTask = this.currentTasks.getNormalTasks().remove(index);
			this.currentTasks.getTrashedTasks().add(removedTask);
			if (willSave) {
				return DataStore.save(this.currentTasks);
			} else {
				return true;
			}
		}
	}

	//@author A0119379R
	@SuppressWarnings("unchecked")
	public Task getUpdatePreview(int index, HashMap<String, Object> toBeUpdated)
			throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Task task = cloner.deepClone(this.currentTasks.getNormalTasks().get(index));
			if (task.isTrashed()) {
				throw new CommandFailedException("Task is trashed");
			}
			Iterator<String> attributes = toBeUpdated.keySet().iterator();
			while (attributes.hasNext()) {
				String currentAttribute = attributes.next();
				Object currentObject = toBeUpdated.get(currentAttribute);

				if (currentAttribute.equals("description")) {
					task.setDescription((String) currentObject);
				} else if (currentAttribute.equals("priority")) {
					task.setPriority((int) currentObject);
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
	
	//@author A0119447Y
	/**
	 * getNormalTaskList
	 * 
	 * return the normal tasks
	 */
	public ArrayList<Task> getNormalTaskList() {
		return this.currentTasks.normalTasks;
	}
	
	//@author A0119447Y
	/**
	 * getTrashedTaskList
	 * 
	 * return the normal tasks
	 */
	public ArrayList<Task> getTrashedTaskList() {
		return this.currentTasks.trashedTasks;
	}
	
	//@author A0119447Y
	/**
	 * getTrashedTaskList
	 * 
	 * return the normal tasks
	 */
	public ArrayList<Task> getFinishedTaskList() {
		return this.currentTasks.finishedTasks;
	}

	//@author A0119444E
	/**
	 * update updates the task with the index according to the key-value pairs
	 * in toBeUpdated.
	 * 
	 * @param index
	 * @param toBeUpdated
	 *            attributes to be updated
	 * @throws CommandFailedException
	 */
	@SuppressWarnings("unchecked")
	public void update(int index, HashMap<String, Object> toBeUpdated)
			throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			this.updateUndoable();
			Task task = this.currentTasks.getNormalTasks().get(index);
			Iterator<String> attributes = toBeUpdated.keySet().iterator();
			while (attributes.hasNext()) {
				String currentAttribute = attributes.next();
				Object currentObject = toBeUpdated.get(currentAttribute);

				if (currentAttribute.equals("description")) {
					task.setDescription((String) currentObject);
				} else if (currentAttribute.equals("priority")) {
					task.setPriority((int) currentObject);
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
		}
		DataStore.save(this.currentTasks);
	}
	
	//@author A0119447Y
	/**
	 * done
	 * mark a task as done
	 * 
	 * @param index
	 * @throws CommandFailedException
	 */
	public boolean done(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			this.updateUndoable();
			Task doneTask = this.currentTasks.getNormalTasks().remove(index);
			this.currentTasks.getFinishedTasks().add(doneTask);
			return DataStore.save(this.currentTasks);
		}
	}

	//@author A0119447Y
	/**
	 * deleteAll
	 * deletes all current tasks
	 * 
	 * @throws CommandFailedException
	 */
	public boolean deleteAll() throws CommandFailedException {
		this.updateUndoable();
		for (int i = this.currentTasks.getNormalTasks().size() - 1; i >= 0; i--) {
			this.delete(i, false);
		}
		return DataStore.save(this.currentTasks);
	}

	//@author A0119444E-unused
//	/**
//	 * clear all current tasks
//	 */
//	public void clear() {
//		this.updateUndoable();
//		ArrayList<Task> toBeCleared = new ArrayList<Task>();
//
//		for (Task task : currentTasks) {
//			if (task.isTrashed()) {
//				toBeCleared.add(task);
//			}
//		}
//
//		for (Task task : toBeCleared) {
//			currentTasks.remove(task);
//		}
//		DataStore.save(this.currentTasks);
//	}
	
	//@author A0119447Y
	/**
	 * emptyTrash
	 * clear all the trahsed task
	 */
	public boolean emptyTrash() {
		this.updateUndoable();
		this.currentTasks.getTrashedTasks().clear();
		return DataStore.save(this.currentTasks);
	}

	//@author A0119444E-unused
//	/**
//	 * move new added task to correct position
//	 * 
//	 * @param index
//	 */
//	private void moveAddedTask(int index) {
//		Task newAddedTask = currentTasks.get(currentTasks.size() - 1);
//		for (int i = currentTasks.size() - 1; i > index + 1; i--) {
//			currentTasks.set(i, currentTasks.get(i - 1));
//		}
//		currentTasks.set(index + 1, newAddedTask);
//	}

	//@author A0119444E-unused
//	/**
//	 * move new trashed task to the end of currentTasks
//	 * 
//	 * @param index
//	 */
//	private void moveTrashedTask(int index) {
//		int lastTaskIndex = currentTasks.size();
//		Task newTrashedTask = currentTasks.get(index);
//		currentTasks.add(newTrashedTask);
//		for (int i = index; i < lastTaskIndex; i++) {
//			currentTasks.set(i, currentTasks.get(i + 1));
//		}
//		currentTasks.remove(lastTaskIndex);
//	}

	//@author A0119444E-unused
//	/**
//	 * get valid categories from current tasks' categories
//	 * category is not supported now
//	 * @return
//	 */
//	private ArrayList<String> getValidCategory() {
//		ArrayList<String> validCategory = new ArrayList<String>();
//		if (currentTasks.equals(null) || currentTasks.isEmpty()) {
//
//		} else {
//			for (Task task : currentTasks) {
//				validCategory.add(task.getCategory());
//			}
//		}
//		return validCategory;
//	}

	//@author A0119444E-unused
//	/**
//	 * create a new category
//	 * category is not supported now
//	 * 
//	 * @param category
//	 * @throws CommandFailedException
//	 */
//	public void createCategory(String category) throws CommandFailedException {
//		if (validCategory.contains(category)) {
//			throw new CommandFailedException("invalid category");
//		} else {
//			validCategory.add(category);
//		}
//	}

	//@author A0119444E-unused
//	/**
//	 * deleteCategory
//	 * delete a category
//	 * caetgory is not supported now
//	 * @param category
//	 * @throws CommandFailedException
//	 */
//	public void deleteCategory(String category) throws CommandFailedException {
//		if (!validCategory.contains(category)) {
//			throw new CommandFailedException("no such category");
//		} else {
//			for (Task task : currentTasks) {
//				if (task.getCategory().equals(category)) {
//					task.setCategory(Constant.DEFAULT_CATEGORY);
//				}
//			}
//			validCategory.remove(category);
//		}
//	}

	//@author A0119444E-unused
	/**
	 * show a joke to user
	 * joke is not supported now
	 */
	public void showJoke() {
		System.out.println("How can you expect a Todo-List software to provide you a joke!");
		System.out.println("This function is actually the joke.");
		System.out.println("If you really want some, go to jokes.cc.com ");
	}

	//@author A0119447Y
	/**
	 * 
	 * @param index
	 * @return
	 * @throws CommandFailedException
	 */
	public Task retrieve(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			return this.currentTasks.getNormalTasks().get(index);
		}
	}

	//@author A0119447Y
	/**
	 * find gets the list of tasks that meet the constraint.
	 * 
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Task> find(Constraint constraint) throws Exception {
		Iterator<Task> taskIterator = this.currentTasks.getNormalTasks().iterator();
		ArrayList<Task> matchedTasks = new ArrayList<Task>();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (constraint.isMeeted(task)) {
				matchedTasks.add(task);
			}
		}
		return matchedTasks;
	}

	//@author A0119447Y
	/**
	 * isValidIndex
	 * 
	 * @param index
	 * @return
	 */
	private boolean isValidIndex(int index) {
		if ((index < 0) || (index > this.currentTasks.getNormalTasks().size())) {
			return false;
		} else {
			return true;
		}
	}
	
	//@author A0119444E-unused
//	/**
//	 * getTaskList 
//	 * gets the list of every non-trashed tasks of the user.
//	 * 
//	 * @return
//	 */
//	public ArrayList<Task> getTaskList() {
//		ArrayList<Task> nonTrashedTasks = new ArrayList<Task>();
//		for (Task task : this.currentTasks) {
//			if (!task.isTrashed()) {
//				nonTrashedTasks.add(task);
//			}
//		}
//		return nonTrashedTasks;
//	}

	// system level static methods

	//@author A0119447Y-unused
	/**
	 * showHelp shows the application manual.
	 * 
	 * @return
	 */
	public static String showHelp() {

		return "'Help' has not been implemented yet";
	}
	
	//@author A0119447Y
	/**
	 * exit exits the application
	 */
	public static void exit() {
		UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_SESSION_END);
		System.exit(0);
	}
}