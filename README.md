# AMQP Testing

Project tests interlok-amqp features

## What it does

This project contains a single instance of Interlok that will attempt to connect to a locally running RabbitMQ instance over AMQP.  Once started a single polling trigger will produce a message every 10 seconds and send to a queue on your local broker.  
A second workflow will consume the message and move to a second queue.

![amqp diagram](/amqp.png "amqp diagram")
 
## Getting started

### RabbitMQ

```
docker run -d -p 15672:15672 -p 5672:5672 --hostname my-rabbit --name rabbit_man rabbitmq:3-management
```

Then you'll need to log into the management portal and create a new virtual host.  Browse to the portal [here](http://localhost:15672/#/vhosts).  You should land on the virtual hosts configuration page (username = guest, password = guest), simply add a new one with the name "test".

### Interlok

* `./gradlew clean build`
* `(cd ./build/distribution && java -jar lib/interlok-boot.jar)`
