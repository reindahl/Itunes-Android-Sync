package findFiles;
import java.util.ArrayList;


public class Playlist {
	private String name="";
	private ArrayList<Track> tracks=new ArrayList<Track>();
	
	public Playlist(String name) {
		this.name=name;
	}

	
	@Override
	public String toString() {
		return name;
	}
	
	public String getName(){
		return name;
	}
	
	public void add(Track track){
		tracks.add(track);
	}
	
	public ArrayList<Track> getTracks(){
		return tracks;
	}


	public int size() {
		return tracks.size();
	}
}