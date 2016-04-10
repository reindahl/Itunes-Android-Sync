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
	
	
	
	public static void synchronise(){

		HashMap<String, Path> existingFiles = find();
		
		droid.delete(existingFiles);
		
		System.out.println("size needed to be copied: "+helperFiles.Conversion.byteToString(size, false));
	
		droid.copy();	
	}
	
	/**
	 * find prepared files (hard links) that should be synchronised. 
	 * 
	 * @return
	 */
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
		System.out.println("toltal size: "+helperFiles.Conversion.byteToString(size, false));
		
		return findFilevisitor.files;
	}
	
	
	
	/**
	 * finds the device that should be synchronised
	 */
	public static void findDroid(){
		
		droid = Device.find();

	}
	


}
