#!/bin/bash

# This command creates a new repository. It essentially creates the .ptm folder and sets
# it up with all of the files and folders that ptm needs.

if [ -d .ptm ] ; then
    cat << END_FILE
This folder is already versioned with ptm.
END_FILE
    exit 4;
fi
if [ -e .ptm ] ; then
    cat << END_FILE
There is a file present in this folder called ".ptm". ptm needs to create a folder by that same name. Delete the file named .ptm, then run "ptm create" again.
END_FILE
    exit 5;
fi

# Everything's in order. We'll create the folders and stuff now.

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
basefolder=\`pwd\`
if [ \$# -ge 3 ] ; then
    endrev=\$3
fi
printstatus=false
if [ \$# -ge 4 ] ; then
    printstatus=true
fi
END_FILE
chmod +x commandlist

echo -n 0 > revision

cd ..
