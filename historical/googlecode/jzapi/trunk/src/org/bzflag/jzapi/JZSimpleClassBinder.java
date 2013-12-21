package org.bzflag.jzapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.bzflag.jzapi.JZSimpleBinder.Binder;

/**
 * A program that generates bindings for the fields of a c++ class, using the
 * binding types in JZSimpleBinder. It creates native getters for all methods
 * who's type supports output, and native setters for all methods who's type
 * supports input. It issues notes on which types were bound (and which, of
 * getters and setters, were bound), and which types were skipped.
 * 
 * @author Alexander Boyd
 * 
 */
public class JZSimpleClassBinder
{
    
    private static String targetClassName;
    private static String targetNativeClass;
    private static StringWriter dataOutputRegisterNatives =
        new StringWriter();
    private static StringWriter dataOutputJavaMethods =
        new StringWriter();
    private static StringWriter dataOutputNativeMethods =
        new StringWriter();
    private static StringWriter dataOutputNativeHeaders =
        new StringWriter();
    private static PrintWriter outputRegisterNatives =
        new PrintWriter(dataOutputRegisterNatives);
    private static PrintWriter outputJavaMethods =
        new PrintWriter(dataOutputJavaMethods);
    private static PrintWriter outputNativeMethods =
        new PrintWriter(dataOutputNativeMethods);
    private static PrintWriter outputNativeHeaders =
        new PrintWriter(dataOutputNativeHeaders);
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        File inputFile =
            new File(
                "bind-input/classbinder-playerupdatestate.txt");
        String inputFileContents = readFile(inputFile);
        String[] inputTokenized =
            inputFileContents.split("\n", 3);
        targetClassName = inputTokenized[0].trim();
        targetNativeClass = inputTokenized[1].trim();
        String remainingInput = inputTokenized[2];
        String[] remainingTokens =
            remainingInput.split("\n");
        String nativePrefix =
            targetClassName.substring(targetClassName
                .lastIndexOf("/") + 1);
        for (String token : remainingTokens)
        {
            token = token.trim();
            if (token.endsWith(";"))
                token =
                    token.substring(0, token.length() - 1);
            if (token.equals("") || token.startsWith("//")
                || token.startsWith("#"))
                continue;
            String[] subtokens = token.split("\\ ", 2);
            if (subtokens.length < 2)
                throw new RuntimeException(token);
            String type = subtokens[0];
            String name = subtokens[1];
            type = type.trim();
            name = name.trim();
            /*
             * Resolve pointers with * being on the name instead of type,
             * resolve arrays that are in the same manner,
             */
            if (name.startsWith("*"))
            {
                name = name.substring(1);
                type += "*";
            }
            if (name.endsWith("]"))
            {
                int oi = name.indexOf("[");
                type += name.substring(oi);
                name = name.substring(0, oi);
                type = type.trim();
                name = name.trim();
            }
            boolean isArrayType = type.endsWith("]");
            if (!JZSimpleBinder.isBound(type))
            {
                System.out
                    .println("Skipping token on invalid type: "
                        + token);
                continue;
            }
            String nameCap =
                name.substring(0, 1).toUpperCase()
                    + name.substring(1);
            Binder binder = JZSimpleBinder.getBinder(type);
            boolean addGetter = binder.supportsOutput(type);
            boolean addSetter = binder.supportsInput(type);
            if (addGetter)
            {
                outputJavaMethods.println("public native "
                    + binder.javaReturnSpec(type) + " get"
                    + nameCap + "();");
                outputRegisterNatives
                    .println("\tregisterNative(\""
                        + targetClassName + "\", \"get"
                        + nameCap + "\", \"()"
                        + binder.sigComponent(type)
                        + "\", cb_" + nativePrefix + "_get"
                        + nameCap + ");");
                outputNativeHeaders.println(""
                    + binder.nativeReturnType(type)
                    + " JNICALL cb_" + nativePrefix
                    + "_get" + nameCap
                    + "(JNIEnv *env, jobject self);");
                outputNativeMethods.println(""
                    + binder.nativeReturnType(type)
                    + " JNICALL cb_" + nativePrefix
                    + "_get" + nameCap
                    + "(JNIEnv *env, jobject self)");
                outputNativeMethods.println("{");
                outputNativeMethods.println("\t"
                    + targetNativeClass
                    + "* nativeSelf = reinterpret_cast<"
                    + targetNativeClass
                    + "*> (getPointer(env, self));");
                outputNativeMethods.println("\t"
                    + binder.nativeReturnSpec(type)
                    + " nativeSelf->" + name + ";");
                outputNativeMethods.println("\t"
                    + binder.nativeReturnStatement(type));
                outputNativeMethods.println("}");
                outputNativeMethods.println();
            }
            else
            {
                System.out
                    .println("Skipping getter on lack of output support: "
                        + token);
            }
            if (addSetter)
            {
                outputJavaMethods
                    .println("public native void set"
                        + nameCap
                        + "("
                        + binder
                            .javaParamSpec(type, "name")
                        + ");");
                outputRegisterNatives
                    .println("\tregisterNative(\""
                        + targetClassName + "\", \"set"
                        + nameCap + "\", \"("
                        + binder.sigComponent(type) + ")V"
                        + "\", cb_" + nativePrefix + "_set"
                        + nameCap + ");");
                outputNativeHeaders.println("void"
                    + " JNICALL cb_" + nativePrefix
                    + "_set" + nameCap
                    + "(JNIEnv *env, jobject self, "
                    + binder.nativeParamSpec(type, name)
                    + ");");
                outputNativeMethods.println("void"
                    + " JNICALL cb_" + nativePrefix
                    + "_set" + nameCap
                    + "(JNIEnv *env, jobject self, "
                    + binder.nativeParamSpec(type, name)
                    + ")");
                outputNativeMethods.println("{");
                outputNativeMethods.println("\t"
                    + targetNativeClass
                    + "* nativeSelf = reinterpret_cast<"
                    + targetNativeClass
                    + "*> (getPointer(env, self));");
                String pre =
                    binder.toNativePrefix(type, name);
                if (isArrayType)
                {
                    outputNativeMethods.println("\t"
                        + binder.arrayAssignment(type,
                            name, "nativeSelf->" + name));
                }
                else
                {
                    if (!pre.equals(""))
                        outputNativeMethods.println(pre);
                    outputNativeMethods
                        .println("\tnativeSelf->"
                            + name
                            + " = "
                            + binder.nativeCallSpec(type,
                                name) + ";");
                    String post =
                        binder.toNativeSuffix(type, name);
                    if (!post.equals(""))
                        outputNativeMethods.println(post);
                }
                outputNativeMethods.println("}");
                outputNativeMethods.println();
            }
            else
            {
                System.out
                    .println("Skipping setter on lack of input support: "
                        + token);
            }
        }
        System.out.println("output java code: \n\n"
            + dataOutputJavaMethods + "\n\n");
        System.out.println("output native headers: \n\n"
            + dataOutputNativeHeaders + "\n\n");
        System.out.println("output native code: \n\n"
            + dataOutputNativeMethods + "\n\n");
        System.out.println("output native bindings: \n\n"
            + dataOutputRegisterNatives);
    }
    
    public static String readFile(File file)
    {
        try
        {
            ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
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
    
    /**
     * Writes the string specified to the file specified.
     * 
     * @param string
     *            A string to write
     * @param file
     *            The file to write <code>string</code> to
     */
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais =
                new ByteArrayInputStream(string
                    .getBytes("UTF-8"));
            FileOutputStream fos =
                new FileOutputStream(file);
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
    
    /**
     * Copies the contents of one stream to another. Bytes from the source
     * stream are read until it is empty, and written to the destination stream.
     * Neither the source nor the destination streams are flushed or closed.
     * 
     * @param in
     *            The source stream
     * @param out
     *            The destination stream
     * @throws IOException
     *             if an I/O error occurs
     */
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
    
}
