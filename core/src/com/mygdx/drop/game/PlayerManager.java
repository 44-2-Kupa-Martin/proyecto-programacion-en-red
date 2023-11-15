package com.mygdx.drop.game;

import java.io.Serializable;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.drop.game.Item.Category;

/**
 * Represents a class capable of managing what the player sees and does.
 */
public interface PlayerManager {
	/** Called when a key was pressed
	 * 
	 * @param keycode one of the constants in {@link Input.Keys}
	 * @return whether the input was processed */
	public boolean keyDown (String playerName, int keycode);

	/** Called when a key was released
	 * 
	 * @param keycode one of the constants in {@link Input.Keys}
	 * @return whether the input was processed */
	public boolean keyUp (String playerName, int keycode);

	/** Called when a key was typed
	 * 
	 * @param character The character
	 * @return whether the input was processed */
	public boolean keyTyped (String playerName, char character);

	/** Called when the screen was touched or a mouse button was pressed. The button parameter will be {@link Buttons#LEFT} on iOS.
	 * @param worldX The x coordinate, origin is in the upper left corner
	 * @param worldY The y coordinate, origin is in the upper left corner
	 * @param pointer the pointer for the event.
	 * @param button the button
	 * @return whether the input was processed */
	public boolean touchDown (String playerName, float worldX, float worldY, int pointer, int button);

	/** Called when a finger was lifted or a mouse button was released. The button parameter will be {@link Buttons#LEFT} on iOS.
	 * @param pointer the pointer for the event.
	 * @param button the button
	 * @return whether the input was processed */
	public boolean touchUp (String playerName, float worldX, float worldY, int pointer, int button);

	/** Called when the touch gesture is cancelled. Reason may be from OS interruption to touch becoming a large surface such as
	 * the user cheek). Relevant on Android and iOS only. The button parameter will be {@link Buttons#LEFT} on iOS.
	 * @param pointer the pointer for the event.
	 * @param button the button
	 * @return whether the input was processed */
	public boolean touchCancelled (String playerName, float worldX, float worldY, int pointer, int button);

	/** Called when a finger or the mouse was dragged.
	 * @param pointer the pointer for the event.
	 * @return whether the input was processed */
	public boolean touchDragged (String playerName, float worldX, float worldY, int pointer);

	/** Called when the mouse was moved without any buttons being pressed. Will not be called on iOS.
	 * @return whether the input was processed */
	public boolean mouseMoved (String playerName, float worldX, float worldY);

	/** Called when the mouse wheel was scrolled. Will not be called on iOS.
	 * @param amountX the horizontal scroll amount, negative or positive depending on the direction the wheel was scrolled.
	 * @param amountY the vertical scroll amount, negative or positive depending on the direction the wheel was scrolled.
	 * @return whether the input was processed. */
	public boolean scrolled (String playerName, float amountX, float amountY);
	
	public Item getItem(String playerName, int index);
	
	public Item getCursorItem(String playerName);
	
	public int getSelectedSlot(String playerName);
	
	public Stats getStats(String playerName);
	
	public void swapItem(String playerName, int index1, int index2);
	
	public Vector2 getPlayerPosition(String playerName);
	
	/** The height of the world in tiles */
	public int getWorldHeight();
	/** The width of the world in tiles */
	public int getWorldWidth();
	
	/**
	 * All the components of the latest frame that is to be shown on the screen
	 * @return An array of components to be drawn
	 */
	public FrameComponent[] getFrameData();
	
	/**
	 * A component/part of a frame  
	 */
	
	public boolean isConnected();
	
	public void closeSession();
	
	public void step(float ola);
	
	public static class FrameComponent implements Serializable {
		private static final long serialVersionUID = 3776117174371190594L;
		public final float x_mt;
		public final float y_mt;
		public final float width_mt;
		public final float height_mt;
		public final float rotation_deg;
		public final int assetId;
		/** -1 for static sprites */
		public final int animationFrameIndex;
		
		public FrameComponent(int assetId, float x_mt, float y_mt, float width_mt, float height_mt) {
			this(assetId, x_mt, y_mt, width_mt, height_mt, 0, -1);
		}
		
		public FrameComponent(int assetId, float x_mt, float y_mt, float width_mt, float height_mt, float rotation_deg) {
			this(assetId, x_mt, y_mt, width_mt, height_mt, rotation_deg, -1);
		}
		
		public FrameComponent(int assetId, float x_mt, float y_mt, float width_mt, float height_mt, float rotation_deg, int animationFrameIndex) {
			this.x_mt = x_mt;
			this.y_mt = y_mt;
			this.width_mt = width_mt;
			this.height_mt = height_mt;
			this.rotation_deg = rotation_deg;
			this.assetId = assetId;
			this.animationFrameIndex = animationFrameIndex;
		}
		
		
	}
	
	public static class ItemData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6332253545214934473L;
		public final Category category;
		public final int textureId;
		public ItemData(Category category, int textureId) {
			super();
			this.category = category;
			this.textureId = textureId;
		}
		
		
	}
	
	
}
