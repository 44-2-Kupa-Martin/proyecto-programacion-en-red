package com.mygdx.drop.etc.events;

import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.dynamicentities.DroppedItem;

public class CanPickupEvent extends Event {
	public final DroppedItem droppedItem;
	private boolean pickedUp;
	public CanPickupEvent(DroppedItem droppedItem) {
		this.droppedItem = droppedItem;
		this.pickedUp = false;
	}
	
	public final boolean wasPickedUp() {
		return pickedUp;
	}
	
	public final void markPickedUp() {
		this.pickedUp = true;
	}
}
