package model;

import java.util.logging.Level;

import infrastructure.UtilityMethod;

public class CommandFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	//@author A0119447Y
	//Parameterless Constructor
    public CommandFailedException() {
    	super();
    }

    //@author A0119447Y
    //Constructor that accepts a message
    public CommandFailedException(String message)
    {
       super(message);
       UtilityMethod.logger.log(Level.INFO, message);
    }

}
