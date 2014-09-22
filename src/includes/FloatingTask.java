package includes;

import java.util.ArrayList;

public class FloatingTask extends Task {

	/**
	 * constructor
	 * @param description
	 * @param category
	 * @param priority
	 * @param task_id
	 * @param repeated_period
	 * @param tag
	 */
	public FloatingTask(String description, 
						String category, 
						int priority,
						String task_id, 
						int repeated_period, 
						ArrayList<String> tag) {
		super(description, category, priority, task_id, repeated_period, tag);
		this.type = TaskType.FLOATING;
	}

}
