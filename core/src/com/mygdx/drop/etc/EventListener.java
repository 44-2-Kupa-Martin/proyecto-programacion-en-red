package com.mygdx.drop.etc;

public interface EventListener {
	/** Try to handle the given event, if it is applicable.
	 * @return true if the event should be considered {@link Event#handle() handled} by the world. */
	public abstract boolean handle (Event event);
}
