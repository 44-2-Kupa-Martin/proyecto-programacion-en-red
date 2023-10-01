package com.mygdx.drop.etc.events.handlers;

import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.FreeSlotEvent;

public class FreeSlotEventHandler implements EventHandler {

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof FreeSlotEvent)) 
			return false;
		FreeSlotEvent freeSlotEvent = (FreeSlotEvent)event;
		return onFreeSlot(freeSlotEvent); 
	}

	public boolean onFreeSlot(FreeSlotEvent event) { return false; }
}
