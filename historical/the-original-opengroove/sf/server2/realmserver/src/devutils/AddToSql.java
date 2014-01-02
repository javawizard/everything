package devutils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        final JFrame frame = new JFrame(
            "AddToSql - OpenGroove Realm Server");
        frame.setSize(350, 550);
        frame.setLocationRelativeTo(null);
        frame.getContentPane()
            .setLayout(new BorderLayout());
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls,
            BoxLayout.Y_AXIS));
        final JComboBox target = new JComboBox(
            new String[] { "persistant", "large" });
        target.setToolTipText("Target file");
        fa(target);
        controls.add(target);
        final JComboBox type = new JComboBox(new String[] {
            "select", "select list", "insert", "update",
            "delete" });
        type.setToolTipText("Type of statement");
        fa(type);
        controls.add(type);
        final JTextField statementId = new JTextField();
        statementId.setToolTipText("Statement name/id");
        fa(statementId);
        controls.add(statementId);
        final JComboBox parameterClass = new JComboBox(
            classes.toArray());
        parameterClass.setToolTipText("Parameter class");
        fa(parameterClass);
        controls.add(parameterClass);
        final JComboBox resultClassBox = new JComboBox(
            classes.toArray());
        resultClassBox.setToolTipText("Result class");
        fa(resultClassBox);
        controls.add(resultClassBox);
        final JTextArea statementArea = new JTextArea();
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
                if (statementId.getText().trim().equals(""))
                {
                    JOptionPane.showMessageDialog(frame,
                        "Enter some info first.");
                    return;
                }
                File sqlFile = new File(target
                    .getSelectedItem()
                    + "sqlmap.xml");
                String statementType = (String) (type
                    .getSelectedItem()
                    .equals("select list") ? "select"
                    : type.getSelectedItem());
                boolean returnList = type.getSelectedItem()
                    .equals("select list");
                String sql = statementArea.getText();
                String sqlMapContents = readFile(sqlFile);
                ClassChoice parameterChoice = (ClassChoice) parameterClass
                    .getSelectedItem();
                ClassChoice resultChoice = (ClassChoice) resultClassBox
                    .getSelectedItem();
                int sqlMapMarkerIndex = sqlMapContents
                    .indexOf("!ADDTOSQL");
                int sqlMapAddIndex = sqlMapContents
                    .indexOf("\n", sqlMapMarkerIndex) + 1;
                String sqlMapToAdd = "\r\n    <"
                    + statementType
                    + " id=\""
                    + statementId.getText()
                    + "\" "
                    + (parameterChoice.getName() == null ? ""
                        : "\r\n         parameterClass=\""
                            + parameterChoice.getType()
                                .getName() + "\" ")
                    + (resultChoice.getName() == null ? ""
                        : "\r\n         resultClass=\""
                            + resultChoice.getType()
                                .getName() + "\" ")
                    + ">\r\n        "
                    + sql.replace("\n", "\n        ")
                    + "\r\n    </" + statementType
                    + ">\r\n";
                sqlMapContents = sqlMapContents.substring(
                    0, sqlMapAddIndex)
                    + sqlMapToAdd
                    + sqlMapContents
                        .substring(sqlMapAddIndex);
                writeFile(sqlMapContents, sqlFile);
                File dataStoreFile = new File(
                    "src/net/sf/opengroove/realmserver/DataStore.java");
                String dataStoreContents = readFile(dataStoreFile);
                int dataStoreMarkerIndex = dataStoreContents
                    .indexOf("!ADDTOSQL");
                int dataStoreAddIndex = dataStoreContents
                    .indexOf("\n", dataStoreMarkerIndex) + 1;
                boolean hasParam = parameterChoice
                    .getName() != null;
                boolean hasResult = resultChoice.getName() != null;
                String paramClass = hasParam ? parameterChoice
                    .getName()
                    : "";
                String resultClass = hasResult ? resultChoice
                    .getName()
                    : "";
                String dataStoreToAdd = "\r\npublic static "
                    + (hasResult ? resultClass
                        + (returnList ? "[]" : "") : "void")
                    + " "
                    + statementId.getText()
                    + "("
                    + (hasParam ? paramClass + " v" : "")
                    + ")throws SQLException{"
                    + (hasResult ? "return (" + resultClass
                        + (returnList ? "[]" : "") + ") "
                        : "")
                    + "get"
                    + sqlFile.getName().substring(0, 1)
                        .toUpperCase()
                    + "dbClient()."
                    + (statementType.equals("select") ? (returnList ? "queryForList"
                        : "queryForObject")
                        : statementType)
                    + "(\""
                    + statementId.getText()
                    + "\""
                    + (hasParam ? ",v" : "")
                    + ")"
                    + (returnList ? ".toArray(new "
                        + resultClass + "[0])" : "")
                    + ";}\r\n";
                dataStoreContents = dataStoreContents
                    .substring(0, dataStoreAddIndex)
                    + dataStoreToAdd
                    + dataStoreContents
                        .substring(dataStoreAddIndex);
                writeFile(dataStoreContents, dataStoreFile);
                JOptionPane.showMessageDialog(frame,
                    "Successful.");
                statementId.setText("");
                parameterClass.setSelectedIndex(0);
                resultClassBox.setSelectedIndex(0);
                target.setSelectedIndex(0);
                type.setSelectedIndex(0);
                statementArea.setText("");
            }
        });
        frame.show();
    }
    
    /**
     * reads the file specified in to a string. the file must not be larger than
     * 5 MB.
     * 
     * @param file.
     * @return
     */
    public static String readFile(File file)
    {
        try
        {
            if (file.length() > (5 * 1000 * 1000))
                throw new RuntimeException(
                    "the file is "
                        + file.length()
                        + " bytes. that is too large. it can't be larger than 5000000 bytes.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            copy(fis, baos);
            fis.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(
                string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(
                file);
            copy(bais, fos);
            bais.close();
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void copy(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
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
