#!/bin/sh
if [ $# -lt 2 ] ; then
    echo Need two arguments
    exit 2
fi
interval=1
if [ $# -ge 3 ] ; then
    interval=$3
fi
awk -f - $1 $2 $interval << END_FILE
BEGIN {
ORS=" "
start=ARGV[1]
count=ARGV[2]
iv=ARGV[3]
for(i=start;i<=count;i=i+iv)
    print i
}
END_FILE
