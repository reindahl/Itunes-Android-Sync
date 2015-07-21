package syncFiles.UMS;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Deletes files not contained in existingFiles
 * uses the size of the files to estimate if they are the same
 * @author Kasper Reindahl Rasmussen
 *
 */
public class DeleteFilevisitor implements FileVisitor<Path>{
	HashMap<String, Path> existingFiles;
	String rootpath;
	ArrayList<Boolean> delete= new ArrayList<Boolean>();
	public long size=0;
	boolean first=true;
	
	/**
	 * 
	 * @param files
	 * 			files that should not be deleted
	 * @param path
	 * 			
	 */
	public DeleteFilevisitor(HashMap<String, Path> files, String path) {
		existingFiles=files;
		rootpath=path;
		delete.add(false);

	}


	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if(delete.get(delete.size()-1)){
			delete.add(true);
		}else{
			if(existingFiles.get(dir.toString().substring(rootpath.length()))==null && !first){
				delete.add(true);
			}else{
				delete.add(false);
			}
		}
		first=false;
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

		if(delete.get(delete.size()-1)){
			//file placed in folder that should be deleted
			Files.delete(file);
		}else{
			Path result =existingFiles.get(file.toString().substring(rootpath.length()));
			if(result==null){
				Files.delete(file);
			}else if(Files.size(result)==attrs.size()){
					size+=file.toFile().length();
			}else{
				//old version of the file
				Files.delete(file);
//				System.out.println("file changed since last sync:");
//				System.out.println(file.toString());
			}

		}
		
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
