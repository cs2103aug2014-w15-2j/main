package model;

import infrastructure.*;

import java.util.ArrayList;


public class Task {
	
	//@author A0119447Y
	private String description;
	private int priority;
	private ArrayList<String> tag;
	private TimeInterval interval;
	private String status;
	private final int REDUCE_CHAR = 1;
	
	//@author A0119447Y
	/**
	 * constructor 
	 * 
	 * @param description	description task description
	 * @param priority		priority task priority
	 * @param tag			tag task tags list
	 * @param interval		interval task time interval
	 */
	public Task(String description, int priority, ArrayList<String> tag, TimeInterval interval) {
		this.description = description;
		this.priority = priority;
		this.tag = tag;
		this.interval = interval;
		this.status = Constant.TASK_STATUS_ONGOING;
	}

	
	/**
	 * ==================================================================================================
	 * ======================================== API methods =============================================
	 * ==================================================================================================
	 */
	
	//@author A0119447Y
	/**
	 * Insert a tag for this task, user cannot add duplicated tag for a task
	 * 
	 * @param tag	tag the tag to be added
	 * @return 		whether the tag has been added successfully
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

	//@author A0119447Y
	/**
	 * Remove a tag for this task
	 * 
	 * @param tag	tag to be removed
	 * @return 		whether the tag has been removed successfully
	 */
	public boolean removeTag(String tag) {
		return this.tag.remove(tag);
	}
	
	//@author A0119444E
	/**
	 * tagToString convert the tag list to a string.
	 * 
	 * @return tags string
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

	//@author A0119447Y
	/**
	 * Convert the task information to a string for displaying
	 * 
	 * @return 	String for displaying
	 */
	public String toStringForDisplaying() {
		String task = "description:\t";

		task = task + getDescription();
		if (!this.tag.isEmpty()) {
			task = task + "\ntags: \t\t" + tagToString();
		}
	
		task = task + "\npriority: \t" + UtilityMethod.priorityToString(getPriority());
		task = task + this.getInterval().toString();

		return task;
	}

	//@author A0119444E
	/**
	 * getDescription gets the task description
	 * 
	 * @return String description
	 */
	public String getDescription() {
		return this.description;
	}

	//@author A0119444E
	/**
	 * setDescription sets the task description to given newDescription.
	 * 
	 * @param newDescription
	 */
	public void setDescription(String newDescription) {
		try {
			if (isValidDescription(newDescription)){
				this.description = newDescription;
			} else {
				throw new CommandFailedException(Constant.PROMPT_MESSAGE_INVALID_DESCRIPTION);
			}
		} catch (Exception e) {
			
		}
	}

	//@author A0119444E
	/**
	 * getPriority getter of the priority attribute
	 * 
	 * @return int priority
	 */	
	public int getPriority() {
		return this.priority;
	}
	
	//@author A0119444E
	/**
	 * setPriority setter of the priority attribute
	 * 
	 * @param newPriority
	 */
	public void setPriority(int newPriority) {
		try {
			if (isValidPriority(newPriority)){
				this.priority = newPriority;
			} else {
				throw new CommandFailedException(Constant.PROMPT_MESSAGE_INVALID_PRIORITY);
			}
		} catch (Exception e) {

		}
	}	

	//@author A0119444E
	/**
	 * getTag getter of the tag attribute
	 * 
	 * @return ArrayList<String> tag list
	 */
	public ArrayList<String> getTag() {
		return this.tag;
	}

	//@author A0119444E
	/**
	 * setTag setter of the tag attribute
	 * 
	 * @param newTag 
	 */
	public void setTag(ArrayList<String> newTag) {
		try {
			if(isValidTag(newTag)) {
				this.tag = newTag;
			} else {
				throw new CommandFailedException(Constant.PROMPT_MESSAGE_INVALID_TAG);
			}			
		} catch (Exception e) {

		}
	}

	//@author A0119444E
	/**
	 * @override getInterval
	 * @return interval
	 * 
	 */
	public TimeInterval getInterval() {
		return this.interval;
	}

	//@author A0119444E
	/**
	 * setInterval setter of the interval attribute
	 * 
	 * @param newInterval
	 */
	public void setInterval(TimeInterval newInterval) {
		try {
			if(isValidInterval(newInterval)) {
				this.interval = newInterval;
			} else {
				throw new CommandFailedException(Constant.PROMPT_MESSAGE_INVALID_TIME_INTERVAL);
			}
		} catch (Exception e) {
			
		}
	}
	
	//@author A0113029U
	public String getStatus() {
		return this.status;
	}
	
	//@author A0113029U
	public void setStatus(String state) {
		if ((state == null) || (state == "")) {
			this.status = Constant.TASK_STATUS_ONGOING;
			return;
		}
		switch(state.toLowerCase().trim()) {
			case Constant.TASK_STATUS_ONGOING:
				this.status = Constant.TASK_STATUS_ONGOING;
				break;
				
			case Constant.TASK_STATUS_DONE:
				this.status = Constant.TASK_STATUS_DONE;
				break;
				
			case Constant.TASK_STATUS_TRASHED:
				this.status = Constant.TASK_STATUS_TRASHED;
				break;
				
			default:
				this.status = Constant.TASK_STATUS_ONGOING;
				break;
		}
	}
	
	//@author A0113029U
	@Override
	public boolean equals(Object obj) {
		if(obj == null || obj.getClass()!=this.getClass()){
			return false;
		}
		Task task = (Task) obj;
		return (this.getDescription().equals(task.getDescription())) &&
				(this.getStatus().equals(task.getStatus())) &&
				(this.getPriority() == task.getPriority()) &&
				(this.getInterval().equals(task.getInterval())) &&
				(this.getTag().equals(task.getTag()));
	}
	
	// check validity of attributes	
	//@author A0119444E
	/**
	 * check whether a description is valid
	 * 
	 * @param description
	 * @return boolean
	 */
	private boolean isValidDescription(String description){
		if (description.equals(null) || description.equals("")) {
			return false;
		} else {
			return true;
		}
	}
	
	
	
	//@author A0119444E
	/**
	 * check whether a priority is valid
	 * 
	 * @param priority
	 * @return boolean
	 */
	private boolean isValidPriority(int priority){
		if (priority < 1 || priority > 3) {
			return false;
		} else {
			return true;
		}
	}
	
	//@author A0119444E
	/**
	 * check whether a list of tag is valid
	 * 
	 * @param tags
	 * @return boolean	indicates whether the tag list is a valid tag list
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
	
	//@author A0119444E
	/**
	 * check whether all tags in a tag list are valid
	 * 
	 * @param tags
	 * @return boolean	indicates whether all tags are valid tags
	 */
	private boolean isValidOneTag(ArrayList<String> tags){
		for(String tag : tags){
			if (tag.contains(" ")){
				return false;
			}
		}
		return true;
	}
	
	//@author A0119444E
	/**
	 * check whether a interval is valid
	 * 
	 * @param interval
	 * @return boolean	indicates whether the TimeInterval is a valid TimeInterval
	 */
	private boolean isValidInterval(TimeInterval interval){
		if (interval == null) {
			return false;
		} else {
			return true;
		}
	}	
	
	// check task type
	
	//@author A0119447Y
	/**
	 * Check whether a task is trashed
	 *
	 * @return boolean	indicates whether a task is trashed
	 */
	public boolean isTrashed() {
		if (this.status.equals(Constant.TASK_STATUS_TRASHED)) {
			return true;
		} else {
			return false;
		}
	}
	
	//@author A0119447Y
	/**
	 * Check whether a task is ongoing
	 *
	 * @return 	whether a task is ongoing
	 */
	public boolean isOngoing() {
		if (this.status.equals(Constant.TASK_STATUS_ONGOING)) {
			return true;
		} else {
			return false;
		}
	}
	
	//@author A0119447
	/**
	 * Check whether a task is done
	 *
	 * @return 	whether a task is done
	 */
	public boolean isDone() {
		if (this.status.equals(Constant.TASK_STATUS_DONE)) {
			return true;
		} else {
			return false;
		}
	}
	
	//@author A0119447Y
	/**
	 * Check whether the task is floating task, i.e. there is no start date or end date for it
	 * 
	 * @return 	whether the task is floating task
	 */
	public boolean isFloating() {
		if (this.getInterval().getStartDate().equals(Constant.FLOATING_START_DATE)) {
			return true;
		} else {
			return false;
		}
	}
	
	//@author A0119447Y
	/**
	 * Check whether the task is deadline task, i.e. there is only a deadline for it
	 * 
	 * @return 	whether the task is deadline task
	 */
	public boolean isDeadline() {
		if (this.getInterval().getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			return true;
		} else {
			return false;
		}
	}
	
	//@author A0119447Y
	/**
	 * Check whether the task is timed task
	 * 
	 * @return 	whether the task is timed task
	 */
	public boolean isTimed() {
		if (!this.isDeadline() && !this.isFloating()) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * ==================================================================================================
	 * ========================================= Unused methods =========================================
	 * ==================================================================================================
	 */
	//@author A0119444E-unused
//	/**
//	 * toString convert the task information to a string for storage.
//	 * Json is used to manage DataStore so this method is not needed
//	 * 
//	 * @return a task string
//	 */
//	public String toString() {
//		String task = new String();
//
//		task = task + getTaskId() + '`';
//		task = task + getDescription() + '`';
//		task = task + getCategory() + '`';
//		task = task + tagToString() + '`';
//		task = task + Integer.toString(getRepeatedPeriod()) + '`';
//		task = task + Integer.toString(getPriority()) + '`';
//		Long start = getInterval().getStartDate().getTime();
//		task = task + start + '`';
//		Long end = getInterval().getEndDate().getTime();
//		task = task + end;
//
//		return task;
//	}
	
	//@author A0119444E-unused
//	/**
//	 * getDescription getter of the category attribute
//	 * category is not supported now
//	 * 
//	 * @return
//	 */
//	public String getCategory() {
//		return this.category;
//	}
	
	//@author A0119444E-unused
//	/**
//	 * setDescription setter of the category attribute
//	 * category is not supported now
//	 * 
//	 * @param newCategory
//	 */
//	
//	public void setCategory(String newCategory) {
//		try {
//			isValidCategory(newCategory);
//			this.category = newCategory;
//		} catch (Exception e) {
//
//		}
//	}
	
	//@author A0119444E-unused
//	/**
//	 * getTaskId getter of the task_id attribute
//	 * 
//	 * @return String
//	 * 
//	 */
//	public String getTaskId() {
//		return this.task_id;
//	}

	//@author A0119444E-unused
//	/**
//	 * getRepeatedPeriod getter of the repeated_period attribute
//	 * 
//	 * @return int
//	 */
//	public int getRepeatedPeriod() {
//		return this.repeated_period;
//	}

	//@author A0119444E-unused
//	/**
//	 * setRepeatedPeriod setter of the repeated_period attribute
//	 * 
//	 * @param newRepeatedPeriod
//	 */
//	public void setRepeatedPeriod(int newRepeatedPeriod) {
//		try {
//			if(isValidRepeatedPeriod(newRepeatedPeriod)){
//				this.repeated_period = newRepeatedPeriod;
//			} else {
//				throw new CommandFailedException();
//			}
//		} catch (Exception e) {
//
//		}
//	}
	
	//@author A0119444E-unused
//	/**
//	 * check whether a category is valid
//	 * 
//	 * @param category
//	 * @return boolean
//	 */
//	private boolean isValidCategory(String category){
//		if (description.equals(null) || description.equals("")) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
	//@author A0119444E-unused
//	/**
//	 * check whether a task is trashed
//	 * @return boolean
//	 */
//	public boolean isTrashed() {
//		if (tag.contains(Constant.TRASHED_TAG)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	//@author A0119444E
//	/**
//	 * check whether a repeated period is valid
//	 * 
//	 * @param repeatedPeriod
//	 * @return boolean
//	 */
//	private boolean isValidRepeatedPeriod(int repeatedPeriod){
//		if (repeatedPeriod < 1 || repeatedPeriod > 4) {
//			return false;
//		} else {
//			return true;
//		}
//	}
}
