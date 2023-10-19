package com.mygdx.drop.etc.events.listeners;

import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.FreeSlotEvent;

public class FreeSlotEventListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof FreeSlotEvent)) 
			return false;
		FreeSlotEvent freeSlotEvent = (FreeSlotEvent)event;
		return onFreeSlot(freeSlotEvent); 
	}

	public boolean onFreeSlot(FreeSlotEvent event) { return false; }
}
