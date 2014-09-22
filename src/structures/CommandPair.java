package structures;

import java.util.ArrayList;

public class CommandPair {
	public String command;
	public ArrayList<String> parameters;
	
	public CommandPair(String thisCommand, ArrayList<String> thisParameters) {
		this.command = thisCommand;
		this.parameters = thisParameters;
	}
}
