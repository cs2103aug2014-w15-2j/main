package test;

import static org.junit.Assert.*;
import infrastructure.Constant.COMMAND_TYPE;
import infrastructure.NerParser;
import org.junit.Test;

public class NerCommandPickerTest {

	static NerParser nerParser = new NerParser();

	@Test
	public void testAdd() {
		compareCommandEnum("add task 1", COMMAND_TYPE.ADD);
		compareCommandEnum("addd task 1", COMMAND_TYPE.ADD);
		compareCommandEnum("+ task 1", COMMAND_TYPE.ADD);
		compareCommandEnum("insert task 1", COMMAND_TYPE.ADD);
		compareCommandEnum("ins task 1", COMMAND_TYPE.ADD);
		compareCommandEnum("jia task 1", COMMAND_TYPE.ADD);
	}
	
	@Test
	public void testUpdate() {
		compareCommandEnum("update 1 task 1", COMMAND_TYPE.UPDATE);
		compareCommandEnum("change 3 task 1", COMMAND_TYPE.UPDATE);
	}
	
	@Test
	public void testDelete() {
		compareCommandEnum("delete 1,2,3", COMMAND_TYPE.DELETE);
		compareCommandEnum("delete 1,2,3", COMMAND_TYPE.DELETE);
		compareCommandEnum("remove 1,2,3", COMMAND_TYPE.DELETE);
		compareCommandEnum("remove 1", COMMAND_TYPE.DELETE);
		compareCommandEnum("trash 1", COMMAND_TYPE.DELETE);
		compareCommandEnum("TRASH 1", COMMAND_TYPE.DELETE);
	}
	
	@Test
	public void testDisplay() {
		compareCommandEnum("show", COMMAND_TYPE.DISPLAY);
		compareCommandEnum("display", COMMAND_TYPE.DISPLAY);
	}
	
	@Test
	public void testSearch() {
		compareCommandEnum("search today", COMMAND_TYPE.SEARCH);
		compareCommandEnum("SEARCH today", COMMAND_TYPE.SEARCH);
		compareCommandEnum("Search today", COMMAND_TYPE.SEARCH);
		compareCommandEnum("find tmr", COMMAND_TYPE.SEARCH);
		compareCommandEnum("Find tmr", COMMAND_TYPE.SEARCH);
		compareCommandEnum("FIND tmr", COMMAND_TYPE.SEARCH);
	}
	
	@Test
	public void testUndoRedo() {
		compareCommandEnum("undo", COMMAND_TYPE.UNDO);
		compareCommandEnum("UNDO", COMMAND_TYPE.UNDO);
		compareCommandEnum("redo", COMMAND_TYPE.REDO);
		compareCommandEnum("REDO", COMMAND_TYPE.REDO);
	}
	
	@Test
	public void testRecover() {
		compareCommandEnum("recover", COMMAND_TYPE.RECOVER);
		compareCommandEnum("RECOVER", COMMAND_TYPE.RECOVER);
	}
	
	@Test
	public void testEmptyTrash() {
		compareCommandEnum("empty trash", COMMAND_TYPE.EMPTY_TRASH);
		compareCommandEnum("empty", COMMAND_TYPE.EMPTY_TRASH);
	}
	
	@Test
	public void testReload() {
		compareCommandEnum("reload", COMMAND_TYPE.RELOAD);
		compareCommandEnum("reload model", COMMAND_TYPE.RELOAD);
	}
	
	@Test
	public void testClear() {
		compareCommandEnum("clear", COMMAND_TYPE.CLEAR);
	}
	
	private void compareCommandEnum (String userInputString, COMMAND_TYPE command) {
		try {
			COMMAND_TYPE result = nerParser.pickCommand(userInputString);
			assertTrue(command == result);
		} catch (Exception e){
			fail();
		}
	}

}
