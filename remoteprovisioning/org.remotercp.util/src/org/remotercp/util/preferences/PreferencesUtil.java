package org.remotercp.util.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

public class PreferencesUtil {

	public static SortedMap<String, String> createPreferencesFromFile(
			File preferencesFile) throws IOException {
		SortedMap<String, String> preferences = new TreeMap<String, String>();

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

				/*
				 * store preferences in map.
				 */
				if (key != null) {
					if (value == null)
						value = "";
					preferences.put(key, value);
				}
				// System.out.println(line);
			}

			count++;
		}
		bufReader.close();
		reader.close();

		return preferences;
	}

}
