package org.opengroove.g4.common.object;

import java.io.Serializable;

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
 * attempt to deserialize the object.
 * 
 * @author Alexander Boyd
 * 
 */
public class PassThroughObject implements Serializable
{
    
}
