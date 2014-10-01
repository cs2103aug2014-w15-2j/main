package includes;

import java.util.ArrayList;

public class Toolbox {
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
	public static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	public static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
	public static void showToUser(String contentsToBeShown) {
		System.out.println(contentsToBeShown);
	}
}
