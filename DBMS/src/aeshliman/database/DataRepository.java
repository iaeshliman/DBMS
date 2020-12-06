package aeshliman.database;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.*;

public class DataRepository
{
	// Instance variables
	private HashEntry[] hashes;
	private File srcFile;
	private File dstFile;
	private File logFile;
	
	// Locks
	private ReentrantReadWriteLock rwLock;
	
	// Constructor
	public DataRepository(String src, String dst, String log)
	{		
		// Initialize locks
		this.rwLock = new ReentrantReadWriteLock(true);
		
		// Initialize instance variables
		this.srcFile = new File(src);
		this.dstFile = new File(dst);
		this.logFile = new File(log);
		
		// Initialize hash table
		this.hashes = new HashEntry[20];
		for(int i=0; i<hashes.length; i++)
		{
			hashes[i] = new HashEntry();
		}
	}
	
	// Getters
	public ReentrantReadWriteLock getLock()
	{
		return this.rwLock;
	}
	
	public HashEntry getAccount(int account)
	{
		return hashes[account%20];
	}
	
	// Operations
	public void readFile()
	{
		try(Scanner scan = new Scanner(srcFile))
		{
			while(scan.hasNext())
			{
				String[] line = scan.nextLine().split("\t");
				
				int accountNum = Integer.parseInt(line[2]);
				float balance = Float.parseFloat(line[3]);
				
				Account temp = new Account(line[0],line[1],accountNum,balance);
				
				hashes[accountNum%hashes.length].add(temp);
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File " + srcFile.getName() + " not found");
		}
	}
	
	// Adds a line to the log file
	public void appendLog(String line)
	{
		try(FileWriter write = new FileWriter(logFile,true))
		{
			write.write(line);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// Writes the contents of the object to a file
	public void writeFile()
	{
		try(FileWriter write = new FileWriter(dstFile))
		{
			write.write(this.formatFile());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// Returns a string of the object formatted for writing to a file
	public String formatFile()
	{
		String toFile = "";
		
		for(HashEntry hash : hashes)
		{
			toFile += hash.formatFile() + "\n";
		}
		
		return toFile.trim();
	}
	
	// toString
	public String toString()
	{
		String toString = "";
		
		for(HashEntry hash : hashes)
		{
			toString += "Key " + hash.getFirst().getAccountNum()%hashes.length + "\n";
			toString += hash.toString() + "\n";
		}
		
		return toString;
	}
}
