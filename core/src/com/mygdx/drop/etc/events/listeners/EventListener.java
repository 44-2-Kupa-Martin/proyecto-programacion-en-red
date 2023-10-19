package com.mygdx.drop.etc.events.listeners;

import com.mygdx.drop.etc.events.Event;
//TODO rename all event handlers to listeners
/**
 * An object capable of handling an {@link Event}
 */
public interface EventListener {
	/**
	 * Try to handle the given event, if it is applicable.
	 * 
	 * @return true if the event should be considered {@link Event#handle() handled}
	 */
	public boolean handle(Event event);

}
