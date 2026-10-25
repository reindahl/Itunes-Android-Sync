package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import settings.Settings;
import syncFiles.Device;
import syncFiles.Sync;
import syncFiles.UMS.DeviceUMS;
import findFiles.Find;

@FixMethodOrder(MethodSorters.DEFAULT)
public class test {
	String droidPath ="test files/Droid/";
	@Before
	public void setup(){
		Settings.hardlinkPath="test files/Android Sync/";
		Settings.itunesPath="test files/iTunes/";
		Settings.excludedMediaTypes = "";
	}

	@After
	public void tearDown() throws IOException{
		Settings.excludedMediaTypes = "";
		if(Files.exists(Paths.get(droidPath+"audiobooks"))){
			purgeDirectory(new File(droidPath+"audiobooks"));
			Files.deleteIfExists(Paths.get(droidPath+"audiobooks"));
		}

		purgeDirectory(new File(droidPath+"music"));
		if(Files.exists(Paths.get(droidPath+"Playlists"))){
			purgeDirectory(new File(droidPath+"Playlists"));
			Files.delete(Paths.get(droidPath+"Playlists"));
		}
	}
	void purgeDirectory(File dir) {
		for (File file: dir.listFiles()) {
			if (file.isDirectory()) purgeDirectory(file);
			file.delete();
		}
	}

	ArrayList<String> findFilesInDirectory(File dir, ArrayList<String> foundFiles) {
		
		for (File file: dir.listFiles()) {
			foundFiles.add(file.getPath());
			if (file.isDirectory()) {
				findFilesInDirectory(file, foundFiles);

			}
		}
		return foundFiles;
	}
	
	@Test
	public void find() throws FileNotFoundException {
		purgeDirectory(new File(Settings.hardlinkPath));
		assertEquals(0, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());
		Find.FindFiles();
		assertEquals(13, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());
		assertEquals(findFilesInDirectory(new File(Settings.itunesPath+"/iTunes Media/Music"), new ArrayList<>()).size()+1 /*music*/ + 4 /*playlists*/, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());

	}

	@Test
	public void delete() throws FileNotFoundException {
		Find.FindFiles();
		Path Droid=Paths.get(droidPath);
		Path regularAndroidfile = Paths.get(Droid.toString()+"\\test.test");
		Path testfile2 = null;
		try {
			Files.createDirectories(Paths.get(Droid.toString()+"\\music\\"));
			testfile2= Files.createFile(Paths.get(Droid.toString()+"\\music\\test.test"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
		assertTrue(Files.exists(regularAndroidfile));
		Device device=new DeviceUMS(Droid);

		device.delete(Sync.find());
		System.out.println(regularAndroidfile);
		assertTrue(Files.exists(regularAndroidfile));
		assertFalse(Files.exists(testfile2));

	}

	@Test
	public void copy() {
		assertEquals(4, findFilesInDirectory(new File(droidPath), new ArrayList<>()).size());
		assertEquals(13, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());

		Device device= new DeviceUMS(droidPath);
		device.copy();
		
		assertEquals(13 + 3 /*droid/, .droid, test.test*/, findFilesInDirectory(new File(droidPath), new ArrayList<>()).size());
		assertEquals(13, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());
	}


	@Test
	public void sync() throws FileNotFoundException {
		purgeDirectory(new File(Settings.hardlinkPath));
		assertEquals(4, findFilesInDirectory(new File(droidPath), new ArrayList<>()).size());
		assertEquals(0, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());
		Find.FindFiles();
		Sync.droid = new DeviceUMS(droidPath);
		Sync.synchronise();
		assertEquals(13 + 3 /*droid/, .droid, test.test*/, findFilesInDirectory(new File(droidPath), new ArrayList<>()).size());
		assertEquals(13, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());
	}
	
	@Test
	public void excludedMediaTypesAreNotIncludedInPreparedSyncSet() throws FileNotFoundException {
		Settings.excludedMediaTypes = "audiobooks";
		purgeDirectory(new File(Settings.hardlinkPath));
		Find.FindFiles();

		assertEquals(0, findFilesInDirectory(new File(Settings.hardlinkPath + "audiobooks"), new ArrayList<>()).size());
		assertTrue(findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).stream().noneMatch(p -> p.contains("audiobooks")));

		Sync.droid = new DeviceUMS(droidPath);
		Sync.synchronise();
		assertTrue(findFilesInDirectory(new File(droidPath), new ArrayList<>()).stream().noneMatch(p -> p.contains("audiobooks")));
		assertEquals(findFilesInDirectory(new File(Settings.itunesPath + "/iTunes Media/Music"), new ArrayList<>()).size() + 1 /*music*/ + 4 /*playlists*/, findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).size());
	}
	
	@Test
	public void excludedMediaTypesLeaveExistingTargetFolderUntouchedOnSecondSync() throws FileNotFoundException, IOException {
		Settings.excludedMediaTypes = "";
		purgeDirectory(new File(Settings.hardlinkPath));
		Find.FindFiles();
		Sync.droid = new DeviceUMS(droidPath);
		Sync.synchronise();

		Path audiobookTargetFolder = Paths.get(droidPath, "audiobooks");
		assertTrue(Files.exists(audiobookTargetFolder));

		Settings.excludedMediaTypes = "audiobooks";
		purgeDirectory(new File(Settings.hardlinkPath));
		Find.FindFiles();
		Sync.synchronise();

		assertTrue(Files.exists(audiobookTargetFolder));
		assertTrue(findFilesInDirectory(new File(Settings.hardlinkPath), new ArrayList<>()).stream().noneMatch(path -> path.contains("audiobooks")));
	}

}