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
	 * Finds and prepare files for synchronisation. 
	 * It reads the Itunes library and creates hardlinks for the ticked files in Itunes
	 */
	public static void FindFiles(){
		
		ArrayList<Track> tracks=readItunesXmlSax();
		
		tracks.forEach(e -> normaliseTrack(e));

		//delete old unwanted hardlinks
		DeleteVisitor visitor=new DeleteVisitor(tracks);
		try {
			Files.walkFileTree(Paths.get(Settings.hardlinkPath), visitor);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}

		//hardlink ticked files
		tracks.stream().filter(t -> !t.Disabled && t.Type != null).forEach(t -> createHardlink(t));

		//add file to avoid android scanning directory (it thinks its music)
		if(Files.notExists(Paths.get(Settings.hardlinkPath+"audiobooks/"+".nomedia"))){
			try {
				Files.createDirectories(Paths.get(Settings.hardlinkPath+"audiobooks/"));
				Files.createFile(Paths.get(Settings.hardlinkPath+"audiobooks/"+".nomedia"));				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//create playlists
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
	 * reads the iTunes library and creates a representation of it
	 * @return
	 */
	private static ArrayList<Track> readItunesXmlSax(){
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
	 * cleans up the tracks paths
	 * generates what the new path should be
	 * @param tracks
	 */
	private static void normaliseTrack(Track track){
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
					throw new UnknownFileTypeException();
				}
				
				if(track.albumArtist.equals("")){
					artist=track.artist;
				}else{
					artist=track.albumArtist;
				}
				track.pathRelativ=Paths.get(track.Type+"/"+pathFix(artist)+"/"+pathFix(track.album)+"/"+pathFix(Paths.get(track.Location).getFileName().toString()));
				track.pathHardlink=Paths.get(Settings.hardlinkPath.toString(),track.pathRelativ.toString());
			} catch (UnsupportedEncodingException | UnknownFileTypeException e) {
				e.printStackTrace();
			}

		}
	}
	
	




	/**
	 * removes symbols which are forbidden in a path  
	 * @param path
	 * @return clean string
	 */
	private static String pathFix(String path){
		return path.replaceAll("[\\//*?\":<>|]", "_").replaceAll("(^ )|( $)", "").replaceAll("(\\.$)", "_");
	}

	private static void createHardlink(Track track){
		Path existingFile=Paths.get(track.Location);
		Path newLink = track.pathHardlink;
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
			System.err.println(existingFile);
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
	 * creates a .m3u playlist with relative paths and bogus track length (track length isn't used by the players)
	 * @param playlist
	 */
	private static void createPlaylist(Playlist playlist){
		playlist.getTracks().removeIf(t-> t.Disabled);
		if(playlist.size()==0){
			return;
		}
		Path path = Paths.get(Settings.hardlinkPath+"Playlists/"+pathFix(playlist.getName())+".m3u");

		try {
			if(!Files.deleteIfExists(path)){
				path=Files.createFile(path);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> text= new ArrayList<String>();
		text.add("#EXTM3U");
		for (Track track : playlist.getTracks()) {
			text.add("#EXTINF:1,"+track.name+" - "+track.artist);
			text.add("..\\"+track.pathHardlink.toString().substring(Settings.hardlinkPath.length()));
		}
		try {
			Files.write(path, text, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}