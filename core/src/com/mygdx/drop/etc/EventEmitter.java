package com.mygdx.drop.etc;

import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.handlers.EventListener;

/**
 * An object capable of listening for {@link Event}s
 */
public interface EventEmitter {
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
	 * Fires an event on the current object. NOTE: event handlers may call this very method to chain
	 * events, implementors must take care to process events in chronological order
	 * 
	 * @param event The event to be handled
	 */
	public void fire(Event event);

}
