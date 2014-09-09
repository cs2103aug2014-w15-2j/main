To-Dare
===================


Supervisor: 	Christopher Chak Hanrui

Extra Feature: 	Power Cooperative Tasks



User Guide
-------------

####1. Login in / Create An Account

When you first run the app, you will be required to log in with your account if you did not log out last time.

1.1 If you do not have one, you can create one with the command:

```create account```

1.2 If you have already had one, you can log into your account with command :

```log in to @<username> @<password>```



####2. Create A Task

The general command format to create a task is<br />
```[<adding command>] <task description> [@<time>][@<members>][@<list name>]```<br />
(2.1) 	```<adding command>``` includes: **add**, **insert**, and **create**.<br />
(2.2) 	```<adding command>``` can be omitted, thus the following two commands are equivalent:<br />
```add project meeting```<br />
and<br />
```project meeting```<br />
(2.3) 	```@<member>``` represents the username of the member, a request will be sent to him or her. After confirmation, the same task will be added to his or her list.<br />
(2.4) 	the format of ```@<time>``` is<br />
	```<period>```	: period including daily, weekly, monthly<br />
	```findSlot```	: find the most appropriate empty slot for this task<br />
	```before <hhmm><ddmmyyyy>```<br />
	```from <hhmm><ddmmyyyy> to <hhmm><ddmmyyyy>```<br />
(2.5) 	To create a sublist, use the following command<br />
	```addList @<list name>```<br />

