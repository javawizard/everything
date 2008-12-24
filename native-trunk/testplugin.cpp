// testplugin.cpp : Defines the entry point for the DLL application.
//

#include "testplugin.h"
#include "bind.h"

using namespace std;

#define JVM_RUNTIME_DLL _T("C:\\Program Files\\Java\\jre1.6.0_05\\bin\\client\\jvm.dll")
#define CLASS_PATH "-Djava.class.path=."
#define EnumEventType 0
#define EnumTeamType 1
#define EnumGameType 2
#define EnumShotType 3
#define EnumFlagQuality 4
#define EnumPlayerStatus 5
#define EnumWorldObjectType 6
#define EnumSolidWorldObjectType 7

BZ_GET_PLUGIN_VERSION

JavaVM *vm;
JNIEnv *env;
jclass Class_java_lang_String;
jmethodID MID_String_init;

void describeIfException(JNIEnv *env)
{
	 if(env->ExceptionCheck())
	 {
		 env->ExceptionDescribe();
		 env->ExceptionClear();
	 }
}

 jvalue
 callMethodByName(JNIEnv *env,
                      jobject obj, 
                      const char *name,
                      const char *descriptor, ...)
 {
     va_list args;
     jclass clazz;
     jmethodID mid;
     jvalue result;
     if (env->EnsureLocalCapacity(2) == JNI_OK) {
         clazz = env->GetObjectClass(obj);
         mid = env->GetMethodID(clazz, name,
                                   descriptor);
         if (mid) {
             const char *p = descriptor;
             /* skip over argument types to find out the 
                return type */
             while (*p != ')') p++;
             /* skip ')' */
             p++;
             va_start(args, descriptor);
             switch (*p) {
             case 'V':
                 env->CallVoidMethodV(obj, mid, args);
				 result.l = 0;
                 break;
             case '[':
             case 'L':
                 result.l = env->CallObjectMethodV(
                                        obj, mid, args);
                 break;
             case 'Z':
                 result.z = env->CallBooleanMethodV(
                                        obj, mid, args);
                 break;
             case 'B':
                 result.b = env->CallByteMethodV(
                                        obj, mid, args);
                 break;
             case 'C':
                 result.c = env->CallCharMethodV(
                                        obj, mid, args);
                 break;
             case 'S':
                 result.s = env->CallShortMethodV(
                                        obj, mid, args);
                 break;
             case 'I':
                 result.i = env->CallIntMethodV(
                                        obj, mid, args);
                 break;
             case 'J':
                 result.j = env->CallLongMethodV(
                                        obj, mid, args);
                 break;
             case 'F':
                 result.f = env->CallFloatMethodV(
                                        obj, mid, args);
                 break;
             case 'D':
                 result.d = env->CallDoubleMethodV(
                                        obj, mid, args);
                 break;
             default:
				 {
					 cout << "illegal descriptor with name " << name << " and descriptor " << descriptor << endl;
					 result.z = false;
					 result.l = 0;
					 describeIfException(env);
				 }
             }
             va_end(args);
         }
		 else
		 {
			 result.z = false;
			 result.l = 0;
			 cout << "illegal instance descriptor (mid = null) with name " << name << " and descriptor " << descriptor << endl;
			 describeIfException(env);
		 }
         env->DeleteLocalRef(clazz);
     }
     return result;
 }

jvalue
 callStaticMethodByName(JNIEnv *env,
                      const char *className,
                      const char *name,
                      const char *descriptor, ...)
 {
     va_list args;
     jclass clazz;
     jmethodID mid;
     jvalue result;
     if (env->EnsureLocalCapacity(2) == JNI_OK) {
         clazz = env->FindClass(className);
		 if(clazz == 0)
		 {
			 cout << "illegal descriptor with name " << name << " and descriptor " << descriptor << endl;
			 result.z=false;
			 result.l=0;
			 describeIfException(env);
			 return result;
		 }
		 mid = env->GetStaticMethodID(clazz, name,
                                   descriptor);
         if (mid) {
             const char *p = descriptor;
             /* skip over argument types to find out the 
                return type */
             while (*p != ')') p++;
             /* skip ')' */
             p++;
             va_start(args, descriptor);
             switch (*p) {
             case 'V':
                 env->CallStaticVoidMethodV(clazz, mid, args);
				 result.z = false;
                 break;
             case '[':
             case 'L':
                 result.l = env->CallStaticObjectMethodV(
                                        clazz, mid, args);
                 break;
             case 'Z':
                 result.z = env->CallStaticBooleanMethodV(
                                        clazz, mid, args);
                 break;
             case 'B':
                 result.b = env->CallStaticByteMethodV(
                                        clazz, mid, args);
                 break;
             case 'C':
                 result.c = env->CallStaticCharMethodV(
                                        clazz, mid, args);
                 break;
             case 'S':
                 result.s = env->CallStaticShortMethodV(
                                        clazz, mid, args);
                 break;
             case 'I':
                 result.i = env->CallStaticIntMethodV(
                                        clazz, mid, args);
                 break;
             case 'J':
                 result.j = env->CallStaticLongMethodV(
                                        clazz, mid, args);
                 break;
             case 'F':
                 result.f = env->CallStaticFloatMethodV(
                                        clazz, mid, args);
                 break;
             case 'D':
                 result.d = env->CallStaticDoubleMethodV(
                                        clazz, mid, args);
                 break;
             default:
				 {
					 result.z=false;
					 result.l=0;
					 cout << "illegal descriptor with name " << name << " and descriptor " << descriptor << endl;
					 describeIfException(env);
				 }
             }
             va_end(args);
         }
		 else
		 {
			 result.z = false;
			 result.l = 0;
			 cout << "illegal static descriptor (mid = null) with name " << name << " and descriptor " << descriptor << " and classname " << className << endl;
			 describeIfException(env);
		 }
         env->DeleteLocalRef(clazz);
     }
     return result;
 }

jclass bzfsLoaderClass;
jmethodID throwRuntimeExceptionMethodId;
jmethodID loadPluginMethodId;
jmethodID unloadPluginsMethodId;
jclass bzfsEventClass;
jfieldID bzfsEventUidFieldId;
jmethodID bzfsEventInitMethod;
jclass pointedClassId;
jfieldID pointerFieldId;
jmethodID getEventForTypeMethodId;

bool loadUtils()
{
	Class_java_lang_String = env->FindClass("java/lang/String");
	if(Class_java_lang_String == 0)
	{
		cout << "Failed loading java.lang.String" << endl;
		return false;
	}
	MID_String_init = env->GetMethodID(Class_java_lang_String,"<init>","([B)V");
	if(MID_String_init == 0)
	{
		cout << "Failed loading String.<init>" << endl;
		return false;
	}
	bzfsLoaderClass = env->FindClass("org/bzflag/jzapi/BzfsLoader");
	if(bzfsLoaderClass == 0)
	{
		cout << "Failed loading bzfsloader.class" << endl;
		return false;
	}
	throwRuntimeExceptionMethodId = env->GetStaticMethodID(bzfsLoaderClass,"throwRuntimeException","(Ljava/lang/String;)V");
	if(throwRuntimeExceptionMethodId == 0)
	{
		cout << "Failed loading bzfsloader.throwRuntimeException" << endl;
		return false;
	}
	loadPluginMethodId = env->GetStaticMethodID(bzfsLoaderClass,"loadPlugin","(Ljava/lang/String;Ljava/lang/String;)Z");
	if(loadPluginMethodId == 0)
	{
		cout << "Failed loading bzfsloader.loadPlugin" << endl;
		return false;
	}
	getEventForTypeMethodId = env->GetStaticMethodID(bzfsLoaderClass,"getEventForType","(I)Lorg/bzflag/jzapi/BzfsEvent;");
	if(getEventForTypeMethodId == 0)
	{
		cout << "Failed loading bzfsloader.getEventForType" << endl;
		return false;
	}
	unloadPluginsMethodId = env->GetStaticMethodID(bzfsLoaderClass,"unloadPlugins","()V");
	if(unloadPluginsMethodId == 0)
	{
		cout << "Failed loading bzfsloader.unloadPlugins" << endl;
		return false;
	}
	bzfsEventClass = env->FindClass("org/bzflag/jzapi/BzfsEvent");
	if(bzfsEventClass == 0)
	{
		cout << "Failed loading bzfsevent.class" << endl;
		return false;
	}
	pointedClassId = env->FindClass("org/bzflag/jzapi/Pointed");
	if(pointedClassId == 0)
	{
		cout << "Failed loading pointed.class" << endl;
		return false;
	}
//	bzfsEventInitMethod = env->GetStaticMethodID(bzfsEventClass,"initEvent","()V");
//	if(bzfsEventInitMethod == 0)
//	{
//		cout << "Failed loading bzfsevent.initEvent\n";
//		return false;
//	}
	pointerFieldId = env->GetFieldID(pointedClassId, "pointer", "J");
	if(pointerFieldId == 0)
	{
		cout << "Failed loading pointed.pointer";
		return false;
	}
	return true;
}

jobject constructDefault(const char* className);

jobject constructDefault(JNIEnv *env, const char* className);

__int64 getPointer(JNIEnv *env, jobject data)
{
	return env->GetLongField(data, pointerFieldId);
}

void setPointer(JNIEnv *env, jobject data, __int64 pointer)
{
	env->SetLongField(data, pointerFieldId, pointer);
}

jstring JNU_NewStringNative(const char *str)
 {
     jstring result;
     jbyteArray bytes = 0;
     int len;
     if (env->EnsureLocalCapacity(2) < 0) {
         return NULL; /* out of memory error */
     }
     len = strlen(str);
     bytes = env->NewByteArray(len);
     if (bytes != NULL) {
         env->SetByteArrayRegion(bytes, 0, len,
                                    (jbyte *)str);
         result = (jstring) env->NewObject(Class_java_lang_String,
                                    MID_String_init, bytes);
         env->DeleteLocalRef(bytes);
         return result;
     } /* else fall through */
     return NULL;
 }

jlong getEventUid(JNIEnv *env, jobject jevent)
{
	return env->GetLongField(jevent,bzfsEventUidFieldId);
}

void setupEvent(JNIEnv *env, jobject jevent)
{
	env->CallVoidMethod(jevent,bzfsEventInitMethod);
}

/**
Causes the environment to throw a RuntimeException. A C++ exception is not
actually thrown; only a Java exception will be raised when the java method
that invokes this one returns.
*/
void throwRuntimeException(JNIEnv *env, const char *message)
{
	env->CallStaticVoidMethod(bzfsLoaderClass,throwRuntimeExceptionMethodId,JNU_NewStringNative(message));
}

jobject getJavaEnumConstant(JNIEnv *env, jint enumId, jint constantIndex)
{
	jvalue obj = callStaticMethodByName(env,"org/bzflag/jzapi/BzfsAPI","getEnumConstant",
		"(II)Ljava/lang/Enum;",enumId,constantIndex);
	if(obj.l == 0)
	{
		cout << "Enum reference was null for enumId " << enumId << " and index " << constantIndex << "" << endl;
		return 0;
	}
	return obj.l;
}

jint getJavaEnumInt(JNIEnv *env, jobject enumObject)
{
	jvalue obj = callStaticMethodByName(env,"org/bzflag/jzapi/BzfsAPI","getEnumOrdinal",
		"(Ljava/lang/Enum;)I",enumObject);
	return obj.i;
}

const char* getStringChars(JNIEnv *env, jstring string)
{
	return env->GetStringUTFChars(string,0);
}

void releaseStringChars(JNIEnv *env, jstring string, char* chars)
{
	env->ReleaseStringUTFChars(string, chars);
}

jobjectArray stringListToStringArray(JNIEnv *env, bz_APIStringList* stringList)
{
	jobjectArray stringArray = env->NewObjectArray(stringList->size(),Class_java_lang_String,0);
	for(int i = 0; i < stringList->size(); i++)
	{
		env->SetObjectArrayElement(stringArray,i,JNU_NewStringNative(stringList->get(i).c_str()));
	}
	return stringArray;
}

jintArray intListToIntArray(JNIEnv *env, bz_APIIntList *list)
{
	jintArray intArray = env->NewIntArray(list->size());
	for(int i = 0; i < list->size(); i++)
	{
		jint newInt[1];
		newInt[0] = list->get(i);
		env->SetIntArrayRegion(intArray,i,1,newInt);
	}
	return intArray;
}

class cJavaEventListener : public bz_EventHandler
{
public:
  cJavaEventListener(jobject ref) : javaListenerRef(ref)
  {
	  javaListenerClass = env->GetObjectClass(ref);
	  javaListenerMethodID = env->GetMethodID(javaListenerClass,"process","(Lorg/bzflag/jzapi/BzfsEvent;)V");
	  /*
	  javaListenerMethodID won't be null, since listeners are 
	  forced to implement the interface that the method is declared on
	  */
  }
  virtual ~cJavaEventListener()
  {
  }

  void process ( bz_EventData *eventData )
  {
	jobject jEventData = env->CallStaticObjectMethod(bzfsLoaderClass, 
		getEventForTypeMethodId, (int) eventData->eventType);
	if(jEventData == 0)
	{
		describeIfException(env);
		cout << "Constructed event data was empty" << endl;
		return;
	}
	jlong eventDataPointer = reinterpret_cast<__int64>(eventData);
	setPointer(env, jEventData, eventDataPointer);
	env->CallVoidMethod(this->javaListenerRef,this->javaListenerMethodID,jEventData);
  }

  bool autoDelete ( void ) { return false;} // this will be used for more then one event

  	jobject javaListenerRef;
	jclass javaListenerClass;
	jmethodID javaListenerMethodID;
};

void Tokenize(const string& str,
                      vector<string>& tokens,
                      const string& delimiters = " ")
{
    // Skip delimiters at beginning.
    string::size_type lastPos = str.find_first_not_of(delimiters, 0);
    // Find first "non-delimiter".
    string::size_type pos     = str.find_first_of(delimiters, lastPos);

    while (string::npos != pos || string::npos != lastPos)
    {
        // Found a token, add it to the vector.
        tokens.push_back(str.substr(lastPos, pos - lastPos));
        // Skip delimiters.  Note the "not_of"
        lastPos = str.find_first_not_of(delimiters, pos);
        // Find next "non-delimiter"
        pos = str.find_first_of(delimiters, lastPos);
    }
}

void setArrayLong(JNIEnv *env, jlongArray targetArray, int offset, jlong value)
{
	env->SetLongArrayRegion(targetArray,offset,1,&value);
}

void registerNative(jclass apiClass,char* methodName, char* methodSignature, void* method)
{
	JNINativeMethod nm;
	nm.name = methodName;
    /* method descriptor assigned to signature field */
    nm.signature = methodSignature;
    nm.fnPtr = method;
    env->RegisterNatives(apiClass, &nm, 1);
}

void registerNative(const char* className, char* methodName, char* methodSignature, void* method)
{
	jclass c = env->FindClass(className);
	if(c == 0)
	{
		cout << "Class " << className << " couldn't be found during native registration" << endl;
		return;
	}
	registerNative(c, methodName, methodSignature, method);
}

//Implementations of native methods

jboolean JNICALL j_registerEvent(JNIEnv *env, jobject self, jint eventTypeIndex, jobject eventHandler)
{
	bz_eEventType eventType = bz_eEventType(eventTypeIndex);
	jobject globalEventHandler = env->NewGlobalRef(eventHandler);
	cJavaEventListener *listener = new cJavaEventListener(globalEventHandler);
	setPointer(env, eventHandler, reinterpret_cast<__int64>(listener));
	return bz_registerEvent(eventType,listener);
}

jboolean JNICALL j_removeEvent(JNIEnv *env, jobject self, jint eventTypeIndex, jobject eventHandler)
{
	bz_eEventType eventType = bz_eEventType(eventTypeIndex);
	cJavaEventListener *listener = reinterpret_cast<cJavaEventListener*>
		(getPointer(env, eventHandler));
	if(listener == 0)
	{
		cout << "Warning: listener unregistered that doesn't exist" << endl;
		return false;
	}
	bool deregisterStatus = bz_removeEvent(eventType,listener);
	if(!deregisterStatus)
	{
		cout << "Warning: deregisteration of an event handler failed" << endl;
		return false;
	}
	env->DeleteGlobalRef(listener->javaListenerRef);
	return true;
}

jintArray JNICALL j_getPlayerIndexList(JNIEnv *env, jobject self)
{
	bz_APIIntList *apiList = bz_getPlayerIndexList();
	jintArray result = intListToIntArray(env,apiList);
	bz_deleteIntList(apiList);
	return result;
}

jlongArray JNICALL j_getFunctionPointers(JNIEnv *env, jobject self)
{
	jlongArray functions = env->NewLongArray(1);
	setArrayLong(env,functions,0,(jlong)&bz_getPlayerCount);
	return functions;
}

//j_event_getEventTime

jobject JNICALL j_event_getEventType(JNIEnv *env, jobject self)
{
	bz_EventData* eventData = reinterpret_cast<bz_EventData*> (getPointer(env, self));
	if(eventData == 0)
	{
		throwRuntimeException(env, "null event data, typically means that an incorrect type was passed in");
		return 0;
	}
	return getJavaEnumConstant(env, EnumEventType, (int) eventData->eventType);
}

jdouble JNICALL j_event_getEventTime(JNIEnv *env, jobject self)
{
	bz_EventData* eventData = reinterpret_cast<bz_EventData*> (getPointer(env, self));
	if(eventData == 0)
	{
		throwRuntimeException(env, "null event data, typically means that an incorrect type was passed in");
		return 0;
	}
	return eventData->eventTime;
}

jint JNICALL j_getPlayerId(JNIEnv *env, jobject self, jint playerIndex)
{
	bz_BasePlayerRecord* record = bz_getPlayerByIndex(playerIndex);
	if(record == 0)
		return -1;
	jint result = record->playerID;
	bz_freePlayerRecord(record);
	return result;
} 

jint JNICALL j_getPlayerIdByCallsign(JNIEnv *env, jobject self, jstring callsign)
{
	const char* callsignChars = env->GetStringUTFChars(callsign,0);
	bz_BasePlayerRecord* record = bz_getPlayerByCallsign(callsignChars);
	env->ReleaseStringUTFChars(callsign,callsignChars);
	if(record == 0)
		return -1;
	jint result = record->playerID;
	bz_freePlayerRecord(record);
	return result;
}

jobject JNICALL j_getPlayerRecord(JNIEnv *env, jobject self, jint playerId)
{
	bz_BasePlayerRecord* record = bz_getPlayerByIndex(playerId);
	if(record == 0)
		return 0;
	jobject jRecord = constructDefault(env, "org/bzflag/jzapi/BasePlayerRecord");
	if(jRecord == 0)
	{
		throwRuntimeException(env, "Missing bpr class");
		return 0;
	}
	setPointer(env, jRecord, reinterpret_cast<__int64> (record));
	return jRecord;
}

void JNICALL j_freePlayerRecord(JNIEnv *env, jobject self, jobject playerRecord)
{
	bz_BasePlayerRecord* record = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, playerRecord));
	if(record != 0)
	{
		bz_freePlayerRecord(record);
		setPointer(env, playerRecord, 0);
	}
	else
	{
		throwRuntimeException(env, "Attempting to double-free a player record");
	}
}

//BEGIN hardcoded event methods

jint JNICALL j_event_playerJoinPart_getPlayerID(JNIEnv *env, jobject self)
{
	bz_PlayerJoinPartEventData_V1* eventData = reinterpret_cast<bz_PlayerJoinPartEventData_V1*> 
		(getPointer(env, self));
    if(eventData == 0)
	{
		throwRuntimeException(env, "null data");
		return 0;
	}
	return eventData->playerID;
}

void JNICALL j_event_playerJoinPart_setPlayerID(JNIEnv *env, jobject self, jint newValue)
{
	bz_PlayerJoinPartEventData_V1* eventData = reinterpret_cast<bz_PlayerJoinPartEventData_V1*> 
		(getPointer(env, self));
    if(eventData == 0)
	{
		throwRuntimeException(env, "null data");
		return;
	}
	eventData->playerID = newValue;
}

//END hardcoded event methods

//BEGIN SimpleBind methods
jfloat JNICALL simplebind_bz_getCurrentTime(JNIEnv *env, jobject self)
{
	jfloat returnValue =  bz_getCurrentTime();
	return returnValue;
}

jint JNICALL simplebind_bz_APIVersion(JNIEnv *env, jobject self)
{
	int returnValue =  bz_APIVersion();
	return returnValue;
}

jboolean JNICALL simplebind_bz_disconnectNonPlayerConnection(JNIEnv *env, jobject self, jint connectionID)
{
	jboolean returnValue =  bz_disconnectNonPlayerConnection(connectionID);
	return returnValue;
}

jint JNICALL simplebind_bz_getNonPlayerConnectionOutboundPacketCount(JNIEnv *env, jobject self, jint connectionID)
{
	int returnValue =  bz_getNonPlayerConnectionOutboundPacketCount(connectionID);
	return returnValue;
}

jstring JNICALL simplebind_bz_getNonPlayerConnectionIP(JNIEnv *env, jobject self, jint connectionID)
{
	const char* returnValue =  bz_getNonPlayerConnectionIP(connectionID);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getNonPlayerConnectionHost(JNIEnv *env, jobject self, jint connectionID)
{
	const char* returnValue =  bz_getNonPlayerConnectionHost(connectionID);
	return JNU_NewStringNative(returnValue);
}

jboolean JNICALL simplebind_bz_hasPerm(JNIEnv *env, jobject self, jint playerID, jstring perm)
{
	const char* perm_cs = env->GetStringUTFChars(perm, 0);
	jboolean returnValue =  bz_hasPerm(playerID, perm_cs);
	env->ReleaseStringUTFChars(perm, perm_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_grantPerm(JNIEnv *env, jobject self, jint playerID, jstring perm)
{
	const char* perm_cs = env->GetStringUTFChars(perm, 0);
	jboolean returnValue =  bz_grantPerm(playerID, perm_cs);
	env->ReleaseStringUTFChars(perm, perm_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_revokePerm(JNIEnv *env, jobject self, jint playerID, jstring perm)
{
	const char* perm_cs = env->GetStringUTFChars(perm, 0);
	jboolean returnValue =  bz_revokePerm(playerID, perm_cs);
	env->ReleaseStringUTFChars(perm, perm_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_getAdmin(JNIEnv *env, jobject self, jint playerID)
{
	jboolean returnValue =  bz_getAdmin(playerID);
	return returnValue;
}

jboolean JNICALL simplebind_bz_validAdminPassword(JNIEnv *env, jobject self, jstring passwd)
{
	const char* passwd_cs = env->GetStringUTFChars(passwd, 0);
	jboolean returnValue =  bz_validAdminPassword(passwd_cs);
	env->ReleaseStringUTFChars(passwd, passwd_cs);
	return returnValue;
}

jstring JNICALL simplebind_bz_getPlayerFlag(JNIEnv *env, jobject self, jint playerID)
{
	const char* returnValue =  bz_getPlayerFlag(playerID);
	return JNU_NewStringNative(returnValue);
}

// returnarg second arg to native call is float* to size 3
jfloatArray JNICALL simplebind_bz_getPlayerPosition(JNIEnv *env, jobject self, jint playerID, jboolean extrapolate)
{
	float returnArray[3];
	bz_getPlayerPosition(playerID,returnArray, extrapolate);
	jfloatArray returnValue = env->NewFloatArray(3);
	env->SetFloatArrayRegion(returnValue, 0, 3, returnArray);
	return returnValue;
}

// returnarg second arg to native call is float* to size 3
jfloatArray JNICALL simplebind_bz_getPlayerRotation(JNIEnv *env, jobject self, jint playerID, jboolean extrapolate)
{
	float returnArray[3];
	bz_getPlayerRotation(playerID, returnArray, extrapolate);
	jfloatArray returnValue = env->NewFloatArray(3);
	env->SetFloatArrayRegion(returnValue, 0, 3, returnArray);
	return returnValue;
}

// returnarg second arg to native call is float* to size 3
jfloatArray JNICALL simplebind_bz_getPlayerVelocity(JNIEnv *env, jobject self, jint playerID)
{
	float returnArray[3];
	bz_getPlayerVelocity(playerID,returnArray);
	jfloatArray returnValue = env->NewFloatArray(3);
	env->SetFloatArrayRegion(returnValue, 0, 3, returnArray);
	return returnValue;
}

// returnarg second is pointer to one float
jfloat JNICALL simplebind_bz_getPlayerAngVel(JNIEnv *env, jobject self, jint playerID)
{
	jfloat returnValue;
	bz_getPlayerAngVel(playerID,&returnValue);
	return returnValue;
}

// returnarg second is pointer to one int
jint JNICALL simplebind_bz_getPlayerPhysicsDriver(JNIEnv *env, jobject self, jint playerID)
{
	int returnValue;
	bz_getPlayerPhysicsDriver(playerID, &returnValue);
	return returnValue;
}

jboolean JNICALL simplebind_bz_isPlayerPaused(JNIEnv *env, jobject self, jint playerID)
{
	jboolean returnValue =  bz_isPlayerPaused(playerID);
	return returnValue;
}

jboolean JNICALL simplebind_bz_canPlayerSpawn(JNIEnv *env, jobject self, jint playerID)
{
	jboolean returnValue =  bz_canPlayerSpawn(playerID);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setPlayerSpawnable(JNIEnv *env, jobject self, jint playerID, jboolean spawn)
{
	jboolean returnValue =  bz_setPlayerSpawnable(playerID, spawn);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setPlayerLimboMessage(JNIEnv *env, jobject self, jint playerID, jstring text)
{
	const char* text_cs = env->GetStringUTFChars(text, 0);
	jboolean returnValue =  bz_setPlayerLimboMessage(playerID, text_cs);
	env->ReleaseStringUTFChars(text, text_cs);
	return returnValue;
}

jobject JNICALL simplebind_bz_getPlayerTeam(JNIEnv *env, jobject self, jint playerID)
{
	int returnValueNumber = (int)  bz_getPlayerTeam(playerID);
	return getJavaEnumConstant(env, 1, returnValueNumber);
}

jstring JNICALL simplebind_bz_getPlayerCallsign(JNIEnv *env, jobject self, jint playerID)
{
	const char* returnValue =  bz_getPlayerCallsign(playerID);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getPlayerIPAddress(JNIEnv *env, jobject self, jint playerID)
{
	const char* returnValue =  bz_getPlayerIPAddress(playerID);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getPlayerReferrer(JNIEnv *env, jobject self, jint playerID)
{
	const char* returnValue =  bz_getPlayerReferrer(playerID);
	return JNU_NewStringNative(returnValue);
}

jboolean JNICALL simplebind_bz_setPayerCustomData(JNIEnv *env, jobject self, jint playerID, jstring key, jstring data)
{
	const char* key_cs = env->GetStringUTFChars(key, 0);
	const char* data_cs = env->GetStringUTFChars(data, 0);
	jboolean returnValue =  bz_setPlayerCustomData(playerID,key_cs,data_cs);
	env->ReleaseStringUTFChars(key, key_cs);
	env->ReleaseStringUTFChars(data, data_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setPlayerOperator(JNIEnv *env, jobject self, jint playerId)
{
	jboolean returnValue =  bz_setPlayerOperator(playerId);
	return returnValue;
}

jint JNICALL simplebind_bz_getTeamPlayerLimit(JNIEnv *env, jobject self, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	int returnValue =  bz_getTeamPlayerLimit((bz_eTeamType) team_n);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setPlayerWins(JNIEnv *env, jobject self, jint playerId, jint wins)
{
	jboolean returnValue =  bz_setPlayerWins(playerId, wins);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setPlayerLosses(JNIEnv *env, jobject self, jint playerId, jint losses)
{
	jboolean returnValue =  bz_setPlayerLosses(playerId, losses);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setPlayerTKs(JNIEnv *env, jobject self, jint playerId, jint tks)
{
	jboolean returnValue =  bz_setPlayerTKs(playerId, tks);
	return returnValue;
}

jfloat JNICALL simplebind_bz_getPlayerRank(JNIEnv *env, jobject self, jint playerId)
{
	jfloat returnValue =  bz_getPlayerRank(playerId);
	return returnValue;
}

jint JNICALL simplebind_bz_getPlayerWins(JNIEnv *env, jobject self, jint playerId)
{
	int returnValue =  bz_getPlayerWins(playerId);
	return returnValue;
}

jint JNICALL simplebind_bz_getPlayerLosses(JNIEnv *env, jobject self, jint playerId)
{
	int returnValue =  bz_getPlayerLosses(playerId);
	return returnValue;
}

jint JNICALL simplebind_bz_getPlayerTKs(JNIEnv *env, jobject self, jint playerId)
{
	int returnValue =  bz_getPlayerTKs(playerId);
	return returnValue;
}

jboolean JNICALL simplebind_bz_resetPlayerScore(JNIEnv *env, jobject self, jint playerId)
{
	jboolean returnValue =  bz_resetPlayerScore(playerId);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setPlayerShotType(JNIEnv *env, jobject self, jint playerId, jobject shotType)
{
	int shotType_n = getJavaEnumInt(env, shotType);
	jboolean returnValue =  bz_setPlayerShotType(playerId, (bz_eShotType) shotType_n);
	return returnValue;
}

jint JNICALL simplebind_bz_getPlayerLag(JNIEnv *env, jobject self, jint playerId)
{
	int returnValue =  bz_getPlayerLag(playerId);
	return returnValue;
}

jint JNICALL simplebind_bz_getPlayerJitter(JNIEnv *env, jobject self, jint playerId)
{
	int returnValue =  bz_getPlayerJitter(playerId);
	return returnValue;
}

jfloat JNICALL simplebind_bz_getPlayerPacketLoss(JNIEnv *env, jobject self, jint playerId)
{
	jfloat returnValue =  bz_getPlayerPacketLoss(playerId);
	return returnValue;
}

jobjectArray JNICALL simplebind_bz_getGroupList(JNIEnv *env, jobject self)
{
	bz_APIStringList* returnList =  bz_getGroupList();
	jobjectArray returnArray = stringListToStringArray(env,returnList);
	bz_deleteStringList(returnList);
	return returnArray;
}

jobjectArray JNICALL simplebind_bz_getGroupPerms(JNIEnv *env, jobject self, jstring group)
{
	const char* group_cs = env->GetStringUTFChars(group, 0);
	bz_APIStringList* returnList =  bz_getGroupPerms(group_cs);
	env->ReleaseStringUTFChars(group, group_cs);
	jobjectArray returnArray = stringListToStringArray(env,returnList);
	bz_deleteStringList(returnList);
	return returnArray;
}

jboolean JNICALL simplebind_bz_groupAllowPerm(JNIEnv *env, jobject self, jstring group, jstring perm)
{
	const char* group_cs = env->GetStringUTFChars(group, 0);
	const char* perm_cs = env->GetStringUTFChars(perm, 0);
	jboolean returnValue =  bz_groupAllowPerm(group_cs, perm_cs);
	env->ReleaseStringUTFChars(group, group_cs);
	env->ReleaseStringUTFChars(perm, perm_cs);
	return returnValue;
}

jobjectArray JNICALL simplebind_bz_getStandardPermList(JNIEnv *env, jobject self)
{
	bz_APIStringList* returnList =  bz_getStandardPermList();
	jobjectArray returnArray = stringListToStringArray(env,returnList);
	bz_deleteStringList(returnList);
	return returnArray;
}

jboolean JNICALL simplebind_bz_sendTextMessage(JNIEnv *env, jobject self, jint from, jint to, jstring message)
{
	const char* message_cs = env->GetStringUTFChars(message, 0);
	jboolean returnValue =  bz_sendTextMessage(from, to, message_cs);
	env->ReleaseStringUTFChars(message, message_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_sendTextMessageToTeam(JNIEnv *env, jobject self, jint from, jobject to, jstring message)
{
	int to_n = getJavaEnumInt(env, to);
	const char* message_cs = env->GetStringUTFChars(message, 0);
	jboolean returnValue =  bz_sendTextMessage(from, (bz_eTeamType) to_n, message_cs);
	env->ReleaseStringUTFChars(message, message_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_sendFetchResMessage(JNIEnv *env, jobject self, jint playerID, jstring URL)
{
	const char* URL_cs = env->GetStringUTFChars(URL, 0);
	jboolean returnValue =  bz_sendFetchResMessage(playerID, URL_cs);
	env->ReleaseStringUTFChars(URL, URL_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_sendJoinServer(JNIEnv *env, jobject self, jint playerID, jstring address, jint port, jint team, jstring referrer)
{
	const char* address_cs = env->GetStringUTFChars(address, 0);
	const char* referrer_cs = env->GetStringUTFChars(referrer, 0);
	//TODO: the sixth parameter was added to the header file since the bindings for this
	//method had been generated. This method, therefore, needs to be regenerated, although
	//the old methods should probably be preserved, calling the new one with an empty
	//sixth argument.
	jboolean returnValue =  bz_sendJoinServer(playerID, address_cs, port, team, referrer_cs, "");
	env->ReleaseStringUTFChars(address, address_cs);
	env->ReleaseStringUTFChars(referrer, referrer_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_fireWorldWep(JNIEnv *env, jobject self, jstring flagType, jfloat lifetime, jfloatArray pos, jfloat tilt, jfloat direction, jint shotID, jfloat dt)
{
	const char* flagType_cs = env->GetStringUTFChars(flagType, 0);
	float pos_f[3];
	float* pos_fb = new float;
	env->GetFloatArrayRegion(pos, 0, 1, pos_fb);
	pos_f[0] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 1, 1, pos_fb);
	pos_f[1] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 2, 1, pos_fb);
	pos_f[2] = (*pos_fb);
	jboolean returnValue =  bz_fireWorldWep(flagType_cs, lifetime, pos_f, tilt, direction, shotID, dt);
	env->ReleaseStringUTFChars(flagType, flagType_cs);
	delete pos_fb;
	return returnValue;
}

jint JNICALL simplebind_bz_fireWorldGM(JNIEnv *env, jobject self, jint targetPlayerID, jfloat lifetime, jfloatArray pos, jfloat tilt, jfloat direction, jfloat dt)
{
	float pos_f[3];
	float* pos_fb = new float;
	env->GetFloatArrayRegion(pos, 0, 1, pos_fb);
	pos_f[0] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 1, 1, pos_fb);
	pos_f[1] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 2, 1, pos_fb);
	pos_f[2] = (*pos_fb);
	int returnValue =  bz_fireWorldGM(targetPlayerID, lifetime, pos_f, tilt, direction, dt);
	delete pos_fb;
	return returnValue;
}

jfloat JNICALL simplebind_bz_getMaxWaitTime(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	jfloat returnValue =  bz_getMaxWaitTime(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

void JNICALL simplebind_bz_setMaxWaitTime(JNIEnv *env, jobject self, jfloat maxTime, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	 bz_setMaxWaitTime(maxTime, name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
}

void JNICALL simplebind_bz_clearMaxWaitTime(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	 bz_clearMaxWaitTime(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
}

jfloat JNICALL simplebind_bz_getBZDBDouble(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jfloat returnValue =  bz_getBZDBDouble(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jstring JNICALL simplebind_bz_getBZDBString(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	bz_ApiString returnValue =  bz_getBZDBString(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return JNU_NewStringNative(returnValue.c_str());
}

jstring JNICALL simplebind_bz_getBZDBDefault(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	bz_ApiString returnValue =  bz_getBZDBDefault(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return JNU_NewStringNative(returnValue.c_str());
}

jboolean JNICALL simplebind_bz_getBZDBBool(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_getBZDBBool(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jint JNICALL simplebind_bz_getBZDBInt(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	int returnValue =  bz_getBZDBInt(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jint JNICALL simplebind_bz_getBZDBItemPerms(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	int returnValue =  bz_getBZDBItemPerms(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_getBZDBItemPersistent(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_getBZDBItemPersistent(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_BZDBItemExists(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_BZDBItemExists(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setBZDBDouble(JNIEnv *env, jobject self, jstring variable, jfloat val, jint perms, jboolean persistent)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_setBZDBDouble(variable_cs, val, perms, persistent);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setBZDBString(JNIEnv *env, jobject self, jstring variable, jstring val, jint perms, jboolean persistent)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	const char* val_cs = env->GetStringUTFChars(val, 0);
	jboolean returnValue =  bz_setBZDBString(variable_cs, val_cs, perms, persistent);
	env->ReleaseStringUTFChars(variable, variable_cs);
	env->ReleaseStringUTFChars(val, val_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setBZDBBool(JNIEnv *env, jobject self, jstring variable, jboolean val, jint perms, jboolean persistent)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_setBZDBBool(variable_cs, val, perms, persistent);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setBZDBInt(JNIEnv *env, jobject self, jstring variable, jint val, jint perms, jboolean persistent)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_setBZDBInt(variable_cs, val, perms, persistent);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_updateBZDBDouble(JNIEnv *env, jobject self, jstring variable, jfloat val)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_updateBZDBDouble(variable_cs, val);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_updateBZDBString(JNIEnv *env, jobject self, jstring variable, jstring val)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	const char* val_cs = env->GetStringUTFChars(val, 0);
	jboolean returnValue =  bz_updateBZDBString(variable_cs, val_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
	env->ReleaseStringUTFChars(val, val_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_updateBZDBBool(JNIEnv *env, jobject self, jstring variable, jboolean val)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_updateBZDBBool(variable_cs, val);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_updateBZDBInt(JNIEnv *env, jobject self, jstring variable, jint val)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	jboolean returnValue =  bz_updateBZDBInt(variable_cs, val);
	env->ReleaseStringUTFChars(variable, variable_cs);
	return returnValue;
}

void JNICALL simplebind_bz_resetBZDBVar(JNIEnv *env, jobject self, jstring variable)
{
	const char* variable_cs = env->GetStringUTFChars(variable, 0);
	 bz_resetBZDBVar(variable_cs);
	env->ReleaseStringUTFChars(variable, variable_cs);
}

void JNICALL simplebind_bz_resetALLBZDBVars(JNIEnv *env, jobject self)
{
	 bz_resetALLBZDBVars();
}

void JNICALL simplebind_bz_debugMessage(JNIEnv *env, jobject self, jint level, jstring message)
{
	const char* message_cs = env->GetStringUTFChars(message, 0);
	 bz_debugMessage(level, message_cs);
	env->ReleaseStringUTFChars(message, message_cs);
}

jint JNICALL simplebind_bz_getDebugLevel(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getDebugLevel();
	return returnValue;
}

jboolean JNICALL simplebind_bz_kickUser(JNIEnv *env, jobject self, jint playerIndex, jstring reason, jboolean notify)
{
	const char* reason_cs = env->GetStringUTFChars(reason, 0);
	jboolean returnValue =  bz_kickUser(playerIndex, reason_cs, notify);
	env->ReleaseStringUTFChars(reason, reason_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_IPBanUser(JNIEnv *env, jobject self, jstring ip, jstring source, jint duration, jstring reason)
{
	const char* ip_cs = env->GetStringUTFChars(ip, 0);
	const char* source_cs = env->GetStringUTFChars(source, 0);
	const char* reason_cs = env->GetStringUTFChars(reason, 0);
	jboolean returnValue =  bz_IPBanUser(ip_cs, source_cs, duration, reason_cs);
	env->ReleaseStringUTFChars(ip, ip_cs);
	env->ReleaseStringUTFChars(source, source_cs);
	env->ReleaseStringUTFChars(reason, reason_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_IDBanUser(JNIEnv *env, jobject self, jstring bzID, jstring source, jint duration, jstring reason)
{
	const char* bzID_cs = env->GetStringUTFChars(bzID, 0);
	const char* source_cs = env->GetStringUTFChars(source, 0);
	const char* reason_cs = env->GetStringUTFChars(reason, 0);
	jboolean returnValue =  bz_IDBanUser(bzID_cs, source_cs, duration, reason_cs);
	env->ReleaseStringUTFChars(bzID, bzID_cs);
	env->ReleaseStringUTFChars(source, source_cs);
	env->ReleaseStringUTFChars(reason, reason_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_HostBanUser(JNIEnv *env, jobject self, jstring hostmask, jstring source, jint duration, jstring reason)
{
	const char* hostmask_cs = env->GetStringUTFChars(hostmask, 0);
	const char* source_cs = env->GetStringUTFChars(source, 0);
	const char* reason_cs = env->GetStringUTFChars(reason, 0);
	jboolean returnValue =  bz_HostBanUser(hostmask_cs, source_cs, duration, reason_cs);
	env->ReleaseStringUTFChars(hostmask, hostmask_cs);
	env->ReleaseStringUTFChars(source, source_cs);
	env->ReleaseStringUTFChars(reason, reason_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_IPUnbanUser(JNIEnv *env, jobject self, jstring ip)
{
	const char* ip_cs = env->GetStringUTFChars(ip, 0);
	jboolean returnValue =  bz_IPUnbanUser(ip_cs);
	env->ReleaseStringUTFChars(ip, ip_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_IDUnbanUser(JNIEnv *env, jobject self, jstring bzID)
{
	const char* bzID_cs = env->GetStringUTFChars(bzID, 0);
	jboolean returnValue =  bz_IDUnbanUser(bzID_cs);
	env->ReleaseStringUTFChars(bzID, bzID_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_HostUnbanUser(JNIEnv *env, jobject self, jstring hostmask)
{
	const char* hostmask_cs = env->GetStringUTFChars(hostmask, 0);
	jboolean returnValue =  bz_HostUnbanUser(hostmask_cs);
	env->ReleaseStringUTFChars(hostmask, hostmask_cs);
	return returnValue;
}

jint JNICALL simplebind_bz_getBanListSize(JNIEnv *env, jobject self, jobject listType)
{
	int listType_n = getJavaEnumInt(env, listType);
	int returnValue =  bz_getBanListSize((bz_eBanListType) listType_n);
	return returnValue;
}

jstring JNICALL simplebind_bz_getBanItem(JNIEnv *env, jobject self, jobject listType, jint item)
{
	int listType_n = getJavaEnumInt(env, listType);
	const char* returnValue =  bz_getBanItem((bz_eBanListType) listType_n, item);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getBanItemReason(JNIEnv *env, jobject self, jobject listType, jint item)
{
	int listType_n = getJavaEnumInt(env, listType);
	const char* returnValue =  bz_getBanItemReason((bz_eBanListType) listType_n, item);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getBanItemSource(JNIEnv *env, jobject self, jobject listType, jint item)
{
	int listType_n = getJavaEnumInt(env, listType);
	const char* returnValue =  bz_getBanItemSource((bz_eBanListType) listType_n, item);
	return JNU_NewStringNative(returnValue);
}

jfloat JNICALL simplebind_bz_getBanItemDurration(JNIEnv *env, jobject self, jobject listType, jint item)
{
	int listType_n = getJavaEnumInt(env, listType);
	jfloat returnValue =  bz_getBanItemDuration((bz_eBanListType) listType_n, item);
	return returnValue;
}

jboolean JNICALL simplebind_bz_getBanItemIsFromMaster(JNIEnv *env, jobject self, jobject listType, jint item)
{
	int listType_n = getJavaEnumInt(env, listType);
	jboolean returnValue =  bz_getBanItemIsFromMaster((bz_eBanListType) listType_n, item);
	return returnValue;
}

jobjectArray JNICALL simplebind_bz_getReports(JNIEnv *env, jobject self)
{
	bz_APIStringList* returnList =  bz_getReports();
	jobjectArray returnArray = stringListToStringArray(env,returnList);
	bz_deleteStringList(returnList);
	return returnArray;
}

jint JNICALL simplebind_bz_getReportCount(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getReportCount();
	return returnValue;
}

jstring JNICALL simplebind_bz_getReportSource(JNIEnv *env, jobject self, jint id)
{
	const char* returnValue =  bz_getReportSource(id);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getReportBody(JNIEnv *env, jobject self, jint id)
{
	const char* returnValue =  bz_getReportBody(id);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getReportTime(JNIEnv *env, jobject self, jint id)
{
	const char* returnValue =  bz_getReportTime(id);
	return JNU_NewStringNative(returnValue);
}

jboolean JNICALL simplebind_bz_clearReport(JNIEnv *env, jobject self, jint id)
{
	jboolean returnValue =  bz_clearReport(id);
	return returnValue;
}

jboolean JNICALL simplebind_bz_clearAllReports(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_clearAllReports();
	return returnValue;
}

jboolean JNICALL simplebind_bz_fileReport(JNIEnv *env, jobject self, jstring message, jstring from)
{
	const char* message_cs = env->GetStringUTFChars(message, 0);
	const char* from_cs = env->GetStringUTFChars(from, 0);
	jboolean returnValue =  bz_fileReport(message_cs, from_cs);
	env->ReleaseStringUTFChars(message, message_cs);
	env->ReleaseStringUTFChars(from, from_cs);
	return returnValue;
}

jint JNICALL simplebind_bz_getLagWarn(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getLagWarn();
	return returnValue;
}

jboolean JNICALL simplebind_bz_setLagWarn(JNIEnv *env, jobject self, jint lagwarn)
{
	jboolean returnValue =  bz_setLagWarn(lagwarn);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setTimeLimit(JNIEnv *env, jobject self, jfloat timeLimit)
{
	jboolean returnValue =  bz_setTimeLimit(timeLimit);
	return returnValue;
}

jfloat JNICALL simplebind_bz_getTimeLimit(JNIEnv *env, jobject self)
{
	jfloat returnValue =  bz_getTimeLimit();
	return returnValue;
}

jboolean JNICALL simplebind_bz_isTimeManualStart(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_isTimeManualStart();
	return returnValue;
}

jboolean JNICALL simplebind_bz_isCountDownActive(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_isCountDownActive();
	return returnValue;
}

jboolean JNICALL simplebind_bz_isCountDownInProgress(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_isCountDownInProgress();
	return returnValue;
}

jboolean JNICALL simplebind_bz_pollActive(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_pollActive();
	return returnValue;
}

jboolean JNICALL simplebind_bz_pollVeto(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_pollVeto();
	return returnValue;
}

jobjectArray JNICALL simplebind_bz_getHelpTopics(JNIEnv *env, jobject self)
{
	bz_APIStringList* returnList =  bz_getHelpTopics();
	jobjectArray returnArray = stringListToStringArray(env,returnList);
	bz_deleteStringList(returnList);
	return returnArray;
}

jobjectArray JNICALL simplebind_bz_getHelpTopic(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	bz_APIStringList* returnList =  bz_getHelpTopic(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	jobjectArray returnArray = stringListToStringArray(env,returnList);
	bz_deleteStringList(returnList);
	return returnArray;
}

jboolean JNICALL simplebind_bz_removeCustomSlashCommand(JNIEnv *env, jobject self, jstring command)
{
	const char* command_cs = env->GetStringUTFChars(command, 0);
	jboolean returnValue =  bz_removeCustomSlashCommand(command_cs);
	env->ReleaseStringUTFChars(command, command_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_killPlayer(JNIEnv *env, jobject self, jint playerID, jboolean spawnOnBase, jint killerID, jstring flagID)
{
	const char* flagID_cs = env->GetStringUTFChars(flagID, 0);
	jboolean returnValue =  bz_killPlayer(playerID, spawnOnBase, killerID, flagID_cs);
	env->ReleaseStringUTFChars(flagID, flagID_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_givePlayerFlag(JNIEnv *env, jobject self, jint playerID, jstring flagType, jboolean force)
{
	const char* flagType_cs = env->GetStringUTFChars(flagType, 0);
	jboolean returnValue =  bz_givePlayerFlag(playerID, flagType_cs, force);
	env->ReleaseStringUTFChars(flagType, flagType_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_removePlayerFlag(JNIEnv *env, jobject self, jint playerID)
{
	jboolean returnValue =  bz_removePlayerFlag(playerID);
	return returnValue;
}

void JNICALL simplebind_bz_resetFlags(JNIEnv *env, jobject self, jboolean onlyUnused)
{
	 bz_resetFlags(onlyUnused);
}

jint JNICALL simplebind_bz_getNumFlags(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getNumFlags();
	return returnValue;
}

jstring JNICALL simplebind_bz_getFlagName(JNIEnv *env, jobject self, jint flag)
{
	bz_ApiString returnValue =  bz_getFlagName(flag);
	return JNU_NewStringNative(returnValue.c_str());
}

jboolean JNICALL simplebind_bz_resetFlag(JNIEnv *env, jobject self, jint flag)
{
	jboolean returnValue =  bz_resetFlag(flag);
	return returnValue;
}

jint JNICALL simplebind_bz_flagPlayer(JNIEnv *env, jobject self, jint flag)
{
	int returnValue =  bz_flagPlayer(flag);
	return returnValue;
}

// returnarg second is pointer to float[3]
jfloatArray JNICALL simplebind_bz_getFlagPosition(JNIEnv *env, jobject self, jint flag)
{
	float returnArray[3];
	bz_getFlagPosition(flag, returnArray);
	jfloatArray returnValue = env->NewFloatArray(3);
	env->SetFloatArrayRegion(returnValue, 0, 3, returnArray);
	return returnValue;
}

jboolean JNICALL simplebind_bz_moveFlag(JNIEnv *env, jobject self, jint flag, jfloatArray pos, jboolean reset)
{
	float pos_f[3];
	float* pos_fb = new float;
	env->GetFloatArrayRegion(pos, 0, 1, pos_fb);
	pos_f[0] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 1, 1, pos_fb);
	pos_f[1] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 2, 1, pos_fb);
	pos_f[2] = (*pos_fb);
	jboolean returnValue =  bz_moveFlag(flag, pos_f, reset);
	delete pos_fb;
	return returnValue;
}

jboolean JNICALL simplebind_bz_setWorldSize(JNIEnv *env, jobject self, jfloat size, jfloat wallHeight)
{
	jboolean returnValue =  bz_setWorldSize(size, wallHeight);
	return returnValue;
}

void JNICALL simplebind_bz_setClientWorldDownloadURL(JNIEnv *env, jobject self, jstring URL)
{
	const char* URL_cs = env->GetStringUTFChars(URL, 0);
	 bz_setClientWorldDownloadURL(URL_cs);
	env->ReleaseStringUTFChars(URL, URL_cs);
}

jstring JNICALL simplebind_bz_getClientWorldDownloadURL(JNIEnv *env, jobject self)
{
	bz_ApiString returnValue =  bz_getClientWorldDownloadURL();
	return JNU_NewStringNative(returnValue.c_str());
}

jboolean JNICALL simplebind_bz_saveWorldCacheFile(JNIEnv *env, jobject self, jstring file)
{
	const char* file_cs = env->GetStringUTFChars(file, 0);
	jboolean returnValue =  bz_saveWorldCacheFile(file_cs);
	env->ReleaseStringUTFChars(file, file_cs);
	return returnValue;
}

jint JNICALL simplebind_bz_getWorldCacheSize(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getWorldCacheSize();
	return returnValue;
}

jboolean JNICALL simplebind_bz_removeCustomMapObject(JNIEnv *env, jobject self, jstring object)
{
	const char* object_cs = env->GetStringUTFChars(object, 0);
	jboolean returnValue =  bz_removeCustomMapObject(object_cs);
	env->ReleaseStringUTFChars(object, object_cs);
	return returnValue;
}

// returnarg void bz_getWorldSize(float*size,float*wallheight)
jfloat JNICALL simplebind_bz_getWorldSize(JNIEnv *env, jobject self)
{
	jfloat returnValue;
	bz_getWorldSize(&returnValue, 0);
	return returnValue;
}

// returnarg void bz_getWorldSize(float*size,float*wallheight)
jfloat JNICALL simplebind_bz_getWorldWallHeight(JNIEnv *env, jobject self)
{
	jfloat returnValue;
	bz_getWorldSize(0, &returnValue);
	return returnValue;
}

jint JNICALL simplebind_bz_getWorldObjectCount(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getWorldObjectCount();
	return returnValue;
}

jint JNICALL simplebind_bz_findWorldObject(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	int returnValue =  bz_findWorldObject(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

jstring JNICALL simplebind_bz_getLinkTeleName(JNIEnv *env, jobject self, jint linkIndex)
{
	const char* returnValue =  bz_getLinkTeleName(linkIndex);
	return JNU_NewStringNative(returnValue);
}

jint JNICALL simplebind_bz_getPhyDrvID(JNIEnv *env, jobject self, jstring phyDrvName)
{
	const char* phyDrvName_cs = env->GetStringUTFChars(phyDrvName, 0);
	int returnValue =  bz_getPhyDrvID(phyDrvName_cs);
	env->ReleaseStringUTFChars(phyDrvName, phyDrvName_cs);
	return returnValue;
}

jstring JNICALL simplebind_bz_getPhyDrvName(JNIEnv *env, jobject self, jint phyDrvID)
{
	const char* returnValue =  bz_getPhyDrvName(phyDrvID);
	return JNU_NewStringNative(returnValue);
}

void JNICALL simplebind_bz_ResetWorldObjectTangibilities(JNIEnv *env, jobject self)
{
	 bz_ResetWorldObjectTangibilities();
}

jboolean JNICALL simplebind_bz_getPublic(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_getPublic();
	return returnValue;
}

jstring JNICALL simplebind_bz_getPublicAddr(JNIEnv *env, jobject self)
{
	bz_ApiString returnValue =  bz_getPublicAddr();
	return JNU_NewStringNative(returnValue.c_str());
}

jint JNICALL simplebind_bz_getPublicPort(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getPublicPort();
	return returnValue;
}

jstring JNICALL simplebind_bz_getPublicDescription(JNIEnv *env, jobject self)
{
	bz_ApiString returnValue =  bz_getPublicDescription();
	return JNU_NewStringNative(returnValue.c_str());
}

jboolean JNICALL simplebind_bz_loadPlugin(JNIEnv *env, jobject self, jstring path, jstring params)
{
	const char* path_cs = env->GetStringUTFChars(path, 0);
	const char* params_cs = env->GetStringUTFChars(params, 0);
	jboolean returnValue =  bz_loadPlugin(path_cs, params_cs);
	env->ReleaseStringUTFChars(path, path_cs);
	env->ReleaseStringUTFChars(params, params_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_unloadPlugin(JNIEnv *env, jobject self, jstring path)
{
	const char* path_cs = env->GetStringUTFChars(path, 0);
	jboolean returnValue =  bz_unloadPlugin(path_cs);
	env->ReleaseStringUTFChars(path, path_cs);
	return returnValue;
}

jstring JNICALL simplebind_bz_pluginBinPath(JNIEnv *env, jobject self)
{
	const char* returnValue =  bz_pluginBinPath();
	return JNU_NewStringNative(returnValue);
}

jboolean JNICALL simplebind_bz_sendPlayCustomLocalSound(JNIEnv *env, jobject self, jint playerID, jstring soundName)
{
	const char* soundName_cs = env->GetStringUTFChars(soundName, 0);
	jboolean returnValue =  bz_sendPlayCustomLocalSound(playerID, soundName_cs);
	env->ReleaseStringUTFChars(soundName, soundName_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_callbackExists(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	jboolean returnValue =  bz_callbackExists(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

jint JNICALL simplebind_bz_getTeamCount(JNIEnv *env, jobject self, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	int returnValue =  bz_getTeamCount((bz_eTeamType) team_n);
	return returnValue;
}

jint JNICALL simplebind_bz_getTeamScore(JNIEnv *env, jobject self, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	int returnValue =  bz_getTeamScore((bz_eTeamType) team_n);
	return returnValue;
}

jint JNICALL simplebind_bz_getTeamWins(JNIEnv *env, jobject self, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	int returnValue =  bz_getTeamWins((bz_eTeamType) team_n);
	return returnValue;
}

jint JNICALL simplebind_bz_getTeamLosses(JNIEnv *env, jobject self, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	int returnValue =  bz_getTeamLosses((bz_eTeamType) team_n);
	return returnValue;
}

void JNICALL simplebind_bz_setTeamWins(JNIEnv *env, jobject self, jobject team, jint wins)
{
	int team_n = getJavaEnumInt(env, team);
	 bz_setTeamWins((bz_eTeamType) team_n, wins);
}

void JNICALL simplebind_bz_setTeamLosses(JNIEnv *env, jobject self, jobject team, jint losses)
{
	int team_n = getJavaEnumInt(env, team);
	 bz_setTeamLosses((bz_eTeamType) team_n, losses);
}

void JNICALL simplebind_bz_resetTeamScore(JNIEnv *env, jobject self, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	 bz_resetTeamScore((bz_eTeamType) team_n);
}

void JNICALL simplebind_bz_resetTeamScores(JNIEnv *env, jobject self)
{
	 bz_resetTeamScores();
}

void JNICALL simplebind_bz_updateListServer(JNIEnv *env, jobject self)
{
	 bz_updateListServer();
}

jboolean JNICALL simplebind_bz_removeURLJob(JNIEnv *env, jobject self, jstring URL)
{
	const char* URL_cs = env->GetStringUTFChars(URL, 0);
	jboolean returnValue =  bz_removeURLJob(URL_cs);
	env->ReleaseStringUTFChars(URL, URL_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_removeURLJobByID(JNIEnv *env, jobject self, jint id)
{
	jboolean returnValue =  bz_removeURLJobByID(id);
	return returnValue;
}

jboolean JNICALL simplebind_bz_stopAllURLJobs(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_stopAllURLJobs();
	return returnValue;
}

jboolean JNICALL simplebind_bz_clipFieldExists(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	jboolean returnValue =  bz_clipFieldExists(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

jstring JNICALL simplebind_bz_getclipFieldString(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	const char* returnValue =  bz_getclipFieldString(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	return JNU_NewStringNative(returnValue);
}

jfloat JNICALL simplebind_bz_getclipFieldFloat(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	jfloat returnValue =  bz_getclipFieldFloat(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

jint JNICALL simplebind_bz_getclipFieldInt(JNIEnv *env, jobject self, jstring name)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	int returnValue =  bz_getclipFieldInt(name_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setclipFieldString(JNIEnv *env, jobject self, jstring name, jstring data)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	const char* data_cs = env->GetStringUTFChars(data, 0);
	jboolean returnValue =  bz_setclipFieldString(name_cs, data_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	env->ReleaseStringUTFChars(data, data_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setclipFieldFloat(JNIEnv *env, jobject self, jstring name, jfloat data)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	jboolean returnValue =  bz_setclipFieldFloat(name_cs, data);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_setclipFieldInt(JNIEnv *env, jobject self, jstring name, jint data)
{
	const char* name_cs = env->GetStringUTFChars(name, 0);
	jboolean returnValue =  bz_setclipFieldInt(name_cs, data);
	env->ReleaseStringUTFChars(name, name_cs);
	return returnValue;
}

jstring JNICALL simplebind_bz_filterPath(JNIEnv *env, jobject self, jstring path)
{
	const char* path_cs = env->GetStringUTFChars(path, 0);
	bz_ApiString returnValue =  bz_filterPath(path_cs);
	env->ReleaseStringUTFChars(path, path_cs);
	return JNU_NewStringNative(returnValue.c_str());
}

jboolean JNICALL simplebind_bz_saveRecBuf(JNIEnv *env, jobject self, jstring _filename, jint seconds)
{
	const char* _filename_cs = env->GetStringUTFChars(_filename, 0);
	jboolean returnValue =  bz_saveRecBuf(_filename_cs, seconds);
	env->ReleaseStringUTFChars(_filename, _filename_cs);
	return returnValue;
}

jboolean JNICALL simplebind_bz_startRecBuf(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_startRecBuf();
	return returnValue;
}

jboolean JNICALL simplebind_bz_stopRecBuf(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_stopRecBuf();
	return returnValue;
}

jstring JNICALL simplebind_bz_toupper(JNIEnv *env, jobject self, jstring val)
{
	const char* val_cs = env->GetStringUTFChars(val, 0);
	const char* returnValue =  bz_toupper(val_cs);
	env->ReleaseStringUTFChars(val, val_cs);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_tolower(JNIEnv *env, jobject self, jstring val)
{
	const char* val_cs = env->GetStringUTFChars(val, 0);
	const char* returnValue =  bz_tolower(val_cs);
	env->ReleaseStringUTFChars(val, val_cs);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_urlEncode(JNIEnv *env, jobject self, jstring val)
{
	const char* val_cs = env->GetStringUTFChars(val, 0);
	const char* returnValue =  bz_urlEncode(val_cs);
	env->ReleaseStringUTFChars(val, val_cs);
	return JNU_NewStringNative(returnValue);
}

void JNICALL simplebind_bz_pauseCountdown(JNIEnv *env, jobject self, jstring pausedBy)
{
	const char* pausedBy_cs = env->GetStringUTFChars(pausedBy, 0);
	 bz_pauseCountdown(pausedBy_cs);
	env->ReleaseStringUTFChars(pausedBy, pausedBy_cs);
}

void JNICALL simplebind_bz_resumeCountdown(JNIEnv *env, jobject self, jstring resumedBy)
{
	const char* resumedBy_cs = env->GetStringUTFChars(resumedBy, 0);
	 bz_resumeCountdown(resumedBy_cs);
	env->ReleaseStringUTFChars(resumedBy, resumedBy_cs);
}

void JNICALL simplebind_bz_startCountdown(JNIEnv *env, jobject self, jint delay, jfloat limit, jstring byWho)
{
	const char* byWho_cs = env->GetStringUTFChars(byWho, 0);
	 bz_startCountdown(delay, limit, byWho_cs);
	env->ReleaseStringUTFChars(byWho, byWho_cs);
}

void JNICALL simplebind_bz_shutdown(JNIEnv *env, jobject self)
{
	 bz_shutdown();
}

jboolean JNICALL simplebind_bz_restart(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_restart();
	return returnValue;
}

void JNICALL simplebind_bz_superkill(JNIEnv *env, jobject self)
{
	 bz_superkill();
}

void JNICALL simplebind_bz_gameOver(JNIEnv *env, jobject self, jint playerID, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	 bz_gameOver(playerID, (bz_eTeamType) team_n);
}

void JNICALL simplebind_bz_reloadLocalBans(JNIEnv *env, jobject self)
{
	 bz_reloadLocalBans();
}

void JNICALL simplebind_bz_reloadMasterBans(JNIEnv *env, jobject self)
{
	 bz_reloadMasterBans();
}

void JNICALL simplebind_bz_reloadGroups(JNIEnv *env, jobject self)
{
	 bz_reloadGroups();
}

void JNICALL simplebind_bz_reloadUsers(JNIEnv *env, jobject self)
{
	 bz_reloadUsers();
}

void JNICALL simplebind_bz_reloadHelp(JNIEnv *env, jobject self)
{
	 bz_reloadHelp();
}

void JNICALL simplebind_bz_newRabbit(JNIEnv *env, jobject self, jint player, jboolean swap)
{
	 bz_newRabbit(player, swap);
}

void JNICALL simplebind_bz_removeRabbit(JNIEnv *env, jobject self, jint player)
{
	 bz_removeRabbit(player);
}

void JNICALL simplebind_bz_changeTeam(JNIEnv *env, jobject self, jint player, jobject team)
{
	int team_n = getJavaEnumInt(env, team);
	 bz_changeTeam(player, (bz_eTeamType) team_n);
}

jboolean JNICALL simplebind_bz_zapPlayer(JNIEnv *env, jobject self, jint player)
{
	jboolean returnValue =  bz_zapPlayer(player);
	return returnValue;
}

jint JNICALL simplebind_bz_getPlayerCount(JNIEnv *env, jobject self)
{
	int returnValue =  bz_getPlayerCount();
	return returnValue;
}

jboolean JNICALL simplebind_bz_anyPlayers(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_anyPlayers();
	return returnValue;
}

jobject JNICALL simplebind_bz_checkBaseAtPoint(JNIEnv *env, jobject self, jfloatArray pos)
{
	float pos_f[3];
	float* pos_fb = new float;
	env->GetFloatArrayRegion(pos, 0, 1, pos_fb);
	pos_f[0] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 1, 1, pos_fb);
	pos_f[1] = (*pos_fb);
	env->GetFloatArrayRegion(pos, 2, 1, pos_fb);
	pos_f[2] = (*pos_fb);
	int returnValueNumber = (int)  bz_checkBaseAtPoint(pos_f);
	delete pos_fb;
	return getJavaEnumConstant(env, 1, returnValueNumber);
}

jobject JNICALL simplebind_bz_getGameType(JNIEnv *env, jobject self)
{
	int returnValueNumber = (int)  bz_getGameType();
	return getJavaEnumConstant(env, 2, returnValueNumber);
}

jboolean JNICALL simplebind_bz_allowJumping(JNIEnv *env, jobject self)
{
	jboolean returnValue =  bz_allowJumping();
	return returnValue;
}

jstring JNICALL simplebind_bz_MD5(JNIEnv *env, jobject self, jstring str)
{
	const char* str_cs = env->GetStringUTFChars(str, 0);
	const char* returnValue =  bz_MD5(str_cs);
	env->ReleaseStringUTFChars(str, str_cs);
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getServerVersion(JNIEnv *env, jobject self)
{
	const char* returnValue =  bz_getServerVersion();
	return JNU_NewStringNative(returnValue);
}

jstring JNICALL simplebind_bz_getProtocolVersion(JNIEnv *env, jobject self)
{
	const char* returnValue =  bz_getProtocolVersion();
	return JNU_NewStringNative(returnValue);
}

jboolean JNICALL simplebind_bz_RegisterCustomFlag(JNIEnv *env, jobject self, jstring abbr, jstring name, jstring helpString, jobject shotType, jobject quality)
{
	printf("native flag add\n");
	const char* abbr_cs = env->GetStringUTFChars(abbr, 0);
	const char* name_cs = env->GetStringUTFChars(name, 0);
	const char* helpString_cs = env->GetStringUTFChars(helpString, 0);
	int shotType_n = getJavaEnumInt(env, shotType);
	int quality_n = getJavaEnumInt(env, quality);
	jboolean returnValue =  bz_RegisterCustomFlag(abbr_cs, name_cs, helpString_cs, (bz_eShotType) shotType_n, (bz_eFlagQuality) quality_n);
	env->ReleaseStringUTFChars(abbr, abbr_cs);
	env->ReleaseStringUTFChars(name, name_cs);
	env->ReleaseStringUTFChars(helpString, helpString_cs);
	return returnValue;
}

//END SimpleBind methods


//void JNICALL (JNIEnv *env, jobject self)
//{
//}

bool registerNatives()
{
	jclass apiClass = env->FindClass("org/bzflag/jzapi/BzfsAPI");
	jclass simpleBindClass = env->FindClass("org/bzflag/jzapi/internal/SimpleBind");
	jclass eventClass = env->FindClass("org/bzflag/jzapi/BzfsEvent");
	if(apiClass == 0 || simpleBindClass == 0 || eventClass == 0)
		return false;

	registerNative(apiClass,"registerEventHandler","(ILorg/bzflag/jzapi/BzfsEventHandler;)V",j_registerEvent);
	registerNative(apiClass,"removeEventHandler","(ILorg/bzflag/jzapi/BzfsEventHandler;)V",j_removeEvent);
	registerNative(apiClass,"getPlayerIndexList","()[I",j_getPlayerIndexList);
	registerNative(apiClass,"getFunctionPointers","()[J",j_getFunctionPointers);
	registerNative(apiClass,"getPlayerId","(I)I", j_getPlayerId);
	registerNative(apiClass,"getPlayerIdByCallsign","(Ljava/lang/String;)I", j_getPlayerIdByCallsign);
	registerNative(apiClass,"getPlayerRecord","(I)Lorg/bzflag/jzapi/BasePlayerRecord;",j_getPlayerRecord);
	registerNative(apiClass,"freePlayerRecord","(Lorg/bzflag/jzapi/BasePlayerRecord;",j_freePlayerRecord);

	registerNative(eventClass,"getEventType","()Lorg/bzflag/jzapi/BzfsAPI$EventType;",j_event_getEventType);
	registerNative(eventClass,"getEventTime","()D",j_event_getEventTime);

	registerNative("org/bzflag/jzapi/events/BzfsPlayerJoinPartEvent", "getPlayerId", "()I", j_event_playerJoinPart_getPlayerID);
	registerNative("org/bzflag/jzapi/events/BzfsPlayerJoinPartEvent", "setPlayerId", "(I)V", j_event_playerJoinPart_setPlayerID);

	//BEGIN SimpleBind registrations
	registerNative(simpleBindClass,"bz_getCurrentTime","()F",simplebind_bz_getCurrentTime);
	registerNative(simpleBindClass,"bz_APIVersion","()I",simplebind_bz_APIVersion);
	registerNative(simpleBindClass,"bz_disconnectNonPlayerConnection","(I)Z",simplebind_bz_disconnectNonPlayerConnection);
	registerNative(simpleBindClass,"bz_getNonPlayerConnectionOutboundPacketCount","(I)I",simplebind_bz_getNonPlayerConnectionOutboundPacketCount);
	registerNative(simpleBindClass,"bz_getNonPlayerConnectionIP","(I)Ljava/lang/String;",simplebind_bz_getNonPlayerConnectionIP);
	registerNative(simpleBindClass,"bz_getNonPlayerConnectionHost","(I)Ljava/lang/String;",simplebind_bz_getNonPlayerConnectionHost);
	registerNative(simpleBindClass,"bz_hasPerm","(ILjava/lang/String;)Z",simplebind_bz_hasPerm);
	registerNative(simpleBindClass,"bz_grantPerm","(ILjava/lang/String;)Z",simplebind_bz_grantPerm);
	registerNative(simpleBindClass,"bz_revokePerm","(ILjava/lang/String;)Z",simplebind_bz_revokePerm);
	registerNative(simpleBindClass,"bz_getAdmin","(I)Z",simplebind_bz_getAdmin);
	registerNative(simpleBindClass,"bz_validAdminPassword","(Ljava/lang/String;)Z",simplebind_bz_validAdminPassword);
	registerNative(simpleBindClass,"bz_getPlayerFlag","(I)Ljava/lang/String;",simplebind_bz_getPlayerFlag);
	registerNative(simpleBindClass,"bz_getPlayerPosition","(IZ)[F",simplebind_bz_getPlayerPosition);
	registerNative(simpleBindClass,"bz_getPlayerRotation","(IZ)[F",simplebind_bz_getPlayerRotation);
	registerNative(simpleBindClass,"bz_getPlayerVelocity","(I)[F",simplebind_bz_getPlayerVelocity);
	registerNative(simpleBindClass,"bz_getPlayerAngVel","(I)F",simplebind_bz_getPlayerAngVel);
	registerNative(simpleBindClass,"bz_getPlayerPhysicsDriver","(I)I",simplebind_bz_getPlayerPhysicsDriver);
	registerNative(simpleBindClass,"bz_isPlayerPaused","(I)Z",simplebind_bz_isPlayerPaused);
	registerNative(simpleBindClass,"bz_canPlayerSpawn","(I)Z",simplebind_bz_canPlayerSpawn);
	registerNative(simpleBindClass,"bz_setPlayerSpawnable","(IZ)Z",simplebind_bz_setPlayerSpawnable);
	registerNative(simpleBindClass,"bz_setPlayerLimboMessage","(ILjava/lang/String;)Z",simplebind_bz_setPlayerLimboMessage);
	registerNative(simpleBindClass,"bz_getPlayerTeam","(I)Lorg/bzflag/jzapi/BzfsAPI$TeamType;",simplebind_bz_getPlayerTeam);
	registerNative(simpleBindClass,"bz_getPlayerCallsign","(I)Ljava/lang/String;",simplebind_bz_getPlayerCallsign);
	registerNative(simpleBindClass,"bz_getPlayerIPAddress","(I)Ljava/lang/String;",simplebind_bz_getPlayerIPAddress);
	registerNative(simpleBindClass,"bz_getPlayerReferrer","(I)Ljava/lang/String;",simplebind_bz_getPlayerReferrer);
	registerNative(simpleBindClass,"bz_setPayerCustomData","(ILjava/lang/String;Ljava/lang/String;)Z",simplebind_bz_setPayerCustomData);
	registerNative(simpleBindClass,"bz_setPlayerOperator","(I)Z",simplebind_bz_setPlayerOperator);
	registerNative(simpleBindClass,"bz_getTeamPlayerLimit","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;)I",simplebind_bz_getTeamPlayerLimit);
	registerNative(simpleBindClass,"bz_setPlayerWins","(II)Z",simplebind_bz_setPlayerWins);
	registerNative(simpleBindClass,"bz_setPlayerLosses","(II)Z",simplebind_bz_setPlayerLosses);
	registerNative(simpleBindClass,"bz_setPlayerTKs","(II)Z",simplebind_bz_setPlayerTKs);
	registerNative(simpleBindClass,"bz_getPlayerRank","(I)F",simplebind_bz_getPlayerRank);
	registerNative(simpleBindClass,"bz_getPlayerWins","(I)I",simplebind_bz_getPlayerWins);
	registerNative(simpleBindClass,"bz_getPlayerLosses","(I)I",simplebind_bz_getPlayerLosses);
	registerNative(simpleBindClass,"bz_getPlayerTKs","(I)I",simplebind_bz_getPlayerTKs);
	registerNative(simpleBindClass,"bz_resetPlayerScore","(I)Z",simplebind_bz_resetPlayerScore);
	registerNative(simpleBindClass,"bz_setPlayerShotType","(ILorg/bzflag/jzapi/BzfsAPI$ShotType;)Z",simplebind_bz_setPlayerShotType);
	registerNative(simpleBindClass,"bz_getPlayerLag","(I)I",simplebind_bz_getPlayerLag);
	registerNative(simpleBindClass,"bz_getPlayerJitter","(I)I",simplebind_bz_getPlayerJitter);
	registerNative(simpleBindClass,"bz_getPlayerPacketLoss","(I)F",simplebind_bz_getPlayerPacketLoss);
	registerNative(simpleBindClass,"bz_getGroupList","()[Ljava/lang/String;",simplebind_bz_getGroupList);
	registerNative(simpleBindClass,"bz_getGroupPerms","(Ljava/lang/String;)[Ljava/lang/String;",simplebind_bz_getGroupPerms);
	registerNative(simpleBindClass,"bz_groupAllowPerm","(Ljava/lang/String;Ljava/lang/String;)Z",simplebind_bz_groupAllowPerm);
	registerNative(simpleBindClass,"bz_getStandardPermList","()[Ljava/lang/String;",simplebind_bz_getStandardPermList);
	registerNative(simpleBindClass,"bz_sendTextMessage","(IILjava/lang/String;)Z",simplebind_bz_sendTextMessage);
	registerNative(simpleBindClass,"bz_sendTextMessage","(ILorg/bzflag/jzapi/BzfsAPI$TeamType;Ljava/lang/String;)Z",simplebind_bz_sendTextMessageToTeam);
	registerNative(simpleBindClass,"bz_sendFetchResMessage","(ILjava/lang/String;)Z",simplebind_bz_sendFetchResMessage);
	registerNative(simpleBindClass,"bz_sendJoinServer","(ILjava/lang/String;IILjava/lang/String;)Z",simplebind_bz_sendJoinServer);
	registerNative(simpleBindClass,"bz_fireWorldWep","(Ljava/lang/String;F[FFFIF)Z",simplebind_bz_fireWorldWep);
	registerNative(simpleBindClass,"bz_fireWorldGM","(IF[FFFF)I",simplebind_bz_fireWorldGM);
	registerNative(simpleBindClass,"bz_getMaxWaitTime","(Ljava/lang/String;)F",simplebind_bz_getMaxWaitTime);
	registerNative(simpleBindClass,"bz_setMaxWaitTime","(FLjava/lang/String;)V",simplebind_bz_setMaxWaitTime);
	registerNative(simpleBindClass,"bz_clearMaxWaitTime","(Ljava/lang/String;)V",simplebind_bz_clearMaxWaitTime);
	registerNative(simpleBindClass,"bz_getBZDBDouble","(Ljava/lang/String;)F",simplebind_bz_getBZDBDouble);
	registerNative(simpleBindClass,"bz_getBZDBString","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_getBZDBString);
	registerNative(simpleBindClass,"bz_getBZDBDefault","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_getBZDBDefault);
	registerNative(simpleBindClass,"bz_getBZDBBool","(Ljava/lang/String;)Z",simplebind_bz_getBZDBBool);
	registerNative(simpleBindClass,"bz_getBZDBInt","(Ljava/lang/String;)I",simplebind_bz_getBZDBInt);
	registerNative(simpleBindClass,"bz_getBZDBItemPerms","(Ljava/lang/String;)I",simplebind_bz_getBZDBItemPerms);
	registerNative(simpleBindClass,"bz_getBZDBItemPersistent","(Ljava/lang/String;)Z",simplebind_bz_getBZDBItemPersistent);
	registerNative(simpleBindClass,"bz_BZDBItemExists","(Ljava/lang/String;)Z",simplebind_bz_BZDBItemExists);
	registerNative(simpleBindClass,"bz_setBZDBDouble","(Ljava/lang/String;FIZ)Z",simplebind_bz_setBZDBDouble);
	registerNative(simpleBindClass,"bz_setBZDBString","(Ljava/lang/String;Ljava/lang/String;IZ)Z",simplebind_bz_setBZDBString);
	registerNative(simpleBindClass,"bz_setBZDBBool","(Ljava/lang/String;ZIZ)Z",simplebind_bz_setBZDBBool);
	registerNative(simpleBindClass,"bz_setBZDBInt","(Ljava/lang/String;IIZ)Z",simplebind_bz_setBZDBInt);
	registerNative(simpleBindClass,"bz_updateBZDBDouble","(Ljava/lang/String;F)Z",simplebind_bz_updateBZDBDouble);
	registerNative(simpleBindClass,"bz_updateBZDBString","(Ljava/lang/String;Ljava/lang/String;)Z",simplebind_bz_updateBZDBString);
	registerNative(simpleBindClass,"bz_updateBZDBBool","(Ljava/lang/String;Z)Z",simplebind_bz_updateBZDBBool);
	registerNative(simpleBindClass,"bz_updateBZDBInt","(Ljava/lang/String;I)Z",simplebind_bz_updateBZDBInt);
	registerNative(simpleBindClass,"bz_resetBZDBVar","(Ljava/lang/String;)V",simplebind_bz_resetBZDBVar);
	registerNative(simpleBindClass,"bz_resetALLBZDBVars","()V",simplebind_bz_resetALLBZDBVars);
	registerNative(simpleBindClass,"bz_debugMessage","(ILjava/lang/String;)V",simplebind_bz_debugMessage);
	registerNative(simpleBindClass,"bz_getDebugLevel","()I",simplebind_bz_getDebugLevel);
	registerNative(simpleBindClass,"bz_kickUser","(ILjava/lang/String;Z)Z",simplebind_bz_kickUser);
	registerNative(simpleBindClass,"bz_IPBanUser","(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Z",simplebind_bz_IPBanUser);
	registerNative(simpleBindClass,"bz_IDBanUser","(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Z",simplebind_bz_IDBanUser);
	registerNative(simpleBindClass,"bz_HostBanUser","(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Z",simplebind_bz_HostBanUser);
	registerNative(simpleBindClass,"bz_IPUnbanUser","(Ljava/lang/String;)Z",simplebind_bz_IPUnbanUser);
	registerNative(simpleBindClass,"bz_IDUnbanUser","(Ljava/lang/String;)Z",simplebind_bz_IDUnbanUser);
	registerNative(simpleBindClass,"bz_HostUnbanUser","(Ljava/lang/String;)Z",simplebind_bz_HostUnbanUser);
	registerNative(simpleBindClass,"bz_getBanListSize","(Lorg/bzflag/jzapi/BzfsAPI$BanListType;)I",simplebind_bz_getBanListSize);
	registerNative(simpleBindClass,"bz_getBanItem","(Lorg/bzflag/jzapi/BzfsAPI$BanListType;I)Ljava/lang/String;",simplebind_bz_getBanItem);
	registerNative(simpleBindClass,"bz_getBanItemReason","(Lorg/bzflag/jzapi/BzfsAPI$BanListType;I)Ljava/lang/String;",simplebind_bz_getBanItemReason);
	registerNative(simpleBindClass,"bz_getBanItemSource","(Lorg/bzflag/jzapi/BzfsAPI$BanListType;I)Ljava/lang/String;",simplebind_bz_getBanItemSource);
	registerNative(simpleBindClass,"bz_getBanItemDurration","(Lorg/bzflag/jzapi/BzfsAPI$BanListType;I)F",simplebind_bz_getBanItemDurration);
	registerNative(simpleBindClass,"bz_getBanItemIsFromMaster","(Lorg/bzflag/jzapi/BzfsAPI$BanListType;I)Z",simplebind_bz_getBanItemIsFromMaster);
	registerNative(simpleBindClass,"bz_getReports","()[Ljava/lang/String;",simplebind_bz_getReports);
	registerNative(simpleBindClass,"bz_getReportCount","()I",simplebind_bz_getReportCount);
	registerNative(simpleBindClass,"bz_getReportSource","(I)Ljava/lang/String;",simplebind_bz_getReportSource);
	registerNative(simpleBindClass,"bz_getReportBody","(I)Ljava/lang/String;",simplebind_bz_getReportBody);
	registerNative(simpleBindClass,"bz_getReportTime","(I)Ljava/lang/String;",simplebind_bz_getReportTime);
	registerNative(simpleBindClass,"bz_clearReport","(I)Z",simplebind_bz_clearReport);
	registerNative(simpleBindClass,"bz_clearAllReports","()Z",simplebind_bz_clearAllReports);
	registerNative(simpleBindClass,"bz_fileReport","(Ljava/lang/String;Ljava/lang/String;)Z",simplebind_bz_fileReport);
	registerNative(simpleBindClass,"bz_getLagWarn","()I",simplebind_bz_getLagWarn);
	registerNative(simpleBindClass,"bz_setLagWarn","(I)Z",simplebind_bz_setLagWarn);
	registerNative(simpleBindClass,"bz_setTimeLimit","(F)Z",simplebind_bz_setTimeLimit);
	registerNative(simpleBindClass,"bz_getTimeLimit","()F",simplebind_bz_getTimeLimit);
	registerNative(simpleBindClass,"bz_isTimeManualStart","()Z",simplebind_bz_isTimeManualStart);
	registerNative(simpleBindClass,"bz_isCountDownActive","()Z",simplebind_bz_isCountDownActive);
	registerNative(simpleBindClass,"bz_isCountDownInProgress","()Z",simplebind_bz_isCountDownInProgress);
	registerNative(simpleBindClass,"bz_pollActive","()Z",simplebind_bz_pollActive);
	registerNative(simpleBindClass,"bz_pollVeto","()Z",simplebind_bz_pollVeto);
	registerNative(simpleBindClass,"bz_getHelpTopics","()[Ljava/lang/String;",simplebind_bz_getHelpTopics);
	registerNative(simpleBindClass,"bz_getHelpTopic","(Ljava/lang/String;)[Ljava/lang/String;",simplebind_bz_getHelpTopic);
	registerNative(simpleBindClass,"bz_removeCustomSlashCommand","(Ljava/lang/String;)Z",simplebind_bz_removeCustomSlashCommand);
	registerNative(simpleBindClass,"bz_killPlayer","(IZILjava/lang/String;)Z",simplebind_bz_killPlayer);
	registerNative(simpleBindClass,"bz_givePlayerFlag","(ILjava/lang/String;Z)Z",simplebind_bz_givePlayerFlag);
	registerNative(simpleBindClass,"bz_removePlayerFlag","(I)Z",simplebind_bz_removePlayerFlag);
	registerNative(simpleBindClass,"bz_resetFlags","(Z)V",simplebind_bz_resetFlags);
	registerNative(simpleBindClass,"bz_getNumFlags","()I",simplebind_bz_getNumFlags);
	registerNative(simpleBindClass,"bz_getFlagName","(I)Ljava/lang/String;",simplebind_bz_getFlagName);
	registerNative(simpleBindClass,"bz_resetFlag","(I)Z",simplebind_bz_resetFlag);
	registerNative(simpleBindClass,"bz_flagPlayer","(I)I",simplebind_bz_flagPlayer);
	registerNative(simpleBindClass,"bz_getFlagPosition","(I)[F",simplebind_bz_getFlagPosition);
	registerNative(simpleBindClass,"bz_moveFlag","(I[FZ)Z",simplebind_bz_moveFlag);
	registerNative(simpleBindClass,"bz_setWorldSize","(FF)Z",simplebind_bz_setWorldSize);
	registerNative(simpleBindClass,"bz_setClientWorldDownloadURL","(Ljava/lang/String;)V",simplebind_bz_setClientWorldDownloadURL);
	registerNative(simpleBindClass,"bz_getClientWorldDownloadURL","()Ljava/lang/String;",simplebind_bz_getClientWorldDownloadURL);
	registerNative(simpleBindClass,"bz_saveWorldCacheFile","(Ljava/lang/String;)Z",simplebind_bz_saveWorldCacheFile);
	registerNative(simpleBindClass,"bz_getWorldCacheSize","()I",simplebind_bz_getWorldCacheSize);
	registerNative(simpleBindClass,"bz_removeCustomMapObject","(Ljava/lang/String;)Z",simplebind_bz_removeCustomMapObject);
	registerNative(simpleBindClass,"bz_getWorldSize","()F",simplebind_bz_getWorldSize);
	registerNative(simpleBindClass,"bz_getWorldWallHeight","()F",simplebind_bz_getWorldWallHeight);
	registerNative(simpleBindClass,"bz_getWorldObjectCount","()I",simplebind_bz_getWorldObjectCount);
	registerNative(simpleBindClass,"bz_findWorldObject","(Ljava/lang/String;)I",simplebind_bz_findWorldObject);
	registerNative(simpleBindClass,"bz_getLinkTeleName","(I)Ljava/lang/String;",simplebind_bz_getLinkTeleName);
	registerNative(simpleBindClass,"bz_getPhyDrvID","(Ljava/lang/String;)I",simplebind_bz_getPhyDrvID);
	registerNative(simpleBindClass,"bz_getPhyDrvName","(I)Ljava/lang/String;",simplebind_bz_getPhyDrvName);
	registerNative(simpleBindClass,"bz_ResetWorldObjectTangibilities","()V",simplebind_bz_ResetWorldObjectTangibilities);
	registerNative(simpleBindClass,"bz_getPublic","()Z",simplebind_bz_getPublic);
	registerNative(simpleBindClass,"bz_getPublicAddr","()Ljava/lang/String;",simplebind_bz_getPublicAddr);
	registerNative(simpleBindClass,"bz_getPublicPort","()I",simplebind_bz_getPublicPort);
	registerNative(simpleBindClass,"bz_getPublicDescription","()Ljava/lang/String;",simplebind_bz_getPublicDescription);
	registerNative(simpleBindClass,"bz_loadPlugin","(Ljava/lang/String;Ljava/lang/String;)Z",simplebind_bz_loadPlugin);
	registerNative(simpleBindClass,"bz_unloadPlugin","(Ljava/lang/String;)Z",simplebind_bz_unloadPlugin);
	registerNative(simpleBindClass,"bz_pluginBinPath","()Ljava/lang/String;",simplebind_bz_pluginBinPath);
	registerNative(simpleBindClass,"bz_sendPlayCustomLocalSound","(ILjava/lang/String;)Z",simplebind_bz_sendPlayCustomLocalSound);
	registerNative(simpleBindClass,"bz_callbackExists","(Ljava/lang/String;)Z",simplebind_bz_callbackExists);
	registerNative(simpleBindClass,"bz_getTeamCount","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;)I",simplebind_bz_getTeamCount);
	registerNative(simpleBindClass,"bz_getTeamScore","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;)I",simplebind_bz_getTeamScore);
	registerNative(simpleBindClass,"bz_getTeamWins","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;)I",simplebind_bz_getTeamWins);
	registerNative(simpleBindClass,"bz_getTeamLosses","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;)I",simplebind_bz_getTeamLosses);
	registerNative(simpleBindClass,"bz_setTeamWins","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;I)V",simplebind_bz_setTeamWins);
	registerNative(simpleBindClass,"bz_setTeamLosses","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;I)V",simplebind_bz_setTeamLosses);
	registerNative(simpleBindClass,"bz_resetTeamScore","(Lorg/bzflag/jzapi/BzfsAPI$TeamType;)V",simplebind_bz_resetTeamScore);
	registerNative(simpleBindClass,"bz_resetTeamScores","()V",simplebind_bz_resetTeamScores);
	registerNative(simpleBindClass,"bz_updateListServer","()V",simplebind_bz_updateListServer);
	registerNative(simpleBindClass,"bz_removeURLJob","(Ljava/lang/String;)Z",simplebind_bz_removeURLJob);
	registerNative(simpleBindClass,"bz_removeURLJobByID","(I)Z",simplebind_bz_removeURLJobByID);
	registerNative(simpleBindClass,"bz_stopAllURLJobs","()Z",simplebind_bz_stopAllURLJobs);
	registerNative(simpleBindClass,"bz_clipFieldExists","(Ljava/lang/String;)Z",simplebind_bz_clipFieldExists);
	registerNative(simpleBindClass,"bz_getclipFieldString","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_getclipFieldString);
	registerNative(simpleBindClass,"bz_getclipFieldFloat","(Ljava/lang/String;)F",simplebind_bz_getclipFieldFloat);
	registerNative(simpleBindClass,"bz_getclipFieldInt","(Ljava/lang/String;)I",simplebind_bz_getclipFieldInt);
	registerNative(simpleBindClass,"bz_setclipFieldString","(Ljava/lang/String;Ljava/lang/String;)Z",simplebind_bz_setclipFieldString);
	registerNative(simpleBindClass,"bz_setclipFieldFloat","(Ljava/lang/String;F)Z",simplebind_bz_setclipFieldFloat);
	registerNative(simpleBindClass,"bz_setclipFieldInt","(Ljava/lang/String;I)Z",simplebind_bz_setclipFieldInt);
	registerNative(simpleBindClass,"bz_filterPath","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_filterPath);
	registerNative(simpleBindClass,"bz_saveRecBuf","(Ljava/lang/String;I)Z",simplebind_bz_saveRecBuf);
	registerNative(simpleBindClass,"bz_startRecBuf","()Z",simplebind_bz_startRecBuf);
	registerNative(simpleBindClass,"bz_stopRecBuf","()Z",simplebind_bz_stopRecBuf);
	registerNative(simpleBindClass,"bz_toupper","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_toupper);
	registerNative(simpleBindClass,"bz_tolower","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_tolower);
	registerNative(simpleBindClass,"bz_urlEncode","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_urlEncode);
	registerNative(simpleBindClass,"bz_pauseCountdown","(Ljava/lang/String;)V",simplebind_bz_pauseCountdown);
	registerNative(simpleBindClass,"bz_resumeCountdown","(Ljava/lang/String;)V",simplebind_bz_resumeCountdown);
	registerNative(simpleBindClass,"bz_startCountdown","(IFLjava/lang/String;)V",simplebind_bz_startCountdown);
	registerNative(simpleBindClass,"bz_shutdown","()V",simplebind_bz_shutdown);
	registerNative(simpleBindClass,"bz_restart","()Z",simplebind_bz_restart);
	registerNative(simpleBindClass,"bz_superkill","()V",simplebind_bz_superkill);
	registerNative(simpleBindClass,"bz_gameOver","(ILorg/bzflag/jzapi/BzfsAPI$TeamType;)V",simplebind_bz_gameOver);
	registerNative(simpleBindClass,"bz_reloadLocalBans","()V",simplebind_bz_reloadLocalBans);
	registerNative(simpleBindClass,"bz_reloadMasterBans","()V",simplebind_bz_reloadMasterBans);
	registerNative(simpleBindClass,"bz_reloadGroups","()V",simplebind_bz_reloadGroups);
	registerNative(simpleBindClass,"bz_reloadUsers","()V",simplebind_bz_reloadUsers);
	registerNative(simpleBindClass,"bz_reloadHelp","()V",simplebind_bz_reloadHelp);
	registerNative(simpleBindClass,"bz_newRabbit","(IZ)V",simplebind_bz_newRabbit);
	registerNative(simpleBindClass,"bz_removeRabbit","(I)V",simplebind_bz_removeRabbit);
	registerNative(simpleBindClass,"bz_changeTeam","(ILorg/bzflag/jzapi/BzfsAPI$TeamType;)V",simplebind_bz_changeTeam);
	registerNative(simpleBindClass,"bz_zapPlayer","(I)Z",simplebind_bz_zapPlayer);
	registerNative(simpleBindClass,"bz_getPlayerCount","()I",simplebind_bz_getPlayerCount);
	registerNative(simpleBindClass,"bz_anyPlayers","()Z",simplebind_bz_anyPlayers);
	registerNative(simpleBindClass,"bz_checkBaseAtPoint","([F)Lorg/bzflag/jzapi/BzfsAPI$TeamType;",simplebind_bz_checkBaseAtPoint);
	registerNative(simpleBindClass,"bz_getGameType","()Lorg/bzflag/jzapi/BzfsAPI$GameType;",simplebind_bz_getGameType);
	registerNative(simpleBindClass,"bz_allowJumping","()Z",simplebind_bz_allowJumping);
	registerNative(simpleBindClass,"bz_MD5","(Ljava/lang/String;)Ljava/lang/String;",simplebind_bz_MD5);
	registerNative(simpleBindClass,"bz_getServerVersion","()Ljava/lang/String;",simplebind_bz_getServerVersion);
	registerNative(simpleBindClass,"bz_getProtocolVersion","()Ljava/lang/String;",simplebind_bz_getProtocolVersion);
	registerNative(simpleBindClass,"bz_RegisterCustomFlag","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/bzflag/jzapi/BzfsAPI$ShotType;Lorg/bzflag/jzapi/BzfsAPI$FlagQuality;)Z",simplebind_bz_RegisterCustomFlag);
	//END SimpleBind registrations

	//BEGIN bz_BasePlayerRecord SimpleClassBind registrations
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getVersion", "()I", cb_BasePlayerRecord_getVersion);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setVersion", "(I)V", cb_BasePlayerRecord_setVersion);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getPlayerID", "()I", cb_BasePlayerRecord_getPlayerID);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setPlayerID", "(I)V", cb_BasePlayerRecord_setPlayerID);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getCallsign", "()Ljava/lang/String;", cb_BasePlayerRecord_getCallsign);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getTeam", "()Lorg/bzflag/jzapi/BzfsAPI$TeamType;", cb_BasePlayerRecord_getTeam);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setTeam", "(Lorg/bzflag/jzapi/BzfsAPI$TeamType;)V", cb_BasePlayerRecord_setTeam);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getIpAddress", "()Ljava/lang/String;", cb_BasePlayerRecord_getIpAddress);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getCurrentFlagID", "()I", cb_BasePlayerRecord_getCurrentFlagID);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setCurrentFlagID", "(I)V", cb_BasePlayerRecord_setCurrentFlagID);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getCurrentFlag", "()Ljava/lang/String;", cb_BasePlayerRecord_getCurrentFlag);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getFlagHistory", "()[Ljava/lang/String;", cb_BasePlayerRecord_getFlagHistory);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getLastUpdateTime", "()F", cb_BasePlayerRecord_getLastUpdateTime);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setLastUpdateTime", "(F)V", cb_BasePlayerRecord_setLastUpdateTime);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getClientVersion", "()Ljava/lang/String;", cb_BasePlayerRecord_getClientVersion);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getSpawned", "()Z", cb_BasePlayerRecord_getSpawned);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setSpawned", "(Z)V", cb_BasePlayerRecord_setSpawned);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getVerified", "()Z", cb_BasePlayerRecord_getVerified);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setVerified", "(Z)V", cb_BasePlayerRecord_setVerified);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getGlobalUser", "()Z", cb_BasePlayerRecord_getGlobalUser);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setGlobalUser", "(Z)V", cb_BasePlayerRecord_setGlobalUser);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getBzID", "()Ljava/lang/String;", cb_BasePlayerRecord_getBzID);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getAdmin", "()Z", cb_BasePlayerRecord_getAdmin);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setAdmin", "(Z)V", cb_BasePlayerRecord_setAdmin);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getOp", "()Z", cb_BasePlayerRecord_getOp);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setOp", "(Z)V", cb_BasePlayerRecord_setOp);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getCanSpawn", "()Z", cb_BasePlayerRecord_getCanSpawn);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setCanSpawn", "(Z)V", cb_BasePlayerRecord_setCanSpawn);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getGroups", "()[Ljava/lang/String;", cb_BasePlayerRecord_getGroups);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getLag", "()I", cb_BasePlayerRecord_getLag);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setLag", "(I)V", cb_BasePlayerRecord_setLag);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getJitter", "()I", cb_BasePlayerRecord_getJitter);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setJitter", "(I)V", cb_BasePlayerRecord_setJitter);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getPacketLoss", "()F", cb_BasePlayerRecord_getPacketLoss);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setPacketLoss", "(F)V", cb_BasePlayerRecord_setPacketLoss);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getRank", "()F", cb_BasePlayerRecord_getRank);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setRank", "(F)V", cb_BasePlayerRecord_setRank);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getWins", "()I", cb_BasePlayerRecord_getWins);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setWins", "(I)V", cb_BasePlayerRecord_setWins);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getLosses", "()I", cb_BasePlayerRecord_getLosses);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setLosses", "(I)V", cb_BasePlayerRecord_setLosses);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "getTeamKills", "()I", cb_BasePlayerRecord_getTeamKills);
	registerNative("org/bzflag/jzapi/BasePlayerRecord", "setTeamKills", "(I)V", cb_BasePlayerRecord_setTeamKills);
	//END bz_BasePlayerRecord SimpleClassBind registrations

	//BEGIN bz_PlayerUpdateState SimpleClassBind registrations
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getStatus", "()Lorg/bzflag/jzapi/BzfsAPI$PlayerStatus;", cb_PlayerUpdateState_getStatus);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setStatus", "(Lorg/bzflag/jzapi/BzfsAPI$PlayerStatus;)V", cb_PlayerUpdateState_setStatus);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getFalling", "()Z", cb_PlayerUpdateState_getFalling);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setFalling", "(Z)V", cb_PlayerUpdateState_setFalling);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getCrossingWall", "()Z", cb_PlayerUpdateState_getCrossingWall);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setCrossingWall", "(Z)V", cb_PlayerUpdateState_setCrossingWall);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getInPhantomZone", "()Z", cb_PlayerUpdateState_getInPhantomZone);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setInPhantomZone", "(Z)V", cb_PlayerUpdateState_setInPhantomZone);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getPos", "()[F", cb_PlayerUpdateState_getPos);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setPos", "([F)V", cb_PlayerUpdateState_setPos);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getVelocity", "()[F", cb_PlayerUpdateState_getVelocity);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setVelocity", "([F)V", cb_PlayerUpdateState_setVelocity);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getRotation", "()F", cb_PlayerUpdateState_getRotation);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setRotation", "(F)V", cb_PlayerUpdateState_setRotation);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getAngVel", "()F", cb_PlayerUpdateState_getAngVel);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setAngVel", "(F)V", cb_PlayerUpdateState_setAngVel);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "getPhydrv", "()I", cb_PlayerUpdateState_getPhydrv);
	registerNative("org/bzflag/jzapi/PlayerUpdateState", "setPhydrv", "(I)V", cb_PlayerUpdateState_setPhydrv);
	//END bz_PlayerUpdateState SimpleClassBind registrations

	return true;
}

typedef jint (JNICALL *CreateJavaVM)(JavaVM **pvm, void **penv, void *args);
CreateJavaVM createJVM;

/**
Constructs an instance of the given class type, using
the class's default constructor.
*/
jobject constructDefault(const char* className)
{
	jclass targetClass = env->FindClass(className);
	if(targetClass == 0)
		return 0;
	jmethodID methodId = env->GetMethodID(targetClass,"<init>","()V");
	if(methodId == 0)
		return 0;
	return env->NewObject(targetClass,methodId);
}

jobject constructDefault(JNIEnv *env, const char* className)
{
	jclass targetClass = env->FindClass(className);
	if(targetClass == 0)
		return 0;
	jmethodID methodId = env->GetMethodID(targetClass,"<init>","()V");
	if(methodId == 0)
		return 0;
	return env->NewObject(targetClass,methodId);
}

class jPluginHandler : public bz_APIPluginHandler
{
public:
	virtual bool handle(bz_ApiString plugin,bz_ApiString params)
	{
		return env->CallStaticBooleanMethod(bzfsLoaderClass, loadPluginMethodId, 
			JNU_NewStringNative(plugin.c_str()), JNU_NewStringNative(params.c_str()));
	}
};

jPluginHandler *globalPluginHandler;

BZF_PLUGIN_CALL int bz_Load ( const char* commandLine )
{
	cout << "loading java plugin" << endl;
	string commandLineString = commandLine;
	vector<string> params;
	Tokenize(commandLineString,params,",");
	if(params.size() < 1)
	{
		cout << "You must specify the classpath as a parameter to the java native plugin" << endl;
		return -1;
	}
	string classpathString(params[0]);
	string classpath("-Djava.class.path=");
	classpath += classpathString;
    JavaVMInitArgs vm_args;
	JavaVMOption options[1];
	jint res;
	size_t classpathLen = classpath.length();
    char* classpathMod = new char [ classpathLen + 1 ];
	strcpy( classpathMod, classpath.c_str() );
	options[0].optionString = classpathMod;
	vm_args.version = JNI_VERSION_1_4;
	vm_args.options = options;
	vm_args.nOptions = 1;
	vm_args.ignoreUnrecognized = JNI_FALSE;

	//load the JVM DLL
	HINSTANCE handle = LoadLibrary(JVM_RUNTIME_DLL);
	if( handle == 0) {
		printf("Failed to load jvm dll %s\n", JVM_RUNTIME_DLL);
		return -1;
	}
	//get the function pointer to JNI_CreateJVM
	createJVM = (CreateJavaVM)GetProcAddress(handle, "JNI_CreateJavaVM");

	res = createJVM(&vm, (void **)&env, &vm_args);
	if (res < 0)  {
		printf("Error creating JVM");
		return -1;
	}

	if(!loadUtils())
	{
		printf("Couldn't load utility classes\n");
	    if(env->ExceptionCheck()) 
		    env->ExceptionDescribe();
		vm->DestroyJavaVM();
		return -1;
	}

	//register the native functions
	if(!registerNatives())
	{
		printf("Couldn't register native functions. This is usually because the JZAPI classes aren't on the classpath you specified. Make sure they are, then try again.\n");
		vm->DestroyJavaVM();
		return -1;
	}
	globalPluginHandler = new jPluginHandler();
	bz_registerCustomPluginHandler("class",globalPluginHandler);
	if(env->ExceptionCheck()) {
		env->ExceptionDescribe();
		return -1;
	}
	return 0;
}

BZF_PLUGIN_CALL int bz_Unload ()
{
	env->CallStaticVoidMethod(bzfsLoaderClass, unloadPluginsMethodId);
	cout << "destroying java vm" << endl;
	vm->DestroyJavaVM();
	cout << "java vm destroyed." << endl;
  return 0;
}

// Local Variables: ***
// mode: C++ ***
// tab-width: 8 ***
// c-basic-offset: 2 ***
// indent-tabs-mode: t ***
// End: ***
// ex: shiftwidth=2 tabstop=8
