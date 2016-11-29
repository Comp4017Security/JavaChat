#!/bin/bash

name=$1

if [[ -n "$name" ]]; then
	keytool -import -keystore ../mySrvTruststore -file ../$1.cer -alias $1
else
    echo "argument error"
fi