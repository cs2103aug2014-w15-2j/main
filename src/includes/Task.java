package includes;

import java.util.ArrayList;



public class Task {
	public String description;
	public String category;
	public int priority;
	public String task_id;
	public int repeated_period;
	public ArrayList<String> tag;
	
	/**
	 * constructor
	 */
	public Task(String description, String category, int priority, String task_id, int repeated_period, ArrayList<String> tag) {
		this.description = description;
		this.category = category;
		this.priority = priority;
		this.task_id = task_id;
		this.repeated_period = repeated_period;
		this.tag = tag;
	}
}
