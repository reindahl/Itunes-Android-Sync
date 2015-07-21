package findFiles;
import java.util.ArrayList;


public class Playlist {
	String name="";
	ArrayList<Track> tracks=new ArrayList<Track>();
	
	public Playlist(String string) {
		name=string;
	}

	
	@Override
	public String toString() {
		
		return name;
	}
	
}
