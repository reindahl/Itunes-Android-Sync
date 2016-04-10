package helperFiles;

public class Conversion {
	/**
	 * Converts bytes to String
	 * @param bytes
	 * @param si units
	 * @return
	 * si == false normal mode
	 * si == true binary mode
	 */
	public static String byteToString(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "KMGTPE" : "kMGTPE").charAt(exp-1) + (si ? "i" : "");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public static String secondsToHMS(double seconds){
		
		int h=(int) (seconds/3600);
		
		int m=(int) (seconds%3600/60);
		
		int s=(int) (seconds%60);

		return h+":"+(m<10?"0":"")+m+":"+(s<10?"0":"")+s;
	}
}
