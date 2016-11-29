#!/bin/bash

name=$1

if [[ -n "$name" ]]; then
	keytool -delete -keystore ../mySrvTruststore -file ../$1.cer -alias $1
else
    echo "argument error"
fi