#!/bin/sh

# clean
mvn clean

# build with maven, skip tests
mvn -e -B package -DskipTests=true

sudo rm bin/evosuite-tests.jar bin/evosuite.jar

# move master/target/evosuite-master-*.jar to newly created bin folder
sudo mv master/target/evosuite-master-*-tests.jar bin/evosuite-tests.jar
sudo mv master/target/evosuite-master-*.jar bin/evosuite.jar
