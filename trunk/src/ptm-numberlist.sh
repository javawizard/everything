#!/bin/sh
if [ $# -lt 2 ] ; then
    echo Need two arguments
    exit 2
fi
awk -f - $1 $2 << END_FILE
BEGIN {
ORS=" "
start=ARGV[1]
count=ARGV[2]
for(i=start;i<=count;i++)
    print i
}
END_FILE
