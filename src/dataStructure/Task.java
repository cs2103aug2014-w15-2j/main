package dataStructure;

import infrastructure.*;

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
	 * 
	 * @author A0119444E
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
	 * 
	 * @author A0119444E
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
	 * addTag insert a tag for this task, user cannot add duplicated tag for a task
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
	 * removeTag remove the tag from the task.
	 * 
	 * @param tag
	 */
	public boolean removeTag(String tag) {
		return this.tag.remove(tag);
	}

	/**
	 * toString convert the task information to a string for storage.
	 * Json is used to manage DataStore so this method is not needed
	 * 
	 * @return a task string
	 * @author A0119444E-unused
	 */
	public String toString() {
		String task = new String();

		task = task + getTaskId() + '`';
		task = task + getDescription() + '`';
		task = task + getCategory() + '`';
		task = task + tagToString() + '`';
		task = task + Integer.toString(getRepeatedPeriod()) + '`';
		task = task + Integer.toString(getPriority()) + '`';
		Long start = getInterval().getStartDate().getTime();
		task = task + start + '`';
		Long end = getInterval().getEndDate().getTime();
		task = task + end;

		return task;
	}

	/**
	 * tagToString convert the tag list to a string.
	 * 
	 * @return tags string
	 * @author A0119444E
	 */
	public String tagToString() {
		String tags = new String();
		if (tag.isEmpty()) {
			tags = "";
		} else {
			for (String aTag : tag) {
				tags = tags + aTag + ",";
			}
			// to remove the last comma
			tags = tags.substring(0, tags.length() - REDUCE_CHAR);
			assert tags.charAt(tags.length()-1) != ',';
		}
		return tags;
	}

	/**
	 * toStringForDisplaying convert the task information to a string for displaying
	 * 
	 * @return 
	 */
	public String toStringForDisplaying() {
		String task = "description:\t";

		task = task + getDescription();
		if (!this.tag.isEmpty()) {
			task = task + "\ntags: \t\t" + tagToString();
		}
	
		task = task + "\npriority: \t\t" + UtilityMethod.priorityToString(getPriority());
		task = task + this.getInterval().toString();

		return task;
	}
	
	// setter and getter

	/**
	 * getPriority gets the task description
	 * 
	 * @return
	 * @author A0119444E
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * setDescription sets the task description to given newDescription.
	 * 
	 * @param newDescription
	 * @author A0119444E
	 */
	public void setDescription(String newDescription) {
		try {
			isValidDescription(newDescription);
			this.description = newDescription;
		} catch (Exception e) {

		}
	}

	/**
	 * getDescription getter of the category attribute
	 * category is not supported now
	 * 
	 * @return
	 * @author A0119444E-unused
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * setDescription setter of the category attribute
	 * category is not supported now
	 * 
	 * @param newCategory
	 * @author A0119444E-unused
	 */
	public void setCategory(String newCategory) {
		try {
			isValidCategory(newCategory);
			this.category = newCategory;
		} catch (Exception e) {

		}
	}

	/**
	 * getPriority getter of the priority attribute
	 * 
	 * @return
	 * @author A0119444E
	 */
	public int getPriority() {
		return this.priority;
	}
	
	/**
	 * setPriority setter of the priority attribute
	 * 
	 * @param newPriority
	 * @author A0119444E
	 */
	public void setPriority(int newPriority) {
		try {
			if(isValidPriority(newPriority)){
				this.priority = newPriority;
			} else {
				throw new CommandFailedException();
			}
		} catch (Exception e) {

		}
	}

	/**
	 * getTaskId getter of the task_id attribute
	 * 
	 * @return
	 * @author A0119444E
	 */
	public String getTaskId() {
		return this.task_id;
	}

	/**
	 * getRepeatedPeriod getter of the repeated_period attribute
	 * 
	 * @return
	 * @author A0119444E
	 */
	public int getRepeatedPeriod() {
		return this.repeated_period;
	}

	/**
	 * setRepeatedPeriod setter of the repeated_period attribute
	 * 
	 * @param newRepeatedPeriod
	 * @author A0119444E
	 */
	public void setRepeatedPeriod(int newRepeatedPeriod) {
		try {
			if(isValidRepeatedPeriod(newRepeatedPeriod)){
				this.repeated_period = newRepeatedPeriod;
			} else {
				throw new CommandFailedException();
			}
		} catch (Exception e) {

		}
	}

	/**
	 * getTag getter of the tag attribute
	 * 
	 * @return
	 * @author A0119444E
	 */
	public ArrayList<String> getTag() {
		return this.tag;
	}

	/**
	 * setTag setter of the tag attribute
	 * 
	 * @param newTag
	 * @author A0119444E
	 */
	public void setTag(ArrayList<String> newTag) {
		try {
			if(isValidTag(newTag)) {
				this.tag = newTag;
			} else {
				throw new CommandFailedException();
			}			
		} catch (Exception e) {

		}
	}

	/**
	 * @override getInterval
	 * @return interval
	 * @author A0119444E
	 */
	public TimeInterval getInterval() {
		return this.interval;
	}

	/**
	 * setInterval setter of the interval attribute
	 * 
	 * @param newInterval
	 * @author A0119444E
	 */
	public void setInterval(TimeInterval newInterval) {
		try {
			if(isValidInterval(newInterval)) {
				this.interval = newInterval;
			} else {
				throw new CommandFailedException();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}
	
	// check validity of attributes
	
	/**
	 * check whether a description is valid
	 * 
	 * @param description
	 * @return boolean
	 * @author A0119444E
	 */
	private boolean isValidDescription(String description){
		if (description.equals(null) || description.equals("")) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * check whether a category is valid
	 * 
	 * @param category
	 * @return boolean
	 * @author A0119444E
	 */
	private boolean isValidCategory(String category){
		if (description.equals(null) || description.equals("")) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * check whether a priority is valid
	 * 
	 * @param priority
	 * @return boolean
	 * @author A0119444E
	 */
	private boolean isValidPriority(int priority){
		if (priority < 1 || priority > 3) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * check whether a repeated period is valid
	 * 
	 * @param repeatedPeriod
	 * @return boolean
	 * @author A0119444E
	 */
	private boolean isValidRepeatedPeriod(int repeatedPeriod){
		if (repeatedPeriod < 1 || repeatedPeriod > 4) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * check whether a list of tag is valid
	 * 
	 * @param tag
	 * @return boolean
	 * @author A0119444E
	 */
	private boolean isValidTag(ArrayList<String> tag){
		if (tag.equals(null) || tag.contains(null) || tag.contains("")) {
			return false;
		} else if (!isValidOneTag(tag)) {
			return false;
		}	else {	
			return true;
		}
	}
	
	/**
	 * check whether all tags in a tag list are valid
	 * 
	 * @param tags
	 * @return boolean
	 * @author linfanshi
	 */
	private boolean isValidOneTag(ArrayList<String> tags){
		for(String tag : tags){
			if (tag.contains(" ")){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * check whether a interval is valid
	 * 
	 * @param interval
	 * @return boolean
	 * @author linfanshi
	 */
	private boolean isValidInterval(TimeInterval interval){
		if (interval == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * check whether a task is trashed
	 * @return boolean
	 * @author linfanshi
	 */
	public boolean isTrashed() {
		if (tag.contains(Constant.TRASHED_TAG)) {
			return true;
		} else {
			return false;
		}
	}
	// check task type
	
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
			return true;
		} else {
			return false;
		}
	}

}
