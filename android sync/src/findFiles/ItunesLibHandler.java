package findFiles;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import findFiles.Track.FileType;


public class ItunesLibHandler extends org.xml.sax.helpers.DefaultHandler {
	HashMap<String, Track> tracks= new HashMap<String, Track>(2000);
	ArrayList<Playlist> playlists = new ArrayList<Playlist>();
	Track currentTrack;
	boolean done=true;
	enum Type{key, string, integer, none}
	enum State{tracks, playlists}
	State state;
	Type type;
	String list="";
	String previousTag="";
	String currentTag="";
	String importantTag="";

	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		done=false;

		if (qName.equalsIgnoreCase("Key")) {
			type=Type.key;

		}
		if (qName.equalsIgnoreCase("Integer")) {
			type=Type.integer;

		}
		if (qName.equalsIgnoreCase("String")) {
			type=Type.string;

		}
		if (qName.equalsIgnoreCase("true") && currentTag.equalsIgnoreCase("Disabled")) {
			currentTrack.Disabled=true;
		}

	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		done=true;
	}




	public void characters(char ch[], int start, int length) throws SAXException {
		//		Tracks
		//			Track ID
		//			Location
		//			Disabled
		//		Playlists
		//			Name
		//				Music
		//				AudioBook


		currentTag=new String(ch, start, length);

		if(type==Type.key){
			importantTag=currentTag;
			type= Type.none;
		}
		if (currentTag.equalsIgnoreCase("Tracks")) {
			state=State.tracks;
		}
		if (currentTag.equalsIgnoreCase("Playlists")) {
			state=State.playlists;
		}



		if(state==State.tracks && (type==Type.string || type==Type.integer) ){
			switch (importantTag.toLowerCase()) {
			case "track type":
				if(currentTag.equalsIgnoreCase("remote")){
					currentTrack.Disabled=true;
					currentTrack.filetype=FileType.remote;
				}
				break;
			case "location":
				currentTrack.Location=currentTrack.Location.concat(currentTag);
				switch (currentTrack.Location.substring(0, 4)) {
				case "file":
					currentTrack.filetype=FileType.file;
					break;
				case "http":
					currentTrack.filetype=FileType.url;
					currentTrack.Disabled=true;
					break;
				default:
					currentTrack.filetype=FileType.unknown;
					break;
				}
				break;
			case "track id":
				currentTrack=new Track();
				currentTrack.ID=currentTag;
				tracks.put(currentTrack.ID, currentTrack);
				break;
			case "name":
				currentTrack.name=currentTrack.name.concat(currentTag);
				break;
			case "album":
				currentTrack.album=currentTrack.album.concat(currentTag);
				break;
			case "artist":
				currentTrack.artist=currentTrack.artist.concat(currentTag);
				break;
			case "album artist":
				currentTrack.albumArtist=currentTrack.albumArtist.concat(currentTag);
				break;
			default:
				break;
			}
			if(importantTag.equalsIgnoreCase("Location")){

				
			}

			if(importantTag.equalsIgnoreCase("track id")){

			}


		}

		if(state==State.playlists && (type==Type.string || type==Type.integer) ){
			if(importantTag.equalsIgnoreCase("name")){
				switch (currentTag.toLowerCase()) {
				case "music":
					list="music";
					break;
				case "books":
					list="audiobooks";
					break;
				case "podcasts":
					list="podcasts";
					break;
				case "library":
					list="podcasts";
					break;
				default:
					list="custom";
//					System.out.println(currentTag);
					playlists.add(new Playlist(currentTag));
					break;
				}


			}
			if(importantTag.equalsIgnoreCase("Distinguished Kind")){

				switch (currentTag.toLowerCase()) {
					case "4":
						list="music";
						break;
					case "5":
						list="audiobooks";
						break;
				}
			}
			if(importantTag.equalsIgnoreCase("Track ID") && (list.equalsIgnoreCase("audiobooks") || list.equalsIgnoreCase("music") || list.equalsIgnoreCase("podcasts"))){
				tracks.get(currentTag).Type=list;

			}else if(importantTag.equalsIgnoreCase("Track ID") && list.equalsIgnoreCase("custom")){
				Track track =tracks.get(currentTag);
				if(!track.Disabled){
					playlists.get(playlists.size()-1).tracks.add(track);
				}
			}
					
				

		}





	}
	public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (systemId.contains("PropertyList-1.0.dtd")) {
			return new InputSource(new FileReader("PropertyList-1.0.dtd"));
		} else {
			return null;
		}
	}
}
