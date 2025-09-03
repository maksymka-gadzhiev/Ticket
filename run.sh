#!/bin/bash

JSON_FILE=$1
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="$JSON_FILE"