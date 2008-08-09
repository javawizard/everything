package net.sf.opengroove.client.ui;

import javax.swing.JPanel;

import net.sf.opengroove.client.com.ListenerManager;

/**
 * This class shows a simple breadcrumb component. An application can hand it a
 * String[], which represents the current path that it should display. The user
 * can click on any of the path components, which calls back to any
 * BreadcrumbListeners registered. Those listeners can update the path that is
 * displayed. It also supports registering a BreadcrumbModel, which should
 * provide to the breadcrumb component the children of any given element. If a
 * breadcrumb model is supplied, then the breadcrumb will display TBD.
 * 
 * @author Alexander Boyd
 * 
 */
public class Breadcrumb extends JPanel
{
    private ListenerManager<BreadcrumbListener> listeners;
}
