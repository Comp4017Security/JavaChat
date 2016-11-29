#!/bin/bash

java -Djavax.net.ssl.keyStore=mySrvKeystore.jks -Djavax.net.ssl.keyStorePassword=111111 -Djavax.net.ssl.trustStore=mySrvKeystore -Djavax.net.ssl.trustStorePassword=111111 ChatServer 8080