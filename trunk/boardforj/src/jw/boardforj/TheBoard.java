package jw.boardforj;

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
}
