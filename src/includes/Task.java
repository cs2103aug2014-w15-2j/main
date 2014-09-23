package includes;

import java.util.ArrayList;
import java.util.Date;

public class Task {
	private static final String DUPLICATED_TAG_ERROR_MESSAGE = "tag %1$s already exists.";

	public String description;
	public String category;
	public int priority;
	public String task_id;
	public int repeated_period;
	public ArrayList<String> tag;
	public TimeInterval interval;

	/**
	 * constructor
	 * 
	 * @param description
	 * @param category
	 * @param priority
	 * @param task_id
	 * @param repeated_period
	 * @param tag
	 * @throws Exception
	 */
	public Task(String description, String category, int priority,
			int repeated_period, ArrayList<String> tag, Date startDate,
			Date endDate) throws Exception {
		this.description = description;
		this.category = category;
		this.priority = priority;
		// this.task_id = ;
		this.repeated_period = repeated_period;
		this.tag = tag;
		this.interval = new TimeInterval(startDate, endDate);
	}
	
	public Task(String task_id, String description, String category, int priority,
			int repeated_period, ArrayList<String> tag, Date startDate,
			Date endDate) throws Exception {
		this.description = description;
		this.category = category;
		this.priority = priority;
		this.task_id = task_id;
		this.repeated_period = repeated_period;
		this.tag = tag;
		this.interval = new TimeInterval(startDate, endDate);
	}
    
	/**
	 * @override getInterval
	 * @return interval
	 */
	public TimeInterval getInterval() {
		return this.interval;
	}
	/**
	 * addTag user cannot add duplicated tag for a task
	 * 
	 * @param tag
	 * @throws CommandFailedException
	 */
	public void addTag(String tag) throws CommandFailedException {
		if (this.tag.contains(tag)) {
			throw new CommandFailedException(String.format(
					DUPLICATED_TAG_ERROR_MESSAGE, tag));
		} else {
			this.tag.add(tag);
		}
	}

	/**
	 * removeTag
	 * 
	 * @param tag
	 */
	public void removeTag(String tag) {
		this.tag.remove(tag);
	}

}
