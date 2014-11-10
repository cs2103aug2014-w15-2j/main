package infrastructure;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import model.CommandFailedException;
import model.Task;
import model.TimeInterval;

import org.junit.Test;

public class ConverterTest {
	
	private static final String ERROR_TESTING = "failed in testing";

	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTaskToMapNull() {
		assertEquals(new LinkedHashMap(), Converter.convertTaskToMap(null));
	}
	
	@SuppressWarnings("rawtypes")
	public void testConvertTaskToMapFloating() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_MEDIUM, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(Constant.SAVE_FORMAT_NO_DATE, ((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL)).get(Constant.SAVE_STARTDATE));
			assertEquals(Constant.SAVE_FORMAT_NO_DATE, ((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL)).get(Constant.SAVE_ENDDATE));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void testConvertTaskToMapDeadline() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			Date endDate = new SimpleDateFormat(Constant.FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 10:00");
			time = new TimeInterval(null, endDate);
			Task task = new Task("task1", Constant.PRIORITY_MEDIUM, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(Constant.SAVE_FORMAT_NO_DATE, ((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL)).get(Constant.SAVE_STARTDATE));
			assertEquals("29-Nov-2014 10:00", ((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL)).get(Constant.SAVE_ENDDATE));
		} catch (CommandFailedException | ParseException e) {
			System.out.println(ERROR_TESTING);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void testConvertTaskToMapPeriod() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			Date startDate = new SimpleDateFormat(Constant.FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 10:00");
			Date endDate = new SimpleDateFormat(Constant.FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 12:00");
			time = new TimeInterval(startDate, endDate);
			Task task = new Task("task1", Constant.PRIORITY_MEDIUM, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals("29-Nov-2014 10:00", ((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL)).get(Constant.SAVE_STARTDATE));
			assertEquals("29-Nov-2014 12:00", ((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL)).get(Constant.SAVE_ENDDATE));
		} catch (CommandFailedException | ParseException e) {
			System.out.println(ERROR_TESTING);
		}
	}

}
