#!/bin/bash

clear

file="Main.class"

if [ ! -f $file ]; then
	echo "Compiling..."
	make
	echo ""
fi

java -classpath .:org-apache-commons-codec.jar Main $1 $2 $3
