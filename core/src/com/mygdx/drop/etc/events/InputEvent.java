package com.mygdx.drop.etc.events;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Null;
import com.mygdx.drop.etc.events.handlers.InputEventHandler;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;

/**
 * Event for world input: touch, mouse, touch/mouse enter/exit entity, mouse scroll, and keyboard
 * events. If the event is marked {@link #handle() handled}, the {@link World} will eat the input
 * (see {@link InputMultiplexer})
 * 
 * @see InputEventHandler
 */
public class InputEvent extends Event {
	public final World world;
	/** The player resposable for the InputEvent */
	public final Player player;
	/** The target of the event, if null no entity was hit */
	public final @Null Entity target;
	private Type type;
	private float worldX_mt, worldY_mt, scrollAmountX, scrollAmountY;
	private int pointer, button, keyCode;
	private @Null Entity relatedEntity; // If an enter/exit event, this field contains the entity being exited/entered
	private char character;
	
	public InputEvent(World world, Player player, Entity target) {
		assert world != null;
		this.world = world;
		assert player != null;
		this.player = player;
		this.target = target;
	}
	
	/**
	 * The world where the InputEvent occurred
	 */
	public World getWorld() { return world; }
	
	public Entity getTarget() { return target; }
	
	/**
	 * The world x coordinate (in meters) where the event occurred. Valid for: touchDown, touchDragged,
	 * touchUp, mouseMoved, enter, and exit.
	 */
	public float getWorldX() { return worldX_mt; }

	public void setWorldX(float worldX_mt) { this.worldX_mt = worldX_mt; }

	/**
	 * The world y coordinate (in meters) where the event occurred. Valid for: touchDown, touchDragged,
	 * touchUp, mouseMoved, enter, and exit.
	 */
	public float getWorldY() { return worldY_mt; }

	public void setWorldY(float worldY_mt) { this.worldY_mt = worldY_mt; }

	/** The type of input event. */
	public Type getType() { return type; }

	public void setType(Type type) { this.type = type; }

	/**
	 * The pointer index for the event. The first touch is index 0, second touch is index 1, etc. Always
	 * -1 on desktop. Valid for: touchDown, touchDragged, touchUp, enter, and exit.
	 */
	public int getPointer() { return pointer; }

	public void setPointer(int pointer) { this.pointer = pointer; }

	/**
	 * The index for the mouse button pressed. Always 0 on Android. Valid for: touchDown and touchUp.
	 * 
	 * @see Buttons
	 */
	public int getButton() { return button; }

	public void setButton(int button) { this.button = button; }

	/** The key code of the key that was pressed. Valid for: keyDown and keyUp. */
	public int getKeyCode() { return keyCode; }

	public void setKeyCode(int keyCode) { this.keyCode = keyCode; }

	/** The character for the key that was typed. Valid for: keyTyped. */
	public char getCharacter() { return character; }

	public void setCharacter(char character) { this.character = character; }

	/** The amount the mouse was scrolled horizontally. Valid for: scrolled. */
	public float getScrollAmountX() { return scrollAmountX; }

	/** The amount the mouse was scrolled vertically. Valid for: scrolled. */
	public float getScrollAmountY() { return scrollAmountY; }

	public void setScrollAmountX(float scrollAmount) { this.scrollAmountX = scrollAmount; }

	public void setScrollAmountY(float scrollAmount) { this.scrollAmountY = scrollAmount; }

	public Entity getRelatedEntity() { return relatedEntity; }

	public void setRelatedEntity(Entity relatedEntity) { this.relatedEntity = relatedEntity; }

	public String toString() { return type.toString(); }

	/** Types of low-level input events supported by the world */
	static public enum Type {
		/** A new touch for a pointer on the world was detected */
		touchDown,
		/** A pointer has stopped touching the world */
		touchUp,
		/** A pointer that is touching the world has moved */
		touchDragged,
		/** The mouse pointer has moved (without a mouse button being active). */
		mouseMoved,
		/**
		 * The mouse pointer or an active touch have entered (i.e., {@link Entity#hit(float, float) hit}) an entity.
		 */
		enter,
		/** The mouse pointer or an active touch have exited an entity. */
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
