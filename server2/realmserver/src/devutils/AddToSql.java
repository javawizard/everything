package devutils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.*;

/**
 * A simple utility for adding new mapped statements to persistantsqlmap.xml and
 * largesqlmap.xml . It asks you which one you want (persistant or large), the
 * type of statement (select, insert, update, or delete), the name, the
 * parameter class, and the result class (for select statements), and the actual
 * sql data to insert. For parameter classes and result classes, it searches for
 * classes in the package net.sf.opengroove.realmserver.data.model and shows
 * those classes. It also shows String, Integer, and Long. The parameter class
 * can be left empty (as would be the case for some select statements). A method
 * stub can also be generated and inserted into DataStore.java. These method
 * stubs accept as arguments either some (or all) of the properties on the
 * parameter class chosen (in the order specified), or an instance of the
 * parameter class specified. The method stubs return either an object of the
 * result class type specified, or an array of that object, depending on what
 * the user chooses. queryForObject or queryForList will be called,
 * respectively, based on which one they choose.
 * 
 * @author Alexander Boyd
 * 
 */
public class AddToSql
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        loadClasses();
        JFrame frame = new JFrame(
            "AddToSql - OpenGroove Realm Server");
        frame.setSize(350, 550);
        frame.setLocationRelativeTo(null);
        frame.getContentPane()
            .setLayout(new BorderLayout());
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls,
            BoxLayout.Y_AXIS));
        JComboBox target = new JComboBox(new String[] {
            "persistant", "large" });
        target.setToolTipText("Target file");
        fa(target);
        controls.add(target);
        JComboBox type = new JComboBox(new String[] {
            "select", "select list", "insert", "update",
            "delete" });
        type.setToolTipText("Type of statement");
        fa(type);
        controls.add(type);
        JTextField statementId = new JTextField();
        statementId.setToolTipText("Statement name/id");
        fa(statementId);
        controls.add(statementId);
        JComboBox parameterClass = new JComboBox(classes
            .toArray());
        parameterClass.setToolTipText("Parameter class");
        fa(parameterClass);
        controls.add(parameterClass);
        JComboBox resultClass = new JComboBox(classes
            .toArray());
        resultClass.setToolTipText("Result class");
        fa(resultClass);
        controls.add(resultClass);
        JTextArea statementArea = new JTextArea();
        controls.add(new JLabel("<html>&nbsp;"));
        frame.getContentPane().add(controls,
            BorderLayout.NORTH);
        frame.getContentPane().add(
            new JScrollPane(statementArea),
            BorderLayout.CENTER);
        JButton done = new JButton("done");
        frame.getContentPane()
            .add(done, BorderLayout.SOUTH);
        done.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO Auto-generated method stub
                
            }
        });
        frame.show();
    }
    
    private static ArrayList<ClassChoice> classes = new ArrayList<ClassChoice>();
    
    private static void loadClasses() throws Exception
    {
        classes.add(new ClassChoice());
        File[] files = new File(
            "src/net/sf/opengroove/realmserver/data/model")
            .listFiles(new FileFilter()
            {
                
                @Override
                public boolean accept(File pathname)
                {
                    return pathname.getName().endsWith(
                        ".java");
                }
            });
        for (File file : files)
        {
            String name = file.getName().substring(0,
                file.getName().length() - 5);
            Class c = Class
                .forName("net.sf.opengroove.realmserver.data.model."
                    + name);
            ClassChoice cc = new ClassChoice();
            cc.setName(c.getName().substring(
                c.getName().lastIndexOf(".") + 1));
            cc.setType(c);
            classes.add(cc);
        }
        ClassChoice cc = new ClassChoice();
        cc.setName("Integer");
        cc.setType(Integer.class);
        classes.add(cc);
        cc = new ClassChoice();
        cc.setName("Long");
        cc.setType(Long.class);
        classes.add(cc);
        cc = new ClassChoice();
        cc.setName("String");
        cc.setType(String.class);
        classes.add(cc);
    }
    
    private static class ClassChoice
    {
        private Class type;
        private String name;
        private String[] properties;
        private String[] propertyTypes;
        
        public String[] getPropertyTypes()
        {
            return propertyTypes;
        }
        
        public void setPropertyTypes(String[] propertyTypes)
        {
            this.propertyTypes = propertyTypes;
        }
        
        public Class getType()
        {
            return type;
        }
        
        public String getName()
        {
            return name;
        }
        
        public String toString()
        {
            if (getName() == null)
                return "";
            return getName();
        }
        
        public String[] getProperties()
        {
            return properties;
        }
        
        public void setType(Class type)
        {
            this.type = type;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public void setProperties(String[] properties)
        {
            this.properties = properties;
        }
    }
    
    private static void fa(JComponent c)
    {
        c.setAlignmentX(c.LEFT_ALIGNMENT);
    }
    
}
