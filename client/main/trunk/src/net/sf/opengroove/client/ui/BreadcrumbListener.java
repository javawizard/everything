package net.sf.opengroove.client.ui;

public interface BreadcrumbListener
{
    /**
     * Indicates that one of the items in the breadcrumb has been clicked.
     * Usually, an application will switch the current folder to the item
     * clicked, and update the breadcrumb's view accordingly.
     * 
     * @param breadcrumb
     *            The source of the event
     * @param items
     *            The list of items currently in this breadcrumb
     * @param index
     *            The index within <code>items</code> of the item that was
     *            clicked
     */
    public void itemClicked(Breadcrumb breadcrumb,
        String[] items, int index);
}
