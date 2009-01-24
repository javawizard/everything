#!/bin/bash

# This command creates a new revision.

# first, we'll read the current revision number into an environment variable. We'll then
# increment it, and store the result in another environment variable.

oldrevision=`cat .ptm/revision`
newrevision=`expr $oldrevision + 1`

# Next, we'll work on building the folder and file changelists.

echo Building sorted file lists

ptm-listfiles.sh d | sort > .ptm/tmp/dirs-wc
ptm-listfiles.sh f | sort > .ptm/tmp/files-wc
cd .ptm/head
ptm-listfiles.sh d | sort > ../tmp/dirs-head
ptm-listfiles.sh f | sort > ../tmp/files-head
cd ../tmp
echo Building changelists
ptm-linediff.sh dirs-head dirs-wc | uniq > dirs-removed
ptm-linediff.sh dirs-wc dirs-head | uniq > dirs-added
ptm-linediff.sh files-head files-wc | uniq > files-removed
cd ..
echo Appending changelists to command file
# Now we'll start writing to the command list file. At this point, the working directory 
# is the .ptm folder.

cat >> commandlist << END_FILE
if [ \$startrev -le $newrevision ] ; then if [ \$endrev -ge $newrevision ] ; then
    if [ \$printstatus ] ; then
        echo Applying revision $newrevision
    fi
    cd \$targetfolder 
    xargs mkdir << ./.ptm/end 
END_FILE
cat tmp/dirs-added >> commandlist
cat >> commandlist << END_FILE
./.ptm/end
    patch -u << \${basefolder}/.ptm/diffs/${newrevision}
    xargs rm -rf << ./.ptm/end
END_FILE
cat tmp/files-removed >> commandlist
cat tmp/dirs-removed >> commandlist
cat >> commandlist << END_FILE
./.ptm/end
fi ; fi
END_FILE

cd head
echo Performing diff of working copy and head
diff -U 0 -a --binary --unidirectional-new-file --exclude=^./.ptm\$ --exclude=^.ptm\$ -r . ../.. >> ../tmp/diff-output
echo Storing diff
cd ..
mv tmp/diff-output diffs/${newrevision}
echo -n $newrevision > revision
echo ${newrevision}_d`date` >> revinfo
echo "${newrevision}_m$*" >> revinfo
echo "${newrevision}_u`whoami`" >> revinfo
cd ..
echo ""
echo Committed revision $newrevision














