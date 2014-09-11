To-Dare
===================


Supervisor: 	Christopher Chak Hanrui

Extra Feature: 	Power Cooperative Tasks



User Guide
-------------

#### 1. Log in / Create An Account
&nbsp;&nbsp;&nbsp;&nbsp;When you first run the app, you will be required to log in with your account if you did not log out last time.
  
&nbsp;&nbsp;&nbsp;&nbsp;1.1 If you do not have one, you can create one with the command:  
```
create account
```  
  
&nbsp;&nbsp;&nbsp;&nbsp;1.2 If you have already had one, you can log into your account with command :  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```log in to @<username> @<password>```  


#### 2. Create A Task  
&nbsp;&nbsp;&nbsp;&nbsp;The general command format to create a task is  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```[<adding command>] <task description> [@<time>][@<members>][@<list name>]```  
  
&nbsp;&nbsp;&nbsp;&nbsp;2.1 ```<adding command>``` includes: ```add```, ```insert```, and ```create```.  
  
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


#### 3. Display Tasks  
&nbsp;&nbsp;&nbsp;&nbsp;The general command format to display tasks is  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```<displaying command> [@<type>][@<component>][@<list name>]```  
  
&nbsp;&nbsp;&nbsp;&nbsp;3.1 The tasks will be shown with an index, which you will use to do further operations on the specific task.  
  
&nbsp;&nbsp;&nbsp;&nbsp;3.2 ```<displaying command>``` includes: ```read```, ```display```, ```search```, and ```find```.  
  
&nbsp;&nbsp;&nbsp;&nbsp;3.3 ```@<type>``` specify the type of view, including  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```list```: tasks are displayed in an ordered list (default)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```schedule```: tasks are displayed in a calendar view  
  
&nbsp;&nbsp;&nbsp;&nbsp;3.4 ```@<component>``` specify the searching requirements, including  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```<search keywords>``` : show the tasks that contain the provided keyword in a list view  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```<time>``` : show the tasks that need to be done before a specific time  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```<member>``` : show the tasks that need to be done with a specific person  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;```ALL``` : show all the records (default)  


