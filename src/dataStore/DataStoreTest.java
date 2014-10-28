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
		initialize();
		assertTrue(DataStore.createAccount("testFile", "thisIsJustATestFile"));
		assertFalse(DataStore.createAccount("testFile", "thisIsJustATestFile"));
		File testFile = new File("testFile");
		testFile.delete();
	}
	
	@Test
	public void testIsAccountExisting() {
		initialize();
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		assertTrue(DataStore.isAccountExisting("testFile"));
		assertFalse(DataStore.isAccountExisting("fileNotExisted"));
		File testFile = new File("testFile");
		testFile.delete();		
	}
	
	@Test
	public void testAuthenticate() {
		initialize();
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		assertTrue(DataStore.authenticate("testFile", "thisIsJustATestFile"));
		assertFalse(DataStore.authenticate("testFile", "wrongPassword"));
		assertFalse(DataStore.authenticate("notTestFile", "thisIsJustATestFile"));
		File testFile = new File("testFile");
		testFile.delete();
	}
	
	@Test
	public void testDestroy() {
		initialize();
		DataStore.createAccount("testFile", "thisIsJustATestFile");
		assertFalse(DataStore.destroy("notTestFile", "thisIsJustATestFile"));
		assertFalse(DataStore.destroy("testFile", "wrongPassword"));
		assertTrue(DataStore.destroy("testFile", "thisIsJustATestFile"));
		File testFile = new File("testFile");
		assertFalse(testFile.exists());
	}
	
}
