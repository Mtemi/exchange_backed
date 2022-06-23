Main technique

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



rear end：Spring、SpringMVC、SpringData、SpringCloud、SpringBoot
Database: Mysql, Mongodb
Others: redis, kafka, Alibaba Cloud OSS, Tencent waterproof verification, Huanxin push
Front-end: Vue, iView, less
Both IOS and Android versions are available.
cloud
Provides the SpringCloud microservice registry function, which is a basic module and must be deployed
Dependency service: none
ucenter-api
Provide user-related interfaces (such as login, registration, asset list), this module is the basic module and must be deployed
Dependent services: mysql, kafka, redis, mongodb, SMS interface, email account
otc-api
Provide OTC function interface, if there is no OTC transaction, it can not be deployed
Dependent services: mysql, redis, mongodb, SMS interface
exchange-api
Provide currency transaction interface, projects without currency transaction can not be deployed
Dependent services: mysql, redis, mongodb, kafka
chat
Provide real-time communication interface, basic module, need to be deployed
Dependent services: mysql, redis, mongodb
admin
Provides all service interfaces of the management background, which must be deployed
Dependent services: mysql, redis, mongodb
wallet
Provide wallet services such as depositing, withdrawing, and obtaining addresses. It is the basic module and must be deployed.
Dependent services: mysql, mongodb, kafka, cloud
market
Provides interface services such as currency price, K-line, and real-time transactions, and OTC transactions do not require deployment
Dependent services: mysql, redis, mongodb, kafka, cloud
exchange
Provide matching transaction services, OTC transactions do not require deployment
Dependent services: mysql, mongodb, kafka
contract-swap-api
Provide interface services such as contract currency price, K line, market news subscription, real-time transaction, etc.
Dependent services: mysql, redis, mongodb, kafka, cloud
open-api (undeveloped)
Provide user API interface
Dependent services: mysql, redis, mongodb, kafka, cloud

Key business introduction
The core modules of the back-end framework are exchange and market modules.

The exhcnge module completely adopts the Java memory processing queue, which greatly speeds up the processing logic, does not involve database operations, and ensures fast processing speed. After the project is started, it adopts the method of inheriting ApplicationListener and runs automatically;

Unprocessed orders are automatically loaded after startup and reloaded into the JVM to ensure the accuracy of the data. After the exchange processes the orders, the transaction records are sent to the market;

The market module is mainly a database operation, which persists user change information to the database. The main difficulty lies in interacting with the front-end socket push. There are two ways for socket push. The web side socket uses SpringSocket, and the mobile terminal uses Netty push. The netty push is processed by timed tasks.

Environment construction
Cents 7.8
MySQL 5.7.16
Redis 6.0
Mongodb 4.0
kafka_2.11-2.2.1
nginx-1.19.0
JDK 1.8
Vue
Zookeeper


Preparing for service deployment
The project uses the Lombok plug-in, no matter what IDE tool you use, be sure to install the Lombok plug-in first
The project uses QueryDsl. If the class starting with Q cannot be found, please compile the corresponding core module first, such as core, exchange-core, and xxx-core.
The jar package not found is in the project jar folder
jdk version 1.8 and above
Initialize sql in the sql folder to configure the configuration file. Opening this setting will automatically create a table #jpa #spring.jpa.hibernate.ddl-auto=update.
Modify service configuration file
Please modify the following configuration according to the actual deployment of the service. The location of the configuration file is as follows. If there is no configuration in the configuration file, it means that the module does not use this function and does not need to be added:

Each module /src/main/resources/application.properties

### service start
 1. maven build package service

 2. Upload the XX.jar under the target folder of each module to your own server

 3. Start the cloud module first, then start the market and exchange modules, and the rest are in no order.

### Questions and suggestions
- With Issue, we will follow up with answers in a timely manner.
- Whatsapp:  +254728141235
- E-mail: bmutua350@gmail.com


Precautions:
When the memory is insufficient, enter top in the linux console to see that the java process occupies a lot of memory (a java process occupies more than 1G). Because there are many jar packages to run, it is necessary to control the memory used by some jar packages. Currently, the following control is performed 4:

- java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  admin-api.jar

- java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  cloud.jar

- java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  wallet.jar
