package com.mygdx.drop.etc.events;

import com.mygdx.drop.etc.EventListener;

/**
 * The base class for an Event
 *
 * @param <TargetType> The type of the object where the event is to be {@link EventListener#fire(Event) fired}
 */
public class Event<TargetType> {
	private TargetType target;
	private boolean handled;
	/** NOTE: this is never enforced */
	private boolean cancelled; // true means propagation was stopped and any action that this event would cause should not happen (to be implemented)

	public Event() {}
	public Event(TargetType target) { this.target = target; }

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

	public TargetType getTarget() { return target; }

	public void setTarget(TargetType target) { this.target = target; }

}
