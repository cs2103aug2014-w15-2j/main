package test;

import static org.junit.Assert.assertEquals;
import infrastructure.Constant;
import infrastructure.Converter;

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

	// These tests are for convertTaskToMap() method from Converter //
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTaskToMapNull() {
		LinkedHashMap taskMap = new LinkedHashMap();
		assertEquals(taskMap, Converter.convertTaskToMap(null));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void testConvertTaskToMapFloating() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_MEDIUM, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(Constant.SAVE_FORMAT_NO_DATE,
					((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL))
							.get(Constant.SAVE_STARTDATE));
			assertEquals(Constant.SAVE_FORMAT_NO_DATE,
					((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL))
							.get(Constant.SAVE_ENDDATE));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@Test
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
			assertEquals(Constant.SAVE_FORMAT_NO_DATE,
					((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL))
							.get(Constant.SAVE_STARTDATE));
			assertEquals("29-November-2014 10:00",
					((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL))
							.get(Constant.SAVE_ENDDATE));
		} catch (CommandFailedException | ParseException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@Test
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
			assertEquals("29-November-2014 10:00",
					((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL))
							.get(Constant.SAVE_STARTDATE));
			assertEquals("29-November-2014 12:00",
					((LinkedHashMap) taskMap.get(Constant.SAVE_TIME_INTERVAL))
							.get(Constant.SAVE_ENDDATE));
		} catch (CommandFailedException | ParseException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTasktoMapPriorityHigh() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_HIGH, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(Constant.PRIORITY_STRING_HIGH,
					(taskMap.get(Constant.SAVE_PRIORITY)));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTasktoMapPriorityMedium() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_MEDIUM, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(Constant.PRIORITY_STRING_MEDIUM,
					(taskMap.get(Constant.SAVE_PRIORITY)));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTasktoMapPriorityLow() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_LOW, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(Constant.PRIORITY_STRING_LOW,
					(taskMap.get(Constant.SAVE_PRIORITY)));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTasktoMapTagEmpty() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_LOW, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(tags, (taskMap.get(Constant.SAVE_TAGS)));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTasktoMapTags() {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("tag1");
		tags.add("tag2");
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_LOW, tags, time);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals(tags, (taskMap.get(Constant.SAVE_TAGS)));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testConvertTasktoMapDescriptionAndStatus() {
		ArrayList<String> tags = new ArrayList<String>();
		TimeInterval time;
		try {
			time = new TimeInterval(null, null);
			Task task = new Task("task1", Constant.PRIORITY_LOW, tags, time);
			task.setStatus(Constant.TASK_STATUS_DONE);
			LinkedHashMap taskMap = Converter.convertTaskToMap(task);
			assertEquals("task1", taskMap.get(Constant.SAVE_DESCRIPTION));
			assertEquals(Constant.TASK_STATUS_DONE,
					taskMap.get(Constant.SAVE_STATUS));
		} catch (CommandFailedException e) {
			System.out.println(ERROR_TESTING);
		}
	}

	// These tests are for convertTaskToMap() method from Converter //
	
	@Test
	public void testConvertMapToTaskNull() {
		try {
			assertEquals(null, Converter.convertMapToTask(null));
		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskFloating() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_MEDIUM);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "-");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			taskMap.put(Constant.SAVE_TAGS, tags);

			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(Constant.FLOATING_START_DATE, task.getInterval()
					.getStartDate());
			assertEquals(Constant.FLOATING_END_DATE, task.getInterval()
					.getEndDate());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskDeadline() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_MEDIUM);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "29-November-2014 12:00");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			taskMap.put(Constant.SAVE_TAGS, tags);

			Date endDate = new SimpleDateFormat(Constant.FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 12:00");
			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(Constant.DEADLINE_START_DATE, task.getInterval()
					.getStartDate());
			assertEquals(endDate, task.getInterval().getEndDate());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskPeriod() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_MEDIUM);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "29-November-2014 10:00");
			timeMap.put(Constant.SAVE_ENDDATE, "29-November-2014 12:00");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			taskMap.put(Constant.SAVE_TAGS, tags);

			Date startDate = new SimpleDateFormat(Constant.FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 10:00");
			Date endDate = new SimpleDateFormat(Constant.FORMAT_DATE,
					Locale.ENGLISH).parse("29-Nov-2014 12:00");
			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(startDate, task.getInterval().getStartDate());
			assertEquals(endDate, task.getInterval().getEndDate());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskPriorityHigh() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_HIGH);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "-");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			taskMap.put(Constant.SAVE_TAGS, tags);

			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(Constant.PRIORITY_HIGH, task.getPriority());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskPriorityMedium() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_MEDIUM);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "-");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			taskMap.put(Constant.SAVE_TAGS, tags);

			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(Constant.PRIORITY_MEDIUM, task.getPriority());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskPriorityLow() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_LOW);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "-");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			taskMap.put(Constant.SAVE_TAGS, tags);

			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(Constant.PRIORITY_LOW, task.getPriority());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskTagEmpty() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_LOW);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "-");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			taskMap.put(Constant.SAVE_TAGS, tags);

			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(new ArrayList<String>(), task.getTag());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskTags() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_LOW);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_ONGOING);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "-");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			tags.add("tag1");
			tags.add("tag2");
			taskMap.put(Constant.SAVE_TAGS, tags);

			ArrayList<String> tag = new ArrayList<String>();
			tag.add("tag1");
			tag.add("tag2");
			Task task = Converter.convertMapToTask(taskMap);
			assertEquals(tag, task.getTag());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConvertMapToTaskDecriptionaAndStatus() {
		try {
			LinkedHashMap taskMap = new LinkedHashMap();
			taskMap.put(Constant.SAVE_DESCRIPTION, "task1");
			taskMap.put(Constant.SAVE_PRIORITY, Constant.PRIORITY_STRING_LOW);
			taskMap.put(Constant.SAVE_STATUS, Constant.TASK_STATUS_DONE);
			LinkedHashMap timeMap = new LinkedHashMap();
			timeMap.put(Constant.SAVE_STARTDATE, "-");
			timeMap.put(Constant.SAVE_ENDDATE, "-");
			taskMap.put(Constant.SAVE_TIME_INTERVAL, timeMap);
			ArrayList tags = new ArrayList();
			tags.add("tag1");
			tags.add("tag2");
			taskMap.put(Constant.SAVE_TAGS, tags);

			Task task = Converter.convertMapToTask(taskMap);
			assertEquals("task1", task.getDescription());
			assertEquals(Constant.TASK_STATUS_DONE, task.getStatus());

		} catch (Exception e) {
			System.out.println(ERROR_TESTING);
		}
	}

}
