package driver;
import findFiles.Find;
import settings.Settings;
import syncFiles.Sync;

/**
 * 
 * @author Kasper Reindahl Rasmussen
 *
 * synchronise android and Itunes  
 */
public class Driver {
	static long start;
	static long elapsedTime;
	static double seconds;
	
	public static void main(String[] args) {
//		test();

		Settings.readSettings();
		if(!Settings.isSettingsValid()){
			System.err.println("invalid settings");
			return;
		}
		
		Sync.findDroid();
		
		System.out.println("Starting sync");
		start = System.nanoTime(); 
		Find.FindFiles();
		elapsedTime = System.nanoTime() - start;
		seconds = (double)elapsedTime / 1000000000.0;
		System.out.println("Find: "+seconds);
		
		
		
		start = System.nanoTime(); 
		Sync.synchronise();
		elapsedTime = System.nanoTime() - start;
		seconds = (double)elapsedTime / 1000000000.0;
		System.out.println("Copy: "+seconds);

		System.out.println("Done");
	}
	

	
	@SuppressWarnings("unused")
	private static void test(){
		Settings.hardlinkPath="test files/Android Sync/";
		Settings.itunesPath="test files/iTunes/";
	}

}
