package aeshliman.UI;

import java.util.LinkedList;
import aeshliman.database.DatabaseManager;

public class TellerInterface
{
	// Instance Variables
	private DatabaseManager dbms;
	private LinkedList<Teller> tellers;
	private final int defaultCount = 3;
	
	// Constructors
	public TellerInterface()
	{
		this.dbms = null;
		this.tellers = new LinkedList<Teller>();
		
		for(int i=0; i<defaultCount; i++)
		{
			this.tellers.add(new Teller(this));
		}
	}
	
	public TellerInterface(int tellerCount)
	{
		this.dbms = null;
		this.tellers = new LinkedList<Teller>();
		
		for(int i=0; i<tellerCount; i++)
		{
			this.tellers.add(new Teller(this));
		}
	}
	
	public TellerInterface(DatabaseManager dbms)
	{
		this.dbms = dbms;
		this.tellers = new LinkedList<Teller>();
		
		for(int i=0; i<defaultCount; i++)
		{
			this.tellers.add(new Teller(this));
		}
	}
	
	public TellerInterface(DatabaseManager dbms, int tellerCount)
	{
		this.dbms = dbms;
		this.tellers = new LinkedList<Teller>();
		
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
	
	public void addRequest(String request)
	{
		this.dbms.addRequest(request);
	}
	
	
	
	
}
