package devutils;

import javax.swing.JFrame;

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
    public static void main(String[] args)
    {
        JFrame frame = new JFrame(
            "AddToSql - OpenGroove Realm Server");
        frame.setSize(350, 550);
        frame.setLocationRelativeTo(null);
        frame.show();
    }
    
}
