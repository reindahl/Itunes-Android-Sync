package syncFiles;

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

	
	public CopyProgress(Device device, long size){
		this.device=device;
		this.size=size;
	}

	public void run() {
		long progress;

		long start;
		long elapsedTime;
		double elapsedTimeSeconds = 0;

		double estimate=0;
		start = System.nanoTime();

		while(size!=device.size){
			device.monitor(false);
			progress=device.size;

			try {
				if(progress!=0){
					elapsedTime = System.nanoTime() - start;

					elapsedTimeSeconds = (double)elapsedTime / 1000000000.0;
					if(elapsedTimeSeconds>0 ){
						estimate=(size-progress)/(progress/elapsedTimeSeconds);
						
						System.out.println("size left: "+helperFiles.conversion.humanReadableByteCount(size-progress, false));

						System.out.println("estimated time left: "+helperFiles.conversion.secondsToHMS(estimate));
						
						if(((long)elapsedTimeSeconds)>0){
							long transferRate =progress/(long)elapsedTimeSeconds;
							System.out.println("transfer Rate: "+helperFiles.conversion.humanReadableByteCount(transferRate, false)+"/s");
						}




						

					}
				}
			} catch (ArithmeticException e) {
				System.err.println("Arithmetic Exception:"+elapsedTimeSeconds+" "+progress+" "+((long)elapsedTimeSeconds));
				e.printStackTrace();
			}


		}
	}
}
