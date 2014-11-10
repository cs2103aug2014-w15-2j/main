package model;

import infrastructure.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeInterval {
	private Date startDate;
	private Date endDate;
	
	//@author A0119447Y
	/**
	 * Constructor for TimeInterval class
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
		
		if (!isValid(startDate, endDate)) {
			throw new CommandFailedException("invalid time interval");
		} else {
			this.startDate = startDate;
			this.endDate = endDate;
		}
	}
	
	//@author A0119447Y
	/**
	 * Constructor for empty TimeInterval
	 */
	public TimeInterval() {
		// by default it is floating task
		this.startDate = Constant.FLOATING_START_DATE;
		this.endDate = Constant.FLOATING_END_DATE;
	}

	//@author A0119447Y
	/**
	 * Retrieve the start date
	 * 
	 * @return 	the start date of this time interval
	 */
	public Date getStartDate() {
		return this.startDate;
	}
	
	//@author A0119447Y
	/**
	 * Retrieve the end date
	 * 
	 * @return 	the end date of this time interval
	 */
	public Date getEndDate() {
		return this.endDate;
	}
	
	/**
	 * check whether a time interval is valid
	 * @param startDate
	 * @param endDate
	 * @return boolean
	 */
	//@author A0119444E
	public static boolean isValid(Date startDate, Date endDate){
		if (startDate.after(endDate)) {
			return false;
		} else {
			return true;
		}
	}
	
	//@author A0119447Y
	/**
	 * Check whether two time interval are overlapped
	 * 
	 * @param firstInterval
	 * @param secondInterval
	 * @return	whether two time interval are overlapped
	 */
	public static boolean isOverlapped(TimeInterval firstInterval, TimeInterval secondInterval) {
		if ((firstInterval.getStartDate().after(secondInterval.getEndDate())) || 
		   (firstInterval.getEndDate().before(secondInterval.getStartDate()))) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * check is overlapped for an interval and a deadline
	 * @param interval
	 * @param deadline
	 * @return
	 */
	//@author A0119444E
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
	//@author A0119379R
	public String toString() {
		String text = new String();
		if (this.getEndDate().equals(Constant.FLOATING_END_DATE)) {
			text = text + Constant.TIME_MESSAGE_FLOATING;
		} else if (this.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			// deadline task
			String deadline = new SimpleDateFormat("HH:mm   EEEE dd/MMMM/yyyy", Locale.ENGLISH).format(this.getEndDate());
			text = text + "\nDeadline: \t" + deadline;
		} else {
			String start = new SimpleDateFormat("HH:mm   EEEE dd/MMMM/yyyy", Locale.ENGLISH).format(this.getStartDate());
			String end = new SimpleDateFormat("HH:mm   EEEE dd/MMMM/yyyy", Locale.ENGLISH).format(this.getEndDate());
			text = text + "\nTime: \t\tfrom \t" + start + "\n\t\tto \t" + end;
		}
		
		return text;
	}
	
	//@author A0119447Y
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
