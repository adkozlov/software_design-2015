#!/bin/bash

cp -r ../../src/ru .
find . -type f -name "*.java" | xargs javac -Xlint:deprecation -classpath .:../../lib/annotations-13.0.jar -d ../../out/production/software_design-2015/