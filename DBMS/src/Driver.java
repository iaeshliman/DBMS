import java.util.LinkedList;
import java.util.Queue;
import aeshliman.database.DataManager;
import aeshliman.database.DatabaseManager;
import aeshliman.database.PortThread;

/*
 * Author: Isaac Aeshliman
 * Date:
 * Description:
 */

public class Driver
{	
	public static void main(String[] args)
	{	
		DatabaseManager dbms = new DatabaseManager();
		
		DataManager dm = dbms.getDM();
		
		//System.out.println("===== Current State of the Data Manager =====\n" + dm);
		
		Queue<String> reqs = dbms.getRequests();
		LinkedList<PortThread> threads = dbms.getThreads();
		
		reqs.add("42020 42040 1000;42060 42080 500;42100 42001 50000");
		reqs.add("42021 42041 1500;42061 42081 250");
		
		threads.get(0).start();
		
		//System.out.println("===== Updated State of the Data Manager =====\n" + dm);
	}
}
