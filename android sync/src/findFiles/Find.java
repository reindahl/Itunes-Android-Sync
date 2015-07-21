package findFiles;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import settings.Settings;
/**
 * 
 * @author Kasper Reindahl Rasmussen
 *
 */
public class Find{


	static ArrayList<Playlist> playLists= new ArrayList<Playlist>();

	/**
	 * reads the itunes lib and creates hardlinks for the checked files
	 */
	public static void FindFiles(){
		
		ArrayList<Track> tracks=xmlSax();
		
		tracks.forEach(e -> fixTrack(e));

		
		DeleteVisitor visitor=new DeleteVisitor(tracks);
		try {
			Files.walkFileTree(Paths.get(Settings.hardlinkPath), visitor);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		tracks.stream().filter(t -> !t.Disabled).forEach(t -> hardlink(t));
		
		
		if(Files.notExists(Paths.get(Settings.hardlinkPath+"audiobooks/"+".nomedia"))){
			try {
				if(Files.notExists(Paths.get(Settings.hardlinkPath+"audiobooks/"))){
					Files.createDirectories(Paths.get(Settings.hardlinkPath+"audiobooks/"));
				}
				Files.createFile(Paths.get(Settings.hardlinkPath+"audiobooks/"+".nomedia"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if(Files.notExists(Paths.get(Settings.hardlinkPath+"Playlists"))){
				Files.createDirectory(Paths.get(Settings.hardlinkPath+"Playlists"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		playLists.forEach(p -> createPlaylist(p));


		System.out.println("done");
	}

	
	/**
	 * cleans up the tracks to follow a more easily read path
	 * creates what the hardlinks parts should be
	 * @param tracks
	 */
	public static void fixTrack(Track track){
		if(!track.Disabled){
			String artist;
			try {
				switch (track.filetype) {
				case file:
					track.Location=URLDecoder.decode(track.Location, "UTF-8").substring(17);
					break;
				case url:
					track.Location=track.Location;
					break;
				case remote:
					track.Disabled=true;
				default:
					track.Disabled=true;
					throw new FileTypeUnknown();
				}
				
				if(track.albumArtist.equals("")){
					artist=track.artist;
				}else{
					artist=track.albumArtist;
				}
				track.path=Paths.get(Settings.hardlinkPath+track.Type+"/"+pathFix(artist)+"/"+pathFix(track.album)+"/"+pathFix(Paths.get(track.Location).getFileName().toString()));
			} catch (UnsupportedEncodingException | FileTypeUnknown e) {
				e.printStackTrace();
			}

		}
	}
	
	

	/**
	 * reads iTunes library and creates a representation of it
	 * @return
	 */
	public static ArrayList<Track> xmlSax(){
		ItunesLibHandler handler = new ItunesLibHandler();
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(Settings.itunesPath+"iTunes Library.xml", handler);

		} catch (Exception e) {
			e.printStackTrace();
		}
		playLists= handler.playlists;
		
		return new ArrayList<Track>(handler.tracks.values());
	}


	/**
	 * removes symbols which are forbidden in a path  
	 * @param path
	 * @return clean string
	 */
	public static String pathFix(String path){
		return path.replaceAll("[\\//*?\":<>|]", "_").replaceAll("(^ )|( $)", "").replaceAll("(\\.$)", "_");
	}

	public static void hardlink(Track track){
//		System.out.println(track);
		Path existingFile=Paths.get(track.Location);

		Path newLink = track.path;
		if(!Files.exists(newLink.getParent())){
			try {
				Files.createDirectories(newLink.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		try {
			if(!Files.exists(newLink)){
				Files.createLink(newLink, existingFile);
				
			}else if(!Files.isSameFile(newLink, existingFile)){
				Files.delete(newLink);
				Files.createLink(newLink, existingFile);
			}
			
			
		}catch(FileAlreadyExistsException e) {
			e.printStackTrace();
		}catch (NoSuchFileException e) {
			System.err.print("File do not exist "+track+"\n");
		}catch(AccessDeniedException e){
			System.out.println(existingFile);
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (UnsupportedOperationException e) {
			// Some file systems do not
			// support adding an existing
			// file to a directory.
			e.printStackTrace();

		}

	}
	/**
	 * creates a .m3u play list with relative paths and bogus track length
	 * @param playlist
	 */
	public static void createPlaylist(Playlist playlist){
		if(playlist.tracks.size()==0){
			return;
		}
//		System.out.println(playlist.name);
		Path path = Paths.get(Settings.hardlinkPath+"Playlists/"+pathFix(playlist.name)+".m3u");

		try {
			if(!Files.deleteIfExists(path)){
				path=Files.createFile(path);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> text= new ArrayList<String>();
		text.add("#EXTM3U");
		//		System.out.println("size "+playlist.tracks.size());
		for (Track track : playlist.tracks) {
			text.add("#EXTINF:1,"+track.name+" - "+track.artist);
			text.add("..\\"+track.path.toString().substring(Settings.hardlinkPath.length()));
		}
		try {
			Files.write(path, text, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}