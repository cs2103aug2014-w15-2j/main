package dataStructure;

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
	public TimeInterval(Date startDate, Date endDate) throws Exception {
		if (startDate == null) {
			startDate = this.startDate = new Date(0L);
		}
		
		if (endDate == null) {
			endDate = this.endDate = new Date(Long.MAX_VALUE);
		}
		
		
		if (startDate.after(endDate)) {
			throw new Exception("invalid time interval");
		} else {
			this.startDate = startDate;
			this.endDate = endDate;
		}
	}
	
	public TimeInterval() {
		this.startDate = new Date(0L);
		this.endDate = new Date(Long.MAX_VALUE);
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
}
