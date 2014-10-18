package dataStore;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.io.FileNotFoundException;

import infrastructure.Constant;

public class DataStoreTest {
	
	public void initialize() {
		File oldTestFile = new File("testFile");
		if(oldTestFile.exists()) {
			oldTestFile.delete();
		}
	}
	
	@Test
	public void testCreateAccount() {
		assertTrue(DataStore.createAccount("testFile", "thisIsJustATestFile"));
		assertFalse(DataStore.createAccount("testFile", "thisIsJustATestFile"));
		File testFile = new File("testFile");
		testFile.delete();
	}
	
	@Test
	public void testIsAccountExisting() {
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		assertTrue(DataStore.isAccountExisting("testFile"));
		assertFalse(DataStore.isAccountExisting("fileNotExisted"));
		File testFile = new File("testFile");
		testFile.delete();
	}
	
	@Test
	public void testAuthenticate() {
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		assertTrue(DataStore.authenticate("testFile", "thisIsJustATestFile"));
		assertFalse(DataStore.authenticate("testFile", "wrongPassword"));
		assertFalse(DataStore.authenticate("notTestFie", "thisIsJustATestFile"));
		File testFile = new File("testFile");
		testFile.delete();
	}
	
	@Test
	public void testDestroy() {
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		assertFalse(DataStore.destroy("notTestFile", "thisIsJustATestFile"));
		assertFalse(DataStore.destroy("testFile", "wrongPassword"));
		assertTrue(DataStore.destroy("testFile", "thisIsJustATestFile"));
		assertFalse(DataStore.isAccountExisting("testFile"));
	}
	
	@Test
	public void testSave() {
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		assertFalse(DataStore.save("notTestFile", null));
		assertTrue(DataStore.save("testFile", null));
		try {
			BufferedReader bw = new BufferedReader(new FileReader("testFile"));
			assertTrue(bw.readLine().equals("thisIsJustATestFile"));
			assertTrue(bw.readLine().equals(Constant.SPLIT_SECTION));
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetCurrentTask() {
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		File testFile = new File("testFile");
		try {
			assertEquals(0, DataStore.getCurrentTasks(testFile).size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		testFile.delete();
	}

}
