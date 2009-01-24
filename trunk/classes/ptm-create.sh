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
cat > messages << END_FILE
#!/bin/sh
if [ $# -lt 1 ] ; then
    echo messages called with no arguments
    exit 2
fi
END_FILE
chmod +x messages
# create the commandlist file 
echo \#\!/bin/sh > commandlist
echo if \[ \$# -lt 1 \] \; then >> commandlist
echo echo commandlist called with no arguments >> commandlist
echo exit 2 >> commandlist
echo fi >> commandlist
chmod +x commandlist
cd ..
