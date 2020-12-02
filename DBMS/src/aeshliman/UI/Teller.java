package aeshliman.UI;

import java.util.Random;

public class Teller
{
	// Constants
	private final int maxAccNum = 42100;
	private final int minAccNum = 42001;
	private final float maxBalance = 5000;
	private final float minBalance = 100;
	
	// Instance Variables
	private TellerInterface tellerInterface;
	private static int requestCount = 10;
	
	// Constructors
	public Teller(TellerInterface tellerInterface)
	{
		this.tellerInterface = tellerInterface;
	}
	
	public void run()
	{
		while(requestCount>0)
		{
			this.tellerInterface.addRequest(this.newRequest());
			requestCount--;
		}
	}
	
	
	// Operations
	
	public String newRequest()
	{
		String result = "";
		Random ran = new Random();
		for(int i=0; i<ran.nextInt(5)+1;i++)
		{
			boolean duplicate = true;
			
			int accNum1 = 0;
			int accNum2 = 0;
			float balance = 0;
			
			accNum1 = ran.nextInt(maxAccNum-minAccNum+1)+minAccNum;
			
			while(duplicate)
			{
				accNum2 = ran.nextInt(maxAccNum-minAccNum+1)+minAccNum;
				
				if(accNum1!=accNum2) duplicate = false;
			}
			
			balance = ran.nextFloat()*(maxBalance-minBalance)+minBalance;
			balance = (float) (Math.round(balance*100.0)/100.0);
			
			result += accNum1 + " " + accNum2 + " " + balance + ";";
		}
		
		return result.substring(0,result.length()-1);
	}
}
