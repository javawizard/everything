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

bz_eTeamType colorNameToDef(const char* color);
const char* colorDefToName(bz_eTeamType team);
int parseInt(std::string value);
void bzn_outputData(std::string value);
void stringSplit(std::string string, std::vector<std::string>* vector,
		int maxItems);

void processStdinString(std::string* currentString)
{
	size_t firstSpaceIndex = currentString->find(" ");
	if (firstSpaceIndex == std::string::npos)
	{
		bzn_outputData(
				"bznerror noinputspace There was no space in the input line.");
		return;
	}
	std::string command = currentString->substr(0, firstSpaceIndex);
	std::string arguments = currentString->substr(firstSpaceIndex + 1,
			std::string::npos);
	if (command == "say")
	{
		bz_sendTextMessage(BZ_SERVER, BZ_ALLUSERS, arguments.c_str());
	}
	else if (command == "saytofromplayer")
	{
		int space1 = arguments.find(" ");
		int space2 = arguments.find(" ", space1 + 1);
		std::string string1 = arguments.substr(0, space1);
		std::string string2 = arguments.substr(space1 + 1, (space2 - space1)
				- 1);
		bz_sendTextMessage(BZ_SERVER, BZ_ALLUSERS, arguments.c_str());
	}
	else
	{
		bzn_outputData(
				"bznerror invalidcommand The command specified is not valid");
	}
}

// START EVENT HANDLERS

void eProcessTickEvent(bz_TickEventData *eventData)
{
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

// END EVENT HANDLERS

class BZNetworkEventHandler: public bz_EventHandler,
		public bz_CustomSlashCommandHandler
{
	public:
		virtual void process(bz_EventData *eventData)
		{
			if (eventData->eventType == bz_eTickEvent)
			{
				eProcessTickEvent((bz_TickEventData*) eventData);
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
	// Perhaps allow this to be configured via an argument, and
	// then have this value be a BZNetwork configuration setting
	bz_setMaxWaitTime(1.0);
	if (pthread_create(&stdinReadThread, NULL, &threadedStdinReadLoop, NULL))
	{
		bzn_outputData(
				"bznfail readthread The stdin read thread could not be created.");
		return 1;
	}
	bzn_outputData("bznload");
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

int bz_Unload(void)
{
	bz_removeCustomSlashCommand("bzn");
	bz_removeEvent(bz_eTickEvent, &singleEventHandler);
	bzn_outputData("bznunload");
	return 0;
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
		std::string search = " ", int maxItems = 10000)
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
	}
	if (afterLastMatchedIndex < string.size())
	{
		vector->push_back(string.substr(afterLastMatchedIndex,
				std::string::npos));
	}
}
