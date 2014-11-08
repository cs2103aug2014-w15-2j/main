package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
	
	@Test
	public void testDate1() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("tomorrow");
		TimeInterval results;
		try {
			Date startDate = formatter.parse("2014-11-09 00:00");
			Date endDate = formatter.parse("2014-11-09 23:59");
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
	public void testDate2() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("today");
		TimeInterval results;
		try {
			Date startDate = formatter.parse("2014-11-08 00:00");
			Date endDate = formatter.parse("2014-11-08 23:59");
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
		TimeInterval results;
		try {
			Date startDate = formatter.parse("2014-11-01 00:00");
			Date endDate = formatter.parse("2014-11-01 23:59");
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
	public void testDate5() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next Friday");
		dateList.add("from 14:00 to 18:00");
		TimeInterval results;
		try {
			Date startDate = formatter.parse("2014-11-14 14:00");
			Date endDate = formatter.parse("2014-11-14 18:00");
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
	public void testDate6() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next Friday");
		dateList.add("14:00 - 18:00");
		TimeInterval results;
		try {
			Date startDate = formatter.parse("2014-11-14 14:00");
			Date endDate = formatter.parse("2014-11-14 18:00");
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
