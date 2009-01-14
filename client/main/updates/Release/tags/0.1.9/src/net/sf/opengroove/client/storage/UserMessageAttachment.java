package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.Default;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface UserMessageAttachment
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    /**
     * True if this attachment is embedded, false if it is external. External
     * attachments are those that show up in the attachments pane; embedded
     * attachments are attachments used for other purposes, such as a voice
     * message or an image within the message itself.
     * 
     * @return
     */
    @Property
    public boolean isEmbedded();
    
    public void setEmbedded(boolean embedded);
    
    @Property
    public boolean isFolder();
    
    public void setFolder(boolean folder);
    
    /**
     * For embedded attachments, true if the attachment is internal or not. An
     * internal attachment is one used for some opengroove-specific purpose;
     * embedded attachments that are not internal are those that are embedded
     * within the message body. For example, if the user records a voice memo as
     * part of the message (using the voice recording buttons built in to
     * opengroove's message creation dialog), then the voice memo would be
     * stored as an embedded, internal attachment. If the user pasted an image
     * into the body of the message itself, then the image would be stored as an
     * embedded (but not internal) attachment.
     * 
     * @return
     */
    @Property
    public boolean isInternal();
    
    public void setInternal(boolean internal);
    
    /**
     * For internal (and therefore, embedded) attachments, this is the internal
     * type. Currently, only two internal types are defined, "voice", which
     * indicates that this attachment is a voice memo, and "invitation", which
     * indicates that this is a workspace invitation file.
     * 
     * @return
     */
    @Property
    @Default(stringValue="")
    public String getInternalType();
    
    public void setInternalType(String type);
    
    /**
     * Returns the size, in bytes, of the attachment. This should not change
     * even if the attachment file is deleted.
     * 
     * @return
     */
    @Property
    public int getSize();
    
    public void setSize(int size);
    
}
