package syncFiles.MTP;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

import jmtp.PortableDeviceAudioObject;
import jmtp.PortableDeviceContainerObject;
import jmtp.PortableDeviceFolderObject;
import jmtp.PortableDeviceObject;
import jmtp.PortableDeviceStorageObject;
import settings.Settings;
import syncFiles.CopyProgress;
import syncFiles.Device;
import syncFiles.Sync;

public class DeviceMTP extends Device {

	PortableDeviceStorageObject storage;
	long sizeOfExistingFiles=0;

	public DeviceMTP(PortableDeviceStorageObject storage) {
		this.storage=storage;
	}

	@Override
	public void delete(HashMap<String, Path> existingFiles){
		System.out.println("delete");

		for (PortableDeviceObject child : storage.getChildObjects()) {
			if(child instanceof PortableDeviceFolderObject){
				PortableDeviceFolderObject dir= (PortableDeviceFolderObject) child;
				String tmpPath= dir.getOriginalFileName();
				if(existingFiles.containsKey(dir.getName())){
					//explore
					deleteWalk(tmpPath, dir, existingFiles);
				}

			}

		}
		
		Sync.size-=sizeOfExistingFiles;

	}
	
	public void deleteWalk(String path, PortableDeviceFolderObject folder, HashMap<String, Path> existingFiles){

		for (PortableDeviceObject child : folder.getChildObjects()) {
			String tmpPath= path+"\\"+child.getOriginalFileName();
			if(child instanceof PortableDeviceFolderObject){

				PortableDeviceFolderObject dir= (PortableDeviceFolderObject) child;

				if(existingFiles.containsKey(tmpPath)){
					//explore
					deleteWalk(tmpPath, dir, existingFiles);

				}else{
					//delete
					System.out.println(tmpPath);
					dir.delete(true);
				}


			}else{

				if(!existingFiles.containsKey(tmpPath)){
					System.out.println(tmpPath);
					child.delete();
				}else{
					sizeOfExistingFiles+=child.getSize().longValueExact();
				}
			}

		}



	}

	@Override
	public void copy() {
		System.out.println("copy");
		Path path=Paths.get(Settings.hardlinkPath);
		CopyProgress progress = new CopyProgress(this, Sync.size);
		progress.start();
		copyWalk(path,  storage );
		monitor();

	}
	private void copyWalk(Path path, PortableDeviceContainerObject folder){

		//find existing files
		HashSet<String> paths = new HashSet<>();
		try(DirectoryStream<Path> dir= Files.newDirectoryStream(path)){
			for (Path existingPaths : dir) {
				paths.add(existingPaths.getFileName().toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//find existing files already on device
		for (PortableDeviceObject child : folder.getChildObjects()) {

			paths.remove(child.getOriginalFileName());
			if(child instanceof PortableDeviceFolderObject){
				PortableDeviceFolderObject file= (PortableDeviceFolderObject) child;
				String tmpPath = path.toString()+"\\"+child.getOriginalFileName();

				if(Files.exists(Paths.get(tmpPath))){

					copyWalk(Paths.get(tmpPath), file);
				}
			}else{
				//compare and if necessary replace file

				Path tmpPath = path.resolve(child.getOriginalFileName());
				try {
					//FIXME
//					if(Files.exists(tmpPath) && Files.getLastModifiedTime(tmpPath).toMillis()!=child.getDateModified().getTime()){
//						System.out.println("difference");
//						System.out.println(tmpPath);
//						System.out.println(Files.getLastModifiedTime(tmpPath)+"\n"+ child.getDateModified());
//						System.out.println(Files.getLastModifiedTime(tmpPath).toMillis()+"\n"+ child.getDateModified().getTime());
//						System.in.read();
//					}
//					if(!(Files.getLastModifiedTime(tmpPath).toMillis()<=child.getDateModified().getTime() && child.getSize().longValueExact()==Files.size(tmpPath))){
					if(Files.exists(tmpPath) && child.getSize().longValueExact()!=Files.size(tmpPath)){
						System.out.println(tmpPath);
						child.delete();
						PortableDeviceAudioObject file=folder.addAudioObject(tmpPath.toFile());
						sizeOfFilesCopied+=file.getSize().longValueExact();
						monitor(tmpPath.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				} 
				
			}

		}
		//create missing files
		for (String name : paths) {
			Path tmpPath= Paths.get(path+"/"+name);
			if(Files.isDirectory(tmpPath)){
				PortableDeviceFolderObject tmpFolder= folder.createFolderObject(name);
				copyWalk(tmpPath, tmpFolder);
			}else{
				try {
					System.out.println(tmpPath);
					PortableDeviceAudioObject file=folder.addAudioObject(tmpPath.toFile());
					sizeOfFilesCopied+=file.getSize().longValueExact();
					monitor(tmpPath.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	


	@Override
	public String toString() {
		return storage.getParent().getName()+" - " +storage.getDescription();
	}


}
