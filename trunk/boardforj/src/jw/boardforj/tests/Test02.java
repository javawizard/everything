package jw.boardforj.tests;

import jw.boardforj.Post;
import jw.boardforj.TheBoard;

public class Test02
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TheBoard board = new TheBoard();
        Post post = board.getPost(56986);
        System.out.println(post);
    }
    
}
