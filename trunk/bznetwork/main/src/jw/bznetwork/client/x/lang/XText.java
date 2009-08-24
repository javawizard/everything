package jw.bznetwork.client.x.lang;

public class XText extends XNode
{
    private String text;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public XText(String text)
    {
        super();
        this.text = text;
    }

    public XText()
    {
        super();
    }
}
