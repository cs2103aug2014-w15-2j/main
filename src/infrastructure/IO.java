package infrastructure;

import java.util.Scanner;
import views.*;

public class IO {

	private static Scanner scanner_ = new Scanner(System.in);
	private Constant.UI_MODE mode = null;
	public static IO cliIO = new IO(Constant.UI_MODE.CLI);
	public static IO guiIO = new IO(Constant.UI_MODE.GUI);
	
	public IO(Constant.UI_MODE mode) {
		this.mode = mode;
	}
	/**
	 * print the string
	 * @param contentsToBeShown
	 */
	public void showToUser(String contentsToBeShown, Object caller) {
		if (this.mode.equals(Constant.UI_MODE.CLI)) {
			System.out.println(contentsToBeShown);
		} else if (this.mode.equals(Constant.UI_MODE.GUI)) {
			((ListOfXiaoMingViewsController) caller).setDisplay(contentsToBeShown);
		}
	}
	
	public String readCommand(Object caller) {
		if (this.mode.equals(Constant.UI_MODE.CLI)) {
			return scanner_.nextLine();
		} else if (this.mode.equals(Constant.UI_MODE.GUI)) {
			return ((ListOfXiaoMingViewsController) caller).getUserInput();
		} else {
			return null;
		}
	}
}
