package model;

import java.util.ArrayList;

//@author A0119447Y
/**
 * TaskBox class is used to contain the three different task lists for user, namely,
 * ongoing tasks list, finished tasks list, and trashed tasks list.
 *
 */
public class TaskBox {
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> finishedTasks;
	private ArrayList<Task> trashedTasks;
	
	//@author A0119447Y
	/**
	 * Constructor for task box class
	 */
	public TaskBox() {
		this.ongoingTasks = new ArrayList<Task>();
		this.finishedTasks = new ArrayList<Task>();
		this.trashedTasks = new ArrayList<Task>();
	}
	
	//@author A0119447Y
	/**
	 * Retrieve the ongoing task from the task box
	 * 
	 * @return array list of all the ongoing tasks
	 */
	public ArrayList<Task> getOngoingTasks() {
		return ongoingTasks;
	}
	
	//@author A0119447Y
	/**
	 * Retrieve the finished task from the task box
	 * 
	 * @return array list of all the finshed tasks
	 */
	public ArrayList<Task> getFinishedTasks() {
		return finishedTasks;
	}
	
	//@author A0119447Y
	/**
	 * Retrieve the trashed task from the task box
	 * 
	 * @return array list of all the trashed tasks
	 */
	public ArrayList<Task> getTrashedTasks() {
		return trashedTasks;
	}
	
	//@author A0113029U
	@Override
	public boolean equals(Object obj) {
		if(obj == null || obj.getClass()!=this.getClass()) {
			return false;
		}
		TaskBox tb = (TaskBox) obj; 
		return (this.getOngoingTasks().equals(tb.getOngoingTasks())) &&
				(this.getFinishedTasks().equals(tb.getFinishedTasks())) &&
				(this.getTrashedTasks().equals(tb.getTrashedTasks()));
	}
	
}
