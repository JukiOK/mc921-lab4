#!/bin/bash
set -x
#compiling the Sum.g4 in java files
java -jar "antlr-4.7.2-complete.jar" -no-listener -visitor Functions.g4
#exporting the class path
export CLASSPATH=".:antlr-4.7.2-complete.jar:$CLASSPATH"
#compiling the .java generated from Functions.g4 with MyParser.java and AddVisitor.java
javac *.java
#feeding a string and reading the tokens
for i in 1 2 3 4 5 6 8; do
  cat "test${i}.sm" | java org.antlr.v4.gui.TestRig Functions root -tokens
  #feeding a string and reading tree in list style
  cat "test${i}.sm" | java org.antlr.v4.gui.TestRig Functions root -tree
  #feeding a string and printing a graphical tree
  cat "test${i}.sm" | java org.antlr.v4.gui.TestRig Functions root
  #execute the implemented visitor
  cat "test${i}.sm" | java MyParser > "result${i}.txt"
done

cat "test7.sm" | java org.antlr.v4.gui.TestRig Functions root -tokens
#feeding a string and reading tree in list style
cat "test7.sm" | java org.antlr.v4.gui.TestRig Functions root -tree
#feeding a string and printing a graphical tree
cat "test7.sm" | java org.antlr.v4.gui.TestRig Functions root -gui
#execute the implemented visitor
cat "test7.sm" | java MyParser > "result7.txt"
