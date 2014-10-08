package reference;

import java.text.SimpleDateFormat;
import java.util.Date;

import reference.*;

public class TimeInterval {
	private Date startDate;
	private Date endDate;
	
	/**
	 * constructor
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	public TimeInterval(Date startDate, Date endDate) throws Exception {
		if ((startDate == null) && (endDate == null)) {
			this.startDate = Constant.FLOATING_START_DATE;
			this.endDate = Constant.FLOATING_END_DATE;
			
		} else if (startDate == null) {
			this.startDate = Constant.DEADLINE_START_DATE;
		} else if (startDate.after(endDate)) {
			throw new Exception("invalid time interval");
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
	
	/**
	 * @override
	 * toString
	 * 
	 * @return
	 */
	public String toString() {
		String text = new String();
		if (this.getStartDate().equals(Constant.FLOATING_START_DATE)) {
			// floating task, do nothing
		} else if (this.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			// deadline task
			String deadline = new SimpleDateFormat("dd/MMMM/yyyy HH:mm").format(this.getEndDate());
			text = text + "\n\t deadline: " + deadline + ";\n";
		} else {
			String start = new SimpleDateFormat("dd/MMMM/yyyy HH:mm").format(this.getStartDate());
			String end = new SimpleDateFormat("dd/MMMM/yyyy HH:mm").format(this.getEndDate());
			text = text + "\n\t from " + start + " to " + end + ";\n";
		}
		
		return text;
	}
}
