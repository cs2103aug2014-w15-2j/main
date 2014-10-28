package dataStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TestingCache {

	// FOR TESTING ONLY
	/**
	 * cacheAccount caching
	 * 
	 * @param username
	 * @return true if succeed, false otherwise
	 */
	public static boolean cacheAccount(String username) {
		if (!DataStore.isAccountExisting(username)) {
			return false;
		}
		try {
			File cached = new File("_cache");
			if (!cached.exists()) {
				cached.createNewFile();
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter("_cache"));
			bw.write(username);
			bw.close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * getCachedAccount
	 * 
	 * @return cached account name
	 */
	public static String getCachedAccount() {
		try {
			File cached = new File("_cache");
			if (!cached.exists()) {
				cached.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader("_cache"));
			String cachedName = reader.readLine();

			reader.close();
			return cachedName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * clearCache
	 * 
	 * @return boolean
	 */
	public static boolean clearCache() {
		File cache = new File("_cache");
		return cache.delete();
	}
	
}
