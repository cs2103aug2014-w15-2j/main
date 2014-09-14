package includes;

import java.util.ArrayList;
import java.util.Date;

public class TimedTask extends Task {
	TimeInterval interval;
	
	/**
	 * constructor
	 * @param description
	 * @param category
	 * @param priority
	 * @param task_id
	 * @param repeated_period
	 * @param tag
	 * @param interval
	 */
	public TimedTask(String description, 
					 String category, 
					 int priority, 
					 String task_id, 
					 int repeated_period, 
					 ArrayList<String> tag,
					 Date startDate,
					 Date endDate) {
		super(description, category, priority, task_id, repeated_period, tag);
		try {
			this.interval = new TimeInterval(startDate, endDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.type = TaskType.TIMED;
	}
	
	
	/**
	 * @override getInterval
	 * @return interval
	 */
	public TimeInterval getInterval() {
		return this.interval;
	}

}
