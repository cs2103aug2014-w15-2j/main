package infrastructure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import model.CommandFailedException;
import model.Pair;
import model.Task;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public abstract class UtilityMethod {
	//@author A0119379R

	public static int selectDifferentDate(Calendar c1, Calendar c2, Calendar c3)
			throws CommandFailedException {
		Calendar today = Calendar.getInstance();
		boolean c1same = c1.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& c1.get(Calendar.DAY_OF_YEAR) == today
						.get(Calendar.DAY_OF_YEAR);
		boolean c2same = c2.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& c2.get(Calendar.DAY_OF_YEAR) == today
						.get(Calendar.DAY_OF_YEAR);
		boolean c3same = c3.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& c3.get(Calendar.DAY_OF_YEAR) == today
						.get(Calendar.DAY_OF_YEAR);

		if (c1same && c2same && c2same) {
			if (c1.get(Calendar.HOUR_OF_DAY) == 0
					&& c1.get(Calendar.MINUTE) == 0) {
				return 1;
			} else if (c2.get(Calendar.HOUR_OF_DAY) == 0
					&& c2.get(Calendar.MINUTE) == 0) {
				return 2;
			} else {
				return 3;
			}
		} else if (!c1same && c2same && c3same) {
			return 1;
		} else if (c1same && !c2same && c3same) {
			return 2;
		} else if (c1same && c2same && !c3same) {
			return 3;
		} else {
			throw new CommandFailedException(
					"two dates are different from today");
		}
	}

	public static Calendar dateToCalendar(Date date) {
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

	public static String priorityToString(int priority) {
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
	 * 
	 * @param list
	 * @return
	 */
	public static String taskListToString(ArrayList<Task> list) {
		String returnValue = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				returnValue = (i + 1) + ". "
						+ list.get(i).toStringForDisplaying();
			} else {
				returnValue = returnValue + "\n\n" + (i + 1) + ". "
						+ list.get(i).toStringForDisplaying();
			}
		}

		return returnValue;
	}

	/**
	 * to get the first word of a string
	 * 
	 * @param userCommand
	 * @return
	 */
	public static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}

	/**
	 * to remove the first word of a string
	 * 
	 * @param userCommand
	 * @return
	 */
	public static String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
	}

	/**
	 * print the string
	 * 
	 * @param contentsToBeShown
	 */
	public static void showToUser(String contentsToBeShown) {
		System.out.println(contentsToBeShown);
	}

	public static void copyFile(InputStream in, String d) throws IOException {
		File dest = new File(d);
		
		if (!dest.exists()) {
			dest.createNewFile();
			OutputStream out = null;
			try {
				out = new FileOutputStream(dest);
	
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
		}
	}

	  
	public static void copyUserNlpFiles(InputStream[] tsvStreams, InputStream[] propStreams, InputStream[] gzStreams) throws IOException {
		assert (tsvStreams.length == 11);
		assert (propStreams.length == 11);
		
		System.err.println("initializeUserTrainingModel");
		File rootDirectory = new File(Constant.FILE_PATH_ROOT);
		if (!rootDirectory.exists()) {
			rootDirectory.mkdir();
		}
	
		File nlpRootDirectory = new File(Constant.FILE_PATH_NLP_ROOT);
		if (!nlpRootDirectory.exists()) {
			nlpRootDirectory.mkdir();
		}
		
		for (int i = 0; i < 11; i++) {
			copyFile(tsvStreams[i], Constant.TSVS_USER[i]);
			copyFile(propStreams[i], Constant.PROPS_USER[i]);
			copyFile(gzStreams[i], Constant.GZS_USER[i]);
		}
	}
	
	
	public static void makeDraggable(final Stage stage, final Node draggedNode) {
		final Pair<Double, Double> dragDelta = new Pair<Double, Double>();
		draggedNode.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				dragDelta.setHead(stage.getX() - mouseEvent.getScreenX());
				dragDelta.setTail(stage.getY() - mouseEvent.getScreenY());
				draggedNode.setCursor(Cursor.MOVE);
			}
		});
		
		draggedNode.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				draggedNode.setCursor(Cursor.HAND);
			}
		});
		
		draggedNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				stage.setX(mouseEvent.getScreenX() + dragDelta.head);
				stage.setY(mouseEvent.getScreenY() + dragDelta.tail);
			}
		});
		
		draggedNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					draggedNode.setCursor(Cursor.HAND);
				}
			}
		});
		
		draggedNode.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					draggedNode.setCursor(Cursor.DEFAULT);
				}
			}
		});
	}


	//@author A0119379R
	// logger
	public static Logger logger = Logger.getLogger("ListOfXiaoMing");
}

