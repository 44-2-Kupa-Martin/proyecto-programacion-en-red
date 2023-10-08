package com.mygdx.drop.etc.events;

/**
 * The base class for an Event
 *
 */
public class Event {
	/** The meaning of this flag is event-dependent, but as a general rule of thumb an event is considered handled if one or many (unspecified) actions or side-effects were successfully performed */
	private boolean handled;

	/** NOTE: this is almost never enforced */
	//TODO enforce this
	/** {@code true} means propagation was stopped */
	private boolean stopped;

	public Event() {}
	/**
	 * Marks this event as handled.
	 */
	public void handle() { handled = true; }
	
	/** Marks this event as stopped */
	public void stop() { this.stopped = true; }

	/** {@link #handle()} */
	public boolean isHandled() { return handled; }
	
	public boolean isStopped() { return stopped; }
}
