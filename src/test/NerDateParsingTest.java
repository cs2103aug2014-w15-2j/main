package test;

import static org.junit.Assert.*;
import infrastructure.Constant;
import infrastructure.NerParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import modal.CommandFailedException;
import modal.TimeInterval;

import org.junit.Test;

public class NerDateParsingTest {
	static NerParser nerParser = new NerParser();
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);
	
	
	/**
	 * ====================================================================
	 * test cases for a timed task
	 * ====================================================================
	 */
	
	@Test
	public void testDate1() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("tomorrow");
		this.testTimed(dateList, "2014-11-10 00:00", "2014-11-10 23:59");
	}
	
	@Test
	public void testDate2() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("today");
		this.testTimed(dateList, "2014-11-09 00:00", "2014-11-09 23:59");
	}
	
	@Test
	public void testDate3() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next Friday");
		TimeInterval results;
		try {
			Date startDate = formatter.parse("2014-11-14 00:00");
			Date endDate = formatter.parse("2014-11-14 23:59");
			TimeInterval expected = new TimeInterval(startDate, endDate);
			results = nerParser.parseTimeInterval(dateList);
			assertEquals(expected.toString(), results.toString());
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (CommandFailedException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate4() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("1 Nov");
		this.testTimed(dateList, "2014-11-01 00:00", "2014-11-01 23:59");
	}
	
	@Test
	public void testDate5() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("this Friday");
		dateList.add("from 14:00 to 18:00");
		this.testTimed(dateList, "2014-11-07 14:00", "2014-11-07 18:00");
	}
	
	@Test
	public void testDate6() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next Friday");
		dateList.add("14:00 - 18:00");
		this.testTimed(dateList, "2014-11-14 14:00", "2014-11-14 18:00");
	}
	
	@Test
	public void testDate7() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("2014-11-08");
		dateList.add("14:00 - 18:00");
		this.testTimed(dateList, "2014-11-08 14:00", "2014-11-08 18:00");
	}
	
	@Test
	public void testDate8() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("2014 oct 18");
		dateList.add("14:00 - 18:00");	
		this.testTimed(dateList, "2014-10-18 14:00", "2014-10-18 18:00");
	}
	
	@Test
	public void testDate9() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("oct 18");
		this.testTimed(dateList, "2014-10-18 00:00", "2014-10-18 23:59");
	}
	
	@Test
	public void testDate10() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("Oct 18, 2015");
		this.testTimed(dateList, "2015-10-18 00:00", "2015-10-18 23:59");
	}
	
	@Test
	public void testDate11() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("2015 Oct 18");
		this.testTimed(dateList, "2015-10-18 00:00", "2015-10-18 23:59");
	}
	
	@Test
	public void testDate12() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("this week");
		this.testTimed(dateList, "2014-11-03 00:00", "2014-11-09 23:59");
	}
	
	@Test
	public void testDate13() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next week");
		this.testTimed(dateList, "2014-11-10 00:00", "2014-11-16 23:59");
	}
	
	@Test
	public void testDate14() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("this month");
		this.testTimed(dateList, "2014-11-01 00:00", "2014-11-30 23:59");
	}
	
	@Test
	public void testDate15() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next month");
		this.testTimed(dateList, "2014-12-01 00:00", "2014-12-31 23:59");
	}
	
	@Test
	public void testDate16() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("this year");
		this.testTimed(dateList, "2014-01-01 00:00", "2014-12-31 23:59");
	}
	
	@Test
	public void testDate17() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next year");
		this.testTimed(dateList, "2015-01-01 00:00", "2015-12-31 23:59");
	}
	/**
	 * ====================================================================
	 * test cases for a deadline
	 * ====================================================================
	 */
	
	@Test
	public void testDate2_1() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("2014 Oct 18 09:00");
		this.testDeadline(dateList, "2014-10-18 09:00");
	}
	
	@Test
	public void testDate2_2() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("today 09:00");
		this.testDeadline(dateList, "2014-11-09 09:00");
	}
	
	
	
	
	
	private void testTimed (ArrayList<String> dateList, String startDateString, String endDateString) {
		TimeInterval results;
		try {
			Date startDate = formatter.parse(startDateString);
			Date endDate = formatter.parse(endDateString);
			TimeInterval expected = new TimeInterval(startDate, endDate);
			results = nerParser.parseTimeInterval(dateList);
			assertEquals(expected.toString(), results.toString());
		} catch (ParseException e1) {
			e1.printStackTrace();
			fail();
		} catch (CommandFailedException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private void testDeadline(ArrayList<String> dateList, String deadline) {
		TimeInterval results;
		try {
			Date startDate = Constant.DEADLINE_START_DATE;
			Date endDate = formatter.parse(deadline);
			TimeInterval expected = new TimeInterval(startDate, endDate);
			results = nerParser.parseTimeInterval(dateList);
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
