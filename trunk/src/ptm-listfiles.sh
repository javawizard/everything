#!/bin/bash

if [ $# -lt 1 ] ; then
    echo need an argument in ptm-listfiles.sh
    exit 7
end

find . -type $1 | grep -v ^\\.\\/\\.ptm\$ | grep -v ^\\.\\/\\.ptm\\/
