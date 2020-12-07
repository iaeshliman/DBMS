package aeshliman.DBMS;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import aeshliman.database.DataRepository;
import aeshliman.database.HashEntry;

/*
 * Author: Isaac Aeshliman
 * Date: 12/7/2020
 * Description: A port in the dbms reading from the request queue, processing account transfers
 * 				in an atomic manner, updating a log file, and writing to the response queue. Also
 * 				writes to the backup request queue and reads from the response hashtable. Requires
 * 				a backup be completed before committing account transfer.
 */

public class Port extends Thread
{
	// Constants
	private final int lockTimeout = 100; // Time spent trying to get lock before rollback
	
	// Instance Variables
	private Queue<String> requests;
	private Queue<String> responses;
	private Queue<String> backupRequests;
	private Hashtable<Integer,String> backupResponses;
	private DataRepository data;
	
	public Port(Queue<String> requests, Queue<String> responses, Queue<String> backupRequests, Hashtable<Integer,String> backupResponses, DataRepository data)
	{
		this.requests = requests;
		this.responses = responses;
		this.backupRequests = backupRequests;
		this.backupResponses = backupResponses;
		this.data = data;
	}
	
	public void run()
	{
		while(true)
		{
			String req = readRequest();
			
			if(req==null) break;
			else
			{
				writeResponse(completeRequest(req));
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
	
	public String readBackupResponse(int id)
	{
		synchronized(backupResponses)
		{
			while(backupResponses.get(id)==null)
			{
				try { backupResponses.wait(); }
				catch(InterruptedException e) { return null; }
			}
				
			return backupResponses.remove(id);
		}
	}
	
	public void writeResponse(String response)
	{
		synchronized(responses)
		{
			responses.add(response);
			responses.notify();
		}
	}
	
	public void writeBackupRequest(String request)
	{
		synchronized(backupRequests)
		{
			backupRequests.add(request);
			backupRequests.notify();
		}
	}
	
	public String completeRequest(String req)
	{
		LinkedList<String> localLog = new LinkedList<String>();
		LinkedList<Lock> locks = new LinkedList<Lock>();
		int id = Integer.parseInt(req.split(";")[0]);
		
		// Appends begin statement to log file
		data.appendLog("<BEGIN " + id + ">\n");
		
		// Will wait until read lock is available
		try
		{
			while(!data.getLock().readLock().tryLock(1000,TimeUnit.MILLISECONDS)) {}
		}
		catch(InterruptedException e) {}
		
		// If soft update fails due to a deadlock force a successful update by locking entire database
		if(!tryUpdate(req,locks,localLog))
		{
			// Performs rollback and unlocks all locks
			rollback(localLog);
			locks.forEach((lock)->lock.unlock());
			data.getLock().readLock().unlock();
			
			// Locks entire data repository to ensure successful update
			try
			{
				while(!data.getLock().writeLock().tryLock(1000,TimeUnit.MILLISECONDS)) {}
			}
			catch(InterruptedException e) {}
			forceUpdate(req);
			
			// Sends request for backup and waits until it receives response
			writeBackupRequest(req);
			readBackupResponse(id);
			
			// Commits and unlocks data repository
			data.appendLog("<COMMIT " + id + ">\n");
			data.getLock().writeLock().unlock();
		}
		else
		{
			// Sends request for backup and waits until it receives response
			writeBackupRequest(req);
			readBackupResponse(id);
			
			// Commits and unlocks locks and data repository
			data.appendLog("<COMMIT " + id + ">\n");
			locks.forEach((lock)->lock.unlock());
			data.getLock().readLock().unlock();
		}
		
		return "<COMPLETE " + id + ">";
	}
	
	public void rollback(LinkedList<String> localLog)
	{
		localLog.forEach((value)->
		{
			// Performs an update in reverse
			Scanner parse = new Scanner(value);
			
			// Initialize local variables
			int id = parse.nextInt();
			int src = parse.nextInt();
			int dst = parse.nextInt();
			float qty = parse.nextFloat();
			
			parse.close();
			
			// Performs reversal of update
			data.getAccount(src).findAccount(src).deposit(qty);
			data.getAccount(dst).findAccount(dst).withdraw(qty);
			
			// Records rollback into the log file
			data.appendLog("<ROLLBACK " + id + ">" + src + "," + dst + "," + qty + "\n");
		});
	}
	
	public boolean tryUpdate(String req, LinkedList<Lock> locks, LinkedList<String> localLog)
	{
		// Splits request into each account transfer
		String[] reqs = req.split(";");
		int id = Integer.parseInt(reqs[0]);
		
		for(int i=1; i<reqs.length; i++)
		{
			// Parses account transfer
			Scanner parse = new Scanner(reqs[i]);
			
			int src = parse.nextInt();
			int dst = parse.nextInt();
			float qty = parse.nextFloat();
			
			parse.close();
			
			// Gets local copies of the locks and hash entries
			HashEntry hashSRC = data.getAccount(src);
			HashEntry hashDST = data.getAccount(dst);
			Lock lockSRC = hashSRC.getLock().writeLock();
			Lock lockDST = hashDST.getLock().writeLock();
			
			try // Attempts to lock each hash entry
			{
				if(lockSRC.tryLock(lockTimeout,TimeUnit.MILLISECONDS))
				{
					locks.add(lockSRC); // Add to lock array once successfully locked
					
					if(lockDST.tryLock(lockTimeout,TimeUnit.MILLISECONDS))
					{
						locks.add(lockDST); // Add to lock array once successfully locked
						
						// Performs account transfer after successfully locking hash entry
						hashSRC.findAccount(src).withdraw(qty);
						hashDST.findAccount(dst).deposit(qty);
						
						// Create local log file for rollback
						localLog.add(id + " " + src + " " + dst + " " + qty + "\n");
						
						// Appends update to log file
						data.appendLog("<UPDATE " + id + ">" + src + "," + dst + "," + qty + "\n");
					}
					else { return false; } // Rollback needed
				}
				else { return false; } // Rollback needed
			}
			catch(InterruptedException e) {}
		}
		return true; // Rollback not needed
	}
	
	public void forceUpdate(String req)
	{
		// Splits request into each account transfer
		String[] reqs = req.split(";");
		int id = Integer.parseInt(reqs[0]);
		
		for(int i=1; i<reqs.length; i++)
		{
			// Parses account transfer
			Scanner parse = new Scanner(reqs[i]);
			
			int src = parse.nextInt();
			int dst = parse.nextInt();
			float qty = parse.nextFloat();
			
			parse.close();
			
			data.getAccount(src).findAccount(src).withdraw(qty);
			data.getAccount(dst).findAccount(dst).deposit(qty);
			
			// Appends update to log file
			data.appendLog("<UPDATE " + id + ">" + src + "," + dst + "," + qty + "\n");
		}
	}
}
