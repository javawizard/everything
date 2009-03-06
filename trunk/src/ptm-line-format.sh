#!/bin/bash
# Takes one or two arguments, source file and dest file. Writes to the source file if
# only one argument.
# This script essentially prepends carriage returns with 02, line feeds with 01, and 0 
# chars with 03.

# xxd -c 1 -p encodes, xxd -c 1 -p -r decodes

sourcefile=$1
destfile=$sourcefile
if [ $# -gt 1 ] ; then
    destfile=$2
fi

# The actual program...

cat $sourcefile | xxd -c 1 -p | awk 'BEGIN {skip = 0