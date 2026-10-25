package settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Settings {
	public static String deviceName = "";
	public static String hardlinkPath = "I:/Android Sync/";
	public static String itunesPath = "I:/iTunes/";
	public static String excludedMediaTypes = "";

	static Path settingsFile = Paths.get("Settings");

	public static Set<String> getExcludedMediaTypes() {
		if (excludedMediaTypes == null || excludedMediaTypes.trim().isEmpty()) {
			return Collections.emptySet();
		}
		HashSet<String> result = new HashSet<>();
		Arrays.stream(excludedMediaTypes.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(String::toLowerCase)
				.forEach(result::add);
		return result;
	}

	public static boolean isExcludedMediaType(String mediaType) {
		if (mediaType == null) {
			return false;
		}
		return getExcludedMediaTypes().contains(mediaType.trim().toLowerCase());
	}

	public static boolean isIgnoredFolderName(String folderName) {
		return isExcludedMediaType(folderName);
	}

	public static boolean shouldInclude(String mediaType) {
		return !isExcludedMediaType(mediaType);
	}

	/**
	 * read settings from Settings file
	 */
	public static void readSettings() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("Settings");

			prop.load(input);

			deviceName = (String) prop.getOrDefault("Device name", "");
			itunesPath = (String) prop.getOrDefault("Itunes Path", itunesPath);
			hardlinkPath = (String) prop.getOrDefault("Itunes Sync path", hardlinkPath);
			excludedMediaTypes = (String) prop.getOrDefault("Excluded media types", excludedMediaTypes);

		} catch (IOException e) {
			System.err.println("Failed to read Settings using default");
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Save current settings to file
	 */
	public static void writeSettings() {
		Properties prop = new Properties();
		prop.setProperty("Device name", deviceName);
		prop.setProperty("Itunes Path", itunesPath);
		prop.setProperty("Itunes Sync path", hardlinkPath);
		prop.setProperty("Excluded media types", excludedMediaTypes);
		OutputStream output = null;

		try {
			output = new FileOutputStream("Settings");
			prop.store(output, "Settings file");
		} catch (IOException e) {
			System.err.println("Failed to save settings");
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static boolean isSettingsValid() {
		return Files.exists(Paths.get(hardlinkPath)) && Files.exists(Paths.get(itunesPath));
	}
}