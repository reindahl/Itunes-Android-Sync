package syncFiles;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import gui.Progress;
import syncFiles.UMS.CopyFilevisitor;


/**
 * 
 * @author Kasper Reindahl Rasmussen
 *	Determines the status of the copying
 */
public class CopyProgress extends Thread {
	Device device;
	CopyFilevisitor copyfilevisitor; 
	long size;


	
	Progress gui;

	public CopyProgress(Device device, long size){
		this.device=device;
		this.size=size;
		
		gui=new Progress();
	}

	public void run() {
		long progress;

		long start;
		long elapsedTime;
		double elapsedTimeSeconds = 0;

		double estimate=0;
		start = System.nanoTime();

		while(size!=device.sizeOfFilesCopied){
			device.monitorWait();
			progress=device.sizeOfFilesCopied;

			try {
				if(progress!=0){
					elapsedTime = System.nanoTime() - start;

					elapsedTimeSeconds = (double)elapsedTime / 1000000000.0;
					if(elapsedTimeSeconds>0 ){
						estimate=(size-progress)/(progress/elapsedTimeSeconds);
						
						System.out.println("size left: "+helperFiles.conversion.humanReadableByteCount(size-progress, false));

						System.out.println("estimated time left: "+helperFiles.conversion.secondsToHMS(estimate));
						gui.update((int)(progress/((double)size)*1000.), 1000);
						if(((long)elapsedTimeSeconds)>0){
							long transferRate =progress/(long)elapsedTimeSeconds;
							System.out.println("transfer Rate: "+helperFiles.conversion.humanReadableByteCount(transferRate, false)+"/s");
							gui.updateInfo(helperFiles.conversion.humanReadableByteCount(transferRate, false),helperFiles.conversion.secondsToHMS(estimate),helperFiles.conversion.humanReadableByteCount(size-progress, false));
						}
						
						int i=0;
						String tmpPath= device.popPath();
						while(tmpPath!=null){
							i++;
							gui.addLine(tmpPath);
							tmpPath=device.popPath();
						}
						System.out.println(i);

					}
				}
			} catch (ArithmeticException e) {
				System.err.println("Arithmetic Exception:"+elapsedTimeSeconds+" "+progress+" "+((long)elapsedTimeSeconds));
				e.printStackTrace();
			}


		}
	}
}
