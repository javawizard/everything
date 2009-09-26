/*
 * This is the BZNetwork plugin. See desc.txt for information on this
 * plugin, or visit http://code.google.com/p/bzsound/wiki/BZNetwork for info.
 */

#include "bzfsAPI.h"
#include "plugin_utils.h"
#include <iostream>
#include <stdio.h>
#include <assert.h>
#ifdef WIN32
#include <windows.h>
#define BZN_MUTEX_INIT CreateMutex(NULL,FALSE,NULL)
#define bzn_mutex_t HANDLE
#define bzn_thread_t HANDLE
#define bzn_mutex_lock(mutexvalue) WaitForSingleObject(mutexvalue, INFINITE)
#define bzn_mutex_unlock(mutexvalue) ReleaseMutex(mutexvalue)
#define strncasecmp strnicmp
#else
#include <pthread.h>
#define BZN_MUTEX_INIT PTHREAD_MUTEX_INITIALIZER
#define bzn_mutex_t pthread_mutex_t
#define bzn_thread_t pthread_t
#define bzn_mutex_lock(mutexvalue) pthread_mutex_lock(& mutexvalue)
#define bzn_mutex_unlock(mutexvalue) pthread_mutex_unlock(& mutexvalue)
#endif
#include <sstream>
#include "bz_PluginUtility.h"

using namespace bz_PluginUtility;

BZ_GET_PLUGIN_VERSION

const char* bzn_build_date = __DATE__ " " __TIME__;

std::vector<std::string*> stdinList;
bzn_mutex_t stdinListLock =
BZN_MUTEX_INIT;
bzn_thread_t stdinReadThread;
std::string currentStdinString;
typedef std::map<std::string, int> stringIntMap;
stringIntMap playerIdsByCallsign;

bz_eTeamType colorNameToDef(const char* color);
const char* colorDefToName(bz_eTeamType team);
int parseInt(std::string value);
void bzn_outputData(std::string value);
void stringSplit(std::string string, std::vector<std::string>* vector,
		std::string search, int maxItems);
int getPlayerByCallsign(std::string callsign);
std::string intToString(int value);

void processStdinString(std::string* currentString)
{
	std::vector<std::string> argumentList;
	stringSplit((*currentString), &argumentList, " ", 2);
	std::string command = argumentList.at(0);
	std::string arguments;
	if (argumentList.size() > 1)
		arguments = argumentList.at(1);
	else
		arguments = "";
	if (command == "say")
	{
		bz_sendTextMessage(BZ_SERVER, BZ_ALLUSERS, arguments.c_str());
	}
	else if (command == "saytofromplayer")
	{
		stringSplit(arguments, &argumentList, "|", 3);
		if (argumentList.size() < 3)
		{
			bzn_outputData(
					"bznerror other Need 3 arguments (separated by pipes) to saytofromplayer: fromcallsign, tocallsign, message, +server for the server, +all for all players");
			return;
		}
		std::string fromString = argumentList.at(0);
		std::string toString = argumentList.at(1);
		std::string messageString = argumentList.at(2);
		int fromInt = getPlayerByCallsign(fromString);
		int toInt = getPlayerByCallsign(toString);
		if (fromInt == BZ_NULLUSER)
		{
			std::string output;
			output += "bznerror nosuchplayer The from player "
				"is not a valid player here: ";
			output += fromString;
			bzn_outputData(output);
			return;
		}
		if (toInt == BZ_NULLUSER)
		{
			std::string output;
			output += "bznerror nosuchplayer The to player "
				"is not a valid player here: ";
			output += toString;
			bzn_outputData(output);
			return;
		}
		bz_sendTextMessage(fromInt, toInt, messageString.c_str());
	}
	else if (command == "saytofromplayerteam")
	{
		stringSplit(arguments, &argumentList, "|", 3);
		if (argumentList.size() < 3)
		{
			bzn_outputData(
					"bznerror other Need 3 arguments (separated by pipes) to saytofromplayerteam: fromcallsign, toteam, message, +server for the server, +all for all players");
			return;
		}
		std::string fromString = argumentList.at(0);
		std::string toString = argumentList.at(1);
		std::string messageString = argumentList.at(2);
		int fromInt = getPlayerByCallsign(fromString);
		bz_eTeamType toTeam = colorNameToDef(toString.c_str());
		if (fromInt == BZ_NULLUSER)
		{
			std::string output;
			output += "bznerror nosuchplayer The from player "
				"is not a valid player here: ";
			output += fromString;
			bzn_outputData(output);
			return;
		}
		if (toTeam == eNoTeam)
		{
			std::string output;
			output += "bznerror nosuchplayer The to team "
				"is not a valid team here: ";
			output += toString;
			bzn_outputData(output);
			return;
		}
		bz_sendTextMessage(fromInt, toTeam, messageString.c_str());
	}
	else if (command == "shutdown")
	{
		bz_shutdown();
	}
	else
	{
		bzn_outputData(
				"bznerror invalidcommand The command specified is not valid");
	}
}

class BZNetworkEventHandler: public bz_EventHandler,
		public EventRegistrar,
		public bz_CustomSlashCommandHandler
{
	public:
		BZNetworkEventHandler() :
			EventRegistrar(this)
		{
			return;
		}
		~BZNetworkEventHandler()
		{
			removeAllEvents();
		}
		virtual void process(bz_EventData *eventData)
		{
			if (eventData->eventType == bz_eTickEvent)
			{
				bz_TickEventData* event = (bz_TickEventData*) eventData;
				bzn_mutex_lock(stdinListLock);
				while (stdinList.size() > 0)
				{
					std::string* currentString = stdinList.at(0);
					processStdinString(currentString);
					stdinList.erase(stdinList.begin());
					delete currentString;
				}
				bzn_mutex_unlock(stdinListLock);
			}
			else if (eventData->eventType == bz_ePlayerJoinEvent)
			{
				bz_PlayerJoinPartEventData* event =
						(bz_PlayerJoinPartEventData*) eventData;
				std::string playerCallsign = event->callsign.c_str();
				std::string playerEmail = event->email.c_str();
				std::string playerGlobal = event->globalUser.c_str();
				if (playerCallsign.find("|") != std::string::npos
						|| playerCallsign.find("+") == 0 || playerEmail.find(
						"|") != std::string::npos || playerEmail.find("+") == 0
						|| playerGlobal.find("|") != std::string::npos
						|| playerGlobal.find("+") == 0)
				{
					bz_kickUser(
							event->playerID,
							"Invalid callsign/email: can't contain | or start with +",
							true);
					return;
				}
				playerIdsByCallsign.insert(std::pair<std::string, int>(
						playerCallsign, event->playerID));
				std::string output;
				output += "playerjoin ";
				output += intToString(event->playerID);
				output += "|";
				output += event->ipAddress.c_str();
				output += "|";
				output += colorDefToName(event->team);
				output += "|";
				output += (event->verified ? "verified" : "notverified");
				output += "|";
				output += (event->callsign.c_str());
				output += "|";
				output += (event->email.c_str());
				output += "|";
				output += (event->globalUser.c_str());
				bzn_outputData(output);
			}
			else if (eventData->eventType == bz_ePlayerPartEvent)
			{
				bz_PlayerJoinPartEventData* event =
						(bz_PlayerJoinPartEventData*) eventData;
				stringIntMap::iterator iter = playerIdsByCallsign.find(
						event->callsign.c_str());
				if (iter != playerIdsByCallsign.end())
				{
					std::string output;
					output += "playerpart ";
					output += intToString(event->playerID);
					output += "|";
					output += event->ipAddress.c_str();
					output += "|";
					output += colorDefToName(event->team);
					output += "|";
					output += (event->verified ? "verified" : "notverified");
					output += "|";
					output += (event->callsign.c_str());
					output += "|";
					output += (event->email.c_str());
					output += "|";
					output += (event->globalUser.c_str());
					output += "|";
					output += (event->reason.c_str());
					bzn_outputData(output);
					playerIdsByCallsign.erase(iter);
				}
			}
			else if (eventData->eventType == bz_eChatMessageEvent)
			{
				bz_ChatEventData* event = (bz_ChatEventData*) eventData;
				std::string output;
				output += "chatmessage ";
				output += intToString(event->from);
				output += "|";
				output += intToString(event->to);
				output += "|";
				output += colorDefToName(event->team);
				output += "|";
				output += event->message.c_str();
				bzn_outputData(output);
			}
			else if (eventData->eventType == bz_eServerMsgEvent)
			{
				bz_ServerMsgEventData* event =
						(bz_ServerMsgEventData*) eventData;
				std::string output;
				output += "chatmessage ";
				output += intToString(BZ_SERVER);
				output += "|";
				output += intToString(event->to);
				output += "|";
				output += colorDefToName(event->team);
				output += "|";
				output += event->message.c_str();
				//commented out because server logs are a bit excessive right now.
				//bzn_outputData(output);
			}
			else if (eventData->eventType == bz_eMessageFilteredEvent)
			{
				bz_MessageFilteredEventData* event =
						(bz_MessageFilteredEventData*) eventData;
				std::string output;
				output += "messagefiltered ";
				output += intToString(event->player);
				output += "|";
				output += event->filteredMessage.c_str();
				bzn_outputData(output);
			}
			else if (eventData->eventType == bz_eKillEvent)
			{
				bz_KillEventData* event = (bz_KillEventData*) eventData;
				bz_PlayerRecord* killerInfo = bz_getPlayerByIndex(
						event->killerID);
				bz_eTeamType killerTeam = killerInfo->team;
				bz_freePlayerRecord(killerInfo);
				bz_PlayerRecord* killedInfo = bz_getPlayerByIndex(
						event->killedID);
				bz_eTeamType killedTeam = killedInfo->team;
				bz_freePlayerRecord(killedInfo);
				if (killerTeam == killedTeam && !(killerTeam == eRogueTeam))
				{
					std::string output;
					output += "teamkill ";
					output += event->killerID;
					output += "|";
					output += event->killedID;
					output += "|";
					output += colorDefToName(killerTeam);
					bzn_outputData(output);
				}
			}
			else if (eventData->eventType == bz_eBanEvent)
			{
				bz_BanEventData* event = (bz_BanEventData*) eventData;
				std::string output;
				output += "ban ";
				output += event->bannerID;
				output += "|";
				output += event->banneeID;
				output += "|";
				output += event->duration;
				output += "|";
				output += event->ipAddress.c_str();
				output += "|";
				output += event->reason.c_str();
				bzn_outputData(output);
			}
			else if (eventData->eventType == bz_eSlashCommandEvent)
			{
				bz_SlashCommandEventData* event =
						(bz_SlashCommandEventData*) eventData;
				std::string output;
				output += "slashcommand ";
				output += intToString(event->from);
				output += "|";
				output += event->message.c_str();
				bzn_outputData(output);
			}
		}
		virtual bool handle(int playerID, bzApiString command,
				bzApiString message, bzAPIStringList *params)
		{
			if (strcasecmp(command.c_str(), "bzn"))
				return false;
			std::string output;
			output += "bznslashcommand ";
			output += message.c_str();
			bzn_outputData(output.c_str());
			return true;
		}
};

#ifdef WIN32
DWORD WINAPI threadedStdinReadLoop( LPVOID );
#else
void* threadedStdinReadLoop(void* bogus);
#endif

BZNetworkEventHandler singleEventHandler;

BZF_PLUGIN_CALL int bz_Load(const char* commandLine)
{
	bz_registerCustomSlashCommand("bzn", &singleEventHandler);
	singleEventHandler.registerEvent(bz_eTickEvent);
	singleEventHandler.registerEvent(bz_ePlayerJoinEvent);
	singleEventHandler.registerEvent(bz_ePlayerPartEvent);
	singleEventHandler.registerEvent(bz_eChatMessageEvent);
	singleEventHandler.registerEvent(bz_eServerMsgEvent);
	singleEventHandler.registerEvent(bz_eMessageFilteredEvent);
	singleEventHandler.registerEvent(bz_eKillEvent);
	singleEventHandler.registerEvent(bz_eBanEvent);
	singleEventHandler.registerEvent(bz_eSlashCommandEvent);
	//	bz_registerEvent(bz_eTickEvent, &singleEventHandler);
	//	bz_registerEvent(bz_ePlayerJoinEvent, &singleEventHandler);
	//	bz_registerEvent(bz_ePlayerPartEvent, &singleEventHandler);
	//	bz_registerEvent(bz_eChatMessageEvent, &singleEventHandler);
	//	bz_registerEvent(bz_eServerMsgEvent, &singleEventHandler);
	//	bz_registerEvent(bz_eMessageFilteredEvent, &singleEventHandler);
	//	bz_registerEvent(bz_eKillEvent, &singleEventHandler);
	//	bz_registerEvent(bz_eBanEvent, &singleEventHandler);
	//	bz_registerEvent(bz_eSlashCommandEvent, &singleEventHandler);
	// Perhaps allow this to be configured via an argument, and
	// then have this value be a BZNetwork configuration setting
	bz_setMaxWaitTime(2.0);
#ifdef WIN32
	DWORD windowsThreadId;
	stdinReadThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE) threadedStdinReadLoop, NULL, 0, &windowsThreadId);
	if(stdinReadThread == NULL)
#else
	if (pthread_create(&stdinReadThread, NULL, &threadedStdinReadLoop, NULL))
#endif
	{
		bzn_outputData(
				"bznfail readthread The stdin read thread could not be created.");
		bz_shutdown();
		return 1;
	}
	playerIdsByCallsign.insert(pair<std::string, int> ("+server", BZ_SERVER));
	playerIdsByCallsign.insert(pair<std::string, int> ("+all", BZ_ALLUSERS));
	std::string loadOutput;
	loadOutput += "bznload ";
	loadOutput += intToString(bz_getTeamPlayerLimit(eRedTeam));
	loadOutput += "|";
	loadOutput += intToString(bz_getTeamPlayerLimit(eGreenTeam));
	loadOutput += "|";
	loadOutput += intToString(bz_getTeamPlayerLimit(eBlueTeam));
	loadOutput += "|";
	loadOutput += intToString(bz_getTeamPlayerLimit(ePurpleTeam));
	loadOutput += "|";
	loadOutput += intToString(bz_getTeamPlayerLimit(eRogueTeam));
	loadOutput += "|";
	loadOutput += intToString(bz_getTeamPlayerLimit(eObservers));
	loadOutput += "|";
	bz_eGameType gameType = bz_getGameType();
	if (gameType == eFFAGame)
		loadOutput += "FreeForAll";
	else if (gameType == eCTFGame)
		loadOutput += "CaptureTheFlag";
	else if (gameType == eRabbitGame)
		loadOutput += "RabbitHunt";
	else
		loadOutput += "UnknownGameType";
	loadOutput += "|";
	loadOutput += bzn_build_date;
	bzn_outputData(loadOutput);
	return 0;
}

BZF_PLUGIN_CALL int bz_Unload(void)
{
	printf("Unloading the bznetwork plugin\n");
	bz_removeCustomSlashCommand("bzn");
	//	bz_removeEvent(bz_eTickEvent, &singleEventHandler);
	//	bz_removeEvent(bz_ePlayerJoinEvent, &singleEventHandler);
	//	bz_removeEvent(bz_ePlayerPartEvent, &singleEventHandler);
	//	bz_removeEvent(bz_eChatMessageEvent, &singleEventHandler);
	//	bz_removeEvent(bz_eServerMsgEvent, &singleEventHandler);
	//	bz_removeEvent(bz_eMessageFilteredEvent, &singleEventHandler);
	//	bz_removeEvent(bz_eKillEvent, &singleEventHandler);
	//	bz_removeEvent(bz_eBanEvent, &singleEventHandler);
	//	bz_removeEvent(bz_eSlashCommandEvent, &singleEventHandler);
	playerIdsByCallsign.clear();
	bzn_outputData("bznunload");
	return 0;
}

void bzn_outputData(std::string value)
{
	if (value.length() > 32743)
	{
		bzn_outputData(
				"bznerror excessiveoutput The output length was too long.");
		return;
	}
	printf("|%.5d%s\n", value.length(), value.c_str());
}

#ifdef WIN32
DWORD WINAPI threadedStdinReadLoop ( LPVOID bogus )
#else
void* threadedStdinReadLoop(void* bogus)
#endif
{
	while (true)
	{
		getline(cin, currentStdinString);
		std::string* newReadString =
				new std::string(currentStdinString.c_str());
		bzn_mutex_lock(stdinListLock);
		stdinList.push_back(newReadString);
		bzn_mutex_unlock(stdinListLock);
	}
#ifdef WIN32
	return TRUE;
#endif
}

bz_eTeamType colorNameToDef(const char* color)
{
	if (!strncasecmp(color, "gre", 3))
		return eGreenTeam;
	if (!strncasecmp(color, "red", 3))
		return eRedTeam;
	if (!strncasecmp(color, "pur", 3))
		return ePurpleTeam;
	if (!strncasecmp(color, "blu", 3))
		return eBlueTeam;
	if (!strncasecmp(color, "rog", 3))
		return eRogueTeam;
	if (!strncasecmp(color, "obs", 3))
		return eObservers;
	if (!strncasecmp(color, "rab", 3))
		return eRabbitTeam;
	if (!strncasecmp(color, "hun", 3))
		return eHunterTeam;
	if (!strncasecmp(color, "adm", 3))
		return eAdministrators;
	return eNoTeam;
}

const char* colorDefToName(bz_eTeamType team)
{
	switch (team)
	{
		case eGreenTeam:
			return ("green");
		case eBlueTeam:
			return ("blue");
		case eRedTeam:
			return ("red");
		case ePurpleTeam:
			return ("purple");
		case eObservers:
			return ("observer");
		case eRogueTeam:
			return ("rogue");
		case eRabbitTeam:
			return ("rabbit");
		case eHunterTeam:
			return ("hunters");
		case eAdministrators:
			return ("admin");
		default:
			return ("noteam");
	}
}

int parseInt(std::string value)
{
	return atoi(value.c_str());
}

void stringSplit(std::string string, std::vector<std::string>* vector,
		std::string search, int maxItems)
{
	vector->clear();
	int attempts = 1;
	int afterLastMatchedIndex = 0;
	int searchSize = search.size();
	int matchedIndex;
	while (attempts < maxItems)
	{
		attempts += 1;
		matchedIndex = string.find(search, afterLastMatchedIndex);
		if (matchedIndex == std::string::npos)
			/*
			 * We'll just break. The if statement after this will take
			 * care of adding the rest of the string to the vector.
			 */
			break;
		/*
		 * If we're here, then we do have a match. We'll substring from
		 * afterLastMatchedIndex to matchedIndex, add that string to the
		 * vector, and set afterLastMatchedIndex to matchedIndex+searchSize.
		 */
		vector->push_back(string.substr(afterLastMatchedIndex, (matchedIndex
				- afterLastMatchedIndex)));
		afterLastMatchedIndex = matchedIndex + searchSize;
	}
	if (afterLastMatchedIndex < string.size())
	{
		vector->push_back(string.substr(afterLastMatchedIndex,
				std::string::npos));
	}
}
/**
 * Returns the id for the specified callsign, or BZ_NULLUSER if that
 * user is not signed on.
 * @param callsign The callsign to search for
 * @return The corresponding id, or BZ_NULLUSER if that user is not
 * connected to the server
 */
int getPlayerByCallsign(std::string callsign)
{
	stringIntMap::iterator iter = playerIdsByCallsign.find(callsign);
	if (iter == playerIdsByCallsign.end())
		return BZ_NULLUSER;
	return iter->second;
}

std::string intToString(int value)
{
	std::string s;
	std::stringstream out;
	out << value;
	s = out.str();
	return s;
}

