package org.remotercp.util.preferences;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

public class PreferencesUtilTest {

	@Test
	public void exportPreferencesTest() {
		try {
			File preferencesFile = File.createTempFile("preferences", ".ini");
			System.out.println(preferencesFile.getPath());
			
			SortedMap<String, String> preferencesMap = new TreeMap<String, String>();
			preferencesMap.put("org.remotercp.color", "blue");
			preferencesMap.put("org.remotercp.font", "helvetica");
			preferencesMap.put("org.remotercp.server", "http://myserver.com");
			preferencesMap.put("org.remotercp.user", "sandra");

			PreferencesUtil.exportPreferencesToFile(preferencesMap,
					preferencesFile.getAbsolutePath());

			FileReader reader = new FileReader(preferencesFile);
			BufferedReader bufReader = new BufferedReader(reader);
			String line;
			int count = 0;
			while ((line = bufReader.readLine()) != null) {
				// first line is the date of the export, ignore it
				if (!line.startsWith("#")) {

					/* split keys and values */
					int keyValueSeparator = line.indexOf("=");
					String key = line.substring(0, keyValueSeparator);
					String value = line.substring(keyValueSeparator + 1);

					assertTrue(preferencesMap.containsKey(key));
					assertTrue(preferencesMap.containsValue(value));
				}

				count++;
			}
			bufReader.close();
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
