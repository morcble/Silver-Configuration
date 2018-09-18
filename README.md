# Silver-Configuration
Remote Configuration Server

Purpose:
    Move all the configurations into Silver server , which can help to manage the configurations online and apply properties changes to your applicaiton once been changed on silver server. It decouples the configuration for various enviroments such as dev, sit ,uat and production enviroment . 

Runtime enviroment required: 
    JDK1.8 or plus
    Mysql server
    
Programe Language:
    java

Prepration steps:
   1. make sure you had setup java enviroment.
   2. create db schema silverdb.
      CREATE SCHEMA silverdb DEFAULT CHARACTER SET utf8 ;

usage steps:

       You may directly use Silver-Configuration/releases/1.0.1/binaries to running up 
       silver server or compible the source code to generate binaries by yourself
       Introduction for this section just for using exsting binaries
    
    1. git clone https://github.com/morcble/Silver-Configuration.git
    
    2. cd Silver-Configuration/releases/1.0.1/binaries/config/config.properties
      config below properties for connectiong your mysql server
        DefaultContext.db.account = root
        DefaultContext.db.password = 23456
        DefaultContext.db.connectstr= jdbc:mysql://<mysql ip>:3306/SilverDB?characterEncoding=utf-8
    
    3. cd Silver-Configuration/releases/1.0.1/binaries/
    
    4. run start.sh(for linux) or start.bat(for windows)

    5. open  http://127.0.1.1:8888/SilverFrontend/index.html on your web browser to see the management page.
    
    6. import Silver-Configuration/releases/1.0.1/binaries/silverClient.jar to your project 
        to load properties from silver server .
    or config your maven as below
    <dependency>
      <groupId>com.cnautosoft.silver</groupId>
      <artifactId>SilverClient</artifactId>
      <version>1.0.1</version>
    </dependency> 
    
    7. client usage:
        PropertiesManager pm = PropertiesManager.getInstance();
        /**
        *
        *
        */
        pm.init(<silver server host>,<silver server port>,<group id>,<item id>);
        pm.getProperty(<configKey>)

 
    
    
    中文介绍 , 请参见博客 https://blog.csdn.net/fengliangjun727/article/details/82669206
