#!/bin/bash

port=$1
limit=$2

if [[ -n "$port" ]]; then
	if [[ -n "$limit" ]]; then
		cd ..
		java -Djavax.net.ssl.keyStore=mySrvKeystore -Djavax.net.ssl.keyStorePassword=111111 -Djavax.net.ssl.trustStore=mySrvTrsutstore -Djavax.net.ssl.trustStorePassword=111111 ChatServer $1 $2
	else
    	echo "need one more argument"
	fi
else
	echo "need two arguments"
fi