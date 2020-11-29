The Process of Performing a Transaction

	1) Thread pulls from queue
	2) Parse the request
		A) Find src account
		B) Find dst account
		C) Find balance transfer
	3) Lock account/update data
	4) Commit changes to persistent storage
	5) Return response
	
Formatting of Log File
	
	REQ : the request statement string
		"TRANS ID - REQ: 3124124 1241234 21312;3145781 158178 2147;852154 1475 100;486189 168416 200"
	UPDATE : the ordering of changes to data
		"TRANS ID - UPDATE: 3124124 -21312"
		"TRANS ID - UPDATE: 1241234 21312"
		"TRANS ID - UPDATE: 3145781 -2147"
		"TRANS ID - UPDATE: 158178 2147"
		"TRANS ID - UPDATE: 852154 -100"
		
		
		"TRANS ID - UPDATE: 1475 100"
		"TRANS ID - UPDATE: 486189 -200"
		"TRANS ID - UPDATE: 168416 200"
	COMMIT : save changes to persistent storage
		"TRANS ID - COMMIT FILENAME"
	ROLL : undo changes to data
		"TRANS ID - ROLL UPDATE: X Y"

		
		