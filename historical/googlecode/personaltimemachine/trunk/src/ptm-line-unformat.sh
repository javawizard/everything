#!/bin/bash
# Takes one or two arguments, source file and dest file. Writes to the source file if
# only one argument.
# This script removes all carriage returns and newlines. Then it replaces all instances
# of 02 in the file with a carriage return, 01 with a newline, and 03 with a 0 
# character, skipping the next character (which would be the actual 0). 

# xxd -c 1 -p encodes, xxd -c 1 -p -r decodes

sourcefile=$1
destfile=$sourcefile
if [ $# -gt 1 ] ; then
    destfile=$2
fi

# The actual program...

cat $sourcefile | xxd -c 1 -p | sed -e '/0a/d' -e '/0d/d' | awk '{
if ($0 == "30")
{
    getline
    if ($0 == "31")
    {
        print "0a"
    }
    else if ($0 == "32")
    {
        print "0d"
    }
    else if ($0 == "33")
    {
        print "30"
        getline
    }
    else
    {
        print "input following 0 char was not 1, 2, or 3, stopping."
        exit 7
    }
}
else
{
    print $0
}
}' | xxd -c 1 -p -r > $destfile