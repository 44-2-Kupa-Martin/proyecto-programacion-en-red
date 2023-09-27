package com.mygdx.drop.etc.events;

/**
 * The base class for an Event
 *
 */
public class Event {
	private boolean handled;
	/** NOTE: this is never enforced */
	//TODO enforce this
	private boolean cancelled; // true means propagation was stopped and any action that this event would cause should not happen (to be implemented)

	public Event() {}
	/**
	 * Marks this event as handled.
	 */
	public void handle() { handled = true; }

	/**
	 * Marks this event cancelled. This {@link #handle() handles} the event and it also cancels any
	 * default action that would have been taken by the code that fired the event. Eg, if the event is
	 * for a checkbox being checked, cancelling the event could uncheck the checkbox.
	 */
	public void cancel() {
		cancelled = true;
		handled = true;
	}

	/** {@link #handle()} */
	public boolean isHandled() { return handled; }

	/** @see #cancel() */
	public boolean isCancelled() { return cancelled; }
}
