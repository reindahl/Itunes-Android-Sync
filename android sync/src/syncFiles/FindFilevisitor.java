package syncFiles;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

/**
 * 
 * @author Reindahl
 *
 *	Finds all files in a given directory
 */
public class FindFilevisitor implements FileVisitor<Path> {
	public HashMap<String, Path> files = new HashMap<>();
	String pcPath;
	long size=0;
	
	public FindFilevisitor(String pcPath) {
		this.pcPath=pcPath;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		
		String path;
		try {
			path=dir.toString().substring(pcPath.length());
			files.put(path,dir);
		} catch (Exception e) {

			if(!Paths.get(pcPath).equals(dir)){
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		String path=file.toString().substring(pcPath.length());
		files.put(path,file);
		size+=file.toFile().length();
//	       if (attrs.isSymbolicLink()) {
//	            System.out.format("Symbolic link: %s ", file);
//	        } else if (attrs.isRegularFile()) {
//	            System.out.format("Regular file: %s ", file);
//	        } else {
//	            System.out.format("Other: %s ", file);
//	        }
//	        System.out.println("(" + attrs.size() + "bytes)");
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.err.println(exc);
		return FileVisitResult.CONTINUE;
	}

}
