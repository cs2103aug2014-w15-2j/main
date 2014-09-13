package includes;

import java.sql.Timestamp;
import java.util.Date;

public class TimeInterval {
	private Timestamp startDate;
	private Timestamp endDate;
	
	/**
	 * constructor
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	public TimeInterval(Timestamp startDate, Timestamp endDate) throws Exception {
		if (startDate.after(endDate)) {
			throw new Exception("invalid time interval");
		} else {
			this.startDate = startDate;
			this.endDate = endDate;
		}
	}
	
	/**
	 * getStartDate
	 * @return start date
	 */
	public Date getStartDate() {
		return (Date) this.startDate;
	}
	
	/**
	 * getEndDate
	 * @return end date
	 */
	public Date getEndDate() {
		return (Date) this.endDate;
	}
}
