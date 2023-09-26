package com.mygdx.drop.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class OptionsInputProcessor implements InputProcessor {
	private final Slider volumeSlider;
    
	public OptionsInputProcessor(Slider volumeSlider) {
        this.volumeSlider = volumeSlider;
    }
	
    @Override
    public boolean keyDown(int keycode) {
        
        return false;
    }

	@Override
	public boolean keyUp(int keycode) {
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		Gdx.app.log("click", "Slider clicked");
		// Convert screen coordinates to stage coordinates
        float stageX = screenX;
        float stageY = Gdx.graphics.getHeight() - screenY; // Invert Y-axis

        // Check if the touch event is within the slider's bounds
        if (volumeSlider.hit(stageX, stageY, true) != null) {
        	Gdx.app.log("Slider", "Slider clicked");
        	
        	// Get the current value of the slider (percentage)
            float volume = volumeSlider.getValue();
            
            
            
            return true; // Consume the event
        }
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		
		return false;
	}

    // Implement other InputProcessor methods as needed
}
