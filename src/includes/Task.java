package includes;

import java.util.ArrayList;
import java.util.Date;



public class Task {
	public enum TaskType {FLOATING, TIMED, DEADLINE}
	private static final String DUPLICATED_TAG_ERROR_MESSAGE = "tag %1$s already exists.";
	
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
	
	/**
	 * isFloatingTask
	 * @return
	 */
	public boolean isFloatingTask() {
		if (this.type.equals(TaskType.FLOATING)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * isTimedTask
	 * @return
	 */
	public boolean isTimedTask() {
		if (this.type.equals(TaskType.TIMED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * isDeadlineTask
	 * @return
	 */
	public boolean isDeadlineTask() {
		if (this.type.equals(TaskType.DEADLINE)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * getInterval
	 * @return
	 * @throws Exception
	 */
	public TimeInterval getInterval() throws Exception {
		TimeInterval interval = null;
		interval = new TimeInterval(new Date(Long.MIN_VALUE), new Date(Long.MIN_VALUE));
		return interval;
	}
	
	/**
	 * addTag
	 * user cannot add duplicated tag for a task
	 * @param tag
	 * @throws CommandFailedException 
	 */
	public void addTag(String tag) throws CommandFailedException {
		if (this.tag.contains(tag)) {
			throw new CommandFailedException(String.format(DUPLICATED_TAG_ERROR_MESSAGE, tag));
		} else {
			this.tag.add(tag);
		}
	}
	
	/**
	 * removeTag
	 * @param tag
	 */
	public void removeTag(String tag) {
		this.tag.remove(tag);
	}
	
}
