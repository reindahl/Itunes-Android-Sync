package syncFiles.MTP;

import java.io.IOException;
import java.math.BigInteger;
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

	public DeviceMTP(PortableDeviceStorageObject storage) {
		this.storage=storage;
		

	}

	@Override
	public void delete(HashMap<String, Path> existingFiles){
		System.out.println("delete");

		for (PortableDeviceObject child : storage.getChildObjects()) {
			if(child instanceof PortableDeviceFolderObject){
				PortableDeviceFolderObject dir= (PortableDeviceFolderObject) child;
				//String tmpPath= pcPath.toString()+"\\"+dir.getName();
				String tmpPath= dir.getOriginalFileName();
				if(existingFiles.containsKey(dir.getName())){
					//explore
					deleteWalk(tmpPath, dir, existingFiles);
				}

			}

		}

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
		monitor(true);
		
		progress.stop();
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
					PortableDeviceAudioObject file=folder.addAudioObject(tmpPath.toFile(), "", "",new BigInteger("123456789"));
					size+=file.getSize().longValueExact();
					monitor(true);
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
