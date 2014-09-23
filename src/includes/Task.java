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
	
	private final int REDUCE_CHAR = 1;

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
	
	/**
	 * 
	 * constructor
	 * 
	 * @param task_id
	 * @param description
	 * @param category
	 * @param priority
	 * @param repeated_period
	 * @param tag
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
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
	
	public String toString(){
		String task = new String();
		
		task.concat(task_id).concat("`");
		task.concat(description).concat("`");
		task.concat(category).concat("`");
		task.concat(toStringAddTags()).concat("`");
		task.concat(Integer.toString(repeated_period)).concat("`");
		task.concat(Integer.toString(priority)).concat("`");
		task.concat(interval.getStartDate().toString()).concat("`");
		task.concat(interval.getEndDate().toString());
		return task;
	}
	
	private String toStringAddTags(){
		String tags = new String();
		for(String aTag : tag){
			tags.concat(aTag).concat(",");
		}
		tags.substring(0, tags.length() - REDUCE_CHAR);
		return tags;
	}
}
