#!/bin/bash

# This program takes the names of two files as command-line arguments, and writes all
# lines that are in the first but not the second to stdout.
if [ $# -lt 2 ] ; then
    echo ptm-linediff.sh needs two files as input
    exit 2
fi
diff -u $1 $2 | tail -n +3 | grep ^- | sed -e "s/^-//"