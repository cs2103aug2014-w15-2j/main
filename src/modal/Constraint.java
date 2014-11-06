package modal;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.util.Date;
import java.util.Iterator;

public class Constraint {
	public String[] keywords;
	public TimeInterval interval;

	/**
	 * constructor
	 * 
	 * @param keyword
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	//@author A0119447Y
	public Constraint(String[] keyword, Date startDate, Date endDate)
			throws Exception {
		this.keywords = keyword;
		this.interval = new TimeInterval(startDate, endDate);
	}

	//@author A0119447Y
	public Constraint(String[] keyword, TimeInterval timeInterval) {
		this.keywords = keyword;
		this.interval = timeInterval;
	}

	//@author A0119447Y
	public Constraint() {
		this.keywords = null;
		this.interval = new TimeInterval();
	}

	/**
	 * isMeeted
	 * 
	 * @param task
	 * @return boolean
	 * @throws Exception
	 */
	
	// @author A0119444E
	public boolean isMeeted(Task task) throws Exception {
		boolean isKeywordMatched = false;
		boolean isIntervalMatched = false;
		boolean isPriorityMatched = false;
		boolean isSearchingDeadline = false;
		boolean isSearchingFloating = false;
		
		// if constraint's time interval is a deadline
		if (this.interval.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			isSearchingDeadline = true;
		}

		if (this.interval.getStartDate().equals(Constant.FLOATING_START_DATE)) {
			isSearchingFloating = true;
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
		
		if (this.keywords != null) {
			for (String keyword : keywords) {
				if (!keyword.equals("")) {
					// test description
					if (task.getDescription().toLowerCase().contains(keyword)) {
						isKeywordMatched = true;
						break;
					}
					
					// test priority
					if (UtilityMethod.priorityToString(task.getPriority()).equals(
							keyword)) {
						isPriorityMatched = true;
						break;
					}
					
					// test tag
					Iterator<String> tagIterator = task.getTag().iterator();
					while (tagIterator.hasNext()) {
						String tag = tagIterator.next();
						if (tag.toLowerCase().contains(keyword)) {
							isKeywordMatched = true;
							break;
						}
					}
				}
			}
		}

		boolean result = (isIntervalMatched || isKeywordMatched || isPriorityMatched);
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

		if (this.keywords != null && !this.keywords.equals("")) {
			result += "Keyword: \n\t";
			
			boolean isFirstLine = true;
			for (String keyword : this.keywords) {
				if (isFirstLine) {
					isFirstLine = false;
				} else {
					result += ", ";
				}
				result += keyword;
			}
		}

		return result.equals("") ? "[No Specific Constraint]" : result;
	}

}
