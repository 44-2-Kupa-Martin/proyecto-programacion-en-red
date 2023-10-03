package com.mygdx.drop.etc.events.handlers;

import com.mygdx.drop.etc.events.CanPickupEvent;
import com.mygdx.drop.etc.events.Event;

public class CanPickupEventHandler implements EventListener {

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof CanPickupEvent))
			return false;

		CanPickupEvent canPickupEvent = (CanPickupEvent) event;
		return onCanPickup(canPickupEvent);
	}

	public boolean onCanPickup(CanPickupEvent event) { return false; }

}
