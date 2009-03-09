#!/bin/bash

# This command creates a new revision.

# first, we'll read the current revision number into an environment variable. We'll then
# increment it, and store the result in another environment variable.

oldrevision=`cat .ptm/revision`
newrevision=`expr $oldrevision + 1`

# Now we'll copy the working copy over to .ptm/tmp/lineformatted, and line-format
# everything in it.

ptm-copy-without-ptm.sh .ptm/tmp/lineformatted
find .ptm/tmp/lineformatted -type f -print0 | xargs -0 ptm-line-format.sh

# Next, we'll work on building the folder and file changelists.

echo Building sorted file lists

cd .ptm/tmp/lineformatted
echo in `pwd`

ptm-listfiles.sh d | sort > ../dirs-wc
ptm-listfiles.sh f | sort > ../files-wc
cd ../../head
echo in `pwd`
ptm-listfiles.sh d | sort > ../tmp/dirs-head
ptm-listfiles.sh f | sort > ../tmp/files-head
cd ../tmp
echo in `pwd`
echo Building changelists
ptm-linediff.sh dirs-head dirs-wc | sort | uniq > dirs-removed
ptm-linediff.sh dirs-wc dirs-head | sort | uniq > dirs-added
ptm-linediff.sh files-wc files-head | sort | uniq > files-added
ptm-linediff.sh files-head files-wc | sort | uniq > files-removed
cat files-added files-removed | sort | uniq > files-changed
ptm-linediff.sh files-wc files-changed | sort | uniq > files-common
cd ..
echo in `pwd`

echo Appending changelists to command file
# Now we'll start writing to the command list file. At this point, the working directory 
# is the .ptm folder.

cat >> commandlist << END_FILE
if [ \$startrev -le $newrevision ] ; then if [ \$endrev -ge $newrevision ] ; then
    if [ \$printstatus ] ; then
        echo Applying revision $newrevision
    fi
    pushd \$targetfolder 
    xargs -r -d "\`echo -ne \\\\\\\\n\`" mkdir -p << ./.ptm/end 
END_FILE
cat tmp/dirs-added >> commandlist
cat >> commandlist << END_FILE
./.ptm/end
    patch -F 0 -p2 -u < \${basefolder}/.ptm/diffs/${newrevision}
    xargs -r -d "\`echo -ne \\\\\\\\n\`" rm -rf << ./.ptm/end
END_FILE
cat tmp/files-removed >> commandlist
cat tmp/dirs-removed >> commandlist
cat >> commandlist << END_FILE
./.ptm/end
    xargs -r -d "\`echo -ne \\\\\\\\n\`" ptm-apply-diff.sh \$basefolder ${newrevision} << ./.ptm/end
END_FILE
cat tmp/files-common >> commandlist
cat >> commandlist << END_FILE
./.ptm/end
popd
fi ; fi
END_FILE

cd head
echo in `pwd`
echo Performing diff of lineformatted working copy and head
cd ..
echo \# diff for revision $newrevision > diffs/$newrevision

cat tmp/files-added | xargs -d "`echo -ne \\\\n`" pt-create-diff.sh $newrevision

echo -n $newrevision > revision
echo ${newrevision}_d`date` >> revinfo
echo "${newrevision}_m$*" >> revinfo
echo "${newrevision}_u`whoami`" >> revinfo

cd ..
echo in `pwd`
echo Moving lineformatted working copy to head
rm -rf .ptm/head
mv .ptm/tmp/lineformatted .ptm/head

echo ""
echo Committed revision $newrevision



