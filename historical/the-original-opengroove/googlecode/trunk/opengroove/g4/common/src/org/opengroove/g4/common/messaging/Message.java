package org.opengroove.g4.common.messaging;

import java.io.Serializable;

/**
 * A stored message from one user to another. This is the kind of message that
 * shows up in the taskbar notification frame and that the user can use to send
 * attachments (files and folders) to other users.
 * 
 * @author Alexander Boyd
 * 
 */
public class Message implements Serializable
{
    private MessageHeader header;
    private MessageAttachment[] attachments;
    public MessageHeader getHeader()
    {
        return header;
    }
    public void setHeader(MessageHeader header)
    {
        this.header = header;
    }
    public MessageAttachment[] getAttachments()
    {
        return attachments;
    }
    public void setAttachments(MessageAttachment[] attachments)
    {
        this.attachments = attachments;
    }
}
