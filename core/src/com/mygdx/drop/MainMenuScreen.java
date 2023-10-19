package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MainMenuScreen implements Screen {
	private final Drop game;
	private final OrthographicCamera camera;
	private Stage stage;
	private Skin skin;
	private float stateTime = 0.0f;

	public MainMenuScreen(Drop game) {
		this.game = game;
	    this.camera = new OrthographicCamera();
	    this.stage = new Stage(new ExtendViewport(800, 480, camera)); // Use ExtendViewport
		
	}

	@Override
	public void show() {
		
		Gdx.input.setInputProcessor(stage);
		
		//Create Table
		Table mainTable = new Table();
		//Set table to fill stage
		mainTable.setFillParent(true);
		//Set alignment of contents in the table
		mainTable.center();
		
		game.batch.begin();
		
		skin = Assets.Skins.Glassy_glassy.get();
		
		Label titleLabel = new Label("Placeholder", skin);
		
		TextButton playButton = new TextButton("Play",skin);
		TextButton optionsButton = new TextButton("Options",skin);
		TextButton exitButton = new TextButton("Exit",skin);
		
		playButton.setTransform(true);
		optionsButton.setTransform(true);
		exitButton.setTransform(true);
		
		game.batch.end();
		
		titleLabel.setFontScale(2.0f * Gdx.graphics.getWidth() / 800f);
		
		//Add a click listener to Play button
		playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
                game.setScreen(new GameScreen(game));
            }
        });
		
		//Add a click listener to Options button
		
		optionsButton.addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	Gdx.app.log("click", "entre a opciones");
	        	game.setScreen(new OptionsScreen(game));
	        }
		});
		
		//Add a click listener to Exit button
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		
		//Set size to buttons
		
		 float buttonWidth = Gdx.graphics.getWidth() * 0.1f;
		 float buttonHeight = buttonWidth * 0.25f;
		 
//		 playButton.setSize(buttonWidth, buttonHeight);
//		 optionsButton.setSize(buttonWidth, buttonHeight);
//		 exitButton.setSize(buttonWidth, buttonHeight);
		
		//Add buttons and lable to table
		 
		mainTable.add(titleLabel).padTop(50).colspan(3).center().row();
		mainTable.row().padTop(200);
		
		mainTable.add(playButton).padBottom(20);
		mainTable.add(optionsButton).padBottom(20);
		mainTable.add(exitButton).padBottom(20).row();
		
		//Add table to stage
		stage.addActor(mainTable);
	}

	@Override
	public void render(float delta) {
		
		//Clear the screen
		
		Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Update the camera and viewport
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        
        //Background Image
        
        Animation<TextureRegion> mainMenuAnimation = new Animation<>(0.1f, 
        		Assets.Animations.menuBackground.get(), PlayMode.LOOP);
        TextureRegion currentFrame = mainMenuAnimation.getKeyFrame(stateTime);
        
        game.batch.begin();
        float viewportWidth = camera.viewportWidth;
        float viewportHeight = camera.viewportHeight;
        game.batch.draw(currentFrame, 0, 0, viewportWidth, viewportHeight);
       
        game.batch.end();
        
        stateTime += Gdx.graphics.getDeltaTime();
        
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
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		
		stage.dispose();
		
	}

}