package dataStructure;

import infrastructure.Constant;
import infrastructure.UtilityMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import dataStore.DataStore;
import reference.*;

public class User {
	
	private ArrayList<Task> currentTasks;
	private ArrayList<String> validCategory;
	private Stack<ArrayList<Task>> undoable;
	private Stack<ArrayList<Task>> redoable;
	private String username;

	/**
	 * constructor
	 * 
	 * @param recordFilePath
	 * @throws Exception
	 */
	public User(String recordFilePath) throws Exception {
		String userFilePath = recordFilePath;
		File userFile = new File(userFilePath);
		undoable = new Stack<ArrayList<Task>>();
		redoable = new Stack<ArrayList<Task>>();
		currentTasks = DataStore.getCurrentTasks(userFile);
		validCategory = getValidCategory();
		username = recordFilePath;
	}

	/**
	 * undo resets the current task list to one step backwards.
	 * 
	 * @throws CommandFailedException
	 */
	public void undo() throws CommandFailedException {
		if (this.undoable.empty()) {
			throw new CommandFailedException(Constant.NO_UNDOABLE_ERROR_MESSAGE);
		} else {
			this.redoable.push(this.currentTasks);

			if (this.redoable.size() > Constant.MAXIMUM_REDO_TIMES) {
				this.redoable.remove(0);
			}

			this.currentTasks = this.undoable.pop();
		}
	}

	/**
	 * redo resets the current task list to one step forwards.
	 * 
	 * @throws CommandFailedException
	 */
	public void redo() throws CommandFailedException {
		if (this.redoable.empty()) {
			throw new CommandFailedException(Constant.NO_REDOABLE_ERROR_MESSAGE);
		} else {
			this.undoable.push(this.currentTasks);

			if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
				this.undoable.remove(0);
			}

			this.currentTasks = this.redoable.pop();
		}
	}

	/**
	 * updateUndoable this method should be called BEFORE every operation
	 * involving task list
	 */
	private void updateUndoable() {
		this.redoable.clear();
		this.undoable.push(this.currentTasks);
		if (this.undoable.size() > Constant.MAXIMUM_UNDO_TIMES) {
			this.undoable.remove(0);
		}
	}

	/**
	 * add adds a task into the task list.
	 * 
	 * @param task
	 */
	public boolean add(Task task) {
		this.updateUndoable();
		this.currentTasks.add(task);
		boolean isSuccessful = DataStore.save(this.username, this.currentTasks);
		return isSuccessful;
	}

	/**
	 * delete deletes the task with the index from the task list.
	 * 
	 * @param index
	 * @throws CommandFailedException
	 */
	public boolean delete(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			boolean isSuccessful = this.currentTasks.get(index).addTag(Constant.TRASHED_TAG);
			DataStore.save(this.username, this.currentTasks);
			return isSuccessful;
		}
	}

	/**
	 * update updates the task with the index according to the key-value pairs in toBeUpdated.
	 * 
	 * @param index
	 * @param toBeUpdated attributes to be updated
	 * @throws CommandFailedException 
	 */
	@SuppressWarnings("unchecked")
	public void update(int index, HashMap<String, Object> toBeUpdated) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Task task = this.currentTasks.get(index);
			Iterator<String> attributes = toBeUpdated.keySet().iterator();
			while (attributes.hasNext()) {
				String currentAttribute = attributes.next();
				Object currentObject = toBeUpdated.get(currentAttribute);
				
				if (currentAttribute.equals("description")) {
					task.setDescription((String) currentObject);
				} else if (currentAttribute.equals("category") && validCategory.contains((String) currentObject)) {
					task.setCategory((String) currentObject);
				} else if (currentAttribute.equals("priority")) {
					task.setPriority((int) currentObject);
				} else if (currentAttribute.equals("repeated_period")) {
					task.setRepeatedPeriod((int) currentObject);
				} else if (currentAttribute.equals("tag")) {
					task.setTag((ArrayList<String>) currentObject);
				} else if (currentAttribute.equals("time_interval")) {
					task.setInterval((TimeInterval) currentObject);
				} else {
					throw new CommandFailedException(Constant.INVALID_UPDATE_MESSAGE);
				}
			}
		}
		DataStore.save(this.username, this.currentTasks);
	}
	
	/**
	 * get valid categories from current tasks' categories
	 * 
	 * @return
	 */
	private ArrayList<String> getValidCategory(){
		ArrayList<String> validCategory = new ArrayList<String>();
		if(currentTasks.equals(null) || currentTasks.isEmpty()){
			
		} else {
			for(Task task : currentTasks){
				validCategory.add(task.getCategory());
			}
		}
		return validCategory;
	}
	/**
	 * create a new category
	 * @param category
	 * @throws CommandFailedException
	 */
	public void createCategory(String category) throws CommandFailedException{
		if (validCategory.contains(category)) {
			throw new CommandFailedException("invalid category");
		} else {
			validCategory.add(category);
		}
	}
	
	/**
	 * delete a category
	 *  
	 * @param category
	 * @throws CommandFailedException
	 */
	public void deleteCategory(String category) throws CommandFailedException{
		if (!validCategory.contains(category)) {
			throw new CommandFailedException("no such category");
		} else {
			for(Task task : currentTasks){
				if(task.getCategory().equals(category)){
					task.setCategory(Constant.DEFAULT_CATEGORY);
				}
			}
			validCategory.remove(category);
		}
	}
	
	/**
	 * show a joke to user
	 */
	public void showJoke(){
		System.out.println("How can you expect a Todo-List software to provide you a joke!😨");
		System.out.println("This function is actually the joke.");
		System.out.println("If you really want some, go to jokes.cc.com ☺️");
	}
	
	/**
	 * getTaskIdByIndex
	 * 
	 * @param index
	 * @return
	 * @throws CommandFailedException
	 */
	private String getTaskIdByIndex(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			return this.currentTasks.get(index).getTaskId();
		}
	}

	/**
	 * retrieve gets the task with the index.
	 * 
	 * @param index
	 * @return the Task, null if there is an error
	 * @throws CommandFailedException
	 */
	public Task retrieve(int index) throws CommandFailedException {
		if (!this.isValidIndex(index)) {
			throw new CommandFailedException(String.format(
					Constant.INVALID_INDEX_ERROR_MESSAGE, index));
		} else {
			Iterator<Task> taskIterator = this.currentTasks.iterator();
			while (taskIterator.hasNext()) {
				Task task = taskIterator.next();
				if (task.getTaskId().equals(this.getTaskIdByIndex(index))) {
					return task;
				}
			}
			return null;
		}
	}

	/**
	 * find gets the list of tasks that meet the constraint.
	 * 
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Task> find(Constraint constraint) throws Exception {
		Iterator<Task> taskIterator = this.currentTasks.iterator();
		ArrayList<Task> matchedTasks = new ArrayList<Task>();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (constraint.isMeeted(task)) {
				matchedTasks.add(task);
			}
		}
		return matchedTasks;
	}

	/**
	 * isValidIndex 
	 * 
	 * @param index
	 * @return
	 */
	private boolean isValidIndex(int index) {
		if ((index < 0) || (index > this.currentTasks.size())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * getTaskList gets the list of every non-trashed tasks of the user.
	 * 
	 * @return
	 */
	public ArrayList<Task> getTaskList() {
		ArrayList<Task> nonTrashedTasks = new ArrayList<Task>();
		for (Task task: this.currentTasks) {
			Iterator<String> tagIterator = task.getTag().iterator();
			boolean isTrashed = false;
			while (tagIterator.hasNext()) {
				if (tagIterator.next().toLowerCase().contains(Constant.TRASHED_TAG)) {
					isTrashed = true;
				}
			}
			
			if (!isTrashed) {
				nonTrashedTasks.add(task);
			}
		}
		return nonTrashedTasks;
	}
	
	
	
	//system level static methods
	
	
	/**
	 * deleteAccount destroys the account
	 * 
	 * @return message
	 */
	public static String deleteAccount() {
		UtilityMethod.showToUser("Please enter the username of the account you want to delete: ");
		String username = UtilityMethod.readCommand();
		UtilityMethod.showToUser("Please enter the password to confirm: ");
		String password = UtilityMethod.readCommand();
		boolean isDeleteSuccessfully = DataStore.destroy(username, password);
		return isDeleteSuccessfully ? "deleted!" : "deletion failed";
	}
	
	/**
	 * userLogIn uses the user name and password in parameters to log in.
	 * @param parameters
	 * @return
	 */
	public static String userLogIn(ArrayList<String> parameters) {
		String username = null;
		String password = null;
		
		if (parameters.size() >= 1) {
			username = parameters.get(0);
			if (parameters.size() >= 2) {
				password = parameters.get(1);
			}
		}
		
		while (username == null) {
			UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_NEED_USERNAME);
			String inputUsername = UtilityMethod.readCommand();
			if (!DataStore.isAccountExisting(inputUsername)) {
				UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_ACCOUNT_NOT_EXIST);
				 if (!UtilityMethod.readCommand().equalsIgnoreCase("Y")) {
					return Constant.RETURN_VALUE_LOG_IN_CANCELLED;
				 }
			} else {
				username = inputUsername;
			}
		}
			
		while (password == null) {
			UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_NEED_PASSWORD);
			password = UtilityMethod.readCommand();
		}
		
		int incorrectPasswordCount = 0;
		while (!DataStore.authenticate(username, password)) {
			incorrectPasswordCount++;
			if (incorrectPasswordCount >= 3) {
				return Constant.RETURN_VALUE_AUTHENTICATION_FAILED;
			}
			UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_PASSWORD_INCORRECT);
			password = UtilityMethod.readCommand();
		}
		
		return username;
	}
	
	/**
	 * createAccount creates account for a user.
	 * 
	 * @param parameters
	 * @return
	 */
	public static String createAccount(ArrayList<String> parameters) {
		String username = null;
		String passwordInput1 = null;
		String passwordInput2 = null;
		
		if (parameters.size() >= 1) {
			username = parameters.get(0);
		}
		
		while (username == null) {
			UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_NEED_USERNAME);
			String inputUsername = UtilityMethod.readCommand();
			if (DataStore.isAccountExisting(inputUsername)) {
				UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_ACCOUNT_EXIST);
				 if (!UtilityMethod.readCommand().equalsIgnoreCase("Y")) {
					return Constant.RETURN_VALUE_LOG_IN_CANCELLED;
				 }
			} else {
				username = inputUsername;
			}
		}
		
		while(!(passwordInput1 != null && passwordInput2 != null && passwordInput1.equals(passwordInput2))) {
			passwordInput1 = null;
			passwordInput2 = null;
			UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_NEED_PASSWORD);
			passwordInput1 = UtilityMethod.readCommand();
			UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_NEED_ENTER_AGAIN);
			passwordInput2 = UtilityMethod.readCommand();
		}
		
		boolean successCreated = DataStore.createAccount(username, passwordInput1);
		UtilityMethod.showToUser(successCreated ?  Constant.PROMPT_MESSAGE_ACCOUNT_CREATED: 
								 Constant.PROMPT_MESSAGE_ACCOUNT_NOT_CREATED);
		return successCreated ? username : null;
	}
	
	/**
	 * showHelp shows the application manual.
	 * 
	 * @return
	 */
	public static String showHelp(){
		
		return "'Help' has not been implemented yet";
	}
	
	/**
	 * exit exits the application
	 */
	public static void exit() {
		UtilityMethod.showToUser(Constant.PROMPT_MESSAGE_SESSION_END);
		System.exit(0);
	}
}
