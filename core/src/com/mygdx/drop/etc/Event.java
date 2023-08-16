package com.mygdx.drop.etc;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.drop.game.World;

public class Event {
	public final Object source;
	public World world;
	public Object target;
	public Object listenerOwner;
	private boolean handled; // true means the event was handled (the stage will eat the input)
	private boolean cancelled; // true means propagation was stopped and any action that this event would cause should not happen

	public Event(Object source) { this.source = source; }

	/**
	 * Marks this event as handled. This causes the {@link World} {@link InputProcessor} methods to
	 * return true, which will eat the event so it is not passed on to the next processor.
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

	public Object getTarget() { return target; }

	public void setTarget(Object target) { this.target = target; }

}
