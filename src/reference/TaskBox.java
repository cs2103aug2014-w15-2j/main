package reference;

import java.util.ArrayList;

import dataStructure.Task;

//@author A0119447Y
public class TaskBox {
	public ArrayList<Task> normalTasks;
	public ArrayList<Task> finishedTasks;
	public ArrayList<Task> trashedTasks;
	
	public TaskBox() {
		this.normalTasks = new ArrayList<Task>();
		this.finishedTasks = new ArrayList<Task>();
		this.trashedTasks = new ArrayList<Task>();
	}
	
	public ArrayList<Task> getNormalTasks() {
		return normalTasks;
	}
	
	public ArrayList<Task> getFinishedTasks() {
		return finishedTasks;
	}
	
	public ArrayList<Task> getTrashedTask() {
		return trashedTasks;
	}
	
}
