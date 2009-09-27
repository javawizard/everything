package org.opengroove.jzbot.commands;

import java.nio.charset.Charset;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.ConfigVars;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.utils.JZUtils;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;

public class ConfigCommand implements Command
{
    
    public String getName()
    {
        return "config";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (!JZBot.isSuperop(hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "You're not a superop.");
            return;
        }
        String[] tokens = arguments.split(" ", 2);
        if (!tokens[0].equals(""))
        {
            ConfigVars var = ConfigVars.valueOf(tokens[0]);
            if (var == null)
                throw new ResponseException(
                        "That isn't a valid var name. Use \"~config\" to see "
                                + "a list of var names.");
            if (tokens.length == 1)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "This variable's current value is \"" + var.get()
                                + "\". You can use \"~config " + var.name()
                                + " <newvalue>\" to set a new value."
                                + " The variable's description is:");
                JZBot.bot.sendMessage(pm ? sender : channel, var
                        .getDescription());
            }
            else
            {
                var.set(tokens[1]);
                JZBot.bot
                        .sendMessage(pm ? sender : channel,
                                "Successfully set the var \"" + var.name()
                                        + "\" to have the value \"" + tokens[1]
                                        + "\".");
            }
        }
        else
        {
            String[] configVarNames = new String[ConfigVars.values().length];
            for (int i = 0; i < configVarNames.length; i++)
            {
                configVarNames[i] = ConfigVars.values()[i].name();
            }
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Use \"~config <varname>\" to see a var (value and description) "
                            + "or \"~config "
                            + "<varname> <value>\" to set a var. Currently, "
                            + "allowed varnames are, separated by spaces: "
                            + StringUtils.delimited(configVarNames, "  "));
        }
    }
}
