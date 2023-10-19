package com.mygdx.drop.etc.events.listeners;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.game.Entity;

/**
 * Unpacks and handles {@link InputEvent}s
 */
public class InputEventListener implements EventListener {
	static private final Vector2 tmpCoords = new Vector2();

	/**
	 * Try to handle the given event, if it is an {@link InputEvent}.
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean handle(Event event) {
		if (!(event instanceof InputEvent)) 
			return false;
		InputEvent inputEvent = (InputEvent)event;
		
//		assert inputEvent.getTarget() != null : "All input events must have a target";

		switch (inputEvent.getType()) {
			case keyDown:
				return keyDown(inputEvent, inputEvent.getKeyCode());
			case keyUp:
				return keyUp(inputEvent, inputEvent.getKeyCode());
			case keyTyped:
				return keyTyped(inputEvent, inputEvent.getCharacter());
		}
		
		if (inputEvent.getTarget() instanceof Entity) {
			tmpCoords.set(((Entity)inputEvent.getTarget()).getRelativeCoordinates(tmpCoords.set(inputEvent.getWorldX(), inputEvent.getWorldY())));			
		} else {
			tmpCoords.set(inputEvent.getWorldX(), inputEvent.getWorldY());
		}

		switch (inputEvent.getType()) {
			case touchDown:
				return touchDown(inputEvent, tmpCoords.x, tmpCoords.y, inputEvent.getPointer(), inputEvent.getButton());
			case touchUp:
				touchUp(inputEvent, tmpCoords.x, tmpCoords.y, inputEvent.getPointer(), inputEvent.getButton());
				return true;
			case touchDragged:
				touchDragged(inputEvent, tmpCoords.x, tmpCoords.y, inputEvent.getPointer());
				return true;
			case mouseMoved:
				return mouseMoved(inputEvent, tmpCoords.x, tmpCoords.y);
			case scrolled:
				return scrolled(inputEvent, tmpCoords.x, tmpCoords.y, inputEvent.getScrollAmountX(), inputEvent.getScrollAmountY());
			case enter:
				enter(inputEvent, tmpCoords.x, tmpCoords.y, inputEvent.getPointer(), inputEvent.getRelatedEntity());
				return false;
			case exit:
				exit(inputEvent, tmpCoords.x, tmpCoords.y, inputEvent.getPointer(), inputEvent.getRelatedEntity());
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
