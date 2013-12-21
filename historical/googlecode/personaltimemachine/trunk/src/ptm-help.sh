if [ $# -lt 1 ] ; then
    echo TODO: finish up help
    echo commands:
    echo     commit
    echo     create
    echo     export
    echo     changed
    
elif [ $1 = commit -o $1 = co ] ; then
    echo Creates a new revision.
elif [ $1 = create ] ; then
    echo Creates a new repository in the current directory.
    echo Files can exist in the current directory. They will not be overwritten, and they can be added to the repository via "ptm commit".
elif [ $1 = export ] ; then
    echo Exports a revision. This can be used to revert portions of the working copy.
    echo This operation tends to be rather slow, so use with caution. However, terminating it via Ctrl+C will not corrupt the repository.
elif [ $1 = changed ] ; then
    echo Show which files have been added, removed, or changed since the last commit.
else
    echo That command isn\'t a ptm command.
    echo Try \"ptm help\" to get a list of all commands.
fi