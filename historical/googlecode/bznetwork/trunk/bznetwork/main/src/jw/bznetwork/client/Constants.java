package jw.bznetwork.client;

public class Constants
{
    
    public enum TargetType
    {
        server, group, banfile, global
    }
    
    public static final String[] LOG_EVENTS = new String[]
    {
            "stdout", "filtered", "report", "slashcommand", "chat-unknown",
            "chat-server", "chat-broadcast", "chat-admin", "chat-team",
            "chat-private", "join", "part", "status"
    };
    public static final String[] LOG_EVENT_DESCRIPTIONS = new String[]
    {
            "A message was printed to stdout or stderr by the bzflag server.",
            "A player sent a message containing bad words, and they were filtered.",
            "A player used the /report command.",
            "A player used a slash command other than /report.",
            "A chat message with an unknown type was sent by a player.",
            "A chat message was sent by the server.",
            "A chat message was sent to all players.",
            "A chat message was sent to the server administrators.",
            "A chat message was sent just to a particular team.",
            "A chat message was sent directly from one player to another.",
            "A player joined the server.", "A player left the server.",
            "The server's status (whether it is running or not) changed."
    };
    
}
