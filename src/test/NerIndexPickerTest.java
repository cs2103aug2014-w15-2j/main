package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import infrastructure.NerParser;

import org.junit.Before;
import org.junit.Test;

public class NerIndexPickerTest {

	static NerParser nerParser = new NerParser();
	
	@Test
	public void testDelete() {
		compareIntegerArray("delete 1 2 3 4 5", new int[]{1,2,3,4,5});
		compareIntegerArray("delete 1,2,3,4,5", new int[]{1,2,3,4,5});
		compareIntegerArray("delete 1,2,3,4,5,", new int[]{1,2,3,4,5});
		compareIntegerArray("delete 1, 2, 3, 4, 5", new int[]{1,2,3,4,5});
		compareIntegerArray("delete 1, 2, 3, 4, 5,", new int[]{1,2,3,4,5});
		compareIntegerArray("delete 1, 2, 3, 4, 5, ", new int[]{1,2,3,4,5});
	}
	
	@Test
	public void testDone() {
		compareIntegerArray("done 1 2 3 4 5", new int[]{1,2,3,4,5});
		compareIntegerArray("done 1,2,3,4,5", new int[]{1,2,3,4,5});
		compareIntegerArray("done 1,2,3,4,5,", new int[]{1,2,3,4,5});
		compareIntegerArray("done 1, 2, 3, 4, 5", new int[]{1,2,3,4,5});
		compareIntegerArray("done 1, 2, 3, 4, 5,", new int[]{1,2,3,4,5});
		compareIntegerArray("done 1, 2, 3, 4, 5, ", new int[]{1,2,3,4,5});
	}

	private void compareIntegerArray(String userInputString, int[] intArray) {
		try {
			ArrayList<Integer> result = nerParser.pickIndex(userInputString);
			for (int i = 0; i < result.size(); i++) {
				assertTrue(result.get(i) == intArray[i]);
			}
		} catch (Exception e){
			fail();
		}
	}
}
