package com.mygdx.drop.etc;

import com.badlogic.gdx.utils.Array;
import com.mygdx.drop.EventManager;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.listeners.EventListener;

/**
 * An object capable of listening for {@link Event}s
 */
public interface EventCapable {
	/**
	 * Sets up an {@link EventListener} to be called when an {@link Event} is {@link #fire(Event) fired}
	 * 
	 * @param listener
	 */
	public void addListener(EventListener listener);

	/**
	 * Removes a previously set {@link EventListener}
	 * 
	 * @param listener The listener to be removed
	 * @return {@code true} if the listener was found and removed, {@code false} otherwise
	 */
	public boolean removeListener(EventListener listener); // TODO all implementations of this method are O(n), optimize to O(1)
	
	/**
	 * Only meant to be called by {@link EventManager}
	 * @return the listeners to be called
	 */
	public Array<EventListener> getListeners();
}
