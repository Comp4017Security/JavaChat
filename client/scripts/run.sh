#!/bin/bash

server=$1
port=$2
name=$3

if [[ -n "$server" ]]; then
	if [[ -n "$port" ]]; then
		if [[ -n "$name" ]]; then
			cd ..
			java -Djavax.net.ssl.trustStore=myCliTruststore -Djavax.net.ssl.trustStorePassword=111111 -Djavax.net.ssl.keyStore=myCliKeystore -Djavax.net.ssl.keyStorePassword=111111 ChatClient $1 $2 $3
		else
    		echo "need one more argument"
		fi
	else
    	echo "need two more arguments"
	fi
else
    echo "need three arguments"
fi