package includes;

import java.util.ArrayList;

public class TimedTask extends Task {
	TimeInterval interval;
	
	/**
	 * constructor
	 * @param description
	 * @param category
	 * @param priority
	 * @param task_id
	 * @param repeated_period
	 * @param tag
	 * @param interval
	 */
	public TimedTask(String description, 
					 String category, 
					 int priority, 
					 String task_id, 
					 int repeated_period, 
					 ArrayList<String> tag,
					 TimeInterval interval) {
		super(description, category, priority, task_id, repeated_period, tag);
		this.interval = interval;
		this.type = TaskType.TIMED;
	}

}
