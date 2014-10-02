package dataStructure;

import java.util.Date;
import java.util.Iterator;

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

	/**
	 * isMeeted
	 * @param task
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean isMeeted(Task task) throws Exception {
		// test description
		if (task.getDescription().toLowerCase().contains(this.keyword)) {
			return true;
		}
		
		// test category
		if (task.getCategory().toLowerCase().contains(this.keyword)) {
			return true;
		}
		
		// test task_id
		if (task.getTaskId().toLowerCase().contains(this.keyword)) {
			return true;
		}
		
		// test tag
		Iterator<String> tagIterator = task.getTag().iterator();
		while (tagIterator.hasNext()) {
			if (tagIterator.next().toLowerCase().contains(this.keyword)) {
				return true;
			}
		}
		
		// test interval
		if (TimeInterval.isOverlapped(this.interval, task.getInterval())) {
			return true;
		}
		return false;
	}
	
}
