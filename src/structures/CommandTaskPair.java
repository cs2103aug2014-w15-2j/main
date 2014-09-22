package structures;

import includes.Task;

public class CommandTaskPair {
	public String command;
	public Task task;
	
	public CommandTaskPair(String thisCommand, Task thisTask) {
		this.command = thisCommand;
		this.task = thisTask;
	}
}
