package findFiles;
import java.nio.file.Path;


public class Track {
	//where the file is placed on the pc
	String Location="";
	String ID;
	String name="";
	String artist="";
	String albumArtist="";
	String album="";
	String Type;
	//where the file should be placed
	Path pathHardlink;
	Path pathRelativ;
	boolean Disabled=false;
	enum FileType{url,file, remote, unknown};
	FileType filetype=FileType.unknown;

	@Override
	public String toString() {
		
		return "\n"+ID+" "+Disabled+" "+Location;
	}
	
	
	
}
