package aeshliman.UI;

import java.util.Queue;
import java.util.Random;

/*
 * Author: Isaac Aeshliman
 * Date: 12/6/2020
 * Description: A teller which generates account transfer requests and writes them to the request queue.
 * 				Waits to generate another transfer request until it receives a response.
 */

public class Teller extends Thread
{
	// Constants
	private final int maxAccNum = 42100;
	private final int minAccNum = 42001;
	private final float maxBalance = 5000;
	private final float minBalance = 100;
	private final int minTransfers = 1;
	private final int maxTransfers = 5;
	
	// Instance Variables
	private static int transactionID = 1;
	private Queue<String> requests;
	private Queue<String> responses;
	private int requestCount;
	
	// Constructors
	public Teller(Queue<String> requests, Queue<String> responses, int requestCount)
	{
		this.requests = requests;
		this.responses = responses;
		this.requestCount = requestCount;
	}
	
	public void run()
	{
		for(int i=0; i<requestCount; i++)
		{
			writeRequest(newRequest());
			readResponse();
		}
	}
	
	// Operations
	public String readResponse()
	{
		synchronized(responses)
		{
			while(responses.isEmpty())
			{
				try { responses.wait(); }
				catch(InterruptedException e) { return null; }
			}
			return responses.poll();
		}
	}
	
	public void writeRequest(String request)
	{
		synchronized(requests)
		{
			requests.add(request);
			requests.notify();
		}
	}
	
	public String newRequest() // Generates a semicolon delimited request with ID at beginning
	{
		String result = transactionID++ + ";";
		Random ran = new Random();
		for(int i=0; i<ran.nextInt(maxTransfers)+minTransfers;i++)
		{
			// Variable declarations
			int accNum1;
			int accNum2;
			float balance;
			
			accNum1 = ran.nextInt(maxAccNum-minAccNum+1)+minAccNum;
			
			do
			{
				accNum2 = ran.nextInt(maxAccNum-minAccNum+1)+minAccNum;
			}
			while(accNum1==accNum2); // Ensures a different second account number
			
			// Generates a balance that no more than 2 decimal places
			balance = ran.nextFloat()*(maxBalance-minBalance)+minBalance;
			balance = (float) (Math.round(balance*100.0)/100.0);
			
			result += accNum1 + " " + accNum2 + " " + balance + ";";
		}
		return result.substring(0,result.length()-1);
	}
}
