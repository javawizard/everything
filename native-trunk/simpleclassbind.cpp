#include "bind.h"

//BEGIN bz_BasePlayerRecord SimpleClassBind methods


jint JNICALL cb_BasePlayerRecord_getVersion(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->version;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setVersion(JNIEnv *env, jobject self, jint version)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->version = version;
}

jint JNICALL cb_BasePlayerRecord_getPlayerID(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->playerID;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setPlayerID(JNIEnv *env, jobject self, jint playerID)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->playerID = playerID;
}

jstring JNICALL cb_BasePlayerRecord_getCallsign(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	bz_ApiString returnValue =  nativeSelf->callsign;
	return JNU_NewStringNative(returnValue.c_str());
}

jobject JNICALL cb_BasePlayerRecord_getTeam(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValueNumber = (int)  nativeSelf->team;
	return getJavaEnumConstant(env, 1, returnValueNumber);
}

void JNICALL cb_BasePlayerRecord_setTeam(JNIEnv *env, jobject self, jobject team)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
int team_n = getJavaEnumInt(env, team);
	nativeSelf->team = (bz_eTeamType) team_n;
}

jstring JNICALL cb_BasePlayerRecord_getIpAddress(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	bz_ApiString returnValue =  nativeSelf->ipAddress;
	return JNU_NewStringNative(returnValue.c_str());
}

jint JNICALL cb_BasePlayerRecord_getCurrentFlagID(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->currentFlagID;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setCurrentFlagID(JNIEnv *env, jobject self, jint currentFlagID)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->currentFlagID = currentFlagID;
}

jstring JNICALL cb_BasePlayerRecord_getCurrentFlag(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	bz_ApiString returnValue =  nativeSelf->currentFlag;
	return JNU_NewStringNative(returnValue.c_str());
}

jobjectArray JNICALL cb_BasePlayerRecord_getFlagHistory(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	bz_APIStringList* returnList = & nativeSelf->flagHistory;
	return stringListToStringArray(env, returnList);
}

jfloat JNICALL cb_BasePlayerRecord_getLastUpdateTime(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jfloat returnValue =  nativeSelf->lastUpdateTime;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setLastUpdateTime(JNIEnv *env, jobject self, jfloat lastUpdateTime)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->lastUpdateTime = lastUpdateTime;
}

jstring JNICALL cb_BasePlayerRecord_getClientVersion(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	bz_ApiString returnValue =  nativeSelf->clientVersion;
	return JNU_NewStringNative(returnValue.c_str());
}

jboolean JNICALL cb_BasePlayerRecord_getSpawned(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->spawned;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setSpawned(JNIEnv *env, jobject self, jboolean spawned)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->spawned = spawned;
}

jboolean JNICALL cb_BasePlayerRecord_getVerified(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->verified;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setVerified(JNIEnv *env, jobject self, jboolean verified)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->verified = verified;
}

jboolean JNICALL cb_BasePlayerRecord_getGlobalUser(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->globalUser;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setGlobalUser(JNIEnv *env, jobject self, jboolean globalUser)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->globalUser = globalUser;
}

jstring JNICALL cb_BasePlayerRecord_getBzID(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	bz_ApiString returnValue =  nativeSelf->bzID;
	return JNU_NewStringNative(returnValue.c_str());
}

jboolean JNICALL cb_BasePlayerRecord_getAdmin(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->admin;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setAdmin(JNIEnv *env, jobject self, jboolean admin)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->admin = admin;
}

jboolean JNICALL cb_BasePlayerRecord_getOp(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->op;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setOp(JNIEnv *env, jobject self, jboolean op)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->op = op;
}

jboolean JNICALL cb_BasePlayerRecord_getCanSpawn(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->canSpawn;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setCanSpawn(JNIEnv *env, jobject self, jboolean canSpawn)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->canSpawn = canSpawn;
}

jobjectArray JNICALL cb_BasePlayerRecord_getGroups(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	bz_APIStringList* returnList = & nativeSelf->groups;
	return stringListToStringArray(env, returnList);
}

jint JNICALL cb_BasePlayerRecord_getLag(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->lag;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setLag(JNIEnv *env, jobject self, jint lag)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->lag = lag;
}

jint JNICALL cb_BasePlayerRecord_getJitter(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->jitter;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setJitter(JNIEnv *env, jobject self, jint jitter)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->jitter = jitter;
}

jfloat JNICALL cb_BasePlayerRecord_getPacketLoss(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jfloat returnValue =  nativeSelf->packetLoss;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setPacketLoss(JNIEnv *env, jobject self, jfloat packetLoss)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->packetLoss = packetLoss;
}

jfloat JNICALL cb_BasePlayerRecord_getRank(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	jfloat returnValue =  nativeSelf->rank;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setRank(JNIEnv *env, jobject self, jfloat rank)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->rank = rank;
}

jint JNICALL cb_BasePlayerRecord_getWins(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->wins;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setWins(JNIEnv *env, jobject self, jint wins)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->wins = wins;
}

jint JNICALL cb_BasePlayerRecord_getLosses(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->losses;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setLosses(JNIEnv *env, jobject self, jint losses)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->losses = losses;
}

jint JNICALL cb_BasePlayerRecord_getTeamKills(JNIEnv *env, jobject self)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	int returnValue =  nativeSelf->teamKills;
	return returnValue;
}

void JNICALL cb_BasePlayerRecord_setTeamKills(JNIEnv *env, jobject self, jint teamKills)
{
	bz_BasePlayerRecord* nativeSelf = reinterpret_cast<bz_BasePlayerRecord*> (getPointer(env, self));
	nativeSelf->teamKills = teamKills;
}

//END bz_BasePlayerRecord SimpleClassBind methods

//BEGIN bz_PlayerUpdateState SimpleClassBind methods

jobject JNICALL cb_PlayerUpdateState_getStatus(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	int returnValueNumber = (int)  nativeSelf->status;
	return getJavaEnumConstant(env, 5, returnValueNumber);
}

void JNICALL cb_PlayerUpdateState_setStatus(JNIEnv *env, jobject self, jobject status)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
int status_n = getJavaEnumInt(env, status);
	nativeSelf->status = (bz_ePlayerStatus) status_n;
}

jboolean JNICALL cb_PlayerUpdateState_getFalling(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->falling;
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setFalling(JNIEnv *env, jobject self, jboolean falling)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	nativeSelf->falling = falling;
}

jboolean JNICALL cb_PlayerUpdateState_getCrossingWall(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->crossingWall;
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setCrossingWall(JNIEnv *env, jobject self, jboolean crossingWall)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	nativeSelf->crossingWall = crossingWall;
}

jboolean JNICALL cb_PlayerUpdateState_getInPhantomZone(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	jboolean returnValue =  nativeSelf->inPhantomZone;
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setInPhantomZone(JNIEnv *env, jobject self, jboolean inPhantomZone)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	nativeSelf->inPhantomZone = inPhantomZone;
}

jfloatArray JNICALL cb_PlayerUpdateState_getPos(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	float* returnArray =  nativeSelf->pos;
	jfloatArray returnValue = env->NewFloatArray(3);
	env->SetFloatArrayRegion(returnValue, 0, 3, returnArray);
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setPos(JNIEnv *env, jobject self, jfloatArray pos)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	env->GetFloatArrayRegion(pos, 0, 3, nativeSelf->pos);
}

jfloatArray JNICALL cb_PlayerUpdateState_getVelocity(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	float* returnArray =  nativeSelf->velocity;
	jfloatArray returnValue = env->NewFloatArray(3);
	env->SetFloatArrayRegion(returnValue, 0, 3, returnArray);
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setVelocity(JNIEnv *env, jobject self, jfloatArray velocity)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	env->GetFloatArrayRegion(velocity, 0, 3, nativeSelf->velocity);
}

jfloat JNICALL cb_PlayerUpdateState_getRotation(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	jfloat returnValue =  nativeSelf->rotation;
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setRotation(JNIEnv *env, jobject self, jfloat rotation)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	nativeSelf->rotation = rotation;
}

jfloat JNICALL cb_PlayerUpdateState_getAngVel(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	jfloat returnValue =  nativeSelf->angVel;
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setAngVel(JNIEnv *env, jobject self, jfloat angVel)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	nativeSelf->angVel = angVel;
}

jint JNICALL cb_PlayerUpdateState_getPhydrv(JNIEnv *env, jobject self)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	int returnValue =  nativeSelf->phydrv;
	return returnValue;
}

void JNICALL cb_PlayerUpdateState_setPhydrv(JNIEnv *env, jobject self, jint phydrv)
{
	bz_PlayerUpdateState* nativeSelf = reinterpret_cast<bz_PlayerUpdateState*> (getPointer(env, self));
	nativeSelf->phydrv = phydrv;
}

//END bz_PlayerUpdateState SimpleClassBind methods
