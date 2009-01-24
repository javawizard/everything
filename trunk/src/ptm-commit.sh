#!/bin/bash

# This command creates a new revision.

# first, we'll read the current revision number into an environment variable. We'll then
# increment it, and store the result in another environment variable.

oldrevision=`cat .ptm/revision`
newrevision=`expr $oldrevision + 1`

# Next, we'll work on building the folder lists.

ptm-listfiles.sh d > .ptm/tmp/dirs-wc
ptm-listfiles.sh f > .ptm/tmp/files-wc
cd .ptm/head
ptm-listfiles.sh d > ../tmp/dirs-head
ptm-listfiles.sh f > ../tmp/files-head