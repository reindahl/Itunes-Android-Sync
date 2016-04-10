package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
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

	}

	@After
	public void tearDown() throws IOException{
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

	ArrayList<String> FindFilesInDic(File dir, ArrayList<String> foundFiles) {
		for (File file: dir.listFiles()) {
			foundFiles.add(file.getPath());
			if (file.isDirectory()) {
				FindFilesInDic(file, foundFiles);

			}
		}
		return foundFiles;
	}
	@Test
	public void find() {
		purgeDirectory(new File(Settings.hardlinkPath));
		assertEquals(0, FindFilesInDic(new File(Settings.hardlinkPath), new ArrayList<>()).size());
		Find.FindFiles();
		assertEquals(40, FindFilesInDic(new File(Settings.hardlinkPath), new ArrayList<>()).size());
		assertEquals(FindFilesInDic(new File(Settings.itunesPath+"/iTunes Media/Music"), new ArrayList<>()).size()+7, FindFilesInDic(new File(Settings.hardlinkPath), new ArrayList<>()).size());

	}

	@Test
	public void delete() {
		Find.FindFiles();
		Path Droid=Paths.get(droidPath);
		Path testfile = Paths.get(Droid.toString()+"\\test.test");
		Path testfile2 = null;
		try {
			testfile2= Files.createFile(Paths.get(Droid.toString()+"\\music\\test.test"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
		assertTrue(Files.exists(testfile));
		Device device=new DeviceUMS(Droid);

		device.delete(Sync.find());
		System.out.println(testfile);
		assertTrue(Files.exists(testfile));
		assertFalse(Files.exists(testfile2));

	}

	@Test
	public void copy() {
		assertEquals(4, FindFilesInDic(new File(droidPath), new ArrayList<>()).size());
		assertEquals(40, FindFilesInDic(new File(Settings.hardlinkPath), new ArrayList<>()).size());

		Device device= new DeviceUMS(droidPath);
		device.copy();

		System.out.println(FindFilesInDic(new File(droidPath), new ArrayList<>()));
		assertEquals(42, FindFilesInDic(new File(droidPath), new ArrayList<>()).size());
		assertEquals(40, FindFilesInDic(new File(Settings.hardlinkPath), new ArrayList<>()).size());
	}


	@Test
	public void sync() {
		purgeDirectory(new File(Settings.hardlinkPath));
		assertEquals(5, FindFilesInDic(new File(droidPath), new ArrayList<>()).size());
		assertEquals(0, FindFilesInDic(new File(Settings.hardlinkPath), new ArrayList<>()).size());
		Find.FindFiles();
		Sync.droid=new DeviceUMS(droidPath);
		Sync.synchronise();
		assertEquals(42, FindFilesInDic(new File(droidPath), new ArrayList<>()).size());
		assertEquals(40, FindFilesInDic(new File(Settings.hardlinkPath), new ArrayList<>()).size());
	}
	
	
}
