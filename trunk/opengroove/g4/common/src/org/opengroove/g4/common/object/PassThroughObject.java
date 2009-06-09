package org.opengroove.g4.common.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.g4.common.TemporaryFileStore;
import org.opengroove.g4.common.data.DataBlock;
import org.opengroove.g4.common.data.DataBlockBuilder;
import org.opengroove.g4.common.data.FileBlockBuilder;

/**
 * A class that allows an object to be serialized and then deserialized for
 * pass-through purposes where the pass-through machine that deserializes and
 * then serializes the object doesn't have the class of the object itself.
 * Normally, ObjectInputStream throws a ClassNotFoundException if this occurs.
 * PassThroughObject gets around that by lazily deserializing the object, so
 * that if the actual object is never requested from the PassThroughObject
 * instance, the object won't be deserialized and hence no exception will occur. <br/>
 * <br/>
 * 
 * PassThroughObject depends on DataBlock and its implementors to cache the data
 * until it is requested. Since it uses FileBlock for pieces of data over 1KB,
 * it is strongly recommended that applications using PassThroughObject start a
 * thread that periodically (like once every 30 seconds) garbage-collects, to
 * avoid excessive temporary files being stored on the system. <br/>
 * <br/>
 * 
 * PassThroughObject also serializes the class name of its object independently
 * from the object itself. This allows the class name of the object to be
 * retrieved without the object itself being deserialized. Applications could
 * then ask PassThroughObject for the class of the object that it holds so that
 * they can check to make sure that they have that class definition before they
 * attempt to deserialize the object.<br/>
 * <br/>
 * 
 * This class correctly handles when the object is set to the value
 * <tt>null</tt>. The classname for a null value is itself null, not the string
 * "null".<br/>
 * <br/>
 * 
 * <b>This class is not thread-safe.</b> If the object is set while this
 * PassThroughObject is being serialized, the results are undefined.
 * 
 * @author Alexander Boyd
 * 
 */
public class PassThroughObject implements Serializable
{
    /*
     * Lifecycle: the user sets an object on pass through object. It is then
     * serialized, which independently serializes the object to a FileBlock,
     * sticks that in the field here, and then default serializes this class.
     * This only occurs if the file block is null; if it's not, then we don't
     * bother to serialize the object.
     * 
     * On deserialization, the file block will be naturally deserialized. On
     * attempting to get the object, if it's null, we deserialize it from the
     * file block. If the file block is null, then we return null, since that
     * means that this pass through was just created from scratch.
     * 
     * There is a serialized boolean called isNull. If this is true, then it
     * means that the object itself is the value null, instead of that the
     * object is null because we haven't deserialized it yet.
     * 
     * Just setting the object doesn't cause it to be serialized to the
     * FileObject; this only happens on serialization, and then only if the
     * FileBlock is not null.
     */

    /**
     * 
     */
    private static final long serialVersionUID = 5813749119144240328L;
    private DataBlock block;
    private transient Object object;
    private boolean isNull;
    private String classname;
    
    public Object getObject()
    {
        /*
         * If isNull is true, then the object is supposed to be null so we
         * return null. If isNull is not true but the object is not null, then
         * we've either deserialized it already or it was previously set on this
         * class so we'll return it. If isNull is not true and the object is
         * null, then we haven't deserialized it yet so we'll deserialize it
         * into the object field.
         */
        if (isNull)
            return null;
        else if (object != null)
            return object;
        else if (block == null)
            throw new RuntimeException(
                "Corrupted pass-through object: The object is null, "
                    + "isNull is false, but there is no data block for the object");
        else
        {
            try
            {
                ObjectInputStream objectIn = new ObjectInputStream(block.getStream());
                this.object = objectIn.readObject();
                objectIn.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            return object;
        }
    }
    
    public void setObject(Object object)
    {
        this.object = object;
        /*
         * clear the block so that it will be re-created the next time this
         * object is serialized
         */
        block = null;
        /*
         * set isNull to be whether this object is null or not
         */
        isNull = object == null;
        if (object == null)
            classname = null;
        else
            classname = object.getClass().getName();
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        /*
         * If the block is null and isNull is false, then we serialize to a new
         * block and stick it in this block. Then we default serialize.
         */
        if (block == null && isNull == false)
        {
            DataBlockBuilder builder = new FileBlockBuilder();
            OutputStream sOut = builder.getStream();
            ObjectOutputStream oOut = new ObjectOutputStream(sOut);
            oOut.writeObject(object);
            oOut.flush();
            oOut.close();
            sOut.close();
            block = builder.finish();
        }
        /*
         * The block exists now, or isNull is true. We'll default serialize now.
         */
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException,
        ClassNotFoundException
    {
        /*
         * We don't do anything here; default reading will suffice.
         * Deserialization is done in the getObject method.
         */
        in.defaultReadObject();
    }
    
    /**
     * Returns the fully-qualified name of the class of the object that was
     * initially set on this pass-through object. This does not result in the
     * object being deserialized, and so can be called even when the actual
     * classname returned from here isn't a valid class under this vm.
     * 
     * @return
     */
    public String getClassname()
    {
        return classname;
    }
}
