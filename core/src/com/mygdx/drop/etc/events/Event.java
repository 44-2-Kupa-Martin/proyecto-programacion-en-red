package com.mygdx.drop.etc.events;

import com.mygdx.drop.etc.EventCapable;

/**
 * The base class for an Event
 */
public class Event<T extends EventCapable> {
	/** The target of the event, i.e the object whose listeners will be called */
	public final T target;
	
	/** The meaning of this flag is event-dependent, but as a general rule of thumb an event is considered handled if one or many (unspecified) actions or side-effects were successfully performed */
	private boolean handled;

	/** {@code true} means propagation was stopped */
	private boolean stopped;

	public Event(T target) {
		assert target != null;
		this.target = target;
	}
	
	public T getTarget() { return target; }
	
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
