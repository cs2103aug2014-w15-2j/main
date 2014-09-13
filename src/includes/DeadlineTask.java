package includes;

import java.util.ArrayList;
import java.util.Date;

public class DeadlineTask extends Task {
	public Date deadline;
	
	/**
	 * constructor
	 * @param description
	 * @param category
	 * @param priority
	 * @param task_id
	 * @param repeated_period
	 * @param tag
	 * @param deadline
	 */
	public DeadlineTask(String description, 
						String category, 
						int priority, 
						String task_id, 
						int repeated_period, 
						ArrayList<String> tag,
						Date deadline) {
		super(description, category, priority, task_id, repeated_period, tag);
		this.deadline = deadline;
	}

}
