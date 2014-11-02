package reference;

import infrastructure.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeInterval {
	private Date startDate;
	private Date endDate;
	
	/**
	 * constructor
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	public TimeInterval(Date startDate, Date endDate) throws CommandFailedException {
		if (startDate == null) {
			if (endDate != null) {
				startDate = Constant.DEADLINE_START_DATE;
			} else {
				startDate = Constant.FLOATING_START_DATE;
			}
		}
		
		if (endDate == null) {
			endDate = Constant.FLOATING_END_DATE;
		}
		
		System.err.println(startDate + " - " + endDate);
		
		if (!isValid(startDate, endDate)) {
			throw new CommandFailedException("invalid time interval");
		} else {
			this.startDate = startDate;
			this.endDate = endDate;
		}
	}
	
	public TimeInterval() {
		// by default it is floating task
		this.startDate = Constant.FLOATING_START_DATE;
		this.endDate = Constant.FLOATING_END_DATE;
	}

	/**
	 * getStartDate
	 * @return start date
	 */
	public Date getStartDate() {
		return this.startDate;
	}
	
	/**
	 * getEndDate
	 * @return end date
	 */
	public Date getEndDate() {
		return this.endDate;
	}
	
	public static boolean isValid(Date startDate, Date endDate){
		if (startDate.after(endDate)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * isOverlapped
	 * @param firstInterval
	 * @param secondInterval
	 * @return
	 */
	public static boolean isOverlapped(TimeInterval firstInterval, TimeInterval secondInterval) {
		if ((firstInterval.getStartDate().after(secondInterval.getEndDate())) || 
		   (firstInterval.getEndDate().before(secondInterval.getStartDate()))) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean isOverlapped(TimeInterval interval, Date deadline) {
		if (interval.startDate.before(deadline) && (interval.endDate.after(deadline) || interval.endDate.equals(deadline))) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * @override
	 * toString
	 * 
	 * @return
	 */
	public String toString() {
		String text = new String();
		if (this.getEndDate().equals(Constant.FLOATING_END_DATE)) {
			text = text + Constant.TIME_MESSAGE_FLOATING;
		} else if (this.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			// deadline task
			String deadline = new SimpleDateFormat("dd/MMMM/yyyy HH:mm").format(this.getEndDate());
			text = text + "\n\t deadline: " + deadline + ";";
		} else {
			String start = new SimpleDateFormat("dd/MMMM/yyyy HH:mm").format(this.getStartDate());
			String end = new SimpleDateFormat("dd/MMMM/yyyy HH:mm").format(this.getEndDate());
			text = text + "\n\t from " + start + " to " + end + ";";
		}
		
		return text;
	}
	
	/**
	 * Two time intervals are equal if and only if their start date and end date are both equal
	 * @param t
	 * @return
	 */
	public boolean equals(TimeInterval t) {
		return (this.getStartDate().compareTo(t.getStartDate()) == 0) &&
				(this.getEndDate().compareTo(t.getEndDate()) == 0);
	}
}
