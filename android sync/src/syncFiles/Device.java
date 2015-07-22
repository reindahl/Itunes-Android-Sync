package syncFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jmtp.PortableDevice;
import jmtp.PortableDeviceManager;
import jmtp.PortableDeviceObject;
import jmtp.PortableDeviceStorageObject;
import syncFiles.MTP.DeviceMTP;
import syncFiles.UMS.DeviceUMS;

public abstract class Device {

	/**
	 * 
	 * @return The device which should be synchronized
	 */
	public static Device find(){
		
		Device droid=null;
		
		ArrayList<Device> devices=new ArrayList<>();
		
		
		devices.addAll(findUMS());
		devices.addAll(findMTP());
		
		if(devices.isEmpty()){
			System.out.println("cant find droid");
			JFileChooser chooser=new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(null);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				String droidPath=chooser.getSelectedFile().getPath();
			    try {
					Files.createFile(Paths.get(droidPath,".Droid"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    droid=new DeviceUMS(droidPath);
			}else{
				System.out.println("these aren't the droids you're looking for");
				System.exit(-1);
			}
		}else if(devices.size()==1){
			System.out.println("found the droid");
			droid= devices.get(0);
		}else {

			droid = (Device)JOptionPane.showInputDialog(
			                    null,
			                    "Which droid are you looking for?",
			                    "Customized Dialog",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    devices.toArray(),
			                    devices.get(0));


			if ((droid == null)) {
				System.out.println("these aren't the droids you're looking for");
				System.exit(-1);
			}
		}
		
		
		return droid;
	}
	

	
	private static ArrayList<Device>  findMTP() {
		
		ArrayList<Device> MTPs=new ArrayList<>();
		
		PortableDeviceManager manager = new PortableDeviceManager();
		
		 for(PortableDevice device : manager) {
		        device.open();

		        // Iterate over deviceObjects
		        for (PortableDeviceObject object : device.getRootObjects()) {
		        	
		            // If the object is a storage object
		            if (object instanceof PortableDeviceStorageObject) {
		                PortableDeviceStorageObject storage = (PortableDeviceStorageObject) object;
		                System.out.println(storage.getName());
		                
		                
		             
		                for (PortableDeviceObject child : storage.getChildObjects()) {
		                	if(child.getName().equals(".Droid")){
		                		MTPs.add(new DeviceMTP(storage));
		                		break;
		                	}
		                	
		                	
		                }
		                
		                

		            }
		        }

		        device.close();
			 }
		
		
		
		return MTPs;
	}



	private static ArrayList<Device> findUMS(){
		
		
		ArrayList<Device> UMS=new ArrayList<>();
		
		// returns pathnames for files and directory


		// for each pathname in pathname array
		for(File path:File.listRoots())		{
			if(Files.exists(Paths.get(path.toString(), ".Droid"))){
				UMS.add(new DeviceUMS(path));
				
			}
		}
		
		return UMS;

	}
	
	public long sizeOfFilesCopied=0;
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
	
	public abstract void delete(HashMap<String, Path> existingFiles);
	
	
	public abstract void copy();
	
}
