package aeshliman.database;

import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class HashEntry
{
	// Instance variables
	private LinkedList<Account> accounts;
	
	// Locks
	private ReentrantReadWriteLock rwLock;
	
	// Constructors
	public HashEntry()
	{
		// Initialize locks
		rwLock = new ReentrantReadWriteLock();
		
		// Initialize instance variables
		this.accounts = new LinkedList<Account>();
	}
	
	// Getters
	public ReentrantReadWriteLock getLock()
	{
		return rwLock;
	}
	
	public LinkedList<Account> getAccounts()
	{
		return this.accounts;
	}
	
	public Account getFirst()
	{
		Account temp = new Account(this.accounts.getFirst());
		return temp;
	}
	
	// Operations
	public void add(Account data)
	{
		this.accounts.add(data);
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
		
		for(Account acc : accounts)
		{
			toFile += acc.formatFile() + "\n";
		}
		
		return toFile.trim();
	}
	
	// toString
	public String toString()
	{
		String toString = "";
		
		for(int i=0; i<this.accounts.size(); i++)
		{
			toString += this.accounts.get(i).toString() + "\n";
		}
		
		return toString;
	}
}
