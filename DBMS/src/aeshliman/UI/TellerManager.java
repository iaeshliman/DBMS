package aeshliman.UI;

import java.util.LinkedList;
import java.util.Queue;

/*
 * Author: Isaac Aeshliman
 * Date: 12/6/2020
 * Description: Manages the teller threads. Initializing, starting, and perform a selection of operations
 * 				to determine information about the status of the tellers.
 */

public class TellerManager
{
	// Constants
	private final int defaultTransactionCount = 3;
	private final int defaultCount = 3;
	
	// Instance Variables
	private LinkedList<Teller> tellers;
	
	// Constructors
	public TellerManager(Queue<String> requests, Queue<String> responses)
	{
		this.tellers = new LinkedList<Teller>();
		
		for(int i=0; i<defaultCount; i++)
		{
			this.tellers.add(new Teller(requests,responses,defaultTransactionCount));
		}
	}
	
	public TellerManager(Queue<String> requests, Queue<String> responses, int tellerCount, int transactionCount)
	{
		this.tellers = new LinkedList<Teller>();
		
		for(int i=0; i<tellerCount; i++)
		{
			this.tellers.add(new Teller(requests,responses,transactionCount));
		}
	}
	
	// Operations
	public void startTellers()
	{
		for(Teller teller : tellers) teller.start();
	}
	
	public boolean tellersRunning()
	{
		for(Teller teller : tellers) if(teller.isAlive()) return true;
		return false;
	}
}
