#!/bin/bash

name=$1

if [[ -n "$name" ]]; then
	keytool -exportcert -keystore myKeystore -alias mykey -file $1.cer
else
    echo "argument error"
fi