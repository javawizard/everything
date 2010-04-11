package jw.boardforj;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The entry point into the BoardForJ API. An instance of this class represents access to
 * the 100-hour board.
 * 
 * @author Alexander Boyd
 * 
 */
public class TheBoard
{
    public static final String DEFAULT_URL = "http://theboard.byu.edu/";
    private static final String ACCESS_URL = "content.php?area=posts";
    private String baseUrl;
    
    /**
     * Creates a new TheBoard object that connects to the default 100-hour board,
     * <tt>http://theboard.byu.edu</tt>. This is the same as
     * <tt>new TheBoard(TheBoard.DEFAULT_URL)</tt>.
     */
    public TheBoard()
    {
        this(DEFAULT_URL);
    }
    
    /**
     * Creates a new TheBoard object that connects to the 100-hour board at the URL
     * specified. Most people will generally want to use {@link #TheBoard()} instead; this
     * constructor primarily exists to allow for testing of this class while not connected
     * to the internet.
     * 
     * @param url
     *            The url of the board
     */
    public TheBoard(String url)
    {
        if (!url.endsWith("/"))
            url = url + "/";
        this.baseUrl = url;
    }
    
    /**
     * Returns the most recent date that the board has posts for.
     * 
     * @return
     */
    public NormalDate getToday()
    {
        JSONObject object = query("today=1");
        NormalDate date = new NormalDate();
        date.setNormalYear(object.getInt("year"));
        date.setNormalMonth(object.getInt("month"));
        date.setDate(object.getInt("date"));
        return date;
    }
    
    /**
     * Returns a list of all years that have posts.
     * 
     * @return
     */
    public NormalDate[] getYears()
    {
        JSONArray list = query("findDates=1").getJSONArray("years");
        NormalDate[] dates = new NormalDate[list.length()];
        for (int i = 0; i < dates.length; i++)
        {
            NormalDate date = new NormalDate();
            date.setNormalYear(list.getInt(i));
            dates[i] = date;
        }
        return dates;
    }
    
    private JSONObject query(String params)
    {
        String urlString = baseUrl + ACCESS_URL;
        if (params != null && !params.equals(""))
            urlString += "&" + params;
        try
        {
            URL url = new URL(urlString);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            URLConnection con = url.openConnection();
            con.addRequestProperty("Referer", "http://jzbot.opengroove.org/");
            InputStream in = con.getInputStream();
            byte[] buffer = new byte[256];
            int amount;
            while ((amount = in.read(buffer)) != -1)
                baos.write(buffer, 0, amount);
            in.close();
            JSONObject object = new JSONObject(new String(baos.toByteArray()));
            return object;
        }
        catch (Exception e)// IOException and JSONException, and I don't really care if it
        // catches everything else, things will still work
        {
            throw new RuntimeException("Exception occurred while calling " + urlString, e);
        }
    }
    
    private String encode(String text)
    {
        return URLEncoder.encode(text);
    }
    
}