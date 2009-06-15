package net.sf.opengroove.client.filesystem;

import java.io.File;

/**
 * This interface represents an object in the filesystem. It is intended to
 * provide functionality similar to File and RandomAccessFile. The reason why
 * it's present at all (over using {@link File}) is so that OpenGroove can
 * provide custom implementations, such as a file system that's encrypted using
 * the user's keys, and so that atomic file modifications are allowed.
 * 
 * @author Alexander Boyd
 * 
 */
public interface FileObject
{
    
}
