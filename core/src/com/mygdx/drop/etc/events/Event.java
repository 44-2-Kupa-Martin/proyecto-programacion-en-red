package com.mygdx.drop.etc.events;

/**
 * The base class for an Event
 *
 */
public class Event {
	private boolean handled;
	/** NOTE: this is never enforced */
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
