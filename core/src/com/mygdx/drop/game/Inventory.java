package com.mygdx.drop.game;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.mygdx.drop.etc.EventCapable;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.etc.events.listeners.EventListener;

public class Inventory implements EventCapable {
	private final Array<EventListener> listeners;
	protected final ObservableReference<Item>[] items;

	
	@SuppressWarnings("unchecked")
	public Inventory(int size) {
		this.listeners = new Array<>();
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

	@Override
	public void addListener(EventListener listener) {listeners.add(listener);}

	@Override
	public boolean removeListener(EventListener listener) { return listeners.removeValue(listener, false); }

	@Override
	public Array<EventListener> getListeners() { return listeners; }
}
