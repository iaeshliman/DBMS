package aeshliman.database;

import java.util.LinkedList;
import java.util.Queue;

public class DatabaseManager
{
	// Instance Variables
	private DataManager dm;
	private Queue<String> requests;
	private Queue<String> responses;
	private LinkedList<PortThread> threads;
	
	public DatabaseManager()
	{
		// Initialize instance variables
		requests = new LinkedList<String>();
		responses = new LinkedList<String>();
		
		// Initialize the data manager
		dm = new DataManager("Accounts.txt","AccountPrimary.txt","LogFile.txt");
		dm.readFile();
		
		// Initialize the port threads
		int threadCount = 1;
		this.threads = new LinkedList<PortThread>();	
		for(int i=0; i<threadCount; i++)
		{
			threads.add(new PortThread(this));
		}
	}
	
	// Getters
	public DataManager getDM()
	{
		return this.dm;
	}
	
	public Queue<String> getRequests()
	{
		return this.requests;
	}
	
	public Queue<String> getResponses()
	{
		return this.responses;
	}
	
	public LinkedList<PortThread> getThreads()
	{
		return this.threads;
	}
	
	
	// Operations
	public void addRequest(String request)
	{
		this.requests.add(request);
	}
	
	
}
