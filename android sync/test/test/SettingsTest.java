package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void excludedMediaTypesAreParsedAndMatchedCaseInsensitively() {
		Settings.excludedMediaTypes = "Audiobooks, podcasts, Music";
		assertTrue(Settings.isExcludedMediaType("audiobooks"));
		assertTrue(Settings.isExcludedMediaType("PODCASTS"));
		assertFalse(Settings.shouldInclude("music"));
		assertTrue(Settings.shouldInclude("movies"));
		assertTrue(Settings.getExcludedMediaTypes().contains("audiobooks"));
		assertTrue(Settings.getExcludedMediaTypes().contains("podcasts"));
	}

	@Test
	public void excludedMediaTypesRoundTripThroughSettingsFile() {
		Settings.excludedMediaTypes = "audiobooks,podcasts";
		Settings.writeSettings();
		Settings.excludedMediaTypes = "";
		Settings.readSettings();

		assertEquals("audiobooks,podcasts", Settings.excludedMediaTypes);
		assertTrue(Settings.isExcludedMediaType("audiobooks"));
		assertTrue(Settings.isExcludedMediaType("podcasts"));
		assertTrue(Settings.shouldInclude("movies"));
		assertFalse(Settings.shouldInclude("audiobooks"));
	}

}