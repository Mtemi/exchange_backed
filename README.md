CRYPTTOPS digital currency trading system

=========================================

Main technique
Backend: Spring, SpringMVC, SpringData, SpringCloud, SpringBoot
Database: Mysql, Mongodb
Others: redis, kafka, Alibaba Cloud OSS, Tencent waterproof verification, Huanxin push
Front-end: Vue, iView, less
Both IOS and Android versions are available.
cloud
Provides SpringCloud microservice registry function, which is a basic module and must be deployed
Dependent service: none
ucenter-api
Provide user-related interfaces (such as login, registration, asset list), this module is the basic module and must be deployed
Dependent services: mysql, kafka, redis, mongodb, SMS interface, email account
otc-api
Provide OTC function interface, if there is no OTC transaction, it can not be deployed
Dependent services: mysql, redis, mongodb, SMS interface
exchange-api
Provide a currency transaction interface, projects without currency transactions can not be deployed
Dependent services: mysql, redis, mongodb, kafka
chat
Provide real-time communication interface, basic module, need to be deployed
Dependent services: mysql, redis, mongodb
admin
Provides all service interfaces of the management background, which must be deployed
Dependent services: mysql, redis, mongodb
wallet
Provide wallet services such as depositing, withdrawing, and obtaining addresses, which are basic modules and must be deployed
Dependent services: mysql, mongodb, kafka, cloud
market
Provides interface services such as currency price, K-line, real-time transaction, etc. OTC transactions do not need to be deployed
Dependent services: mysql, redis, mongodb, kafka, cloud
exchange
Provide matching transaction services, OTC transactions do not require deployment
Dependent services: mysql, mongodb, kafka
contract-swap-api
Provide interface services such as contract currency price, K line, market message subscription, real-time transaction, etc.
Dependent services: mysql, redis, mongodb, kafka, cloud
open-api (undeveloped)
Provide user API interface
Dependent services: mysql, redis, mongodb, kafka, cloud
Key business introduction
The core modules of the back-end framework are exchange and market modules.

The exhcnge module completely adopts the Java memory processing queue, which greatly speeds up the processing logic, does not involve database operations, and ensures fast processing speed. After the project is started, it adopts the method of inheriting ApplicationListener and runs automatically;

Unprocessed orders are automatically loaded after startup and reloaded into the JVM to ensure the accuracy of the data. After the exchange processes the orders, the transaction records are sent to the market;

The market module is mainly a database operation, which persists user change information to the database. The main difficulty lies in interacting with the front-end socket push. There are two ways for socket push. The web side socket uses SpringSocket, and the mobile terminal uses Netty push. The netty push is processed by timed tasks.
Environment setup
Centos 7.8
MySQL 5.7.16
Redis 6.0
MongoDB 4.0
kafka_2.11-2.2.1
nginx-1.19.0
JDK 1.8
Vue
Zookeeper
Service deployment preparation
The project uses the Lombok plug-in, no matter what IDE tool you use, be sure to install the Lombok plug-in first
The project uses QueryDsl. If you encounter a class that starts with Q and cannot be found, please compile the corresponding core modules, such as core, exchange-core, and xxx-core.
The jar package not found is in the project jar folder
JDK version 1.8 or above
Initialize the sql configuration file in the sql folder When the configuration file is opened, this setting will automatically create a table #jpa #spring.jpa.hibernate.ddl-auto=update.
Modify the service configuration file
Please modify the following configuration according to the actual deployment of the service. The location of the configuration file is as follows. If there is no configuration in the configuration file, it means that the module does not use this function and does not need to be added:

Each module /src/main/resources/application.properties


### service start
 1. maven build package service

 2. Upload the XX.jar under the target folder of each module to your own server

 3. Start the cloud module first, then start the market and exchange modules, and the rest are in no order.


Precautions:
When the memory is insufficient, enter top in the linux console to see that the java process occupies a lot of memory (a java process occupies more than 1G). Because there are many jar packages to run, it is necessary to control the memory used by some jar packages. Currently, the following control is performed 4:

-java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k admin-api.jar

-java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k cloud.jar

- java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k wallet.jar
