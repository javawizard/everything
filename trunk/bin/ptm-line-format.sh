#!/bin/bash
# Takes one or two arguments, source file and dest file. Writes to the source file if
# only one argument.
# This script essentially prepends carriage returns with 02, line feeds with 01, and 0 
# chars with 03.

# xxd -c 1 -p encodes, xxd -c 1 -p -r decodes

if [ $# -lt 1 ] ; then
    echo ptm-line-format needs arguments
    exit 25
fi

sourcefile=$1
destfile=$sourcefile
echo lineformatting source $sourcefile dest $destfile 1 $1 2 $2
if [ $# -gt 1 ] ; then
    destfile=$2
fi

# The actual program...

cat $sourcefile | xxd -c 1 -p | awk '{
if ($0 == "0a")
{ 
    print "30"
    print "31"
}
else if($0 == "0d")
{
    print "30"
    print "32"
}
else if($0 == "30")
{
    print "30"
    print "33"
}
print $0
}' | xxd -c 1 -p -r > $destfile