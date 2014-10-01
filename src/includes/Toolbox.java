package includes;

import java.util.ArrayList;

public class Toolbox {
	
	/**
	 * methods to convert a task list to a string
	 * @param list
	 * @return
	 */
	public static String taskListToString(ArrayList<Task> list) {
		String returnValue = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				returnValue = i+ ". " + list.get(i).toString();
			} else {
				returnValue = returnValue + "\n" + i + ". " + list.get(i).toString();
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
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
	
	
	/**
	 * print the string
	 * @param contentsToBeShown
	 */
	public static void showToUser(String contentsToBeShown) {
		System.out.println(contentsToBeShown);
	}
}
