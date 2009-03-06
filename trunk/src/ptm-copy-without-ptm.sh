#!/bin/bash

# Copies a folder to another folder, similar to cp -r, but excludes the .ptm folder and 
# all files within it. One argument is accepted: the destination directory. The source
# directory is the current directory.

# This script doesn't check to make sure that the argument is present; it assumes you're
# smart enough to not call it without arguments. It will also create the destination
# directory and any parent directories if they don't exist already.

mkdir -p $1


echo rsyncing `pwd` to $1

rsync -r --exclude=/.ptm/ . $1