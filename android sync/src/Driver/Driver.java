package Driver;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import settings.Settings;
import syncFiles.Sync;
import findFiles.Find;

/**
 * 
 * @author Kasper Reindahl Rasmussen
 *
 */
public class Driver {
	static long start;
	static long elapsedTime;
	static double seconds;
	
	public static void main(String[] args) {
//		test();
		sync();
		System.out.println("Done");
	}
	
	public static void sync(){

		System.out.println("Starting sync");
		start = System.nanoTime(); 
		Find.FindFiles();
		elapsedTime = System.nanoTime() - start;
		seconds = (double)elapsedTime / 1000000000.0;
		System.out.println("Find: "+seconds);
		
		Sync.findDroid();
		
		start = System.nanoTime(); 
		Sync.sync();
		elapsedTime = System.nanoTime() - start;
		seconds = (double)elapsedTime / 1000000000.0;
		System.out.println("Copy: "+seconds);
		
	}
	
	public void readSettings(){
		List<String> settings = null;
		try {
			settings=Files.readAllLines(Paths.get("Settings"), Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(settings);
	}
	
	@SuppressWarnings("unused")
	private static void test(){
		Settings.hardlinkPath="test files/Android Sync/";
		Settings.itunesPath="test files/iTunes/";
	}

}
