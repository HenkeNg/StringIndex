# Simple Search
A scala Developer Coding Exercise - [link](https://gist.github.com/rockerrecruit/236f7f53f055253e0b71695af7c81ed8) 

### Simple Search
#### How to run
##### Prerequisites
Before running the program you will need text files. Test data provided by the [exercise](https://gist.github.com/rockerrecruit/236f7f53f055253e0b71695af7c81ed8)
##### Running the program
To start the session run:
```
sbt runMain Main <filepath>
```

During the session: 

It is assumed that the initial substring of the input String is a `function`, and the tailing
will be the `search words`
e.g.
```
search> function [word1] [word2] etc..
```
The output will be the ranking of the file based on how many total search hits you get for said file.

Available commands, use:
```
--help
```
