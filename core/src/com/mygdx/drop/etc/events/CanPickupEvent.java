package com.mygdx.drop.etc.events;

import com.mygdx.drop.game.dynamicentities.DroppedItem;

/**
 * Signals that the pickup delay of the {@linkplain DroppedItem} has ended. If the event is handled the item is considered to have been picked up, propagation is stopped and the DroppedItem is disposed
 */
public class CanPickupEvent extends Event<DroppedItem> {
	public CanPickupEvent(DroppedItem droppedItem) {
		super(droppedItem);
	}
	
	@Override
	public final void handle() { 
		super.handle();
		stop();
	}
}
