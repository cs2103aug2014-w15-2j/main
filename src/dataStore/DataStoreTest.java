package dataStore;

//import static org.junit.Assert.*;

//import org.junit.Test;

import java.io.File;

public class DataStoreTest {

	public boolean initialize(String testFile) {
		File oldTestFile = new File(testFile);
		if(oldTestFile.exists()) {
			oldTestFile.delete();
			System.out.println("here " + oldTestFile.getAbsolutePath());
			return false;
		}
		return true;
	}
	
//	@Test
//	public void testGetCurrentTasks() {
//		initialize("testFileGCT");
//		DataStore.createAccount("testFileGCT", "thisIsJustATestFile");
//		File user = new File("testFileGCT");
//		try {
//			assertEquals(0, DataStore.getCurrentTasks(user).size());
//		} catch (Exception e) {
//			System.out.println("Failed.");
//		}
//		File testFile = new File("testFileGCT");
//		testFile.delete();
//	}
	
}
