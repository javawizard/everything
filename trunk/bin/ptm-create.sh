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
#!/bin/bash
if [ \$# -lt 2 ] ; then
    echo commandlist called with no arguments, needs targetfolder startrev [endrev printstatus]
    exit 2
fi
targetfolder=\$1
startrev=\$2
endrev=\$2
if [ \$# -ge 3 ] ; then
    endrev=\$3
fi
printstatus=false
if [ \$# -ge 4 ] ; then
    printstatus=true
fi
END_FILE
chmod +x commandlist

echo 0 > revision

cd ..
