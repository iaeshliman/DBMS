package aeshliman.database;

/*
 * Author: Isaac Aeshliman
 * Date: 11/29/2020
 * Description: A bank account, containing the fist name, last name, account number, and balance.
 */

public class Account
{
	// Instance variables
	private String firstName;
	private String lastName;
	private int accountNum;
	private double balance;
	
	// Constructors
	public Account(String firstName, String lastName, int accountNum, double balance)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountNum = accountNum;
		this.balance = balance;
	}
	
	public Account(Account account)
	{
		this.firstName = account.firstName;
		this.lastName = account.lastName;
		this.accountNum = account.accountNum;
		this.balance = account.balance;
	}
	
	// Getters
	public int getAccountNum()
	{
		return this.accountNum;
	}
	
	// Operators
	public String formatFile() // Returns a string of the object formatted for writing to a file
	{
		return this.firstName + "\t" + this.lastName + "\t" + this.accountNum + "\t" + this.balance;
	}
	
	public boolean equals(int accountNum)
	{
		return this.accountNum == accountNum;
	}
	
	public void withdraw(float qty)
	{
		this.balance -= qty;
	}
	
	public void deposit(float qty)
	{
		this.balance += qty;
	}
	
	// toString
	public String toString()
	{
		return "Name: " + this.firstName + " " + this.lastName + "\tAccount Number: " + this.accountNum + "\tBalance: $" + this.balance; 
	}
}
