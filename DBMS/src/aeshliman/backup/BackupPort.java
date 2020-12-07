package aeshliman.backup;

import java.util.Hashtable;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import aeshliman.database.DataRepository;
import aeshliman.database.HashEntry;

/*
 * Author: Isaac Aeshliman
 * Date: 12/7/2020
 * Description: A backup port reading from the backup request queue, processing account transfers,
 * 				updating a log file, and writing to the backup response hashtable.
 * 				All account transfers must be finalized in the backup before they can be committed.
 */

public class BackupPort extends Thread
{
	// Instance Variables
	private DataRepository data;
	private Queue<String> requests;
	private Hashtable<Integer,String> responses;
	
	// Constructor
	public BackupPort(DataRepository data, Queue<String> requests, Hashtable<Integer,String> responses)
	{
		this.data = data;
		this.requests = requests;
		this.responses = responses;
	}
	
	public void run()
	{
		while(true)
		{
			String req = readRequest();
			
			if(req==null) break;
			else
			{
				writeResponse(Integer.parseInt(req.split(";")[0]),completeRequest(req));
			}
		}
	}
	
	// Operations
	public String readRequest()
	{
		synchronized(requests)
		{
			while(requests.isEmpty())
			{
				try { requests.wait(); }
				catch(InterruptedException e) { return null; }
			}
			return requests.poll();
		}
	}
	
	public void writeResponse(int id, String response)
	{
		synchronized(responses)
		{
			responses.put(id,response);
			responses.notifyAll();
		}
	}
	
	public String completeRequest(String request)
	{
		String[] reqs = request.split(";");
		int id = Integer.parseInt(reqs[0]);
		
		// Appends begin statement to log file
		data.appendLog("<BEGIN " + id + ">\n");
		
		for(int i=1; i<reqs.length; i++)
		{
			// Parses account transfer
			Scanner parse = new Scanner(reqs[i]);
			
			int src = parse.nextInt();
			int dst = parse.nextInt();
			float qty = parse.nextFloat();
			
			parse.close();
				
			// Performs account transfer
			data.getAccount(src).findAccount(src).withdraw(qty);
			data.getAccount(dst).findAccount(dst).deposit(qty);
			
			// Appends update to log file
			data.appendLog("<UPDATE " + id + ">" + src + "," + dst + "," + qty + "\n");
		}
		data.appendLog("<COMMIT " + id + ">\n");
		
		return "<COMPLETE " + id + ">";
	}
	
	public boolean portsWaiting()
	{
		return this.getState()==State.WAITING;
	}
	
	public void portsInterrupt()
	{
		this.interrupt();
	}
}
