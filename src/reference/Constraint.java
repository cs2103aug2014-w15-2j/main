package reference;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.util.Date;
import java.util.Iterator;

import dataStructure.Task;

public class Constraint {
	public String keyword;
	public TimeInterval interval;

	/**
	 * constructor
	 * 
	 * @param keyword
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	public Constraint(String keyword, Date startDate, Date endDate)
			throws Exception {
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
	 * 
	 * @param task
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isMeeted(Task task) throws Exception {
		boolean isKeywordMatched = false;
		boolean isIntervalMatched = false;
		boolean isPriorityMatched = false;
		boolean isSearchingTrashed = false;
		boolean isSearchingDeadline = false;
		boolean isSearchingFloating = false;
		boolean isTrashedTask = false;

		if (this.keyword.contains("trash")) {
			isSearchingTrashed = true;
		}

		// if constraint's time interval is a deadline
		if (this.interval.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			isSearchingDeadline = true;
		}

		if (this.interval.getStartDate().equals(Constant.FLOATING_START_DATE)) {
			isSearchingFloating = true;
		}

		
		if (!keyword.equals("")) {
			// test description
			if (task.getDescription().toLowerCase().contains(this.keyword)) {
				isKeywordMatched = true;
			}
			
			// test priority
			if (UtilityMethod.priorityToString(task.getPriority()).contains(
					this.keyword)) {
				isPriorityMatched = true;
			}
			
			// test tag
			Iterator<String> tagIterator = task.getTag().iterator();
			while (tagIterator.hasNext()) {
				String tag = tagIterator.next();
				if (tag.toLowerCase().contains(this.keyword)) {
					isKeywordMatched = true;
				}
			}
		}

		// test trashed
		if (task.isTrashed()) {
			isTrashedTask = true;
		}

		// test interval
		if (isSearchingDeadline) {
			if (this.interval.getEndDate().after(
					task.getInterval().getEndDate())) {
				isIntervalMatched = true;
			}
			// search for a interval
		} else if (!isSearchingFloating) {
			if (task.isFloating()) {
				// never return floating tasks when search for a time
			} else if (task.isDeadline()
					&& TimeInterval.isOverlapped(this.interval, task
							.getInterval().getEndDate())) {
				return true;
			} else if (task.isTimed()
					&& TimeInterval.isOverlapped(this.interval,
							task.getInterval())) {
				return true;
			} else {

			}
		} else {
			// search floating
		}

		boolean result = ((isIntervalMatched || isKeywordMatched || isPriorityMatched) && !isTrashedTask)
				|| (isSearchingTrashed && isTrashedTask);
		return result;
	}

	@Override
	public String toString() {
		String result = "";

		if (this.interval != null
				&& !this.interval.toString().equals(
						Constant.TIME_MESSAGE_FLOATING)) {
			result += "Time: " + this.interval.toString() + "\n";
		}

		if (this.keyword != null && !this.keyword.equals("")) {
			result += "Keyword: \n\t" + this.keyword;
		}

		return result.equals("") ? "[No Specific Constraint]" : result;
	}

}
