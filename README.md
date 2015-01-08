Float-Bar
=========

轻量级，多功能的悬浮条

##功能
通过调用系统和自写悬浮窗的功能，让悬浮窗拥有下列功能：  
> 1. 自动下拉通知中心，收起通知中心（已根据不同系统进行了优化）  
> 1. 调出后台任务，自己获取了后台任务的对象，通过list展现出来
> 1. 调出系统的后台任务，通过调用SDK中的方法实现了呼出系统的后台任务（在API16以上可用）  
> 1. 模拟点按Home键（通过发送intent）  
> 1. 模拟系统返回键，同样是用了SDK中提供的方法（API16+可用）  
> 1. 打开自带相机	

PS:为了增强软件的稳定性和大大提升软件的功能，这个悬浮窗不是普通的service，而是AccessibilityService，所以不会出现被随意被软件清理的问题。  

##核心技术
这个项目最主要的功能是模拟系统的各种操作，我这里用了系统的辅助功能（AccessibilityService）来实现。关于辅助功能这个东西网上的说明少的可怜，只能通过看Android官方系统新特性获得，这里说明下如何用系统的辅助功能来实现模拟操作。

**1.注册**    
service必须按照规定注册  
    
    <service
    android:name="com.kale.floatbar.service.FloatService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE" >
    <intent-filter>
    <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    
    <meta-data
    android:name="android.accessibilityservice"
    android:resource="@xml/accessibilityservice" />
    </service>

这里写上了权限，还要通过xml文件设置具体的权限  

**2.通过xml配置具体权限**  
在res中的xml中建立一个文件accessibilityservice（可自行定义，和注册时保持一致即可），写上如下代码
  
    <?xml version="1.0" encoding="utf-8"?>  
    <accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"  
    android:accessibilityFeedbackType="feedbackGeneric" 
    android:description="@string/accessibility" />

**3.开启辅助功能**  
用户在使用服务前必须到系统的辅助功能中激活这个应用的辅助功能，这样你自己的AccessibilityService才能正常工作

**4.模拟系统操作**  

*例子：模拟返回键*  
**AccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);**


*解释：*  
> 通过传入不同的常量给performGlobalActionf(int action)，就可以实现模拟系统的各种操作。该方法提供了模拟返回键，Home键，呼出通知中心，调出最近任务四种操作。
> 
> boolean android.accessibilityservice.AccessibilityService.performGlobalAction(int action)
> 
> 
> public final boolean performGlobalAction (int action) 
> Added in API level 16
> Performs a global action. Such an action can be performed at any moment regardless of the current application or user location in that application. For example going back, going home, opening recents, etc.
> 
> Parameters
> action  The action to perform. 
> 
> Returns
> Whether the action was successfully performed.
> See Also
> GLOBAL_ACTION_BACK
> GLOBAL_ACTION_HOME
> GLOBAL_ACTION_NOTIFICATIONS
> GLOBAL_ACTION_RECENTS 

##截图
![](http://7tsyrv.com1.z0.glb.clouddn.com/01.png/medium)
![](http://7tsyrv.com1.z0.glb.clouddn.com/02.png/medium)
![](http://7tsyrv.com1.z0.glb.clouddn.com/03.png/medium)
![](http://7tsyrv.com1.z0.glb.clouddn.com/04.png/medium)
![](http://7tsyrv.com1.z0.glb.clouddn.com/05.png/medium)


##作者

Kale <developer_kale@qq.com>  

![](https://avatars3.githubusercontent.com/u/9552155?v=3&s=460)