package com.mygdx.drop.game.protocol;

import java.io.Serializable;

import com.mygdx.drop.game.Entity;

public class InputReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7733979816571971320L;
	public final String playerName;
	public final Type type;
	public final float worldX_mt, worldY_mt, scrollAmountX, scrollAmountY;
	public final int pointer, button, keyCode;
	public final char character;

	public InputReport(String playerName, Type type, char character) {
		this(playerName, type, 0, 0, 0, 0, 0, 0, 0, character);
	}
	public InputReport(String playerName, Type type, int keyCode) {
		this(playerName, type, 0, 0, 0, 0, 0, 0, keyCode, (char)0);
	}
	
	public InputReport(String playerName, Type type, float worldX_mt, float worldY_mt) {
		this(playerName, type, worldX_mt, worldY_mt, 0, 0, 0, 0, 0, (char)0);
	}
	public InputReport(String playerName, Type type, float worldX_mt, float worldY_mt, int pointer) {
		this(playerName, type, worldX_mt, worldY_mt, 0, 0, pointer, 0, 0, (char)0);
	}
	public InputReport(String playerName, Type type, float worldX_mt, float worldY_mt, int pointer, int button) {
		this(playerName, type, worldX_mt, worldY_mt, 0, 0, pointer, button, 0, (char)0);
	}
	
	public InputReport(String playerName, Type type, float worldX_mt, float worldY_mt, float scrollAmountX, float scrollAmountY,
			int pointer, int button, int keyCode, char character) {
		super();
		this.playerName = playerName;
		this.type = type;
		this.worldX_mt = worldX_mt;
		this.worldY_mt = worldY_mt;
		this.scrollAmountX = scrollAmountX;
		this.scrollAmountY = scrollAmountY;
		this.pointer = pointer;
		this.button = button;
		this.keyCode = keyCode;
		this.character = character;
	}

	public static enum Type {
		/** A new touch for a pointer on the world was detected */
		touchDown,
		/** A pointer has stopped touching the world */
		touchUp,
		/** A pointer that is touching the world has moved */
		touchDragged,
		touchCancelled,
		/** The mouse pointer has moved (without a mouse button being active). */
		mouseMoved,
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
