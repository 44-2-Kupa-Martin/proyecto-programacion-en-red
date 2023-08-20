package com.mygdx.drop.etc.events.handlers;

import com.mygdx.drop.etc.events.Event;

/**
 * An object capable of handling an {@link Event}
 */
public interface EventHandler<EventType extends Event<? extends Object>> {
	/**
	 * Try to handle the given event, if it is applicable.
	 * 
	 * @return true if the event should be considered {@link Event#handle() handled}
	 */
	public boolean handle(EventType event);

}
