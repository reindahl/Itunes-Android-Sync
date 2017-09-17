package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import settings.Settings;

public class SettingsTest {
	@Before
    public void backup() throws IOException {
        Files.copy(Paths.get("Settings"), Paths.get("Settings2"),StandardCopyOption.REPLACE_EXISTING);
    }
	
	@After
    public void restore() throws IOException {
		 Files.copy(Paths.get("Settings2"), Paths.get("Settings"),StandardCopyOption.REPLACE_EXISTING);
    }
	@Test
	public void writeSettings() {
		Settings.deviceName="dfsdfs";
		Settings.hardlinkPath="dfsdsfsfdfsd";
		Settings.itunesPath="mæbmgfæ";
		Settings.writeSettings();
		
		Settings.deviceName="dfsdfs3";
		Settings.hardlinkPath="dfsdsfsfdfsd3";
		Settings.itunesPath="mæbmgfæ3";
		Settings.readSettings();
		
		assertEquals("dfsdfs", Settings.deviceName);
		assertEquals("dfsdsfsfdfsd", Settings.hardlinkPath);
		assertEquals("mæbmgfæ", Settings.itunesPath);
	}

}
