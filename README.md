To-Dare
===================


Supervisor: 	Christopher Chak Hanrui

Extra Feature: 	Power Cooperative Tasks



User Guide
-------------

#### 一、ListView  
1. android-pulltorefresh  
一个强大的拉动刷新开源项目，支持各种控件下拉刷新，ListView、ViewPager、WevView、ExpandableListView、GridView、ScrollView、Horizontal  ScrollView、Fragment上下左右拉动刷新，比下面johannilsson那个只支持ListView的强大的多。并且它实现的下拉刷新ListView在item不足一屏情况下也不会显示刷新提示，体验更好。  
项目地址：https://github.com/chrisbanes/Android-PullToRefresh  
Demo地址：https://github.com/Trinea/TrineaDownload/blob/master/pull-to-refreshview-demo.apk?raw=true  
APP示例：新浪微博各个页面  

1. android-pulltorefresh-listview  
下拉刷新ListView  
项目地址：https://github.com/johannilsson/android-pulltorefresh  
Demo地址：https://github.com/Trinea/TrineaDownload/blob/master/pull-to-refresh-listview-demo.apk?raw=true  
PS：这个被很多人使用的项目实际有不少bug，推荐使用上面的android-pulltorefresh  
   
1. DropDownListView  
下拉刷新及滑动到底部加载更多ListView  
项目地址：https://github.com/Trinea/AndroidCommon  
Demo地址：https://play.google.com/store/apps/details?id=cn.trinea.android.demo  
文档介绍：http://www.trinea.cn/android/dropdown-to-refresh-and-bottom-load-more-listview/  
   
1. DragSortListView  
拖动排序的ListView，同时支持ListView滑动item删除，各个Item高度不一、单选、复选、CursorAdapter做为适配器、拖动背景变化等  
项目地址：https://github.com/bauerca/drag-sort-listview  
Demo地址：https://play.google.com/store/apps/details?id=com.mobeta.android.demodslv  
APP示例：Wordpress Android  

####1. Login in / Create An Account

When you first run the app, you will be required to log in with your account if you did not log out last time.<br />
1.1 If you do not have one, you can create one with the command:<br />
```create account```<br />
1.2 If you have already had one, you can log into your account with command :<br />
```log in to @<username> @<password>```<br />



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

