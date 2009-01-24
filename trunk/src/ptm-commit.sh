#!/bin/bash

# This command creates a new revision.

# first, we'll read the current revision number into an environment variable. We'll then
# increment it, and store the result in another environment variable.

oldrevision=`cat .ptm/revision`
newrevision=`expr $oldrevision + 1`

# Next, we'll work on building the folder and file changelists.

ptm-listfiles.sh d | sort > .ptm/tmp/dirs-wc
ptm-listfiles.sh f | sort > .ptm/tmp/files-wc
cd .ptm/head
ptm-listfiles.sh d | sort > ../tmp/dirs-head
ptm-listfiles.sh f | sort > ../tmp/files-head
cd ../tmp
ptm-linediff.sh dirs-head dirs-wc | uniq > dirs-removed
ptm-linediff.sh dirs-wc dirs-head | uniq > dirs-added
ptm-linediff.sh files-head files-wc | uniq > files-removed
cd ..

# Now we'll start writing to the command list file. At this point, the working directory 
# is the .ptm folder.