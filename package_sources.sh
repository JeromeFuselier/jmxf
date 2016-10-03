#!/bin/sh

VERSION=0.1

cd target
tar cvzf jmxf-$VERSION-src.tar.gz ../src ../pom.xml ../gpl.txt --exclude=.svn --exclude=*.svg
