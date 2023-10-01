package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets.SkinId;

public class OptionsScreen implements Screen {
	private final Drop game;
	private final Stage stage;
	private final Slider volumeSlider;
	private final Slider zoomSlider;
	private final OrthographicCamera camera;
	private final Label volumeLabel;
	private final Label zoomLabel;
	private final Skin skin;
	private final TextButton backButton;

	private final Viewport viewport;

	public OptionsScreen(Drop game) {
		this.game = game;
		this.camera = new OrthographicCamera();
		this.viewport = new ScreenViewport(camera);
		this.stage = new Stage(viewport);
		camera.setToOrtho(false, 800, 480);

		skin = game.assets.get(SkinId.Glassy_glassy);
		volumeSlider = new Slider(0.0f, 1.0f, 0.01f, false, skin); // Adjust min, max, and step size
		volumeSlider.setValue(game.masterVolume); // Set the initial volume level
		volumeLabel = new Label("Volume: " + (int)(game.masterVolume * 100) + "%", skin);
		zoomSlider = new Slider(0.01f, 2, 0.01f, false, skin);
		zoomSlider.setValue(game.zoom);
		zoomLabel = new Label("Zoom: " + (int)(game.zoom * 100) + "%", skin);
		backButton = new TextButton("Back to Main Menu", skin, "small");
		backButton.setTransform(true);

		// Add a listener to the volumeSlider to handle changes
		volumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float volume = volumeSlider.getValue();
				// TODO Update the master volume level here (e.g., using AudioManager or similar)
				game.masterVolume = volume;
				volumeLabel.setText("Volume: " + (int) (volume * 100) + "%");
			}
		});
		
		zoomSlider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float zoom = zoomSlider.getValue();
				game.zoom = zoom;
				zoomLabel.setText("Zoom: " + (int) (zoom * 100) + "%");
			}
			
		});

		// Add a click listener to the "Back" button
		backButton.addListener(new ClickListener() {
			//TODO possibly a memory leak, previous screen was not dispose()d
			@Override
			public void clicked(InputEvent event, float x, float y) { game.setScreen(new MainMenuScreen(game)); }
		});

		System.out.println(backButton.getHeight() + "Altura" + "    " + backButton.getWidth());

		Table table = new Table();
		table.setSkin(skin);
		table.setFillParent(true);
		table.setTouchable(Touchable.enabled);
		table.add(volumeSlider).width(200);
		table.row();
		table.add(volumeLabel).colspan(2).center();
		table.row();
		table.add(zoomSlider).width(200);
		table.row();
		table.add(zoomLabel).width(200);
		table.row();
		table.add(backButton).align(Align.left);
		table.row();

		stage.addActor(table);

//		Table backButtonTable = new Table();
//
//		backButtonTable.setDebug(true);
//
//		backButtonTable.left().bottom();
//
//		backButtonTable.padLeft(10f).padBottom(10f);
//
//		backButtonTable.add(backButton).width(200).height(100);
//
//		stage.addActor(backButtonTable);
	}

	@Override
	public void show() { Gdx.input.setInputProcessor(stage); }

	@Override
	public void render(float delta) {
//		Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
//		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
//		camera.update();
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
