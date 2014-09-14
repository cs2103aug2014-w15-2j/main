package includes;

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
	 */
	public Constraint(String keyword, Date startDate, Date endDate) {
		this.keyword = keyword.toLowerCase();
		try {
			this.interval = new TimeInterval(startDate, endDate);
		} catch (Exception e) {
			// e.printStackTrace();
			// TODO: error message
		}
	}
	
	/**
	 * isMeeted
	 * @param task
	 * @return boolean
	 */
	public boolean isMeeted(Task task) {
		// test description
		if (task.description.toLowerCase().contains(this.keyword)) {
			return true;
		}
		
		// test category
		if (task.category.toLowerCase().contains(this.keyword)) {
			return true;
		}
		
		// test task_id
		if (task.task_id.toLowerCase().contains(this.keyword)) {
			return true;
		}
		
		// test tag
		Iterator<String> tagIterator = task.tag.iterator();
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
