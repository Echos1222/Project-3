# Project-3

This program implements a disk-based B-tree index with a command-line interface. It allows the user to create an index file, insert/search keys, and load or extract to another file. Nodes are stored on disk in 512-byte blocks, and B-tree is persistent across program runs.

Files present:
project3.java Driver File
IndexFile.java Manages operations on a B-tree
Header.java stores file metadata in block 0
BTreeNode.java represents the B-tree node in memory
NodeIO.java interface for reading/writing/allocating nodes
DiskNodeIO.java implements NodeIO with disk access using RandomAccessFile
NodeSerializer.java converts objects to 512-byte blocks for disk storage
BTree.java - implements the B-tree and associated algorithms

To run the program make sure all files are in the same directory and use javac *.java 
The usage pattern will be java project 3 <command> <arguments> 
Examples: 
java project3 create test.idx
java project3 insert test.idx 15 100
java project3 search test.idx 15
java project3 load test.idx input.csv
java project3 print test.idx
java project3 extract test.idx output.csv

Make sure to create the file before inserting 
