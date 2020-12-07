##Database Management System Simulation
This project simulates the process of a database management system.
It utilizes locks to ensure thread safety and a rollback mechanic to account for deadlocks.
All transactions are atomic and all operations are recorded in a log file.
The project contains a number of teller threads representing bank tellers each submitting account transfers to a unified database.
The tellers wait for a response before submitting the next request.
A number of port threads read the requests and process them, performing the operations on the data atomically.
Before a port commits changes, a backup port performs the request on a backup data repository and sends a confirmation message to the port.
Once the backup has been successful, the port can complete its commit and send a response to the teller.
Once all teller requests are finished and processed, all threads are shut down and the changes are stored to a persistent file storage.

##Motivation
Project was developed as an assignment for advanced database management course

##Features
Multiple threads accesses shared data structures concurrently.
Deadlock cases are handled via a rollback and retry with a higher priority lock.
Threads share two queues for communicating workflow between threads.
Backup data repository is updated synchronously with primary data updates.
Log files for primary ports and backup ports are updated as changes are performed.
Can handle any number of teller, primary ports, or transactions.

MIT © [Isaac Aeshliman]()