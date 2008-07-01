package tests;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.servlet.JspServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class Test03
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        Server server = new Server(34567);
        Context context = new Context(server, "/",
            Context.SESSIONS);
        context.setResourceBase("web");
        context.addServlet(new ServletHolder(
            new HttpServlet()
            {
                
                @Override
                protected void service(
                    HttpServletRequest request,
                    HttpServletResponse response)
                    throws ServletException, IOException
                {
                    PrintWriter out = response.getWriter();
                    out
                        .println("<html><body>This is a <b>test page</b>.</body></html>");
                    out.flush();
                }
            }), "/test");
        ServletHolder jsp = new ServletHolder(new JspServlet());
        jsp.setInitParameter("classpath", "classes;lib/*");
        jsp.setInitParameter("scratchdir", "classes");
        context.addServlet(jsp, "*.jsp");
        System.out.println("starting web server");
        server.start();
        System.out.println("running");
    }
    
}
