package modal;

import java.util.ArrayList;

//@author A0119447Y
public class TaskBox {
	private ArrayList<Task> normalTasks;
	private ArrayList<Task> finishedTasks;
	private ArrayList<Task> trashedTasks;
	
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
		return (this.getNormalTasks().equals(tb.getNormalTasks())) &&
				(this.getFinishedTasks().equals(tb.getFinishedTasks())) &&
				(this.getTrashedTasks().equals(tb.getTrashedTasks()));
	}
	
}
