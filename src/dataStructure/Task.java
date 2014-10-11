package dataStructure;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.util.ArrayList;
import java.util.UUID;

import reference.*;

public class Task {
	

	private String description;
	private String category;
	private int priority;
	private String task_id;
	private int repeated_period;
	private ArrayList<String> tag;
	private TimeInterval interval;

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
	 */
	public Task(String description, String category, int priority,
			int repeated_period, ArrayList<String> tag, TimeInterval interval) {
		this.description = description;
		this.category = (category == null) ? Constant.DEFAULT_CATEGORY : category;
		this.priority = priority;
		this.task_id = UUID.randomUUID().toString();
		this.repeated_period = repeated_period;
		this.tag = tag;
		this.interval = interval;
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
	 */
	public Task(String task_id, String description, String category,
			int priority, int repeated_period, ArrayList<String> tag,
			TimeInterval interval) {
		this.description = description;
		this.category = category;
		this.priority = priority;
		this.task_id = task_id;
		this.repeated_period = repeated_period;
		this.tag = tag;
		this.interval = interval;
	}

	/**
	 * addTag user cannot add duplicated tag for a task
	 * 
	 * @param tag
	 * @throws CommandFailedException
	 */
	public boolean addTag(String tag) throws CommandFailedException {
		if (this.tag.contains(tag)) {
			throw new CommandFailedException(String.format(
					Constant.DUPLICATED_TAG_ERROR_MESSAGE, tag));
		} else {
			return this.tag.add(tag);
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

	/**
	 * parse task to string
	 * 
	 * @return a task string
	 */
	public String toString() {
		String task = new String();

		task = task + getTaskId() + '`';
		task = task + getDescription() + '`';
		task = task + getCategory() + '`';
		task = task + toStringAddTags() + '`';
		task = task + Integer.toString(getRepeatedPeriod()) + '`';
		task = task + Integer.toString(getPriority()) + '`';
		Long start = getInterval().getStartDate().getTime();
		task = task + start + '`';
		Long end = getInterval().getEndDate().getTime();
		task = task + end;

		return task;
	}

	/**
	 * add tags for task string
	 * 
	 * @return tags string
	 */
	private String toStringAddTags() {
		String tags = new String();
		if (tag.isEmpty()) {
			tags = "";
		} else {
			for (String aTag : tag) {
				tags = tags + aTag + ",";
			}
			// to remove the last comma
			tags = tags.substring(0, tags.length() - REDUCE_CHAR);
		}
		return tags;
	}

	/**
	 * toDisplayedString
	 * 
	 * @return 
	 */
	public String toDisplayedString() {
		String task = new String();

		task = task + getDescription();
		task = task + "\n\t category: " + getCategory() + ';';
		if (!this.tag.isEmpty()) {
			task = task + "\n\t tags: " + toStringAddTags() + ';';
		}
		
//		task = task + Integer.toString(getRepeatedPeriod()) + '`'; TODO
		task = task + "\n\t priority: " + UtilityMethod.priorityToString(getPriority()) + ';';
		task = task + this.getInterval().toString();

		return task;
	}
	
	// setter and getter

	/**
	 * getPriority getter of the priority attribute
	 * 
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * setDescription setter of the description attribute
	 * 
	 * @param newDescription
	 */
	public void setDescription(String newDescription) {
		try {
			this.description = newDescription;
		} catch (Exception e) {

		}
	}

	/**
	 * getDescription getter of the category attribute
	 * 
	 * @return
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * setDescription setter of the category attribute
	 * 
	 * @param newCategory
	 */
	public void setCategory(String newCategory) {
		try {
			this.category = newCategory;
		} catch (Exception e) {

		}
	}

	/**
	 * getPriority getter of the priority attribute
	 * 
	 * @return
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * setPriority setter of the priority attribute
	 * 
	 * @param newPriority
	 */
	public void setPriority(int newPriority) {
		try {
			this.priority = newPriority;
		} catch (Exception e) {

		}
	}

	/**
	 * getTaskId getter of the task_id attribute
	 * 
	 * @return
	 */
	public String getTaskId() {
		return this.task_id;
	}

	/**
	 * getRepeatedPeriod getter of the repeated_period attribute
	 * 
	 * @return
	 */
	public int getRepeatedPeriod() {
		return this.repeated_period;
	}

	/**
	 * setRepeatedPeriod setter of the repeated_period attribute
	 * 
	 * @param newRepeatedPeriod
	 */
	public void setRepeatedPeriod(int newRepeatedPeriod) {
		try {
			this.repeated_period = newRepeatedPeriod;
		} catch (Exception e) {

		}
	}

	/**
	 * getTag getter of the tag attribute
	 * 
	 * @return
	 */
	public ArrayList<String> getTag() {
		return this.tag;
	}

	/**
	 * setTag setter of the tag attribute
	 * 
	 * @param newTag
	 */
	public void setTag(ArrayList<String> newTag) {
		try {
			this.tag = newTag;
		} catch (Exception e) {

		}
	}

	/**
	 * @override getInterval
	 * @return interval
	 */
	public TimeInterval getInterval() {
		return this.interval;
	}

	/**
	 * setInterval setter of the interval attribute
	 * 
	 * @param interval
	 */
	public void setInterval(TimeInterval inteval) {
		try {
			this.interval = inteval;
		} catch (Exception e) {

		}
	}
	
	/**
	 * isFloating check whether the task is floating task, i.e. there is no start date or end date for it
	 * 
	 * @return boolean
	 */
	public boolean isFloating() {
		if (this.getInterval().getStartDate().equals(Constant.FLOATING_START_DATE)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * isDeadline check whether the task is deadline task, i.e. there is only a deadline for it
	 * 
	 * @return boolean
	 */
	public boolean isDeadline() {
		if (this.getInterval().getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * isTimed check whether the task is timed task
	 * 
	 * @return boolean
	 */
	public boolean isTimed() {
		if (!this.isDeadline() && !this.isFloating()) {
			return false;
		} else {
			return true;
		}
	}

}
