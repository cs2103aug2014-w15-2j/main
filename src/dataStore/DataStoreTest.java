package dataStore;

import static org.junit.Assert.*;

import org.junit.Test;

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
	
	@Test
	public void testCreateAccount() {
		initialize("testFileCA");
		assertTrue(DataStore.createAccount("testFileCA", "thisIsJustATestFile"));
		assertFalse(DataStore.createAccount("testFileCA", "thisIsJustATestFile"));
		File testFile = new File("testFileCA");
		testFile.delete();
	}
	
	@Test
	public void testIsAccountExisting() {
		initialize("testFileAE");
		DataStore.createAccount("testFileAE", "thisIsJustATestFile");
		assertTrue(DataStore.isAccountExisting("testFileAE"));
		assertFalse(DataStore.isAccountExisting("fileNotExisted"));
		File testFile = new File("testFileAE");
		testFile.delete();		
	}
	
	@Test
	public void testAuthenticate() {
		initialize("testFileA");
		DataStore.createAccount("testFileA", "thisIsJustATestFile");
		assertTrue(DataStore.authenticate("testFileA", "thisIsJustATestFile"));
		assertFalse(DataStore.authenticate("testFileA", "wrongPassword"));
		assertFalse(DataStore.authenticate("notTestFile", "thisIsJustATestFile"));
		File testFile = new File("testFileA");
		testFile.delete();
	}
	
	@Test
	public void testDestroy() {
		initialize("testFileD");
		DataStore.createAccount("testFileD", "thisIsJustATestFile");
		assertFalse(DataStore.destroy("notTestFile", "thisIsJustATestFile"));
		assertFalse(DataStore.destroy("testFileD", "wrongPassword"));
		assertTrue(DataStore.destroy("testFileD", "thisIsJustATestFile"));
		assertTrue(initialize("testFileD"));
	}
	
	@Test
	public void testGetCurrentTasks() {
		initialize("testFileGCT");
		DataStore.createAccount("testFileGCT", "thisIsJustATestFile");
		File user = new File("testFileGCT");
		try {
			assertEquals(0, DataStore.getCurrentTasks(user).size());
		} catch (Exception e) {
			System.out.println("Failed.");
		}
		File testFile = new File("testFileGCT");
		testFile.delete();
	}
	
}
