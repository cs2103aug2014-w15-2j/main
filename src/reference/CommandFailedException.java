package reference;

public class CommandFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
