## Startup sequence cloud, exchange, market and others are optional
## The memory limit size is limited to test use, and the operation project is set according to the demand

- nohup java -Xms512m -Xmx512m -jar cloud.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar exchange.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar market.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar exchange-api.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar ucenter-api.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar admin-api.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar wallet.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar wallet_udun.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar chat.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx512m -jar otc-api.jar  >/dev/null 2>&1 &
- nohup java -Xms512m -Xmx1024m -jar contract-swap-api.jar >/dev/null 2>&1 &

### * Only one wallet and wallet_udun can be deployed
### * wallet needs to cooperate with wallet_rpc for local wallet configuration
### * wallet_udun is the third-party wallet U-shield wallet, which needs to be matched with the U-shield wallet Key

