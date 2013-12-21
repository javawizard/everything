/*
 * bz_PluginUtility - a namespace with helpful classes for making some things
 *                    easier when making BZFlag plugins
 * Copyright (C) 2009 Daniel Outmin (A Meteorite)

 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

#ifndef __BZ_PLUGINUTILITY_NAMESPACE__
#define __BZ_PLUGINUTILITY_NAMESPACE__

#ifndef __BZFSAPI_H__
#define __BZFSAPI_H__
#include "bzfsAPI.h"
#include <set>
#endif // __BZFSAPI_H__
namespace bz_PluginUtility
{

	typedef std::set<bz_eEventType, std::less<bz_eEventType> > RegisteredEvents;

	class EventRegistrar
	{
		public:
			EventRegistrar(bz_EventHandler* _handle);
			~EventRegistrar();

			bool registerEvent(const bz_eEventType& event);
			bool removeEvent(const bz_eEventType& event);
			void removeAllEvents();

		private:
			bz_EventHandler* handler;
			RegisteredEvents events;
	};

	EventRegistrar::EventRegistrar(bz_EventHandler* _handler) :
		handler(_handler)
	{
		return;
	}

	EventRegistrar::~EventRegistrar()
	{
		this->removeAllEvents();
	}

	bool EventRegistrar::registerEvent(const bz_eEventType& event)
	{
		std::pair<RegisteredEvents::iterator, bool> result = events.insert(
				event);

		if (result.second)
		{
			if (bz_registerEvent(event, handler))
				return true;
			else
			{ // for some reason, couldn't register the handler, so don't pretend like we did
				events.erase(result.first);
				return false;
			}
		}
		else
		{ // already registered
			return true;
		}
	}

	bool EventRegistrar::removeEvent(const bz_eEventType& event)
	{
		RegisteredEvents::size_type erased = events.erase(event);

		if (erased > 0)
		{
			return bz_removeEvent(event, handler);
		}
		else
		{ // already unregistered event
			return false;
		}
	}

	void EventRegistrar::removeAllEvents()
	{
		for (RegisteredEvents::iterator i = events.begin(); i != events.end(); ++i)
		{
			bz_removeEvent(*i, handler);
		}
	}

} // bz_PluginUtility namespace

#endif // __BZ_PLUGINUTILITY_NAMESPACE__
