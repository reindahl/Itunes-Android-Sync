package syncFiles.UMS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import settings.Settings;
import syncFiles.CopyProgress;
import syncFiles.Device;
import syncFiles.Sync;

public class DeviceUMS extends Device{
	Path path;
	public DeviceUMS(File path) {
		this.path= Paths.get(path.getPath());
	}

	public DeviceUMS(String droidPath) {
		this.path= Paths.get(droidPath);
	}
	
	public DeviceUMS(Path path) {
		this.path=path;
	}

	@Override
	public String toString() {
		return path.toString();
	}
	@Override
	public void delete(HashMap<String, Path> existingFiles){
		System.out.println("delete");
		File dir=new File(Settings.hardlinkPath);
		File[] listOfFiles=dir.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isDirectory()){
				Path startPath =Paths.get(path.toString()+"\\"+listOfFiles[i].getName());
				if(Files.exists(startPath)){	
				try {
					DeleteFilevisitor visitor=new DeleteFilevisitor(existingFiles, path.toString());
					Files.walkFileTree(startPath,visitor );
					Sync.size-=visitor.size;
					System.out.println(listOfFiles[i].getName());
					System.out.println("size not needed to be copied "+helperFiles.conversion.humanReadableByteCount(visitor.size, false));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(-1);
				}
				}
			}
		}
		
	}
	
	@Override
	public void copy(){
		

		System.out.println("Copy");
		try {
			CopyFilevisitor visitor =new CopyFilevisitor(this);
			CopyProgress progress = new CopyProgress(this, Sync.size);
			progress.start();
			Files.walkFileTree(Paths.get(Settings.hardlinkPath), visitor);
			monitor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
