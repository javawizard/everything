#include "bzfsAPI.h"
#include "plugin_utils.h"
#include "jni.h"
#include <stdlib.h>
#include <map>
#include <iostream>
#include <string>
#include <algorithm>
#include <vector>
#include <stdio.h>
#include <tchar.h>
#include <windows.h>
#include <winbase.h>

__int64 getPointer(JNIEnv *env, jobject data);
jstring JNU_NewStringNative(const char *str);
jobject getJavaEnumConstant(JNIEnv *env, jint enumId, jint constantIndex);
jint getJavaEnumInt(JNIEnv *env, jobject enumObject);
void throwRuntimeException(JNIEnv *env, const char *message);
jobjectArray stringListToStringArray(JNIEnv *env, bz_APIStringList* stringList);