package com.mygdx.drop.etc.events.handlers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.game.Entity;

/**
 * Unpacks and handles {@link InputEvent}s
 */
public class InputEventHandler implements EventHandler<InputEvent> {
	static private final Vector2 tmpCoords = new Vector2();

	/**
	 * Try to handle the given event, if it is an {@link InputEvent}.
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean handle(InputEvent event) {
		assert event.getTarget() != null : "All events must have a target";

		switch (event.getType()) {
			case keyDown:
				return keyDown(event, event.getKeyCode());
			case keyUp:
				return keyUp(event, event.getKeyCode());
			case keyTyped:
				return keyTyped(event, event.getCharacter());
		}

		tmpCoords.set(event.getTarget().getRelativeCoordinates(tmpCoords.set(event.getWorldX(), event.getWorldY())));

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

	/**
	 * Called when a mouse button or a finger touch goes down on the entity. When true is returned, the
	 * event is {@link Event#handle() handled}.
	 * 
	 * @see InputEvent
	 */
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { return false; }

	/**
	 * Called when a mouse button or a finger touch goes up anywhere, but only if touchDown previously
	 * returned true for the mouse button or touch. The touchUp event is always {@link Event#handle()
	 * handled}.
	 * 
	 * @see InputEvent
	 */
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {}

	/**
	 * Called when a mouse button or a finger touch is moved anywhere, but only if touchDown previously
	 * returned true for the mouse button or touch. The touchDragged event is always
	 * {@link Event#handle() handled}.
	 * 
	 * @see InputEvent
	 */
	public void touchDragged(InputEvent event, float x, float y, int pointer) {}

	/**
	 * Called any time the mouse is moved when a button is not down. This event only occurs on the
	 * desktop. When true is returned, the event is {@link Event#handle() handled}.
	 * 
	 * @see InputEvent
	 */
	public boolean mouseMoved(InputEvent event, float x, float y) { return false; }

	/**
	 * Called any time the mouse cursor or a finger touch is moved over an actor. On the desktop, this
	 * event occurs even when no mouse buttons are pressed (pointer will be -1).
	 * 
	 * @param fromEntity May be null.
	 * @see InputEvent
	 */
	public void enter(InputEvent event, float x, float y, int pointer, Entity fromEntity) {}

	/**
	 * Called any time the mouse cursor or a finger touch is moved out of an actor. On the desktop, this
	 * event occurs even when no mouse buttons are pressed (pointer will be -1).
	 * 
	 * @param toEntity May be null.
	 * @see InputEvent
	 */
	public void exit(InputEvent event, float x, float y, int pointer, Entity toEntity) {}

	/**
	 * Called when the mouse wheel has been scrolled. When true is returned, the event is
	 * {@link Event#handle() handled}.
	 */
	public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) { return false; }

	/**
	 * Called when a key goes down. When true is returned, the event is {@link Event#handle() handled}.
	 */
	public boolean keyDown(InputEvent event, int keycode) { return false; }

	/**
	 * Called when a key goes up. When true is returned, the event is {@link Event#handle() handled}.
	 */
	public boolean keyUp(InputEvent event, int keycode) { return false; }

	/**
	 * Called when a key is typed. When true is returned, the event is {@link Event#handle() handled}.
	 * 
	 * @param character May be 0 for key typed events that don't map to a character (ctrl, shift, etc).
	 */
	public boolean keyTyped(InputEvent event, char character) { return false; }

}
