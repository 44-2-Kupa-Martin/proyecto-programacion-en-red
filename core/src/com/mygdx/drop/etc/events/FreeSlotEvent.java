package com.mygdx.drop.etc.events;

import com.mygdx.drop.etc.Reference;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.dynamicentities.Player;

public class FreeSlotEvent extends Event {
	public final Player player;
	public final int slotIndex;
	private Reference<Item> freeSlot;
	
	public FreeSlotEvent(Player player, int slotIndex) {
		this.player = player;
		this.slotIndex = slotIndex;
		this.freeSlot = player.items.getItemReference(slotIndex);
	}
	
	public void putItemIntoSlot(Item item) {
		assert item != null;
		stop();
		freeSlot.set(item);
	}
}
