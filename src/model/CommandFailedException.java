package model;

public class CommandFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
	//@author A0119447Y
    public CommandFailedException() {
    	super();
    }

    //Constructor that accepts a message
	//@author A0119447Y
    public CommandFailedException(String message)
    {
       super(message);
    }

}
