package com.mygdx.drop.etc;

import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.handlers.EventHandler;

/**
 * An object capable of listening for {@link Event}s
 */
public interface EventListener {
	/**
	 * Sets up an {@link EventHandler} to be called when an {@link Event} is {@link #fire(Event) fired}
	 * 
	 * @param handler
	 */
	public void addHandler(EventHandler handler);

	/**
	 * Removes a previously set {@link EventHandler}
	 * 
	 * @param handler The handler to be removed
	 * @return {@code true} if the handler was found and removed, {@code false} otherwise
	 */
	public boolean removeHandler(EventHandler handler); // TODO all implementations of this method are O(n), optimize to O(1)

	/**
	 * Fires an event on the current object. NOTE: event handlers may call this very method to chain
	 * events, implementors must take care to process events in chronological order
	 * 
	 * @param event The event to be handled
	 */
	public void fire(Event event);

}
