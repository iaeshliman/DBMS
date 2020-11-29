package aeshliman.database;

import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class MyHash
{
	// Instance variables
	private LinkedList<Account> accounts;
	
	// Locks
	private ReentrantReadWriteLock rwLock;
	private Lock readLock;
	private Lock writeLock;
	
	// Constructors
	public MyHash()
	{
		// Initialize locks
		rwLock = new ReentrantReadWriteLock();
		readLock = rwLock.readLock();
		writeLock = rwLock.writeLock();
		
		// Initialize instance variables
		this.accounts = new LinkedList<Account>();
	}
	
	public ReentrantReadWriteLock getRWLock()
	{
		return rwLock;
	}
	
	public LinkedList<Account> getAccounts()
	{
		return this.accounts;
	}
	
	// Adds a new account to the list
	public void add(Account data)
	{
		writeLock.lock();
		this.accounts.add(data);
		writeLock.unlock();
	}
	
	// Returns the first account in the list
	public Account getFirst()
	{
		readLock.lock();
		Account temp = new Account(this.accounts.getFirst());
		readLock.unlock();
		return temp;
	}
	
	public Account findAccount(int accountNum)
	{
		for(Account account : accounts)
		{
			if(account.equals(accountNum)) return account;
		}
		
		return null;
	}
	
	// Returns a string of the object formatted for writing to a file
	public String formatFile()
	{
		String toFile ="";
		
		readLock.lock();
		for(Account acc : accounts)
		{
			toFile += acc.formatFile() + "\n";
		}
		readLock.unlock();
		
		return toFile.trim();
	}
	
	// toString
	public String toString()
	{
		String toString = "";
		readLock.lock();
		
		for(int i=0; i<this.accounts.size(); i++)
		{
			toString += this.accounts.get(i).toString() + "\n";
		}
		
		readLock.unlock();
		return toString;
	}
}
