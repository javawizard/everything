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

touch revinfo

# create the commandlist file 
cat > commandlist << END_FILE
#!/bin/sh
if [ $# -lt 1 ] ; then
    echo commandlist called with no arguments
    exit 2
fi
END_FILE
chmod +x commandlist

cd ..
