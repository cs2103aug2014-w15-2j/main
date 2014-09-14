package includes;

import java.util.ArrayList;



public class Task {
	public enum TaskType {FLOATING, TIMED, DEADLINE};
	
	public TaskType type;
	public String description;
	public String category;
	public int priority;
	public String task_id;
	public int repeated_period;
	public ArrayList<String> tag;
	
	/**
	 * constructor
	 * @param description
	 * @param category
	 * @param priority
	 * @param task_id
	 * @param repeated_period
	 * @param tag
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
