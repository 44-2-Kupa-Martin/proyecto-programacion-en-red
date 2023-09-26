package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.drop.Assets.SkinId;
import com.mygdx.drop.input.OptionsInputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class OptionsScreen implements Screen {
	private final Drop game;
    private Stage stage;
    private OptionsInputProcessor optionsInputProcessor;
    private Slider volumeSlider;
    private final OrthographicCamera camera;
    private Label volumeLabel;
    private Skin skin;

    public OptionsScreen(Drop game) {
    	this.game = game;
        this.stage = new Stage(new ScreenViewport());
        stage = new Stage(new ScreenViewport());
		this.camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		
        
        game.batch.begin();
        skin = game.assets.get(SkinId.Glassy_glassy);
    	volumeSlider = new Slider(0.0f, 1.0f, 0.01f, false, game.assets.get(SkinId.Glassy_glassy)); // Adjust min, max, and step size as needed
        volumeSlider.setValue(1.0f); // Set the initial volume level
    	
        try {
            volumeLabel = new Label("Volume: 100%", game.assets.get(SkinId.Glassy_glassy));
        } catch (Exception e) {
            e.printStackTrace(); 
            
        }
       
        game.batch.end();
        
        
        
        // Add a listener to the volumeSlider to handle changes
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                Gdx.app.log("Slider", "Slider value changed");
                // TODO Update the master volume level here (e.g., using AudioManager or similar)
                volumeLabel.setText("Volume: " + (int)(volume * 100) + "%");
             // TODO Also update the game's actual volume level here
            }
        });
        
    }

    @Override
    public void show() {
        
    	optionsInputProcessor = new OptionsInputProcessor(volumeSlider);
    	
    	Gdx.input.setInputProcessor(optionsInputProcessor);
    	
    	
        Table table = new Table();
        table.setSkin(skin);
        table.setFillParent(true);
        
        
        
        table.add("Master Volume: ");
        table.add(volumeSlider).width(200);
        table.row();
        table.add(volumeLabel).colspan(2).center(); 
        table.row();

        
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        
    	Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    	
    }

    @Override
    public void resize(int width, int height) {
        
    	stage.getViewport().update(width, height, true);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {
       
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void hide() {
       
    }

    @Override
    public void dispose() {
        
        stage.dispose();
    }
}
