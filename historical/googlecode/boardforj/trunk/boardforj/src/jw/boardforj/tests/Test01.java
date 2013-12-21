package jw.boardforj.tests;

import java.util.Arrays;

import jw.boardforj.NormalDate;
import jw.boardforj.TheBoard;

public class Test01
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TheBoard board = new TheBoard();
        System.out.println(board.getToday());
        System.out.println(Arrays.toString(board.getYears()));
        System.out.println(Arrays.toString(board.getMonths(new NormalDate(2010, 1, 1))));
        System.out.println(Arrays.toString(board.getDays(new NormalDate(2010, 3, 1))));
    }
    
}
