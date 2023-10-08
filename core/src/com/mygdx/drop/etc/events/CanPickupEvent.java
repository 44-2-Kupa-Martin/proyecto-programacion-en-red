package com.mygdx.drop.etc.events;

import com.mygdx.drop.game.dynamicentities.DroppedItem;

/**
 * Signals that the pickup delay of the {@linkplain DroppedItem} has ended. If the event is handled the item is considered to have been picked up, propagation is stopped and the DroppedItem is disposed
 */
public class CanPickupEvent extends Event {
	public final DroppedItem droppedItem;
	public CanPickupEvent(DroppedItem droppedItem) {
		this.droppedItem = droppedItem;
	}
	
	@Override
	public final void handle() { 
		super.handle();
		stop();
	}
}
