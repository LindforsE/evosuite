#!/bin/sh

# clean
mvn clean

# build with maven, skip tests
mvn -e -B package -DskipTests=true

sudo rm bin/SF100/lib/evosuite-tests.jar bin/SF100/lib/evosuite.jar

# move master/target/evosuite-master-*.jar to newly created bin folder
sudo mv master/target/evosuite-master-*-tests.jar bin/SF100/lib/evosuite-tests.jar
sudo mv master/target/evosuite-master-*.jar bin/SF100/lib/evosuite.jar
