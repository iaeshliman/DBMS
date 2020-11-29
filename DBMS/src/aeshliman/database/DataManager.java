package aeshliman.database;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.*;

public class DataManager
{
	// Instance variables
	private MyHash[] hashes;
	private File srcFile;
	private File dstFile;
	private File logFile;
	
	// Locks
	private ReentrantReadWriteLock rwLock;
	private Lock readLock;
	private Lock writeLock;
	
	// Constructor
	public DataManager(String src, String dst, String log)
	{		
		// Initialize locks
		this.rwLock = new ReentrantReadWriteLock();
		this.readLock = rwLock.readLock();
		this.writeLock = rwLock.writeLock();
		
		// Initialize instance variables
		this.srcFile = new File(src);
		this.dstFile = new File(dst);
		this.logFile = new File(log);
		
		// Initialize hash table
		this.hashes = new MyHash[20];
		for(int i=0; i<hashes.length; i++)
		{
			hashes[i] = new MyHash();
		}
	}
	
	public File getSrcFile()
	{
		return this.srcFile;
	}
	
	public File getDstFile()
	{
		return this.dstFile;
	}
	
	public File getLogFile()
	{
		return this.logFile;
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
				double balance = Double.parseDouble(line[3]);
				
				Account temp = new Account(line[0],line[1],accountNum,balance);
				
				writeLock.lock();
				hashes[accountNum%hashes.length].add(temp);
				writeLock.unlock();
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
		
		readLock.lock();
		for(MyHash hash : hashes)
		{
			toFile += hash.formatFile() + "\n";
		}
		readLock.unlock();
		
		return toFile.trim();
	}
	
	public MyHash getAccount(int account)
	{
		return hashes[account%20];
	}
	
	// toString
	public String toString()
	{
		String toString = "";
		readLock.lock();
		
		for(MyHash hash : hashes)
		{
			toString += "Key " + hash.getFirst().getAccountNum()%hashes.length + "\n";
			toString += hash.toString() + "\n";
		}
		
		readLock.unlock();
		return toString;
	}
}
