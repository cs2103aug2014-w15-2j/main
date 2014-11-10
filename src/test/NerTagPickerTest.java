package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import infrastructure.NerParser;

import org.junit.Test;

public class NerTagPickerTest {

	static NerParser nerParser = new NerParser();

	@Test
	public void testAdd() {
		compareStringArray("add task tag with aa", new String[] {"aa"});
		compareStringArray("add task tag with aa,", new String[] {"aa"});
		compareStringArray("add task tag with aa, bb", new String[] {"aa", "bb"});
		compareStringArray("add task tag with aa and bb", new String[] {"aa", "bb"});
		compareStringArray("add task tag with aa, bb, cc", new String[] {"aa", "bb", "cc"});
		compareStringArray("add task tag with aa, bb and cc", new String[] {"aa", "bb", "cc"});
		compareStringArray("add task tag with aa, bb, cc, dd", new String[] {"aa", "bb", "cc", "dd"});
		compareStringArray("add task tag with aa, bb, cc and dd", new String[] {"aa", "bb", "cc", "dd"});
		compareStringArray("add task tag with aa, bb, cc, dd, ee", new String[] {"aa", "bb", "cc", "dd", "ee"});
		compareStringArray("add task tag with aa, bb, cc, dd and ee", new String[] {"aa", "bb", "cc", "dd", "ee"});
	}
	
	@Test
	public void testUpdate() {
		compareStringArray("update 1 tag with aa", new String[] {"aa"});
		compareStringArray("update 1 tag with aa,", new String[] {"aa"});
		compareStringArray("update 1 tag with aa, bb", new String[] {"aa", "bb"});
		compareStringArray("update 1 tag with aa and bb", new String[] {"aa", "bb"});
		compareStringArray("update 1 tag with aa, bb, cc", new String[] {"aa", "bb", "cc"});
		compareStringArray("update 1 tag with aa, bb and cc", new String[] {"aa", "bb", "cc"});
		compareStringArray("update 1 tag with aa, bb, cc, dd", new String[] {"aa", "bb", "cc", "dd"});
		compareStringArray("update 1 tag with aa, bb, cc and dd", new String[] {"aa", "bb", "cc", "dd"});
		compareStringArray("update 1 tag with aa, bb, cc, dd, ee", new String[] {"aa", "bb", "cc", "dd", "ee"});
		compareStringArray("update 1 tag with aa, bb, cc, dd and ee", new String[] {"aa", "bb", "cc", "dd", "ee"});
	}
	
	private void compareStringArray(String userInputString, String[] strArray) {
		try {
			ArrayList<String> result = nerParser.pickTag(userInputString);
			for (int i = 0; i < result.size(); i++) {
				assertTrue(result.get(i).equals(strArray[i]));
			}
		} catch (Exception e){
			fail();
		}
	}
}
