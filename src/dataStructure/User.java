package dataStructure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import dataStore.DataStore;
import reference.*;

public class User {
	
	ArrayList<Task> currentTasks;
	Stack<ArrayList<Task>> undoable;
	Stack<ArrayList<Task>> redoable;
	String username;

	/**
	 * constructor
	 * 
	 * @param recordFilePath
	 * @throws Exception
	 */
	public User(String recordFilePath) throws Exception {
		String userFilePath = recordFilePath;
		File userFile = new File(userFilePath);
		undoable = new Stack<ArrayList<Task>>();
		redoable = new Stack<ArrayList<Task>>();
		currentTasks = DataStore.getCurrentTasks(userFile);
		username = recordFilePath;
	}

	/**
	 * undo
	 * 
	 * @throws CommandFailedException
	 */
	public void undo() throws CommandFailedException {
		if (this.undoable.empty()) {
			throw new CommandFailedException(Constant.NO_UNDOABLE_ERROR_MESSAGE);
		} else {
			this.redoable.push(this.currentTasks);

			if (this.redoable.size() > Constant.MAXIMUM_REDO_TIMES) {
				this.redoable.remove(0);
			}

			this.currentTasks = this.undoable.pop();
		}
	}

	/**
	 * redo
	 * 
	 * @throws CommandFailedException
	 */
	public void redo() throws CommandFailedException {
		if (this.redoable.empty()) {
			throw new CommandFailedException(Constant.NO_REDOABLE_ERROR_MESSAGE);
		} else {
			this.undoable.push(this.currentTasks);

			if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
				this.undoable.remove(0);
			}

			this.currentTasks = this.redoable.pop();
		}
	}

	/**
	 * updateUndoable this method should be called BEFORE every operation
	 * involving task list
	 */
	private void updateUndoable() {
		this.redoable.clear();
		this.undoable.push(this.currentTasks);
		if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
			this.undoable.remove(0);
		}
	}

	/**
	 * add
	 * 
	 * @param task
	 */
	public void add(Task task) {
		this.updateUndoable();
		this.currentTasks.add(task);
		DataStore.save(this.username, this.currentTasks);
	}

	/**
	 * delete
	 * 
	 * @param index
	 * @throws CommandFailedException
	 */
	public void delete(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			this.currentTasks.get(index).addTag(Constant.TRASHED_TAG);
			DataStore.save(this.username, this.currentTasks);
		}
	}

	/**
	 * update
	 * 
	 * @param index
	 *            , attributes to be updated
	 * @param toBeUpdated
	 * @throws CommandFailedException 
	 */
	@SuppressWarnings("unchecked")
	public void update(int index, HashMap<String, Object> toBeUpdated) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Task task = this.currentTasks.get(index);
			Iterator<String> attributes = toBeUpdated.keySet().iterator();
			while (attributes.hasNext()) {
				String currentAttribute = attributes.next();
				Object currentObject = toBeUpdated.get(currentAttribute);
				if (currentAttribute.equals("description")) {
					task.setDescription((String) currentObject);
				} else if (currentAttribute.equals("category")) {
					task.setCategory((String) currentObject);
				} else if (currentAttribute.equals("priority")) {
					task.setPriority((int) currentObject);
				} else if (currentAttribute.equals("repeated_period")) {
					task.setRepeatedPeriod((int) currentObject);
				} else if (currentAttribute.equals("tag")) {
					task.setTag((ArrayList<String>) currentObject);
				} else if (currentAttribute.equals("time_interval")) {
					task.setInterval((TimeInterval) currentObject);
				} else {
					throw new CommandFailedException(Constant.INVALID_UPDATE_MESSAGE);
				}
			}
		}
	}

	/**
	 * getTaskIdByIndex
	 * 
	 * @param index
	 * @return
	 * @throws CommandFailedException
	 */
	public String getTaskIdByIndex(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			return this.currentTasks.get(index).getTaskId();
		}
	}

	/**
	 * retrieve
	 * 
	 * @param index
	 * @return the Task, null if there is an error
	 * @throws CommandFailedException
	 */
	public Task retrieve(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Iterator<Task> taskIterator = this.currentTasks.iterator();
			while (taskIterator.hasNext()) {
				Task task = taskIterator.next();
				if (task.getTaskId().equals(this.getTaskIdByIndex(index))) {
					return task;
				}
			}
			return null;
		}
	}

	/**
	 * find
	 * 
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
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
	private boolean isValidIndex(int index) {
		if ((index < 0) || (index > this.currentTasks.size())) {
			return false;
		} else {
			return true;
		}
	}
}
