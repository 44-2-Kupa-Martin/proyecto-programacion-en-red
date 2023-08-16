package com.mygdx.drop.etc;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.drop.game.Entity;

public class InputListener implements EventListener {
	static private final Vector2 tmpCoords = new Vector2();

	/** Try to handle the given event, if it is an {@link InputEvent}.
	 * <p>
	 * If the input event is of type {@link InputEvent.Type#touchDown} and {@link InputEvent#getTouchFocus()} is true and
	 * {@link #touchDown(InputEvent, float, float, int, int)} returns true (indicating the event was handled) then this listener is
	 * added to the stage's {@link Stage#addTouchFocus(EventListener, Actor, Actor, int, int) touch focus} so it will receive all
	 * touch dragged events until the next touch up event. */
	public boolean handle (Event e) {
		if (!(e instanceof InputEvent)) return false;
		InputEvent event = (InputEvent)e;
		Entity listenerOwner = (Entity)(event.listenerOwner); 

		switch (event.getType()) {
		case keyDown:
			return keyDown(event, event.getKeyCode());
		case keyUp:
			return keyUp(event, event.getKeyCode());
		case keyTyped:
			return keyTyped(event, event.getCharacter());
		}

		tmpCoords.set(listenerOwner.getRelativeCoordinates(tmpCoords.set(event.getWorldX(), event.getWorldY())));

		switch (event.getType()) {
		case touchDown:
			return touchDown(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getButton());
		case touchUp:
			touchUp(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getButton());
			return true;
		case touchDragged:
			touchDragged(event, tmpCoords.x, tmpCoords.y, event.getPointer());
			return true;
		case mouseMoved:
			return mouseMoved(event, tmpCoords.x, tmpCoords.y);
		case scrolled:
			return scrolled(event, tmpCoords.x, tmpCoords.y, event.getScrollAmountX(), event.getScrollAmountY());
		case enter:
			enter(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getRelatedEntity());
			return false;
		case exit:
			exit(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getRelatedEntity());
			return false;
		}
		return false;
	}

	/** Called when a mouse button or a finger touch goes down on the actor. If true is returned, this listener will have
	 * {@link Stage#addTouchFocus(EventListener, Actor, Actor, int, int) touch focus}, so it will receive all touchDragged and
	 * touchUp events, even those not over this actor, until touchUp is received. Also when true is returned, the event is
	 * {@link Event#handle() handled}.
	 * @see InputEvent */
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return false;
	}

	/** Called when a mouse button or a finger touch goes up anywhere, but only if touchDown previously returned true for the mouse
	 * button or touch. The touchUp event is always {@link Event#handle() handled}.
	 * @see InputEvent */
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	}

	/** Called when a mouse button or a finger touch is moved anywhere, but only if touchDown previously returned true for the
	 * mouse button or touch. The touchDragged event is always {@link Event#handle() handled}.
	 * @see InputEvent */
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
	}

	/** Called any time the mouse is moved when a button is not down. This event only occurs on the desktop. When true is returned,
	 * the event is {@link Event#handle() handled}.
	 * @see InputEvent */
	public boolean mouseMoved (InputEvent event, float x, float y) {
		return false;
	}

	/** Called any time the mouse cursor or a finger touch is moved over an actor. On the desktop, this event occurs even when no
	 * mouse buttons are pressed (pointer will be -1).
	 * @param fromActor May be null.
	 * @see InputEvent */
	public void enter (InputEvent event, float x, float y, int pointer, Entity fromActor) {
	}

	/** Called any time the mouse cursor or a finger touch is moved out of an actor. On the desktop, this event occurs even when no
	 * mouse buttons are pressed (pointer will be -1).
	 * @param toActor May be null.
	 * @see InputEvent */
	public void exit (InputEvent event, float x, float y, int pointer, Entity toActor) {
	}

	/** Called when the mouse wheel has been scrolled. When true is returned, the event is {@link Event#handle() handled}. */
	public boolean scrolled (InputEvent event, float x, float y, float amountX, float amountY) {
		return false;
	}

	/** Called when a key goes down. When true is returned, the event is {@link Event#handle() handled}. */
	public boolean keyDown (InputEvent event, int keycode) {
		return false;
	}

	/** Called when a key goes up. When true is returned, the event is {@link Event#handle() handled}. */
	public boolean keyUp (InputEvent event, int keycode) {
		return false;
	}

	/** Called when a key is typed. When true is returned, the event is {@link Event#handle() handled}.
	 * @param character May be 0 for key typed events that don't map to a character (ctrl, shift, etc). */
	public boolean keyTyped (InputEvent event, char character) {
		return false;
	}
}

