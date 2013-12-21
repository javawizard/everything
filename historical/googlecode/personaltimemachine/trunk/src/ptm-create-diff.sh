#!/bin/bash

# Accepts two arguments. The first is the current revision, and the second is a path 
# (relative to the lineformatted working copy) of a
# file that exists in both the working copy and the head. This utility assumes that the
# working directory is the .ptm folder. It then diffs the file in the head and in the
# working copy, and appends it, prepended with _FILENAME (where FILENAME is the path of 
# the file) and appended with *, to diffs/REV, where REV is the current revision.

echo -ne _${2}\\n >> diffs/$1
diff -U 1 -a "head/$2" "tmp/lineformatted/$2" >> diffs/$1
echo -ne \*${2}\\n >> diffs/$1  