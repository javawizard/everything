package com.brightpages.tools.gwt;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

/**
 * Create Async interface for GWT RPC
 * 
 * @author samyem@brightdata.ca
 * 
 */
public class AsyncCreator
{
    private static final Logger log = Logger.getLogger("AsyncCreator");
    
    // remove all return type's gwt type args
    private static final Pattern returnTypeArg = Pattern
            .compile("@gwt.typeArgs\\s*<.*>");
    
    public AsyncCreator()
    {
        super();
    }
    
    /**
     * Command line invocation. Pass the package folder location
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length < 1)
        {
            log
                    .log(
                            Level.WARNING,
                            "No arguments were passed. Pass the location of the package containing the RPC interfaces.");
            System.exit(-1);
        }
        log.info("Generating Asyncs for " + args[0]);
        AsyncCreator asyncCreator = new AsyncCreator();
        asyncCreator.generateAsyncForPackage(new File(args[0]));
    }
    
    /**
     * Given a package location, creates the async versions on the file system
     * 
     * @param file
     * @throws Exception
     */
    public void generateAsyncForPackage(File packageFolder) throws Exception
    {
        if (!packageFolder.isDirectory())
        {
            throw new Exception(packageFolder.getAbsolutePath()
                    + " is not a valid package folder.");
        }
        for (File file : packageFolder.listFiles())
        {
            if (file.isFile() && file.getName().endsWith(".java")
                    && !file.getName().endsWith("Async.java"))
            {
                String asyncVersion = createAsync(file);
                String nonAsyncPath = file.getAbsolutePath();
                File asyncFile = new File(nonAsyncPath.substring(0,
                        nonAsyncPath.length() - ".java".length())
                        + "Async.java");
                if (asyncFile.isFile())
                {
                    asyncFile.delete();
                }
                FileWriter asyncFileWriter = new FileWriter(asyncFile);
                asyncFileWriter.write(asyncVersion);
                asyncFileWriter.close();
                log.info(asyncFile.getAbsolutePath() + " created");
            }
        }
    }
    
    /**
     * Get the async version for the given RPC interface
     * 
     * @param args
     */
    public String createAsync(File file) throws Exception
    {
        StringBuilder asyncVersion = new StringBuilder();
        
        JavaCompiler compiler = javax.tools.ToolProvider
                .getSystemJavaCompiler();
        StandardJavaFileManager standardFileManager;
        if (compiler == null)
        {
            log
                    .log(
                            Level.SEVERE,
                            "No system java compiler available. You may be using JRE. This tool only works with JDK 1.6.");
            System.exit(-1);
        }
        
        standardFileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> java = standardFileManager
                .getJavaFileObjectsFromFiles(Arrays.asList(file));
        
        CompilationTask task = compiler.getTask(null, standardFileManager,
                null, null, null, java);
        JavacTask javacTask = (JavacTask) task;
        Iterable<? extends CompilationUnitTree> parseTree = javacTask.parse();
        
        // Get the class
        for (CompilationUnitTree unit : parseTree)
        {
            // package
            asyncVersion.append("package " + unit.getPackageName().toString()
                    + ";\n\n");
            
            // imports
            for (ImportTree importTree : unit.getImports())
            {
                asyncVersion.append(importTree.toString() + "\n");
            }
            // import async callback
            asyncVersion
                    .append("import com.google.gwt.user.client.rpc.AsyncCallback;\n");
            
            JCCompilationUnit jcUnit = (JCCompilationUnit) unit;
            for (Tree t : unit.getTypeDecls())
            {
                if (t instanceof ClassTree)
                {
                    ClassTree classTree = (ClassTree) t;
                    
                    // class level comment block
                    asyncVersion
                            .append("\n/**\n* Asynchronous interface for "
                                    + classTree.getSimpleName()
                                    + " generated by BrightPages AsyncCreator, modified by Alexander Boyd\n*/\n");
                    
                    // class declaration
                    Tree extendsClause = classTree.getExtendsClause();
                    String extendsStr = "";
                    if (extendsClause != null)
                    {
                        extendsStr = " extends " + extendsClause.toString();
                    }
                    asyncVersion.append("public interface "
                            + classTree.getSimpleName() + "Async " + extendsStr
                            + " { \n\n");
                    
                    for (Tree member : classTree.getMembers())
                    {
                        if (member.getKind().equals(Kind.METHOD))
                        {
                            MethodTree method = (MethodTree) member;
                            String methodReturnString = method.getReturnType()
                                    .toString();
                            if ("|boolean|byte|char|double|float|int|long|short|void|"
                                    .contains("|" + methodReturnString + "|"))
                                methodReturnString = methodReturnString
                                        .substring(0, 1).toUpperCase()
                                        + methodReturnString.substring(1);
                            String comments = jcUnit.docComments.get(method);
                            if (comments != null)
                            {
                                // replace gwt type args for return type
                                comments = returnTypeArg.matcher(comments)
                                        .replaceAll("");
                                asyncVersion.append("/**\n*"
                                        + comments.replaceAll("\n", "\n*")
                                        + "/\n");
                            }
                            
                            // always return void for async
                            asyncVersion.append("public void "
                                    + method.getName());
                            asyncVersion.append("(");
                            for (VariableTree param : method.getParameters())
                            {
                                asyncVersion.append(param.getType().toString()
                                        + " " + param.getName() + ", ");
                            }
                            String throwsExpr = "";
                            boolean hasthrows = false;
                            //Async methods don't themselves throw exceptions; this is therefore commented out
                            /*
                            for (ExpressionTree tree : method.getThrows())
                            {
                                if (!hasthrows)
                                {
                                    throwsExpr = " throws";
                                    hasthrows = true;
                                }
                                throwsExpr += " " + tree.toString();
                            }*/
                            // add async method
                            asyncVersion.append("AsyncCallback<"
                                    + methodReturnString + "> callback)"
                                    + throwsExpr + ";\n\n");
                        }
                        else
                        {
                            asyncVersion.append(t.toString() + "\n");
                        }
                    }
                    asyncVersion.append("}\n");
                }
                else
                {
                    asyncVersion.append(t.toString() + "\n");
                }
            }
        }
        
        standardFileManager.close();
        return asyncVersion.toString();
    }
}
