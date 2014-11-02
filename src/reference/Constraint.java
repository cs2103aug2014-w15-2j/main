package reference;

import infrastructure.Constant;

import java.util.Date;
import java.util.Iterator;

import dataStructure.Task;

public class Constraint {
	public String keyword;
	public TimeInterval interval;
	
	/**
	 * constructor
	 * @param keyword
	 * @param startDate
	 * @param endDate
	 * @throws Exception 
	 */
	public Constraint(String keyword, Date startDate, Date endDate) throws Exception {
		this.keyword = keyword.toLowerCase();
		this.interval = new TimeInterval(startDate, endDate);
	}
	
	public Constraint(String keyword, TimeInterval timeInterval) {
		this.keyword = keyword.toLowerCase();
		this.interval = timeInterval;
	}
	
	public Constraint() {
		this.keyword = "";
		this.interval = new TimeInterval();
	}

	/**
	 * isMeeted
	 * @param task
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean isMeeted(Task task) throws Exception {
		boolean isKeywordMatched = false;
		boolean isIntervalMatched = false;
		boolean isPriorityMatched = false;
		boolean isSearchingTrashed = false;
		boolean isSearchingFloating = false;
		boolean isTrashedTask = false;
		
		if (this.keyword.contains("trash")) {
			isSearchingTrashed = true;
		}
		
		if (this.interval.getStartDate().equals(Constant.FLOATING_START_DATE)) {
			isSearchingFloating = true;
		}
		
		// test description
		if (task.getDescription().toLowerCase().contains(this.keyword)) {
			isKeywordMatched = true;
		}
		
		// test category
		if (task.priorityToString().contains(this.keyword)) {
			isPriorityMatched = true;
		}		
		
		// test tag
		Iterator<String> tagIterator = task.getTag().iterator();
		while (tagIterator.hasNext()) {
			if (tagIterator.next().toLowerCase().contains(this.keyword)) {
				isKeywordMatched = true;
			}
		}
		
		// test trashed
		Iterator<String> anotherTagIterator = task.getTag().iterator();
		while (anotherTagIterator.hasNext()) {
			if (anotherTagIterator.next().toLowerCase().contains(Constant.TRASHED_TAG)) {
				isTrashedTask = true;
			}
		}
		
		// test interval
		if (!isSearchingFloating) {
			if ((!task.isFloating()) && (TimeInterval.isOverlapped(this.interval, task.getInterval()))) {
				isIntervalMatched = true;
			}
		} else {
			isIntervalMatched = true;
		}
		return isIntervalMatched || isKeywordMatched || isPriorityMatched && !(isSearchingTrashed ^ isTrashedTask);
	}
	
	@Override
	public String toString() {
		String result = "";
		
		if (this.interval != null && !this.interval.toString().equals(Constant.TIME_MESSAGE_FLOATING)) {
			result += "Time: " + this.interval.toString() + "\n";
		}
		
		if (this.keyword != null && !this.keyword.equals("")){
			result += "Keyword: \n\t" + this.keyword;
		}
		
		return result.equals("") ? "[No Specific Constraint]" : result;
	}
	
}
