========
= jmxf =
========


Description
===========

jmxf is an open source Java library. It provides an API to decode MXF video 
files. It doesn't depend on any external libraries, making it easy to use on 
any kind of architecture and/or operating system. with pure Java code. It 
targets the MXF/D10 format, MPEG-2 for the video and AES3 for the audio. 

Licensing
=========

This library is free software, it is possible to redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation (see the GNU General Public License for more details). 


How to run the software
=======================

Please download the latest version, avaliable at the address:
http://code.google.com/p/jmxf/downloads/detail?name=jmxf-0.3.jar

The library comes with two classes that can be used to extracts images or
uncompressed audio wave from an MXF file with the following command lines:

$  java -cp jmxf-0.1.jar uk.ac.liv.app.ExtractFrames test.mxf 0 10 
-> Extract the first 10 frames from the text.mxf file

$  java -cp jmxf-0.1.jar uk.ac.liv.app.ExtractAudio test.mxf 0 10 
-> Extract the first 10 frames from the text.mxf file


Other information
=================

website: http://code.google.com/p/jmxf/



