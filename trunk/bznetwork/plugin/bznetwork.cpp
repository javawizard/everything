// genobait.cpp : Defines the entry point for the DLL application.
//

#include "bzfsAPI.h"
#include "plugin_utils.h"
#include <iostream>
#include <stdio.h>
#include <assert.h>
#include <pthread.h>

BZ_GET_PLUGIN_VERSION

std::vector<std::string*> stdinList;
pthread_mutex_t stdinListLock =
PTHREAD_MUTEX_INITIALIZER;
pthread_t stdinReadThread;
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
	else
	{
		bzn_outputData(
				"bznerror invalidcommand The command specified is not valid");
	}
}

class BZNetworkEventHandler: public bz_EventHandler,
		public bz_CustomSlashCommandHandler
{
	public:
		virtual void process(bz_EventData *eventData)
		{
			if (eventData->eventType == bz_eTickEvent)
			{
				bz_TickEventData* event = (bz_TickEventData*) eventData;
				pthread_mutex_lock(&stdinListLock);
				while (stdinList.size() > 0)
				{
					std::string* currentString = stdinList.at(0);
					processStdinString(currentString);
					stdinList.erase(stdinList.begin());
					delete currentString;
				}
				pthread_mutex_unlock(&stdinListLock);
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
		}
		virtual bool handle(int playerID, bzApiString command,
				bzApiString message, bzAPIStringList *params)
		{
			if (strcasecmp(command.c_str(), "bzn"))
				return false;
			bzn_outputData("bzncmd");
			bz_sendTextMessage(BZ_SERVER, playerID,
					"I just received a /bzn command from you.");
			return true;
		}
};

void* threadedStdinReadLoop(void* bogus);

BZNetworkEventHandler singleEventHandler;

BZF_PLUGIN_CALL int bz_Load(const char* /*commandLine*/)
{
	bz_registerCustomSlashCommand("bzn", &singleEventHandler);
	bz_registerEvent(bz_eTickEvent, &singleEventHandler);
	bz_registerEvent(bz_ePlayerJoinEvent, &singleEventHandler);
	bz_registerEvent(bz_ePlayerPartEvent, &singleEventHandler);
	// Perhaps allow this to be configured via an argument, and
	// then have this value be a BZNetwork configuration setting
	bz_setMaxWaitTime(1.0);
	if (pthread_create(&stdinReadThread, NULL, &threadedStdinReadLoop, NULL))
	{
		bzn_outputData(
				"bznfail readthread The stdin read thread could not be created.");
		return 1;
	}
	playerIdsByCallsign.insert(pair<std::string, int> ("+server", BZ_SERVER));
	playerIdsByCallsign.insert(pair<std::string, int> ("+all", BZ_ALLUSERS));
	bzn_outputData("bznload");
	return 0;
}

int bz_Unload(void)
{
	bz_removeCustomSlashCommand("bzn");
	bz_removeEvent(bz_eTickEvent, &singleEventHandler);
	bz_removeEvent(bz_ePlayerJoinEvent, &singleEventHandler);
	bz_removeEvent(bz_ePlayerPartEvent, &singleEventHandler);
	bzn_outputData("bznunload");
	playerIdsByCallsign.clear();
	return 0;
}

void bzn_outputData(std::string value)
{
	if (value.length() > 65523)
	{
		bzn_outputData(
				"bznerror excessiveoutput The output length was too long.");
		return;
	}
	printf("|%.5d%s\n", value.length(), value.c_str());
}

void* threadedStdinReadLoop(void* bogus)
{
	while (true)
	{
		getline(cin, currentStdinString);
		std::string* newReadString =
				new std::string(currentStdinString.c_str());
		pthread_mutex_lock(&stdinListLock);
		stdinList.push_back(newReadString);
		pthread_mutex_unlock(&stdinListLock);
	}
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
	/*
	 * If the vector is not empty, we clear it. Then, while...
	 */
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
