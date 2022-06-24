#!/bin/bash

# replace ... with bucket name. 
mvn clean package
ks3util cp -u target/kcf-demo-1.0-SNAPSHOT-jar-with-dependencies.jar ks3://.../