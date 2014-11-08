package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import infrastructure.Constant;
import infrastructure.NERParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import modal.CommandFailedException;
import modal.TimeInterval;

import org.junit.Test;

public class NERTestDate {
	static final String TERMINATOR = "\n";
	static NERParser nerParser = new NERParser();
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
		this.testTimed(dateList, "2014-11-09 00:00", "2014-11-09 23:59");
	}
	
	@Test
	public void testDate2() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("today");
		this.testTimed(dateList, "2014-11-08 00:00", "2014-11-08 23:59");
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
		dateList.add("2014 Oct 18");
		dateList.add("14:00 - 18:00");	
		this.testTimed(dateList, "2014-10-18 14:00", "2014-10-18 18:00");
	}
	
	
	/**
	 * ====================================================================
	 * test cases for a deadline
	 * ====================================================================
	 */
	
	@Test
	public void testDate9() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("2014 Oct 18 09:00");
		this.testDeadline(dateList, "2014-10-18 09:00");
	}
	
	@Test
	public void testDate10() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("today 09:00");
		this.testDeadline(dateList, "2014-11-08 09:00");
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
		} catch (CommandFailedException e) {
			fail();
			e.printStackTrace();
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
		} catch (CommandFailedException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	
}
