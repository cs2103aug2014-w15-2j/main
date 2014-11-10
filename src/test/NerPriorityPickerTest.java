package test;

import static org.junit.Assert.*;
import infrastructure.Constant;
import infrastructure.NerParser;

import org.junit.Test;

public class NerPriorityPickerTest {
	
	static NerParser nerParser = new NerParser();
	
	@Test
	public void testAdd() {
		compareIntegerArray("add task priority high", Constant.PRIORITY_HIGH);
		compareIntegerArray("add task priority hig", Constant.PRIORITY_HIGH);
		compareIntegerArray("add task priority hi", Constant.PRIORITY_HIGH);
		compareIntegerArray("add task priority h", Constant.PRIORITY_HIGH);
		compareIntegerArray("add task priority low", Constant.PRIORITY_LOW);
		compareIntegerArray("add task priority lo", Constant.PRIORITY_LOW);
		compareIntegerArray("add task priority l", Constant.PRIORITY_LOW);
		compareIntegerArray("add task priority medium", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("add task priority mediu", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("add task priority medi", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("add task priority med", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("add task priority me", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("add task priority m", Constant.PRIORITY_MEDIUM);
	}
	
	@Test
	public void testUpdate() {
		compareIntegerArray("update 1 priority high", Constant.PRIORITY_HIGH);
		compareIntegerArray("update 1 priority hig", Constant.PRIORITY_HIGH);
		compareIntegerArray("update 1 priority hi", Constant.PRIORITY_HIGH);
		compareIntegerArray("update 1 priority h", Constant.PRIORITY_HIGH);
		compareIntegerArray("update 1 priority low", Constant.PRIORITY_LOW);
		compareIntegerArray("update 1 priority lo", Constant.PRIORITY_LOW);
		compareIntegerArray("update 1 priority l", Constant.PRIORITY_LOW);
		compareIntegerArray("update 1 priority medium", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("update 1 priority mediu", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("update 1 priority medi", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("update 1 priority med", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("update 1 priority me", Constant.PRIORITY_MEDIUM);
		compareIntegerArray("update 1 priority m", Constant.PRIORITY_MEDIUM);
	}

	private void compareIntegerArray(String userInputString, int expectedPriority) {
		try {
			System.out.println(userInputString);
			int result = nerParser.pickPriority(userInputString);
			assertTrue(result == expectedPriority);
		} catch (Exception e){
			fail();
		}
	}
}
