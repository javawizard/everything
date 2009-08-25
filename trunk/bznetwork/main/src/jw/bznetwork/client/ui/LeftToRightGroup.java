package jw.bznetwork.client.ui;

import com.google.gwt.user.client.ui.HTML;

/**
 * A widget that can be used to indicate that something to its left is being
 * grouped into something to its right.
 * 
 * @author Alexander Boyd
 * 
 */
public class LeftToRightGroup extends HTML
{
    public LeftToRightGroup()
    {
        super(
                "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"height: 100%;\">\n"
                        + "                <tbody>\n"
                        + "                    <tr>\n"
                        + "                        <td style=\"border: 1px solid black; border-left: 1px none; width: 8px;\">&nbsp;</td>\n"
                        + "                        <td valign=\"middle\">\n"
                        + "                        <table width=\"12\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n"
                        + "                            <tbody>\n"
                        + "                                <tr>\n"
                        + "                                    <td>\n"
                        + "                                </td></tr>\n"
                        + "                                <tr>\n"
                        + "                                    <td width=\"100%\" height=\"1\" style=\"background-color: black;\">\n"
                        + "                                </td></tr>\n"
                        + "                                <tr>\n"
                        + "                                    <td>\n"
                        + "                                </td></tr>\n"
                        + "                            </tbody>\n"
                        + "                        </table>\n"
                        + "                        </td>\n"
                        + "                        \n"
                        + "                    </tr>\n"
                        + "                </tbody>\n" + "            </table>");
    }
    
    public LeftToRightGroup(String height)
    {
        this();
        setHeight(height);
    }
}
