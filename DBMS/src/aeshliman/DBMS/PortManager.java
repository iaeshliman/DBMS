package aeshliman.DBMS;

import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.Queue;
import aeshliman.database.DataRepository;

/*
 * Author: Isaac Aeshliman
 * Date: 12/6/2020
 * Description: Manages the port threads. Initializing, starting, and perform a selection of operations
 * 				to either determine information about the status of the ports or end them.
 */

public class PortManager
{
	// Constants
	private final int defaultPortCount = 3;
	
	// Instance Variables
	private LinkedList<Port> ports;
	
	public PortManager(Queue<String> requests, Queue<String> responses, DataRepository data)
	{
		this.ports = new LinkedList<Port>();
		
		for(int i=0; i<defaultPortCount; i++)
		{
			ports.add(new Port(requests,responses,data));
		}
	}
	
	public PortManager(Queue<String> requests, Queue<String> responses, DataRepository data, int portCount)
	{
		this.ports = new LinkedList<Port>();
		
		for(int i=0; i<portCount; i++)
		{
			ports.add(new Port(requests,responses,data));
		}
	}
	
	// Operations
	public void startPorts()
	{
		for(Port port : ports) port.start();
	}
	
	public boolean portsWaiting()
	{
		for(Port port : ports) if(port.getState()!=State.WAITING) return false;
		return true;
	}
	
	public void portsInterrupt()
	{
		for(Port port : ports) port.interrupt();
		
	}
}
