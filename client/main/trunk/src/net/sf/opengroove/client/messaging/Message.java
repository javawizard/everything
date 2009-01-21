package net.sf.opengroove.client.messaging;

import java.util.ArrayList;

import net.sf.opengroove.client.model.UserComputer;

/**
 * This class represents a stored message, as seen by users of the message
 * hierarchy.
 * 
 * @author Alexander Boyd
 * 
 */
public class Message
{
    private ArrayList<UserComputer> recipients;
    private UserComputer sender;
}
