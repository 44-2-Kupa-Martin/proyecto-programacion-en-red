package com.mygdx.drop.etc;

import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.handlers.EventHandler;

/**
 * An object capable of listening for {@link Event}s
 */
public interface EventListener<EventType extends Event<? extends Object>> {
	/**
	 * Sets up an {@link EventHandler} to be called when an {@link Event} is {@link #fire(Event) fired}
	 * 
	 * @param handler
	 */
	public void addHandler(EventHandler<EventType> handler);

	/**
	 * Removes a previously set {@link EventHandler}
	 * 
	 * @param handler The handler to be removed
	 * @return {@code true} if the handler was found and removed, {@code false} otherwise
	 */
	public boolean removeHandler(EventHandler<EventType> handler); //TODO all implementations of this method are O(n), optimize to O(1)
	
	/**
	 * Fires an event on the current object. The {@link Event#target} property will be set to
	 * {@code this} before calling the {@link EventHandler}s
	 * 
	 * @param event The event to be handled
	 * @return {@code true} if the event was cancelled, {@code false} otherwise
	 */
	public boolean fire(EventType event); //TODO: all implementations of this method ignore Event.handled() and Event.cancelled(). Enforce such cases

}
