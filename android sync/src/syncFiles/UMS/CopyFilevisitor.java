package syncFiles.UMS;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import settings.Settings;

public class CopyFilevisitor implements FileVisitor<Path> {
	DeviceUMS device;
	
	public CopyFilevisitor(DeviceUMS device) {
		this.device=device;

		
	}
	
	
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		System.out.println(dir);
		try {
			Files.createDirectory(Paths.get(device.path.toString()+"\\"+dir.toString().substring(Settings.hardlinkPath.length())));
		}catch(FileAlreadyExistsException e){
			
		}catch (Exception e) {
			if(!dir.equals(Paths.get(Settings.hardlinkPath))){
				e.printStackTrace();
				System.exit(-1);
			}
		}


		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		System.out.println(file);
		Path path=Paths.get(device.path.toString()+"\\"+file.toString().substring(Settings.hardlinkPath.length()));
		
		if(Files.exists(path) && Files.getLastModifiedTime(file).compareTo(Files.getLastModifiedTime(path)) <= 0  && Files.size(file)==Files.size(path)){

			return FileVisitResult.CONTINUE;
		}
		
		Files.copy(file, Paths.get(path+".new"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		Files.move(Paths.get(path+".new"), path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		device.size+=file.toFile().length();
		device.monitor(true);


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
	
	boolean release=false;
	public synchronized void monitor(boolean state){
		
		if(state==true){
			release=true;
			notifyAll();
		}else{
			try {
				do{
					wait();
				}while(!release);
				release=false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	
}
