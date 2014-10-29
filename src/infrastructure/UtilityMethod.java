package infrastructure;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import reference.CommandFailedException;
import dataStructure.Task;

public abstract class UtilityMethod {
	private static Scanner scanner_ = new Scanner(System.in);
	
	public static int selectDifferentDate(Calendar c1, Calendar c2, Calendar c3) throws CommandFailedException {
		Calendar today = Calendar.getInstance();
		boolean c1same = c1.get(Calendar.YEAR) == today.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
		boolean c2same = c2.get(Calendar.YEAR) == today.get(Calendar.YEAR) && c2.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
		boolean c3same = c3.get(Calendar.YEAR) == today.get(Calendar.YEAR) && c3.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

		if (c1same && c2same && c2same) {
			if (c1.get(Calendar.HOUR_OF_DAY) == 0 && c1.get(Calendar.MINUTE) == 0) {
				return 1;
			} else if (c2.get(Calendar.HOUR_OF_DAY) == 0 && c2.get(Calendar.MINUTE) == 0) {
				return 2;
			} else {
				return 3;
			}
		} else if (!c1same && c2same && c3same) {
			return 1;
		} else if (c1same && !c2same && c3same) {
			return 2;
		} else  if (c1same && c2same && !c3same) {
			return 3;
		} else {
			throw new CommandFailedException("two dates are different from today");
		}
	}
	
	public static Calendar dateToCalendar(Date date){ 
		if (date == null) {
			return null;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		}
	}
	
	public static Date selectEarlierDate(Date d1, Date d2) {
		return d1.before(d2) ? d1 : d2;
	}
	
	public static Date selectLaterDate(Date d1, Date d2) {
		return d1.before(d2) ? d2 : d1;
	}
	
	public static String readCommand() {
		return scanner_.nextLine();
	}
	
	
	public static String priorityToString (int priority) {
		switch (priority) {
			case Constant.PRIORITY_LOW:
				return "low";
			case Constant.PRIORITY_MEDIUM:
				return "medium";
			case Constant.PRIORITY_HIGH:
				return "high";
			default:
				return "not sure";
		}
	}
	
	
	/**
	 * methods to convert a task list to a string
	 * @param list
	 * @return
	 */
	public static String taskListToString(ArrayList<Task> list) {
		String returnValue = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				returnValue = (i+1) + ". " + list.get(i).toStringForDisplaying();
			} else {
				returnValue = returnValue + "\n\n" + (i+1) + ". " + list.get(i).toStringForDisplaying();
			}
		}
		
		return returnValue;
	}
	
	
	/**
	 * to get the first word of a string
	 * @param userCommand
	 * @return
	 */
	public static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	
	
	/**
	 * to remove the first word of a string
	 * @param userCommand
	 * @return
	 */
	public static String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
	}
	
	
	/**
	 * print the string
	 * @param contentsToBeShown
	 */
	public static void showToUser(String contentsToBeShown) {
		System.out.println(contentsToBeShown);
	}
}
