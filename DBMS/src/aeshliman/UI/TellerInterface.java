package aeshliman.UI;

import java.util.Hashtable;
import java.util.LinkedList;
import aeshliman.database.DatabaseManager;

public class TellerInterface
{
	// Constants
	private final int defaultCount = 3;
	
	// Instance Variables
	private DatabaseManager dbms;
	private LinkedList<Teller> tellers;
	private Hashtable<Integer,Teller> transactionMap;
	
	// Constructors
	public TellerInterface()
	{
		this.dbms = null;
		this.tellers = new LinkedList<Teller>();
		this.transactionMap = new Hashtable<Integer,Teller>();
		
		for(int i=0; i<defaultCount; i++)
		{
			this.tellers.add(new Teller(this));
		}
	}
	
	public TellerInterface(int tellerCount)
	{
		this.dbms = null;
		this.tellers = new LinkedList<Teller>();
		this.transactionMap = new Hashtable<Integer,Teller>();
		
		for(int i=0; i<tellerCount; i++)
		{
			this.tellers.add(new Teller(this));
		}
	}
	
	public TellerInterface(DatabaseManager dbms)
	{
		this.dbms = dbms;
		this.tellers = new LinkedList<Teller>();
		this.transactionMap = new Hashtable<Integer,Teller>();
		
		for(int i=0; i<defaultCount; i++)
		{
			this.tellers.add(new Teller(this));
		}
	}
	
	public TellerInterface(DatabaseManager dbms, int tellerCount)
	{
		this.dbms = dbms;
		this.tellers = new LinkedList<Teller>();
		this.transactionMap = new Hashtable<Integer,Teller>();
		
		for(int i=0; i<tellerCount; i++)
		{
			this.tellers.add(new Teller(this));
		}
	}
	
	// Getters
	
	
	
	
	// Operations
	public void startTellers()
	{
		for(Teller t : tellers)
		{
			t.run();
		}
	}
	
	public void addRequest(String request, Teller teller)
	{
		int id = Integer.parseInt(request.split(";")[0]);
		this.transactionMap.put(id,teller);
		this.dbms.addRequest(request);
	}
	
	public void addResponse(String response)
	{
		int id = Integer.parseInt(response.split(";")[0]);
		
	}
}
