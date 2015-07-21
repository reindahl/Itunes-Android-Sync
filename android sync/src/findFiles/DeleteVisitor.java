package findFiles;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Kasper Reindahl Rasmussen
 *
 *	Removes tracks which are disabled
 *
 */
public class DeleteVisitor implements FileVisitor<Path>{

	HashMap<Path, Boolean> paths=new HashMap<>();
	ArrayList<Boolean> delete= new ArrayList<Boolean>();
	public DeleteVisitor(Track[] tracks){
		for(Track track: tracks){
			paths.put(track.path, track.Disabled);
		}
	}
	public DeleteVisitor(ArrayList<Track> tracks){
		tracks.forEach(track -> paths.put(track.path, track.Disabled));
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		delete.add(true);
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if(delete.get(delete.size()-1)){
			Files.delete(dir);
		}
		delete.remove(delete.size()-1);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Boolean exist =paths.get(file);
		if(exist==null || exist){
			Files.delete(file);
		}else{
			for (int i = 0; i < delete.size(); i++) {
				delete.set(i, false);
			}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.err.println(exc);
		return FileVisitResult.CONTINUE;
	}

}
