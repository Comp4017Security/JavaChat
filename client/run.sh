#!/bin/bash

name=$1

if [[ -n "$name" ]]; then
	java -Djavax.net.ssl.trustStore=myCLiTrustKeystore -Djavax.net.ssl.trustStorePassword=111111 -Djavax.net.ssl.keyStore=myCliKeystore -Djavax.net.ssl.keyStorePassword=111111 ChatClient 0.0.0.0 8080 $1
else
    echo "argument error"
fi