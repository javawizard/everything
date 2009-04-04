package org.opengroove.jzbot.commands;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import net.sf.opengroove.common.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.utils.OneTimeIterable;

public class GoogleCommand implements Command
{
    
    public String getName()
    {
        return "google";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
        String arguments)
    {
        if (arguments.equals(""))
            throw new ResponseException("specify search terms after the google command");
        try
        {
            URL url =
                new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q="
                    + URLEncoder.encode(arguments));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            URLConnection con = url.openConnection();
            con.addRequestProperty("Referer", "http://jzbot.opengroove.org/");
            InputStream in = con.getInputStream();
            StringUtils.copy(in, baos);
            in.close();
            System.out.println(new String(baos.toByteArray()));
            JSONObject j = new JSONObject(new String(baos.toByteArray()));
            JSONObject responseDataObject = j.getJSONObject("responseData");
            JSONArray resultsObject = responseDataObject.getJSONArray("results");
            JZBot.bot.sendMessage(pm ? sender : channel, "Search results for "
                + arguments + ", " + resultsObject.length() + " result"
                + (resultsObject.length() == 1 ? "" : "s")
                + ", http://google.com/search?q=" + URLEncoder.encode(arguments));
            String currentList = "";
            for (int i = 0; i < resultsObject.length() && i < 4; i++)
            {
                JSONObject result = resultsObject.getJSONObject(i);
                String resultText =  result.getString("url")
                + " "
                + result.getString("title").replace("<b>", "").replace("</b>", "");
                JZBot.bot.sendMessage(pm ? sender : channel,);
            }
            JZBot.bot.sendMessage(pm ? sender : channel, "End of search results");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
}
