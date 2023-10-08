package com.mygdx.drop.game;

import java.util.Iterator;

import com.mygdx.drop.etc.ObservableReference;

public class Inventory {
	protected final ObservableReference<Item>[] items;
	
	@SuppressWarnings("unchecked")
	public Inventory(int size) {
		this.items = (ObservableReference<Item>[]) new ObservableReference<?>[size];
	}
	
	/**
	 * Finds the first free slot in the inventory
	 * 
	 * @return the index of the free slot or {@code -1} if there isn't
	 */
	public int findFreeInventorySlot() {
		for (int i = 0; i < items.length; i++) {
			if (items[i].get() == null)
				return i;
		}
		return -1;
	}
	
	public <T extends Item> boolean hasItem(Class<T> itemClass) {
		for (int i = 0; i < items.length; i++) {
			if (itemClass.isInstance(items[i].get()))
				return true;
		}
		return false;
	}
	
	public <T extends Item> void consumeItem(Class<T> itemClass) {
		for (int i = 0; i < items.length; i++) {
			if (itemClass.isInstance(items[i].get())) {
				if (items[i].get().consume()) 
					items[i].set(null);
				break;
			}
		}
	}
	
	public ObservableReference<Item> getItemReference(int index) {
		return items[index];
	}
}
