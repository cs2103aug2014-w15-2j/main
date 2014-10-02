package dataStructure;

public class CommandFailedException extends Exception {

	//Parameterless Constructor
    public CommandFailedException() {
    	super();
    }

    //Constructor that accepts a message
    public CommandFailedException(String message)
    {
       super(message);
    }

}
