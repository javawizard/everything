#!/bin/bash

# This program takes the names of two files as command-line arguments, and writes all
# lines that are in the first but not the second to stdout.
if [ $# -lt 2 ] ; then
    echo ptm-andnotfile.sh needs two files as input
    exit 2
fi
