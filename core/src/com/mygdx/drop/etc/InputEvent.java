package com.mygdx.drop.etc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Null;
import com.mygdx.drop.game.Entity;

/** Event for actor input: touch, mouse, touch/mouse actor enter/exit, mouse scroll, and keyboard events.
 * @see InputListener */
public class InputEvent extends Event {
	public InputEvent(Object source) { super(source); }

	private Type type;
	private float worldX, worldY, scrollAmountX, scrollAmountY;
	private int pointer, button, keyCode;
	private @Null Entity relatedEntity; // If an enter/exit event, this field contains the entity being exited/entered
	private char character;

	/** The world x coordinate where the event occurred. Valid for: touchDown, touchDragged, touchUp, mouseMoved, enter, and
	 * exit. */
	public float getWorldX () {
		return worldX;
	}

	public void setWorldX (float worldX) {
		this.worldX = worldX;
	}

	/** The world x coordinate where the event occurred. Valid for: touchDown, touchDragged, touchUp, mouseMoved, enter, and
	 * exit. */
	public float getWorldY () {
		return worldY;
	}

	public void setWorldY (float worldY) {
		this.worldY = worldY;
	}

	/** The type of input event. */
	public Type getType () {
		return type;
	}

	public void setType (Type type) {
		this.type = type;
	}

	/** The pointer index for the event. The first touch is index 0, second touch is index 1, etc. Always -1 on desktop. Valid for:
	 * touchDown, touchDragged, touchUp, enter, and exit. */
	public int getPointer () {
		return pointer;
	}

	public void setPointer (int pointer) {
		this.pointer = pointer;
	}

	/** The index for the mouse button pressed. Always 0 on Android. Valid for: touchDown and touchUp.
	 * @see Buttons */
	public int getButton () {
		return button;
	}

	public void setButton (int button) {
		this.button = button;
	}

	/** The key code of the key that was pressed. Valid for: keyDown and keyUp. */
	public int getKeyCode () {
		return keyCode;
	}

	public void setKeyCode (int keyCode) {
		this.keyCode = keyCode;
	}

	/** The character for the key that was typed. Valid for: keyTyped. */
	public char getCharacter () {
		return character;
	}

	public void setCharacter (char character) {
		this.character = character;
	}

	/** The amount the mouse was scrolled horizontally. Valid for: scrolled. */
	public float getScrollAmountX () {
		return scrollAmountX;
	}

	/** The amount the mouse was scrolled vertically. Valid for: scrolled. */
	public float getScrollAmountY () {
		return scrollAmountY;
	}

	public void setScrollAmountX (float scrollAmount) {
		this.scrollAmountX = scrollAmount;
	}

	public void setScrollAmountY (float scrollAmount) {
		this.scrollAmountY = scrollAmount;
	}
	
	public Entity getRelatedEntity() { return relatedEntity; }
	
	public void setRelatedEntity(Entity relatedEntity) { this.relatedEntity = relatedEntity; }

	public String toString () {
		return type.toString();
	}

	/** Types of low-level input events supported by scene2d. */
	static public enum Type {
		/** A new touch for a pointer on the stage was detected */
		touchDown,
		/** A pointer has stopped touching the stage. */
		touchUp,
		/** A pointer that is touching the stage has moved. */
		touchDragged,
		/** The mouse pointer has moved (without a mouse button being active). */
		mouseMoved,
		/** The mouse pointer or an active touch have entered (i.e., {@link Actor#hit(float, float, boolean) hit}) an actor. */
		enter,
		/** The mouse pointer or an active touch have exited an actor. */
		exit,
		/** The mouse scroll wheel has changed. */
		scrolled,
		/** A keyboard key has been pressed. */
		keyDown,
		/** A keyboard key has been released. */
		keyUp,
		/** A keyboard key has been pressed and released. */
		keyTyped
	}
}
