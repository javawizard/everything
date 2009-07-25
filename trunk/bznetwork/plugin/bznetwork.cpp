// genobait.cpp : Defines the entry point for the DLL application.
//

#include "bzfsAPI.h"
#include "plugin_utils.h"
#include <iostream>
#include <stdio.h>
#include <assert.h>
#include <pthread.h>

BZ_GET_PLUGIN_VERSION

std::vector<std::string> stdinList;
pthread_mutex_t stdinListLock =
PTHREAD_MUTEX_INITIALIZER;
pthread_t stdinReadThread;

void bzn_outputData(std::string value);

class BZNetworkEventHandler: public bz_EventHandler,
		public bz_CustomSlashCommandHandler
{
	public:
		virtual void process(bz_EventData *eventData)
		{
		}
		virtual bool handle(int playerID, bzApiString command,
				bzApiString message, bzAPIStringList *params)
		{
			if (strcasecmp(command.c_str(), "bzn")) // is it for me ?
				return false;
			bzn_outputData("Message received");
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
	assert(value.length() < 65529);
	printf("|%.5d%s\n", value.length(), value.c_str());
}

int bz_Unload(void)
{
	bz_removeCustomSlashCommand("bzn");
	bzn_outputData("bznunload");
	return 0;
}

void* threadedStdinReadLoop(void* bogus)
{

}

// Local Variables: ***
// mode:C++ ***
// tab-width: 8 ***
// c-basic-offset: 2 ***
// indent-tabs-mode: t ***
// End: ***
// ex: shiftwidth=2 tabstop=8

