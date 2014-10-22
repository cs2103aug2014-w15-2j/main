package test;

import static org.junit.Assert.assertEquals;
import infrastructure.NERParser;

import java.util.ArrayList;

import org.junit.Test;

import reference.CommandFailedException;

public class NERTestDate {
	static final String TERMINATOR = "\n";
	
	@Test
	public void testDate1() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("tomorrow");
		String results;
		String expected = "\n\t from 22/October/2014 00:01 to 22/October/2014 23:59;";
		System.out.println("testDate1:");
		System.out.println("expects: " + expected);
		try {
			results = NERParser.parseTimeInterval(dateList).toString();
			System.out.println("results: " + results);
			assertEquals(expected + TERMINATOR, results);
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate2() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("today");
		String results;
		String expected = "\n\t from 21/October/2014 00:01 to 21/October/2014 23:59;";
		System.out.println("testDate2:");
		System.out.println("expects: " + expected);
		try {
			results = NERParser.parseTimeInterval(dateList).toString();
			System.out.println("results: " + results);
			assertEquals(expected + TERMINATOR, results);
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate3() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("this Friday");
		String results;
		String expected = "\n\t from 24/October/2014 00:01 to 24/October/2014 23:59;";
		System.out.println("testDate3:");
		System.out.println("expects: " + expected);
		try {
			results = NERParser.parseTimeInterval(dateList).toString();
			System.out.println("results: " + results);
			assertEquals(expected + TERMINATOR, results);
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate4() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("1 Nov");
		String results;
		String expected = "\n\t from 01/November/2014 00:01 to 01/November/2014 23:59;";
		System.out.println("testDate4:");
		System.out.println("expects: " + expected);
		try {
			results = NERParser.parseTimeInterval(dateList).toString();
			System.out.println("results: " + results);
			assertEquals(expected + TERMINATOR, results);
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate5() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next Friday");
		dateList.add("from 14:00 to 18:00");
		String results;
		String expected = "\n\t from 31/October/2014 14:00 to 31/October/2014 18:00;";
		System.out.println("testDate5:");
		System.out.println("expects: " + expected);
		try {
			results = NERParser.parseTimeInterval(dateList).toString();
			System.out.println("results: " + results);
			assertEquals(expected + TERMINATOR, results);
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate6() {
		ArrayList<String> dateList= new ArrayList<String>();
		dateList.add("next Friday");
		dateList.add("14:00 - 18:00");
		String results;
		String expected = "\n\t from 31/October/2014 14:00 to 31/October/2014 18:00;";
		System.out.println("testDate6:");
		System.out.println("expects: " + expected);
		try {
			results = NERParser.parseTimeInterval(dateList).toString();
			System.out.println("results: " + results);
			assertEquals(expected + TERMINATOR, results);
		} catch (CommandFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
