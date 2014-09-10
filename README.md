To-Dare
===================


Supervisor: 	Christopher Chak Hanrui

Extra Feature: 	Power Cooperative Tasks



User Guide
-------------

#### 1. Login in / Create An Account
&nbsp;&nbsp;&nbsp;&nbsp;When you first run the app, you will be required to log in with your account if you did not log out last time.  
&nbsp;&nbsp;&nbsp;&nbsp;1.1 If you do not have one, you can create one with the command:  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```create account```  
&nbsp;&nbsp;&nbsp;&nbsp;1.2 If you have already had one, you can log into your account with command :  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```log in to @<username> @<password>```  


#### 2. Create A Task  
&nbsp;&nbsp;&nbsp;&nbsp;The general command format to create a task is  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```[<adding command>] <task description> [@<time>][@<members>][@<list name>]```  
&nbsp;&nbsp;&nbsp;&nbsp;2.1 ```<adding command>``` includes: **add**, **insert**, and **create**.  
&nbsp;&nbsp;&nbsp;&nbsp;2.2 ```<adding command>``` can be omitted, thus the following two commands are equivalent:  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```add project meeting``` and ```project meeting```  
&nbsp;&nbsp;&nbsp;&nbsp;2.3 ```@<member>``` represents the username of the member, a request will be sent to him or her. After confirmation, the same task will be added to his or her list.  
&nbsp;&nbsp;&nbsp;&nbsp;2.4 the format of ```@<time>``` is  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```<period>```	: period including daily, weekly, monthly  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```findSlot```	: find the most appropriate empty slot for this task  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```before <hhmm><ddmmyyyy>```  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```from <hhmm><ddmmyyyy> to <hhmm><ddmmyyyy>```  
&nbsp;&nbsp;&nbsp;&nbsp;2.5 To create a sublist, use the following command  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```addList @<list name>```  

