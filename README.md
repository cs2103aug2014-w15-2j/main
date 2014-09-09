To-Dare
===================


Supervisor: 	Christopher Chak Hanrui

Extra Feature: 	Power Cooperative Tasks


----------


User Guide
-------------

####1. Login in / Create An Account

When you first run the app, you will be required to log in with your account if you did not log out last time.<br />
(1.1) 	If you do not have one, you can create one with the command:
<div style="text-align:center">```create account```</div>
(1.2) 	If you have already had one, you can log into your account with command :
<div style="text-align:center">```log in to @<username> @<password>```</div>


####2. Create A Task
The general command format to create a task is 
<center>```[<adding command>] <task description> [@<time>][@<members>][@<list name>]```</center>
(2.1) 	```<adding command>``` includes: **add**, **insert**, and **create**.

(2.2) 	```<adding command>``` can be omitted, thus the following two commands are equivalent:
```add project meeting```   
and 
```project meeting```

(2.3) 	```@<member>``` represents the username of the member, a request will be sent to him or her. After confirmation, the same task will be added to his or her list. 

(2.4) 	the format of ```@<time>``` is
	```<period>```	: period including daily, weekly, monthly
	```findSlot```	: find the most appropriate empty slot for this task
	```before <hhmm><ddmmyyyy>```
	```from <hhmm><ddmmyyyy> to <hhmm><ddmmyyyy>```
(2.5) 	To create a sublist, use the following command
	```addList @<list name>```

