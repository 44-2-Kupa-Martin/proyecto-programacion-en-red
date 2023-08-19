package com.mygdx.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.drop.Assets.SkinId;

public class MainMenuScreen implements Screen {
	private final Drop game;
	private final OrthographicCamera camera;
	private Stage stage;

	public MainMenuScreen(Drop game) {
		this.game = game;
		this.camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		this.stage = new Stage(new ScreenViewport());
	}

	@Override
	public void show() {
		
		Gdx.input.setInputProcessor(stage);
		
		//Create Table
		Table mainTable = new Table();
		//Set table to fill stage
		mainTable.setFillParent(true);
		//Set alignment of contents in the table
		mainTable.top();
		
		game.batch.begin();
		TextButton playButton = new TextButton("Play",game.assets.get(SkinId.Glassy_glassy));
		TextButton optionsButton = new TextButton("Options",game.assets.get(SkinId.Glassy_glassy));
		TextButton exitButton = new TextButton("Exit",game.assets.get(SkinId.Glassy_glassy));
		
		game.batch.end();
		
		//Add listeners to buttons
		playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(game));
            }
        });
		
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		//Add buttons to table
		mainTable.add(playButton);
		mainTable.row();
		
		mainTable.add(optionsButton);
		mainTable.row();
		
		mainTable.add(exitButton);
		
		//Add table to stage
		stage.addActor(mainTable);
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
		
		// See below for what true means.
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