package syncFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import settings.Settings;


public class Sync {
	public static Device droid= null;
	public static long size=0;
	
	
	
	public static void sync(){
		
		
		HashMap<String, Path> existingFiles = find();
		
		droid.delete(existingFiles);
		
		System.out.println("size needed to be copied: "+helperFiles.conversion.humanReadableByteCount(size, false));
	
		droid.copy();
		
	}
	

	public static HashMap<String, Path> find(){
		System.out.println("find");
		FindFilevisitor findFilevisitor = new FindFilevisitor(Settings.hardlinkPath);
		try {
			Files.walkFileTree(Paths.get(Settings.hardlinkPath),findFilevisitor );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		size=findFilevisitor.size;
		System.out.println("toltal size: "+helperFiles.conversion.humanReadableByteCount(size, false));
		
		return findFilevisitor.files;
	}
	
	
	
	
	public static void SyncTCP(){
		
	}
	
	
	/**
	 * finds the device that should be synchronized
	 */
	public static void findDroid(){
		
		droid = Device.find();

	}
	


}
