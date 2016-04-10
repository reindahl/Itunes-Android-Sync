package settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class Settings {
	public static String deviceName="";
	public static String hardlinkPath= "I:/Android Sync/";
	public static String itunesPath="I:/iTunes/";
	
	static Path settingsFile =Paths.get("Settings");
	
	/**
	 * read settings from Settings file
	 */
	public static void readSettings(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("Settings");

			prop.load(input);
			
			deviceName=(String) prop.getOrDefault("Device name","");
			itunesPath=(String) prop.getOrDefault("Itunes Path",itunesPath);
			hardlinkPath=(String) prop.getOrDefault("Itunes Sync path", hardlinkPath);
			
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
	public static void writeSettings(){
		Properties prop = new Properties();
		prop.setProperty("Device name", deviceName);
		prop.setProperty("Itunes Path", itunesPath);
		prop.setProperty("Itunes Sync path", hardlinkPath);
		OutputStream output = null;

		try {
			output = new FileOutputStream("Settings");
			prop.store(output, "Settings file");
		} catch (IOException e) {
			System.err.println("Failed to save settings");
			e.printStackTrace();
		}finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
	public static boolean isSettingsValid(){
		return Files.exists(Paths.get(hardlinkPath)) && Files.exists(Paths.get(itunesPath));
	}
}


