package aeshliman.database;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

public class PortThread extends Thread
{
	// Instance Variables
	private DatabaseManager dbms;
	private DataManager dm;
	private String name;
	private static int count = 0;
	private static int tranId = 0;
	
	public PortThread(DatabaseManager dbms)
	{
		this.dbms = dbms;
		this.dm = dbms.getDM();
		this.name = "Port " + count++;
	}
	
	public void run()
	{
		while(!dbms.getRequests().isEmpty())
		{
			this.update(dbms.getRequests().poll());
		}
		
		dm.appendLog("<END " + this.name + ">\n");
	}
	
	public void update(String req)
	{
		String[] queries = req.split(";");
		LinkedList<Lock> locks = new LinkedList<Lock>();
		int id = ++tranId;
		dm.appendLog("<BEGIN " + id + ">\n");
		
		for(int i=0; i<queries.length; i++)
		{
			Scanner parse = new Scanner(queries[i]);
			
			int src = parse.nextInt();
			int dst = parse.nextInt();
			float qty = parse.nextFloat();
			
			parse.close();
			
			MyHash hash = dm.getAccount(src);
			Lock wLock = hash.getRWLock().writeLock();
			locks.add(wLock);
			
			wLock.lock();
			hash.findAccount(src).withdraw(qty);
			
			hash = dm.getAccount(dst);
			
			wLock.lock();
			hash.findAccount(dst).deposit(qty);
			
			dm.appendLog("<UPDATE " + id + ">" + src + "," + dst + "," + qty + "\n");
		}
		
		locks.forEach((value) -> {value.unlock();});
		dm.appendLog("<COMMIT " + id + ">\n");
	}
	
	public String toString()
	{
		return name;
	}
}
