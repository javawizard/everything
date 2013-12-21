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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * A simple binder. This class creates native methods and corresponding java
 * native methods (intended for the SimpleBind class), and code that should be
 * added to the c++ registerNatives method (including method signatures). The
 * binder will only operate on methods with primitive arguments (including char*
 * strings) or return types. It will skip over methods (and issue a warning)
 * that have any other sort of arguments, such as lists.<br/><br/>
 * 
 * This class currently won't handle arrays. If you need support for an array,
 * then probably the best thing to do is to create the method binding without
 * the array argument, and then go in and add support for the array. Similarly,
 * support for pointers (other than char* pointers) is lacking, so a similar
 * solution should be used.<br/><br/>
 * 
 * I'm working on supporting enums. The basic support for them will only allow
 * enums that start with bz_e*****, where ***** is anything as long as the first
 * char is caps. For example, bz_eTeamType would work, but bz_eteamType
 * wouldn't. An enum that corresponds to this one (with bz_e stripped from the
 * beginning) is expected in BzfsAPI. A conversion will be added that simply
 * converts based on ordinals. This causes some problems for enum types that are
 * declared in bzfsAPI.h to start at an index other than 0, so these should be
 * manually edited.
 * 
 * @author Alexander Boyd
 * 
 */
public class JZSimpleBinder
{
    private static final Set<String> alreadyBound =
        new HashSet<String>();
    
    public static abstract class Binder
    {
        /**
         * True if this binder supports input. This means that the binder can
         * bind parameter types.
         */
        private boolean supportsInput;
        /**
         * True if this binder supports output. This means that the binder can
         * bind return types.
         */
        private boolean supportsOutput;
        
        public Binder(boolean supportsInput,
            boolean supportsOutput)
        {
            this.supportsInput = supportsInput;
            this.supportsOutput = supportsOutput;
        }
        
        public boolean supportsInput(String type)
        {
            return supportsInput;
        }
        
        public boolean supportsOutput(String type)
        {
            return supportsOutput;
        }
        
        public abstract boolean canHandleType(String type);
        
        public abstract String javaParamSpec(String type,
            String name);
        
        /**
         * The text that will be included in the native code definition's list
         * of parameters.
         * 
         * @param type
         * @param name
         * @return
         */
        public abstract String nativeParamSpec(String type,
            String name);
        
        /**
         * The text that will be inserted into the actual call into the native
         * library for parameters.
         * 
         * @param type
         * @param name
         * @return
         */
        public abstract String nativeCallSpec(String type,
            String name);
        
        public abstract String javaReturnSpec(String type);
        
        /**
         * The code that should be placed before the native call. This usually
         * would declare a variable to hold the returned value.
         * 
         * @param type
         * @return
         */
        public abstract String nativeReturnSpec(String type);
        
        /**
         * Returns what shoul be the last statement in a native method. It
         * should cause the method to return the value previously stored by
         * nativeReturnSpec.
         * 
         * @param type
         * @return
         */
        public abstract String nativeReturnStatement(
            String type);
        
        /**
         * Returns the type that should be placed in the native method
         * definition as the return type.
         * 
         * @param type
         * @return
         */
        public abstract String nativeReturnType(String type);
        
        public abstract String toNativePrefix(String type,
            String name);
        
        public abstract String toNativeSuffix(String type,
            String name);
        
        public abstract String sigComponent(String type);
        
        public String arrayAssignment(String type,
            String name, String assignmentTarget)
        {
            return "";
        }
    }
    
    private static Binder[] boundTypes = new Binder[] {
    // int (and unsigned int) binder
        new Binder(true, true)
        {
            
            public boolean canHandleType(String type)
            {
                return type.equals("int")
                    || type.equals("unsigned int");
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                return "int " + name;
            }
            
            public String javaReturnSpec(String type)
            {
                return "int";
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                return "jint " + name;
            }
            
            public String nativeReturnSpec(String type)
            {
                return "int returnValue = ";
            }
            
            public String nativeReturnStatement(String type)
            {
                return "return returnValue;";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                return "";
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                return "";
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                return "" + name;
            }
            
            public String sigComponent(String type)
            {
                return "I";
            }
            
            public String nativeReturnType(String type)
            {
                return "jint";
            }
        },
        /*
         * float and double binder. Both are bound to the java float type, since
         * it holds the precision of the c++ double type as well.
         */
        new Binder(true, true)
        {
            
            public boolean canHandleType(String type)
            {
                // TODO Auto-generated method stub
                return type.equals("float")
                    || type.equals("double");
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                return "float " + name;
            }
            
            public String javaReturnSpec(String type)
            {
                return "float";
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                return name;
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                return "jfloat " + name;
            }
            
            public String nativeReturnSpec(String type)
            {
                return "jfloat returnValue = ";
            }
            
            public String nativeReturnStatement(String type)
            {
                return "return returnValue;";
            }
            
            public String nativeReturnType(String type)
            {
                return "jfloat";
            }
            
            public String sigComponent(String type)
            {
                return "F";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                return "";
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                return "";
            }
        },/*
             * String binder (binds const char*, and bz_ApiString in return
             * types)
             */
        new Binder(true, true)
        {
            
            public boolean canHandleType(String type)
            {
                return type.equals("const char*")
                    || type.equals("const char *")
                    || type.equals("unsigned char*")
                    || type.equals("bz_ApiString")
                    || type.equals("const bz_ApiString");
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                return "String " + name;
            }
            
            public String javaReturnSpec(String type)
            {
                return "String";
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                return "jstring " + name;
            }
            
            public String nativeReturnSpec(String type)
            {
                if (!(type.equals("bz_ApiString") || type
                    .equals("const bz_ApiString")))
                    return "const char* returnValue = ";
                else
                    return "bz_ApiString returnValue = ";
            }
            
            public String nativeReturnStatement(String type)
            {
                return "return JNU_NewStringNative(returnValue"
                    + ((type.equals("bz_ApiString") || type
                        .equals("const bz_ApiString")) ? ".c_str()"
                        : "") + ");";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                if (type.equals("bz_ApiString")
                    || type.equals("const bz_ApiString"))
                    throw new RuntimeException();
                return "const char* " + name
                    + "_cs = env->GetStringUTFChars("
                    + name + ", 0);";
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                if (type.equals("bz_ApiString")
                    || type.equals("const bz_ApiString"))
                    throw new RuntimeException();
                return "env->ReleaseStringUTFChars(" + name
                    + ", " + name + "_cs);";
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                if (type.equals("bz_ApiString")
                    || type.equals("const bz_ApiString"))
                    throw new RuntimeException();
                return name + "_cs";
            }
            
            public String sigComponent(String type)
            {
                return "Ljava/lang/String;";
            }
            
            public String nativeReturnType(String type)
            {
                return "jstring";
            }
            
            public boolean supportsInput(String type)
            {
                return type.endsWith("*");
            }
        },
        /*
         * Void binder, won't bind parameters (in particular, won't bind void* )
         */
        new Binder(false, true)
        {
            
            public boolean canHandleType(String type)
            {
                // TODO Auto-generated method stub
                return type.equals("void");
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                throw new RuntimeException();
            }
            
            public String javaReturnSpec(String type)
            {
                return "void";
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                throw new RuntimeException();
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                throw new RuntimeException();
            }
            
            public String nativeReturnSpec(String type)
            {
                return "";
            }
            
            public String nativeReturnStatement(String type)
            {
                return "";
            }
            
            public String sigComponent(String type)
            {
                return "V";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                return "";
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                return "";
            }
            
            public String nativeReturnType(String type)
            {
                return "void";
            }
        },
        /*
         * Boolean binder
         */
        new Binder(true, true)
        {
            
            public boolean canHandleType(String type)
            {
                return type.equals("bool");
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                return "boolean " + name;
            }
            
            public String javaReturnSpec(String type)
            {
                return "boolean";
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                return "" + name;
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                return "jboolean " + name;
            }
            
            public String nativeReturnSpec(String type)
            {
                // TODO Auto-generated method stub
                return "jboolean returnValue = ";
            }
            
            public String nativeReturnStatement(String type)
            {
                return "return returnValue;";
            }
            
            public String sigComponent(String type)
            {
                return "Z";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                return "";
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                return "";
            }
            
            public String nativeReturnType(String type)
            {
                return "jboolean";
            }
        },
        /*
         * Enum binder
         */
        new Binder(true, true)
        {
            private int indexFromType(String type)
            {
                if (!type.startsWith("bz_e"))
                    return -1;
                type = type.substring("bz_e".length());
                return BzfsAPI.getEnumTypeIndex(type);
            }
            
            private String nameFromType(String type)
            {
                if (!type.startsWith("bz_e"))
                    return null;
                type = type.substring("bz_e".length());
                return type;
            }
            
            private String classFromType(String type)
            {
                if (!type.startsWith("bz_e"))
                    return null;
                type = type.substring("bz_e".length());
                return BzfsAPI.getEnumIndexClass(type)
                    .replace(".", "/");
            }
            
            public boolean canHandleType(String type)
            {
                if (!type.startsWith("bz_e"))
                    return false;
                if (indexFromType(type) == -1)
                {
                    System.err.println("Enum type " + type
                        + " is not present in BzfsAPI");
                    return false;
                }
                return true;
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                return nameFromType(type) + " " + name;
            }
            
            public String javaReturnSpec(String type)
            {
                return nameFromType(type);
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                return "(" + type + ") " + name + "_n";
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                return "jobject " + name;
            }
            
            public String nativeReturnSpec(String type)
            {
                return "int returnValueNumber = (int) ";
            }
            
            public String nativeReturnStatement(String type)
            {
                return "return getJavaEnumConstant(env, "
                    + indexFromType(type)
                    + ", returnValueNumber);";
            }
            
            public String nativeReturnType(String type)
            {
                return "jobject";
            }
            
            public String sigComponent(String type)
            {
                return "L" + classFromType(type) + ";";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                return "int " + name
                    + "_n = getJavaEnumInt(env, " + name
                    + ");";
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                return "";
            }
        },
        /*
         * float array (fixed size only; won't handle float*)
         */
        new Binder(true, true)
        {
            private int elementCount(String type)
            {
                return Integer.parseInt(type.substring(type
                    .indexOf("[") + 1, type.indexOf("]")));
            }
            
            public boolean canHandleType(String type)
            {
                return type.matches("float\\[[0-9]*\\]");
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                return "float[] " + name;
            }
            
            public String javaReturnSpec(String type)
            {
                return "float[]";
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                return name + "_f";
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                return "jfloatArray " + name;
            }
            
            public String nativeReturnSpec(String type)
            {
                return "float* returnArray = ";
            }
            
            public String nativeReturnStatement(String type)
            {
                return "jfloatArray returnValue = env->NewFloatArray("
                    + elementCount(type)
                    + ");\nenv->SetFloatArrayRegion(returnValue, 0, "
                    + elementCount(type)
                    + ", returnArray);"
                    + "\nreturn returnValue;";
            }
            
            public String nativeReturnType(String type)
            {
                return "jfloatArray";
            }
            
            public String sigComponent(String type)
            {
                return "[F";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                String code = "";
                for (int i = 0; i < elementCount(type); i++)
                {
                    code +=
                        "\nenv->GetFloatArrayRegion("
                            + name + ", " + i + ", 1, "
                            + name + "_fb);\n" + name
                            + "_f[" + i + "] = (*" + name
                            + "_fb);";
                }
                return "float " + name + "_f["
                    + elementCount(type) + "];\n"
                    + "float* " + name + "_fb = new float;"
                    + code;
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                return "delete " + name + "_fb;";
            }
            
            public String arrayAssignment(String type,
                String name, String assignmentTarget)
            {
                int size = elementCount(type);
                return "env->GetFloatArrayRegion(" + name
                    + ", 0, " + size + ", "
                    + assignmentTarget + ");";
            }
            
        },
        /*
         * bz_APIStringList and bz_APIStringList* binder (return types only)
         */
        new Binder(false, true)
        {
            
            public boolean canHandleType(String type)
            {
                return type.equals("bz_APIStringList*")
                    || type.equals("bz_APIStringList");
            }
            
            public String javaParamSpec(String type,
                String name)
            {
                throw new RuntimeException();
            }
            
            public String javaReturnSpec(String type)
            {
                return "String[]";
            }
            
            public String nativeCallSpec(String type,
                String name)
            {
                throw new RuntimeException();
            }
            
            public String nativeParamSpec(String type,
                String name)
            {
                throw new RuntimeException();
            }
            
            public String nativeReturnSpec(String type)
            {
                return "bz_APIStringList*"
                    + " returnList = "
                    + (type.endsWith("*") ? "" : "&");
            }
            
            public String nativeReturnStatement(String type)
            {
                if (type.endsWith("*"))
                    return "jobjectArray returnArray = stringListToStringArray(env, returnList);\n"
                        + "bz_deleteStringList(returnList);\n"
                        + "return returnArray;";
                else
                    return "return stringListToStringArray(env, returnList);";
            }
            
            public String nativeReturnType(String type)
            {
                return "jobjectArray";
            }
            
            public String sigComponent(String type)
            {
                return "[Ljava/lang/String;";
            }
            
            public String toNativePrefix(String type,
                String name)
            {
                return "";
            }
            
            public String toNativeSuffix(String type,
                String name)
            {
                return "";
            }
        } };
    
    private static final String targetClassName =
        "org/bzflag/jzapi/internal/SimpleBind";
    private static StringWriter dataOutputRegisterNatives =
        new StringWriter();
    private static StringWriter dataOutputJavaMethods =
        new StringWriter();
    private static StringWriter dataOutputNativeMethods =
        new StringWriter();
    private static PrintWriter outputRegisterNatives =
        new PrintWriter(dataOutputRegisterNatives);
    private static PrintWriter outputJavaMethods =
        new PrintWriter(dataOutputJavaMethods);
    private static PrintWriter outputNativeMethods =
        new PrintWriter(dataOutputNativeMethods);
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        int bindingsSuccessful = 0;
        int bindingsFailed = 0;
        File inputFile = new File("bind-input/simple.txt");
        String inputFileContents = readFile(inputFile);
        String[] inputTokenized =
            inputFileContents.split("\n");
        for (String token : inputTokenized)
        {
            token = token.trim();
            if (token.equals("") || token.startsWith("//")
                || token.startsWith("#"))
                continue;
            if (token.startsWith("BZF_API "))
            {
                token =
                    token.substring("BZF_API ".length());
                token = token.trim();
            }
            else
            {
                continue;
            }
            int openParenIndex = token.indexOf("(");
            int closeParenIndex = token.indexOf(")");
            if (openParenIndex == -1
                || closeParenIndex == -1)
                throw new RuntimeException(
                    "missing paren on token " + token);
            String mixedPrefix =
                token.substring(0, openParenIndex);
            String mixedParameters =
                token.substring(openParenIndex + 1,
                    closeParenIndex);
            mixedPrefix = mixedPrefix.trim();
            mixedParameters = mixedParameters.trim();
            int lastPrefixSpace =
                mixedPrefix.lastIndexOf(" ");
            String[] mixedParameterList =
                mixedParameters.split(",");
            String returnType = "void";
            String functionName = null;
            if (lastPrefixSpace != -1)
            {
                returnType =
                    mixedPrefix.substring(0,
                        lastPrefixSpace);
                functionName =
                    mixedPrefix.substring(lastPrefixSpace)
                        .trim();
            }
            else
            {
                functionName = mixedPrefix.trim();
                if (functionName.equals(""))
                    throw new RuntimeException(token);
            }
            returnType = returnType.trim();
            if (functionName.startsWith("*"))
            {
                functionName = functionName.substring(1);
                returnType += "*";
            }
            String[] parameterTypes;
            String[] parameterNames;
            if (mixedParameterList[0].equals("")
                || mixedParameterList[0].trim().equals(
                    "void"))
            {
                parameterTypes = new String[0];
                parameterNames = new String[0];
            }
            else
            {
                parameterTypes =
                    new String[mixedParameterList.length];
                parameterNames =
                    new String[mixedParameterList.length];
            }
            boolean hasInvalidType = false;
            for (int i = 0; i < parameterTypes.length; i++)
            {
                String parameterSpec =
                    mixedParameterList[i];
                parameterSpec = parameterSpec.trim();
                int lastSpaceIndex =
                    parameterSpec.lastIndexOf(" ");
                if (lastSpaceIndex == -1)
                    throw new RuntimeException(
                        "Parameter without a name on token "
                            + token);
                parameterTypes[i] =
                    parameterSpec.substring(0,
                        lastSpaceIndex).trim();
                parameterNames[i] =
                    parameterSpec
                        .substring(lastSpaceIndex + 1);
                if (parameterNames[i].startsWith("*"))
                {
                    parameterNames[i] =
                        parameterNames[i].substring(1);
                    parameterTypes[i] += "*";
                }
                if (parameterNames[i].endsWith("]"))
                {
                    parameterTypes[i] +=
                        parameterNames[i]
                            .substring(parameterNames[i]
                                .indexOf("["));
                    parameterNames[i] =
                        parameterNames[i].substring(0,
                            parameterNames[i].indexOf("["));
                }
                if (!isBound(parameterTypes[i]))
                {
                    // System.out
                    // .println("breaking on param of type \""
                    // + parameterTypes[i]
                    // + "\" with name \""
                    // + parameterNames[i] + "\"");
                    hasInvalidType = true;
                }
            }
            if (!isBound(returnType))
            {
                hasInvalidType = true;
            }
            if (hasInvalidType)
            {
                System.out
                    .println("disallowed type in token "
                        + token + ", skipping this token");
                bindingsFailed++;
                continue;
            }
            String tokenSuffix =
                token.substring(closeParenIndex + 1);
            tokenSuffix = tokenSuffix.trim();
            if (tokenSuffix.endsWith(";"))
            {
                tokenSuffix =
                    tokenSuffix.substring(0, tokenSuffix
                        .length() - 1);
                tokenSuffix = tokenSuffix.trim();
            }
            Properties props = new Properties();
            String[] propTokens = tokenSuffix.split("\\;");
            for (String propToken : propTokens)
            {
                if (propToken.trim().equals(""))
                    continue;
                int equalsIndex = propToken.indexOf("=");
                if (equalsIndex == -1)
                    throw new RuntimeException(token);
                props.setProperty(propToken.substring(0,
                    equalsIndex), propToken
                    .substring(equalsIndex + 1));
            }
            processMethod(functionName, returnType,
                parameterTypes, parameterNames, props);
            bindingsSuccessful++;
        }
        System.out.println("output java code: \n\n"
            + dataOutputJavaMethods + "\n\n");
        System.out.println("output native code: \n\n"
            + dataOutputNativeMethods + "\n\n");
        System.out.println("output native bindings: \n\n"
            + dataOutputRegisterNatives);
        System.out.println();
        System.out.println();
        System.out
            .println("Methods successfully bound:                "
                + bindingsSuccessful);
        System.out
            .println("Methods that failed binding:               "
                + bindingsFailed);
    }
    
    private static void processMethod(String functionName,
        String returnType, String[] paramTypes,
        String[] paramNames, Properties props)
    {
        /*
         * The first thing we need to do is get a list of binders for each
         * parameter, and get the binder for the return type. We'll get a binder
         * even for the void return type, since there is a void binder for just
         * such a case.
         */
        Binder[] paramBinders =
            new Binder[paramTypes.length];
        Binder returnBinder = getBinder(returnType);
        for (int i = 0; i < paramBinders.length; i++)
        {
            paramBinders[i] = getBinder(paramTypes[i]);
        }
        /*
         * Now we'll create the native registration text line.
         */
        outputRegisterNatives
            .print("\tregisterNative(simpleBindClass,\""
                + functionName + "\",\"(");
        for (int i = 0; i < paramBinders.length; i++)
        {
            outputRegisterNatives.print(paramBinders[i]
                .sigComponent(paramTypes[i]));
        }
        outputRegisterNatives.print(")"
            + returnBinder.sigComponent(returnType)
            + "\",simplebind_" + functionName + ");");
        outputRegisterNatives.println();
        /*
         * Next, we'll create the java native methods. We don't need to worry
         * about indentation in these, since Eclipse will format the code for us
         * when it's added to the java file.
         */
        outputJavaMethods.print("public static native "
            + returnBinder.javaReturnSpec(returnType) + " "
            + functionName + "(");
        for (int i = 0; i < paramTypes.length; i++)
        {
            if (i != 0)
                outputJavaMethods.print(",");
            outputJavaMethods
                .print(paramBinders[i].javaParamSpec(
                    paramTypes[i], paramNames[i]));
        }
        outputJavaMethods.println(");");
        /*
         * The java native methods are done. Now we'll do the actual native
         * code.
         */
        if (props.getProperty("comment") != null)
        {
            outputNativeMethods.println("// "
                + props.getProperty("comment"));
        }
        outputNativeMethods.print(""
            + returnBinder.nativeReturnType(returnType)
            + " JNICALL simplebind_" + functionName
            + "(JNIEnv *env, jobject self");
        for (int i = 0; i < paramTypes.length; i++)
        {
            outputNativeMethods.print(", "
                + paramBinders[i].nativeParamSpec(
                    paramTypes[i], paramNames[i]));
        }
        outputNativeMethods.println(")");
        outputNativeMethods.println("{");
        for (int i = 0; i < paramTypes.length; i++)
        {
            String paramPrefix =
                paramBinders[i].toNativePrefix(
                    paramTypes[i], paramNames[i]);
            if (paramPrefix.trim().equals(""))
                continue;
            paramPrefix =
                paramPrefix.replace("\r\n", "\n").replace(
                    "\n", "\n\t");
            outputNativeMethods.print("\t");
            outputNativeMethods.println(paramPrefix);
        }
        outputNativeMethods.print("\t"
            + returnBinder.nativeReturnSpec(returnType)
            + " " + functionName + "(");
        for (int i = 0; i < paramTypes.length; i++)
        {
            if (i != 0)
                outputNativeMethods.print(", ");
            outputNativeMethods.print(paramBinders[i]
                .nativeCallSpec(paramTypes[i],
                    paramNames[i]));
        }
        outputNativeMethods.println(");");
        for (int i = 0; i < paramTypes.length; i++)
        {
            String paramSuffix =
                paramBinders[i].toNativeSuffix(
                    paramTypes[i], paramNames[i]);
            if (paramSuffix.trim().equals(""))
                continue;
            paramSuffix =
                paramSuffix.replace("\r\n", "\n").replace(
                    "\n", "\n\t");
            outputNativeMethods.print("\t");
            outputNativeMethods.println(paramSuffix);
        }
        String outputReturnStatement =
            returnBinder.nativeReturnStatement(returnType);
        if (!outputReturnStatement.trim().equals(""))
            outputNativeMethods.println("\t"
                + outputReturnStatement);
        outputNativeMethods.println("}");
        outputNativeMethods.println();
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
    
    public static int memberIndex(String[] strings,
        String string)
    {
        for (int i = 0; i < strings.length; i++)
        {
            if (strings[i].equals(string))
                return i;
        }
        return -1;
    }
    
    public static boolean isBound(String type)
    {
        return getBoundTypeIndex(type) != -1;
    }
    
    public static int getBoundTypeIndex(String type)
    {
        for (int i = 0; i < boundTypes.length; i++)
        {
            if (boundTypes[i].canHandleType(type))
                return i;
        }
        return -1;
    }
    
    public static Binder getBinder(String type)
    {
        if (!isBound(type))
            return null;
        return boundTypes[getBoundTypeIndex(type)];
    }
}
