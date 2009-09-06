package jw.bznetwork.server;

import jw.bznetwork.client.data.model.IrcBot;

import org.jibble.pircbot.PircBot;

public class IrcServerBot extends PircBot
{
    private boolean isLive;
    
    private int botid;
    private IrcBot bot;
    
    public IrcBot getBot()
    {
        return bot;
    }
    
    public void bznDestroy()
    {
        isLive = false;
        quitServer();
        dispose();
    }
    
    public IrcServerBot(IrcBot bot)
    {
        this.bot = bot;
        setAutoNickChange(true);
        super.setLogin(bot.getNick());
        super.setName(bot.getNick());
        super.setVersion("BZNetwork IRC bot");
        super
                .setFinger("BZNetwork totally owns everyone else. And I don't much "
                        + "like PircBot's built-in finger response so I'm overriding it.");
        startConnect();
    }
    
    public void startConnect()
    {
        new Thread()
        {
            public void run()
            {
                startConnect1();
            }
        }.start();
    }
    
    private void startConnect1()
    {
        try
        {
            connect(bot.getServer(), bot.getPort(), ("".equals(bot
                    .getPassword()) ? null : bot.getPassword()));
        }
        catch (Exception e)
        {
            onDisconnect();
        }
    }
    
    @Override
    protected void onDisconnect()
    {
        while (isLive && !isConnected())
        {
            try
            {
                reconnect();
            }
            catch (Exception e)
            {
                System.err.println("BZNetwork irc bot reconnect error");
                e.printStackTrace();
                try
                {
                    Thread.sleep(15 * 1000);
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    @Override
    protected void onConnect()
    {
        if (!isLive)
        {
            disconnect();
            return;
        }
        joinChannel(bot.getChannel());
    }
    
    protected void onMessage(String channel, String sender, String login,
            String hostname, String message)
    {
        // TODO: add some creative replies here
    }
    
    protected void onPrivateMessage(String sender, String login,
            String hostname, String message)
    {
        // TODO: add some creative replies here
    }
    
    protected void onAction(String sender, String login, String hostname,
            String target, String action)
    {
        // TODO: add some creative replies here
    }
    
}
