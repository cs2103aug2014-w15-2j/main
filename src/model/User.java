package model;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.rits.cloning.*;

import dataStore.DataStore;

public class User {

	//@author A0119447Y
	public TaskBox currentTasks;
	private Stack<TaskBox> undoable;
	private Stack<TaskBox> redoable;
	private Cloner cloner = new Cloner();

	//@author A0119447Y
	/**
	 * Constructor for class User
	 * 
	 * @throws Exception
	 */
	public User() throws Exception {
		currentTasks = DataStore.loadFileData();
		undoable = new Stack<TaskBox>();
		redoable = new Stack<TaskBox>();
	}

	/**
	 * ========================================================================
	 * Functionality methods: CRUD, undo, redo, search
	 * ========================================================================
	 */

	//@author A0119447Y
	/**
	 * Reset the current task list to one step backwards.
	 * 
	 * @return whether the task has been undone successfully
	 * @throws CommandFailedException
	 */
	public void undo() throws CommandFailedException {
		if (this.undoable.empty()) {
			// no archived task list in the history
			throw new CommandFailedException(Constant.NO_UNDOABLE_ERROR_MESSAGE);
		} else {
			this.redoable.push(cloner.deepClone(this.currentTasks));

			if (this.redoable.size() > Constant.MAXIMUM_REDO_TIMES) {
				this.redoable.remove(0);
			}

			// reload current task list 
			this.currentTasks = cloner.deepClone(this.undoable.pop());

			DataStore.save(this.currentTasks);
		}
	}

	//@author A0119447Y
	/**
	 * Reset the current task list to one step forwards.
	 * 
	 * @return whether the task has been redone successfully
	 * @throws CommandFailedException
	 */
	public void redo() throws CommandFailedException {
		if (this.redoable.empty()) {
			// no archived task list in the history
			throw new CommandFailedException(Constant.NO_REDOABLE_ERROR_MESSAGE);
		} else {
			this.undoable.push(cloner.deepClone(this.currentTasks));

			if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
				this.undoable.remove(0);
			}

			// reload current task list 
			this.currentTasks = cloner.deepClone(this.redoable.pop());

			DataStore.save(this.currentTasks);
		}
	}

	//@author A0119447Y
	/**
	 * Add a task into the current task list.
	 * 
	 * @param task 	task the task to be added
	 * @return		whether the task has been added successfully
	 */
	public boolean add(Task task) {
		this.updateUndoable();
		this.currentTasks.getOngoingTasks().add(task);
		boolean isSuccessful = DataStore.save(this.currentTasks);
		return isSuccessful;
	}

	//@author A0119447Y
	/**
	 * Delete given index from the specified list name 
	 * 
	 * @param index		index of the task in the corresponding list
	 * @param listName	listName where the task is delete from
	 * @return			whether the task has been deleted successfully
	 * @throws CommandFailedException
	 */
	public boolean delete(int index, String listName)
			throws CommandFailedException {
		switch (listName) {
		case Constant.TASK_LIST_TODO:
			return this.deleteOngoing(index, true);

		case Constant.TASK_LIST_FINISHED:
			return this.deleteFinished(index, true);

		default:
			throw new CommandFailedException(
					Constant.PROMPT_MESSAGE_INVALID_TASK_LISE);
		}
	}

	//@author A0119447Y
	/**
	 * Deletes all tasks from the corresponding list
	 * 
	 * @param listName	listName where the task is delete from
	 * @return			whether the tasks have been deleted successfully
	 * @throws CommandFailedException
	 */
	public boolean deleteAll(String listName) throws CommandFailedException {
		switch (listName) {
		case Constant.TASK_LIST_TODO:
			return this.deleteAllOngoing();

		case Constant.TASK_LIST_FINISHED:
			return this.deleteAllFinished();

		default:
			throw new CommandFailedException(
					Constant.PROMPT_MESSAGE_INVALID_TASK_LISE);
		}
	}

	//@author A0119447Y
	/**
	 * Put the trashed tasks back to ongoing task list
	 * 
	 * @param trashedIndex	index of task in trashed list
	 * @return				whether the tasks have been deleted successfully
	 * @throws CommandFailedException
	 */
	public boolean putBack(int trashedIndex) throws CommandFailedException {
		if (!this.isValidTrashedIndex(trashedIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, trashedIndex));
		} else {
			this.updateUndoable();
			// the task list to be put back
			Task putBackTask = cloner.deepClone(this.currentTasks
					.getTrashedTasks().remove(trashedIndex));
			putBackTask.setStatus(Constant.TASK_STATUS_ONGOING);
			this.currentTasks.getOngoingTasks().add(putBackTask);
			
			return DataStore.save(this.currentTasks);
		}
	}

	//@author A0119379R
	@SuppressWarnings("unchecked")
	public Task getUpdatePreview(int index, HashMap<String, Object> toBeUpdated)
			throws CommandFailedException {
		if (!this.isValidOngoingIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Task task = cloner.deepClone(this.currentTasks.getOngoingTasks()
					.get(index));
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

	//@author A0119444E
	/**
	 * update updates the task with the index according to the key-value pairs
	 * in toBeUpdated.
	 * 
	 * @param ongoingIndex
	 * @param toBeUpdated
	 *            attributes to be updated
	 * @throws CommandFailedException
	 */
	@SuppressWarnings("unchecked")
	public void update(int ongoingIndex, HashMap<String, Object> toBeUpdated)
			throws CommandFailedException {
		if (!this.isValidOngoingIndex(ongoingIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, ongoingIndex));
		} else {
			this.updateUndoable();
			//get the task that users want to update
			Task task = this.currentTasks.getOngoingTasks().get(ongoingIndex);
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
	 * Mark a ongoing task as done
	 * 
	 * @param normalIndex	the index in the ongoing task list
	 * @return				whether the tasks have been marked as done successfully
	 * @throws CommandFailedException
	 */
	public boolean done(int normalIndex) throws CommandFailedException {
		if (!this.isValidOngoingIndex(normalIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, normalIndex));
		} else {
			this.updateUndoable();
			Task doneTask = cloner.deepClone(this.currentTasks.getOngoingTasks()
					.remove(normalIndex));
			doneTask.setStatus(Constant.TASK_STATUS_DONE);
			this.currentTasks.getFinishedTasks().add(doneTask);
			return DataStore.save(this.currentTasks);
		}
	}

	//@author A0119447Y
	/**
	 * Mark a finished task as ongoing
	 * 
	 * @param finishedIndex		the index in the finished task list
	 * @return					whether the tasks have been marked as ongoing successfully
	 * @throws CommandFailedException
	 */
	public boolean unDone(int finishedIndex) throws CommandFailedException {
		if (!this.isValidFinishedIndex(finishedIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, finishedIndex));
		} else {
			this.updateUndoable();
			Task doneTask = cloner.deepClone(this.currentTasks.getFinishedTasks()
					.remove(finishedIndex));
			doneTask.setStatus(Constant.TASK_STATUS_ONGOING);
			this.currentTasks.getOngoingTasks().add(doneTask);
			return DataStore.save(this.currentTasks);
		}
	}

	//@author A0119447Y
	/**
	 * Clear all the trashed task list
	 * 
	 * @return 	whether the trashed tasks have been cleared successfully
	 */
	public boolean emptyTrash() {
		this.updateUndoable();
		this.currentTasks.getTrashedTasks().clear();
		return DataStore.save(this.currentTasks);
	}

	//@author A0119379R
	public Task retrieve(int index, String listName)
			throws CommandFailedException {
		switch (listName) {
		case Constant.TASK_LIST_TODO:
			return this.retrieveFromNormalList(index);

		case Constant.TASK_LIST_FINISHED:
			return this.retrieveFromFinishedList(index);

		case Constant.TASK_LIST_TRASHED:
			return this.retrieveFromTrashedList(index);

		default:
			throw new CommandFailedException(
					"There is no list found with name: " + listName);
		}
	}

	//@author A0119444E
	/**
	 * retrieveFromNormalList
	 * 
	 * @param normalIndex
	 * @return
	 * @throws CommandFailedException
	 */
	public Task retrieveFromNormalList(int normalIndex)
			throws CommandFailedException {
		if (!this.isValidOngoingIndex(normalIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, normalIndex));
		} else {
			return this.currentTasks.getOngoingTasks().get(normalIndex);
		}
	}

	//@author A0119444E
	/**
	 * retrieveFromTrashedList
	 * 
	 * @param trashedIndex
	 * @return
	 * @throws CommandFailedException
	 */
	public Task retrieveFromTrashedList(int trashedIndex)
			throws CommandFailedException {
		if (!this.isValidTrashedIndex(trashedIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, trashedIndex));
		} else {
			return this.currentTasks.getTrashedTasks().get(trashedIndex);
		}
	}

	//@author A0119444E
	/**
	 * retrieveFromFinishedList
	 * 
	 * @param finishedIndex
	 * @return
	 * @throws CommandFailedException
	 */
	public Task retrieveFromFinishedList(int finishedIndex)
			throws CommandFailedException {
		if (!this.isValidFinishedIndex(finishedIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, finishedIndex));
		} else {
			return this.currentTasks.getFinishedTasks().get(finishedIndex);
		}
	}

	//@author A0119447Y
	/**
	 * Retrieve the list of tasks that meet the constraint.
	 * 
	 * @param constraint	constraint the constraint to be searched
	 * @param listName		listName the list where the tasks are to be searched
	 * @return				array list of results that match the constraint
	 * @throws Exception
	 */
	public ArrayList<Task> find(Constraint constraint, String listName)
			throws Exception {
		switch (listName) {
		case Constant.TASK_LIST_TODO:
			return this.findOngoing(constraint);

		case Constant.TASK_LIST_FINISHED:
			return this.findFinished(constraint);

		case Constant.TASK_LIST_TRASHED:
			return this.findTrashed(constraint);

		default:
			throw new CommandFailedException(String.format(Constant.PROMPT_MESSAGE_NO_TASK_FOUND_IN_LIST, listName));
		}
	}

	/**
	 * ========================================================================
<<<<<<< HEAD
	 * Auxiliary methods
=======
	 * ========================== Auxiliary methods ===========================
>>>>>>> f1ee8313f8cf415eb246fe8c00a3eac0f0ad88fa
	 * ========================================================================
	 */

	//@author A0119447Y
	/**
	 * Retrieve the ongoing task list
	 * 
	 * @return 	array list of all the ongoing tasks
	 */
	public ArrayList<Task> getOngoingTaskList() {
		return this.currentTasks.getOngoingTasks();
	}

	//@author A0119447Y
	/**
	 * getTrashedTaskList
	 * 
	 * return the normal tasks
	 */
	public ArrayList<Task> getTrashedTaskList() {
		return this.currentTasks.getTrashedTasks();
	}

	//@author A0119447Y
	/**
	 * getTrashedTaskList
	 * 
	 * return the normal tasks
	 */
	public ArrayList<Task> getFinishedTaskList() {
		return this.currentTasks.getFinishedTasks();
	}

	/**
	 * ========================================================================
	 * ========================== Private methods =============================
	 * ========================================================================
	 */

	//@author A0119447Y
	/**
	 * deleteNormal deletes the task with the index from the normal task list.
	 * 
	 * @param ongoingIndex
	 * @param willSave
	 * @throws CommandFailedException
	 */
	private boolean deleteOngoing(int ongoingIndex, boolean willUpdateUndo)
			throws CommandFailedException {
		if (!this.isValidOngoingIndex(ongoingIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, ongoingIndex));
		} else {
			if (willUpdateUndo) {
				this.updateUndoable();
			}
			Task removedTask = cloner.deepClone(this.currentTasks
					.getOngoingTasks().remove(ongoingIndex));
			removedTask.setStatus(Constant.TASK_STATUS_TRASHED);
			this.currentTasks.getTrashedTasks().add(removedTask);
			return DataStore.save(this.currentTasks);
		}
	}

	//@author A0119447Y
	/**
	 * deleteFinished deletes the task with the index from the finished task
	 * list.
	 * 
	 * @param finishedIndex
	 * @param willSave
	 * @throws CommandFailedException
	 */
	private boolean deleteFinished(int finishedIndex, boolean willUpdateUndo)
			throws CommandFailedException {
		if (!this.isValidFinishedIndex(finishedIndex)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, finishedIndex));
		} else {
			if (willUpdateUndo) {
				this.updateUndoable();
			}
			Task removedTask = cloner.deepClone(this.currentTasks
					.getFinishedTasks().remove(finishedIndex));
			removedTask.setStatus(Constant.TASK_STATUS_TRASHED);
			this.currentTasks.getTrashedTasks().add(removedTask);
			return DataStore.save(this.currentTasks);
		}
	}

	//@author A0119447Y
	/**
	 * deleteAllOngoing deletes all current tasks
	 * 
	 * @throws CommandFailedException
	 */
	public boolean deleteAllOngoing() throws CommandFailedException {
		this.updateUndoable();
		for (int i = this.currentTasks.getOngoingTasks().size() - 1; i >= 0; i--) {
			this.deleteOngoing(i, false);
		}
		return DataStore.save(this.currentTasks);
	}

	//@author A0119447Y
	/**
	 * deleteAllFinished deletes all finished tasks
	 * 
	 * @throws CommandFailedException
	 */
	public boolean deleteAllFinished() throws CommandFailedException {
		this.updateUndoable();
		for (int i = this.currentTasks.getFinishedTasks().size() - 1; i >= 0; i--) {
			this.deleteFinished(i, false);
		}
		return DataStore.save(this.currentTasks);
	}

	//@author A0119444E
	/**
	 * find gets the list of tasks that meet the constraint.
	 * 
	 * @param constraint
	 * @return normal tasks that meet the constraint
	 * @throws Exception
	 */
	private ArrayList<Task> findOngoing(Constraint constraint) throws Exception {
		Iterator<Task> taskIterator = this.currentTasks.getOngoingTasks()
				.iterator();
		ArrayList<Task> matchedTasks = new ArrayList<Task>();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (constraint.isMeeted(task)) {
				matchedTasks.add(task);
			}
		}
		return matchedTasks;
	}

	//@author A0119444E
	/**
	 * findTrashed gets the list of trashed tasks that meet the constraint.
	 * 
	 * @param constraint
	 * @return trashed tasks that meet the constraint
	 * @throws Exception
	 */
	private ArrayList<Task> findTrashed(Constraint constraint) throws Exception {
		Iterator<Task> taskIterator = this.currentTasks.getTrashedTasks()
				.iterator();
		ArrayList<Task> matchedTasks = new ArrayList<Task>();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (constraint.isMeeted(task)) {
				matchedTasks.add(task);
			}
		}
		return matchedTasks;
	}

	//@author A0119444E
	/**
	 * findFinished gets the list of finished tasks that meet the constraint.
	 * 
	 * @param constraint
	 * @return finished tasks that meet the constraint
	 * @throws Exception
	 */
	private ArrayList<Task> findFinished(Constraint constraint)
			throws Exception {
		Iterator<Task> taskIterator = this.currentTasks.getFinishedTasks()
				.iterator();
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
	 * updateUndoable this method should be called BEFORE every operation
	 * involving task list
	 */
	private void updateUndoable() {
		this.redoable.clear();
		this.undoable.push(cloner.deepClone(this.currentTasks));
		if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
			this.undoable.remove(0);
		}
	}

	//@author A0119444E
	/**
	 * isValidOngoingIndex
	 * 
	 * @param index
	 * @return boolean
	 */
	private boolean isValidOngoingIndex(int index) {
		if ((index < 0) || (index > this.currentTasks.getOngoingTasks().size())) {
			return false;
		} else {
			return true;
		}
	}

	//@author A0119444E
	/**
	 * isValidTrashedIndex
	 * 
	 * @param index
	 * @return boolean
	 */
	private boolean isValidTrashedIndex(int index) {
		if ((index < 0) || (index > this.currentTasks.getTrashedTasks().size())) {
			return false;
		} else {
			return true;
		}
	}

	//@author A0119444E
	/**
	 * isValidFinishedIndex
	 * 
	 * @param index
	 * @return boolean
	 */
	private boolean isValidFinishedIndex(int index) {
		if ((index < 0)
				|| (index > this.currentTasks.getFinishedTasks().size())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * =================================================================================
	 * ========================== System level static methods ==========================
	 * =================================================================================
	 */
	//@author A0119447Y
	/**
	 * exit exits the application
	 */
	public static void exit() {
		UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_SESSION_END);
		System.exit(0);
	}

	/**
	 * ========================================================================
	 * ========================== Unused methods ==============================
	 * ========================================================================
	 */

	//@author A0119444E-unused
	// /**
	// * add
	// * adds a task into the task list.
	// *
	// * @param task
	// */
	//
	// public boolean add(Task task) {
	// this.updateUndoable();
	// if (taskEndIndex == -1 || taskEndIndex == currentTasks.size() - 1) {
	// this.currentTasks.add(task);
	// } else {
	// this.currentTasks.add(task);
	// moveAddedTask(taskEndIndex);
	// }
	// taskEndIndex++;
	// boolean isSuccessful = DataStore.save(this.currentTasks);
	// return isSuccessful;
	// }

	//@author A0119444E-unused
	// /**
	// * delete deletes the task with the index from the task list.
	// *
	// * @param index
	// * @throws CommandFailedException
	// */
	// public boolean delete(int index) throws CommandFailedException {
	// if (!this.isValidIndex(index)) {
	// throw new CommandFailedException(String.format(
	// Constant.INVALID_INDEX_ERROR_MESSAGE, index));
	// } else {
	// this.updateUndoable();
	// boolean isSuccessful = this.currentTasks.get(index).addTag(
	// Constant.TRASHED_TAG);
	// moveTrashedTask(index);
	// taskEndIndex--;
	// DataStore.save(this.currentTasks);
	// return isSuccessful;
	// }
	// }

	//@author A0119444E-unused
	// /**
	// * clear all current tasks
	// */
	// public void clear() {
	// this.updateUndoable();
	// ArrayList<Task> toBeCleared = new ArrayList<Task>();
	//
	// for (Task task : currentTasks) {
	// if (task.isTrashed()) {
	// toBeCleared.add(task);
	// }
	// }
	//
	// for (Task task : toBeCleared) {
	// currentTasks.remove(task);
	// }
	// DataStore.save(this.currentTasks);
	// }

	//@author A0119444E-unused
	// /**
	// * move new added task to correct position
	// *
	// * @param index
	// */
	// private void moveAddedTask(int index) {
	// Task newAddedTask = currentTasks.get(currentTasks.size() - 1);
	// for (int i = currentTasks.size() - 1; i > index + 1; i--) {
	// currentTasks.set(i, currentTasks.get(i - 1));
	// }
	// currentTasks.set(index + 1, newAddedTask);
	// }

	//@author A0119444E-unused
	// /**
	// * move new trashed task to the end of currentTasks
	// *
	// * @param index
	// */
	// private void moveTrashedTask(int index) {
	// int lastTaskIndex = currentTasks.size();
	// Task newTrashedTask = currentTasks.get(index);
	// currentTasks.add(newTrashedTask);
	// for (int i = index; i < lastTaskIndex; i++) {
	// currentTasks.set(i, currentTasks.get(i + 1));
	// }
	// currentTasks.remove(lastTaskIndex);
	// }

	//@author A0119444E-unused
	// /**
	// * get valid categories from current tasks' categories
	// * category is not supported now
	// * @return
	// */
	// private ArrayList<String> getValidCategory() {
	// ArrayList<String> validCategory = new ArrayList<String>();
	// if (currentTasks.equals(null) || currentTasks.isEmpty()) {
	//
	// } else {
	// for (Task task : currentTasks) {
	// validCategory.add(task.getCategory());
	// }
	// }
	// return validCategory;
	// }

	//@author A0119444E-unused
	// /**
	// * create a new category
	// * category is not supported now
	// *
	// * @param category
	// * @throws CommandFailedException
	// */
	// public void createCategory(String category) throws CommandFailedException
	// {
	// if (validCategory.contains(category)) {
	// throw new CommandFailedException("invalid category");
	// } else {
	// validCategory.add(category);
	// }
	// }

	//@author A0119444E-unused
	// /**
	// * deleteCategory
	// * delete a category
	// * caetgory is not supported now
	// * @param category
	// * @throws CommandFailedException
	// */
	// public void deleteCategory(String category) throws CommandFailedException
	// {
	// if (!validCategory.contains(category)) {
	// throw new CommandFailedException("no such category");
	// } else {
	// for (Task task : currentTasks) {
	// if (task.getCategory().equals(category)) {
	// task.setCategory(Constant.DEFAULT_CATEGORY);
	// }
	// }
	// validCategory.remove(category);
	// }
	// }

	//@author A0119444E-unused
	// /**
	// * getTaskList
	// * gets the list of every non-trashed tasks of the user.
	// *
	// * @return
	// */
	// public ArrayList<Task> getTaskList() {
	// ArrayList<Task> nonTrashedTasks = new ArrayList<Task>();
	// for (Task task : this.currentTasks) {
	// if (!task.isTrashed()) {
	// nonTrashedTasks.add(task);
	// }
	// }
	// return nonTrashedTasks;
	// }

	//@author A0119444E-unused
	/**
	 * show a joke to user joke is not supported now
	 */
	public void showJoke() {
		System.out
				.println("How can you expect a Todo-List software to provide you a joke!");
		System.out.println("This function is actually the joke.");
		System.out.println("If you really want some, go to jokes.cc.com ");
	}

	//@author A0119447Y-unused
	/**
	 * showHelp shows the application manual.
	 * 
	 * @return
	 */
	public static String showHelp() {

		return "'Help' has not been implemented yet";
	}

	public boolean recover(int i, String currentListName) throws CommandFailedException {
		switch (currentListName) {
		case Constant.TASK_LIST_FINISHED:
			return this.unDone(i);
		case Constant.TASK_LIST_TRASHED:
			return this.putBack(i);
		default:
			throw new CommandFailedException("unrecognized list name");
		}
	}
}