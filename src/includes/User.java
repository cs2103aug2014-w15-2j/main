package includes;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;

import data.DataStore;

public class User {
	private static final String TRASHED_TAG = "trashed";
	private static final String NO_REDOABLE_ERROR_MESSAGE = "nothing available for redoing";
	private static final String NO_UNDOABLE_ERROR_MESSAGE = "nothing available for undoing";
	private static final String INVALID_INDEX_ERROR_MESSAGE = "invalid task index %1$d";
	private static final String INVALID_UPDATE_MESSAGE = "invalid task attributes";
	ArrayList<Task> currentTasks;
	Stack<ArrayList<Task>> undoable;
	Stack<ArrayList<Task>> redoable;
	String username;

	protected final int MAXIMUM_UNDO_TIMES = 10;
	protected final int MAXIMUM_REDO_TIMES = 10;

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
			throw new CommandFailedException(NO_UNDOABLE_ERROR_MESSAGE);
		} else {
			this.redoable.push(this.currentTasks);

			if (this.redoable.size() > MAXIMUM_REDO_TIMES) {
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
			throw new CommandFailedException(NO_REDOABLE_ERROR_MESSAGE);
		} else {
			this.undoable.push(this.currentTasks);

			if (this.undoable.size() > MAXIMUM_UNDO_TIMES) {
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
		if (this.undoable.size() > MAXIMUM_UNDO_TIMES) {
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
		if(!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			this.currentTasks.get(index).addTag(TRASHED_TAG);
			DataStore.save(this.username, this.currentTasks);
		}
	}
	
	/**
	 * update
	 * 
	 * @param task, attributes to be updated
	 * @param toBeUpdated
	 * @throws CommandFailedException 
	 */
	public void update(Task task, Dictionary<String, Object> toBeUpdated) throws CommandFailedException{
		Enumeration<String> attributes = toBeUpdated.keys();
		if(attributes.hasMoreElements()) {
			String currentAttribute = attributes.nextElement();
			Object currentObject = toBeUpdated.get(currentAttribute);
			if(currentAttribute == "description") {
				task.setDescription((String) currentObject);
			} else if(currentAttribute == "category") {
				task.setCategory((String) currentObject);
			} else if(currentAttribute == "priority") {
				task.setPriority((int) currentObject);
			} else if(currentAttribute == "repeated_period") {
				task.setRepeatedPeriod((int) currentObject);
			} else if(currentAttribute == "tag") {
				task.setTag((ArrayList<String>) currentObject);
			} else if (currentAttribute == "time_interval") {
				task.setInterval((TimeInterval) currentObject);
			} else {
				throw new CommandFailedException(INVALID_UPDATE_MESSAGE);
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
					INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			return this.currentTasks.get(index).task_id;
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
					INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Iterator<Task> taskIterator = this.currentTasks.iterator();
			while (taskIterator.hasNext()) {
				Task task = taskIterator.next();
				if (task.task_id.equals(this.getTaskIdByIndex(index))) {
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
