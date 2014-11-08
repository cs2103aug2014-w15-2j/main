package test;

import static org.junit.Assert.*;
import infrastructure.Constant;
import infrastructure.NerParser;
import infrastructure.UtilityMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import modal.CommandFailedException;
import modal.TimeInterval;

import org.junit.Before;
import org.junit.Test;

public class NerDatePickerTest {
	
	private static String DATE_TODAY;
	private static  String DATE_TOMORROW;
	private static  String[] thisWeek = {"2014-11-03", "2014-11-04", "2014-11-05", "2014-11-06", "2014-11-07", "2014-11-08", "2014-11-09"};
	private static  String[] nextWeek = {"2014-11-10", "2014-11-11", "2014-11-12", "2014-11-13", "2014-11-14", "2014-11-15", "2014-11-16"};
	static NerParser nerParser = new NerParser();
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);

	@Before
	public void setUp() throws Exception {
		SimpleDateFormat setUpFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Calendar c = UtilityMethod.dateToCalendar(new Date());
		DATE_TODAY = setUpFormatter.format(c.getTime());
		c.add(Calendar.DAY_OF_MONTH, 1);
		DATE_TOMORROW = setUpFormatter.format(c.getTime());		
	}

	@Test
	public void testToday() {
		String[] expressions = {"today", "TODAY", "Today"};
		
		for (String expression : expressions) {
			this.testTimeIntervalEqual("add task " + expression, DATE_TODAY + " 00:00", DATE_TODAY + " 23:59");
			this.testTimeIntervalEqual("add task by " + expression, "DEADLINE", DATE_TODAY + " 23:59");
			this.testTimeIntervalEqual("add task before " + expression, "DEADLINE", DATE_TODAY + " 23:59");
			this.testTimeIntervalEqual("add task until " + expression, "DEADLINE", DATE_TODAY + " 23:59");
			this.testTimeIntervalEqual("add task till " + expression, "DEADLINE", DATE_TODAY + " 23:59");
		}
	}
	
	@Test
	public void testTomorrow() {
		String[] expressions = {"tmr", "TMR", "Tmr", "tomorrow", "TOMORROW", "Tomorrow"};
		
		for (String expression : expressions) {
			this.testTimeIntervalEqual("add task " + expression, DATE_TOMORROW + " 00:00", DATE_TOMORROW + " 23:59");
			this.testTimeIntervalEqual("add task by " + expression, "DEADLINE", DATE_TOMORROW + " 23:59");
			this.testTimeIntervalEqual("add task before " + expression, "DEADLINE", DATE_TOMORROW + " 23:59");
			this.testTimeIntervalEqual("add task until " + expression, "DEADLINE", DATE_TOMORROW + " 23:59");
			this.testTimeIntervalEqual("add task till " + expression, "DEADLINE", DATE_TOMORROW + " 23:59");
		}
	}
	
	
	
	@Test
	public void testWeekDay() {
		String[][] weeks = {{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"},
				{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"},
				{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"},
				{"mon", "tue", "wed", "thur", "fri", "sat", "sun"},
				{"MON", "TUE", "WED", "THUR", "FRI", "SAT", "SUN"},
				{"Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"}};
		
		String[] modifiers = {"this", "next", "THIS", "NEXT"};
		
		for (String modifier : modifiers) {
			switch (modifier.toLowerCase()) {
				case "next":
					for (String[] week : weeks) {
						for (int i = 0; i < 7; i ++) {
							this.testTimeIntervalEqual("add task " + modifier + " " + week[i], nextWeek[i] + " 00:00", nextWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task by " + modifier + " " + week[i], "DEADLINE", nextWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task before " + modifier + " " + week[i], "DEADLINE", nextWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task until " + modifier + " " + week[i], "DEADLINE", nextWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task till " + modifier + " " + week[i], "DEADLINE", nextWeek[i] + " 23:59");
						}
					}
					break;
					
				case "this":
					for (String[] week : weeks) {
						for (int i = 0; i < 7; i ++) {
							this.testTimeIntervalEqual("add task " + modifier + " " + week[i], thisWeek[i] + " 00:00", thisWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task by " + modifier + " " + week[i], "DEADLINE", thisWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task before " + modifier + " " + week[i], "DEADLINE", thisWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task until " + modifier + " " + week[i], "DEADLINE", thisWeek[i] + " 23:59");
							this.testTimeIntervalEqual("add task till " + modifier + " " + week[i], "DEADLINE", thisWeek[i] + " 23:59");
						}
					}
					break;
			}
		}
	}
	
	
	
	@Test
	public void testSpecificDay() {
		String[] expressions = {"Oct 28", "28 Oct", "2014 Oct 28", "2014-10-28", "2014 Oct 28"};
		
		for (String expression : expressions) {
			this.testTimeIntervalEqual("add task " + expression, "2014-10-28" + " 00:00", "2014-10-28" + " 23:59");
			this.testTimeIntervalEqual("add task by " + expression, "DEADLINE", "2014-10-28" + " 23:59");
			this.testTimeIntervalEqual("add task before " + expression, "DEADLINE", "2014-10-28" + " 23:59");
			this.testTimeIntervalEqual("add task until " + expression, "DEADLINE", "2014-10-28" + " 23:59");
			this.testTimeIntervalEqual("add task till " + expression, "DEADLINE", "2014-10-28" + " 23:59");
		}
	}
	
	@Test
	public void testWeek() {
		//Monday is considered as the first day of a week
		this.testTimeIntervalEqual("add task this week", thisWeek[0] + " 00:00", thisWeek[6] + " 23:59");
		this.testTimeIntervalEqual("add task next week", nextWeek[0] + " 00:00", nextWeek[6] + " 23:59");
	}
	
	@Test
	public void testMonth() {
		//Monday is considered as the first day of a week
		this.testTimeIntervalEqual("add task this month", "2014-11-01 00:00", "2014-11-30 23:59");
		this.testTimeIntervalEqual("add task next month", "2014-12-01 00:00", "2014-12-31 23:59");
	}
	
	@Test
	public void testYear() {
		//Monday is considered as the first day of a week
		this.testTimeIntervalEqual("add task this year", "2014-01-01 00:00", "2014-12-31 23:59");
		this.testTimeIntervalEqual("add task next year", "2015-01-01 00:00", "2015-12-31 23:59");
	}
	
	@Test
	public void testExploratory() {
		
	}
	
	private void testTimeIntervalEqual(String userInputString, String startDateString, String endDateString) {
		TimeInterval results;
		try {
			Date startDate;
			if (!startDateString.equals("DEADLINE")) {
				startDate = formatter.parse(startDateString);
			} else {
				startDate = Constant.DEADLINE_START_DATE;
			}
			Date endDate = formatter.parse(endDateString);
			TimeInterval expected = new TimeInterval(startDate, endDate);
			results = nerParser.pickTimeInterval(userInputString);
			assertEquals(expected.toString(), results.toString());
		} catch (ParseException e1) {
			e1.printStackTrace();
			fail();
		} catch (CommandFailedException e) {
			e.printStackTrace();
			fail();
		}
	}

}
