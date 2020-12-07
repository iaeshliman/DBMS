import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import aeshliman.DBMS.PortManager;
import aeshliman.UI.TellerManager;
import aeshliman.backup.BackupPort;
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
		String backupFile = "AccountReplicate.txt";
		String logFile = "LogFile.txt";
		String backupLogFile = "BackupLogFile.txt";
		
		// Shared queues for transaction processing
		Queue<String> requests = new LinkedList<String>();
		Queue<String> responses = new LinkedList<String>();
		Queue<String> backupRequests = new LinkedList<String>();
		Hashtable<Integer,String> backupResponses = new Hashtable<Integer,String>();
		
		// Initialization of objects
		DataRepository data = new DataRepository(srcFile,dstFile,logFile);
		DataRepository backupData = new DataRepository(srcFile,backupFile,backupLogFile);
		PortManager portManager = new PortManager(requests,responses,backupRequests,backupResponses,data,3);
		TellerManager tellerManager = new TellerManager(requests,responses,5,7);
		BackupPort backupPort = new BackupPort(backupData,backupRequests,backupResponses);
		
		// Starting threads and reading from persistent storage
		data.readFile();
		backupData.readFile();
		portManager.startPorts();
		tellerManager.startTellers();
		backupPort.start();
		
		// Checks that all tellers have finished and all ports are waiting for new requests
		while(tellerManager.tellersRunning()) {}
		while(!portManager.portsWaiting()||!backupPort.portsWaiting()) {}
		
		// Interrupts all ports as there will be no more notify calls
		portManager.portsInterrupt();
		backupPort.portsInterrupt();
		
		// Stores changes into persistent storage and appends <END> to log file
		data.writeFile();
		data.appendLog("<END>\n");
		backupData.writeFile();
		backupData.appendLog("<END>\n");
		
		// Goodbye
		System.out.println("DBMS complete");
	}
}
