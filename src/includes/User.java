package includes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class User {
	private static final String TRASHED_TAG = "trashed";
	ArrayList<Task> currentTasks;
	Stack<ArrayList<Task>> undoable;
	Stack<ArrayList<Task>> redoable;
	
	protected final int MAXIMUM_UNDO_TIMES = 10;
	
	/**
	 * undo
	 * @return boolean
	 */
	public boolean undo() {
		if (this.undoable.empty()) {
			return false;
		} else {
			this.redoable.push(this.currentTasks);
			
			if (this.redoable.size() > MAXIMUM_UNDO_TIMES) {
				this.redoable.remove(0);
			}
			
			this.currentTasks = this.undoable.pop();
			return true;
		}
	}
	
	/**
	 * redo
	 * @return boolean
	 */
	public boolean redo() {
		if (this.redoable.empty()) {
			// cannot redo
			return false;
		} else {
			this.undoable.push(this.currentTasks);
			
			if (this.undoable.size() > MAXIMUM_UNDO_TIMES) {
				this.undoable.remove(0);
			}
			
			this.currentTasks = this.redoable.pop();
			return true;
		}
	}
	
	/**
	 * updateUndoable
	 * this method should be called BEFORE every operation involving task list
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
	 * @param task
	 * @return true for success, false for failure
	 */
	public boolean add(Task task) {
		try {
			this.updateUndoable();
			this.currentTasks.add(task);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	/**
	 * delete
	 * @param index
	 * @throws CommandFailedException 
	 */
	public void delete(int index) throws CommandFailedException {
		this.currentTasks.get(index).addTag(TRASHED_TAG);
	}
	
	/**
	 * getTaskIdByIndex
	 * @param index
	 * @return
	 */
	public String getTaskIdByIndex(int index) {
		if (index >= this.currentTasks.size()) {
			// Error message/ how?
			return null;
		} else {
			return this.currentTasks.get(index).task_id;
		}
	}
	
	/**
	 * retrieve
	 * @param task_id
	 * @return the Task, null if there is an error
	 */
	public Task retrieve(int task_id) {
		Iterator<Task> taskIterator = this.currentTasks.iterator();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (task.task_id.equals(this.getTaskIdByIndex(task_id))) {
				return task;
			}
		}
		
		return null;
	}
	
	/**
	 * find
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
}
