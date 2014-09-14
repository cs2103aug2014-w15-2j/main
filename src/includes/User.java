package includes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class User {
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
			// cannot undo
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
	public void updateUndoable() {
		this.redoable.clear();
		this.undoable.push(this.currentTasks);
		if (this.undoable.size() > MAXIMUM_UNDO_TIMES) {
			this.undoable.remove(0);
		}
	}
	
	/**
	 * add
	 * @param task
	 */
	public void add(Task task) {
		this.updateUndoable();
		this.currentTasks.add(task);
		// TODO: display message
	}
	
	/**
	 * delete
	 * @param task_id
	 */
	public void delete(int task_id) {
		// TODO: update the tag by appending 'trashed' tag
	}
	
	/**
	 * getTaskIdByIndex
	 * @param task_id
	 * @return
	 */
	public String getTaskIdByIndex(int task_id) {
		if (task_id >= this.currentTasks.size()) {
			// Error message/ how?
			return null;
		} else {
			return this.currentTasks.get(task_id).task_id;
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
}
