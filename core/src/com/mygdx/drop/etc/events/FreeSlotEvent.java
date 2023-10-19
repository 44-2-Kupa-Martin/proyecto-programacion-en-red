package com.mygdx.drop.etc.events;

import com.mygdx.drop.etc.Reference;
import com.mygdx.drop.game.Inventory;
import com.mygdx.drop.game.Item;

/**
 * Signals that an slot in an inventory has been freed. If the event is handled the slot is considered to have been filled with an item 
 */
public class FreeSlotEvent extends Event<Inventory> {
	public final int slotIndex;
	private Reference<Item> freeSlot;
	
	public FreeSlotEvent(Inventory inventory, int slotIndex) {
		super(inventory);
		this.slotIndex = slotIndex;
		this.freeSlot = inventory.getItemReference(slotIndex);
	}
	
	public Inventory getInventory() {
		return target;
	}
	
	/**
	 * Calls {@link #handle()} implicitly
	 * @param item
	 */
	public void putItemIntoSlot(Item item) {
		assert item != null;
		freeSlot.set(item);
		handle();
	}
	
	@Override
	public void handle() { 
		super.handle();
		stop();
	}
}
