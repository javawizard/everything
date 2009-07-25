// genobait.cpp : Defines the entry point for the DLL application.
//

#include "bzfsAPI.h"
#include "plugin_utils.h"
#include <iostream>

BZ_GET_PLUGIN_VERSION

BZF_PLUGIN_CALL int bz_Load(const char* /*commandLine*/) {
	cout << "-------- Loaded the genobait plugin" << endl;
	bz_debugMessage(4, "genobait plugin loaded");
	return 0;
}

BZF_PLUGIN_CALL int bz_Unload(void) {
	bz_debugMessage(4, "genobait plugin unloaded");
	return 0;
}

// Local Variables: ***
// mode:C++ ***
// tab-width: 8 ***
// c-basic-offset: 2 ***
// indent-tabs-mode: t ***
// End: ***
// ex: shiftwidth=2 tabstop=8

