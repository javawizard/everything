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

void bzn_outputData(std::string value);

void processStdinString(std::string* currentString)
{
	size_t firstSpaceIndex = currentString->find(" ");
	if (firstSpaceIndex == std::string::npos)
		bzn_outputData(
				"bznerror noinputspace There was no space in the input line.");
	std::string commandName = currentString->substr(0, firstSpaceIndex);
	std::string commandArguments = currentString->substr(firstSpaceIndex + 1,
			std::string::npos);
	printf("Command: \"%s\", Arguments: \"%s\"\n", commandName.c_str(),
			commandArguments.c_str());
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

// Local Variables: ***
// mode:C++ ***
// tab-width: 8 ***
// c-basic-offset: 2 ***
// indent-tabs-mode: t ***
// End: ***
// ex: shiftwidth=2 tabstop=8

