import java.util.LinkedList;
import java.util.Queue;
import aeshliman.DBMS.PortManager;
import aeshliman.UI.TellerManager;
import aeshliman.database.DataRepository;

/*
 * Author: Isaac Aeshliman
 * Date:
 * Description: Drives the DBMS program. Initializes all objects and runs methods to start all threads.
 * 				Checks for when the threads are finished then ends the program and finalizes the log file.
 */

public class Driver
{	
	public static void main(String[] args)
	{
		// Files for persistent storage
		String srcFile = "Accounts.txt";
		String dstFile = "AccountPrimary.txt";
		String logFile = "LogFile.txt";
		
		// Shared queues for transaction processing
		Queue<String> requests = new LinkedList<String>();
		Queue<String> responses = new LinkedList<String>();

		// Initialization of objects
		DataRepository data = new DataRepository(srcFile,dstFile,logFile);
		PortManager portManager = new PortManager(requests,responses,data);
		TellerManager tellerManager = new TellerManager(requests,responses);
		
		// Starting threads and reading from persistent storage
		data.readFile();
		portManager.startPorts();
		tellerManager.startTellers();
		
		// Checks that all tellers have finished and all ports are waiting for new requests
		while(tellerManager.tellersRunning()) {}
		while(!portManager.portsWaiting()) {}
		
		// Interrupts all ports as there will be no more notify calls
		portManager.portsInterrupt();
		
		// Stores changes into persistent storage and appends <END> to log file
		data.writeFile();
		data.appendLog("<END>\n");
		
		// Goodbye
		System.out.println("DBMS complete");
	}
}
