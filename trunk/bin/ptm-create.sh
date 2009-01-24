#!/bin/bash

# This command creates a new repository. It essentially creates the .ptm folder and sets
# it up with all of the files and folders that ptm needs.

# TODO: check to see if the folder already exists
mkdir .ptm
cd .ptm
# create folders
mkdir head
mkdir tmp
mkdir diffs
# create the messages file
echo \#\!/bin/sh > messages
echo if \[ \$# -lt 1 \] \; then >> messages
echo echo messages called with no arguments >> messages
echo exit 2 >> messages
echo fi >> messages
chmod +x messages
# create the commandlist file 
echo \#\!/bin/sh > commandlist
echo if \[ \$# -lt 1 \] \; then >> commandlist
echo echo commandlist called with no arguments >> commandlist
echo exit 2 >> commandlist
echo fi >> commandlist
chmod +x commandlist
cd ..
