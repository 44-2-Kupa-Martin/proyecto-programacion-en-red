package com.mygdx.drop.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.GameScreen;
import com.mygdx.drop.MainMenuScreen;

public class GameMenu {

	private final Drop game;
	private Table escapeTable;
	private final TextButton resume;
	private final TextButton options;
	private final TextButton exit;
	private final TextButton back;
	private Table optionsTable;
	private final Slider volumeSlider;
	private final Slider zoomSlider;
	private final Label volumeLabel;
	private final Label zoomLabel;
	private final Skin skin;
	
	public GameMenu(Drop game) {
		
		assert Drop.game != null : "Inventory created before game instance!";
		this.game = Drop.game;
		
		skin = Assets.Skins.Glassy_glassy.get();

		
		this.resume = new TextButton("Resume", skin);
		this.options = new TextButton("Options", skin);
		this.exit = new TextButton("Exit", skin);
		this.volumeSlider = new Slider(0.0f, 1.0f, 0.01f, false, skin); // Adjust min, max, and step size
		this.volumeSlider.setValue(game.masterVolume); // Set the initial volume level
		this.volumeLabel = new Label("Volume: " + (int)(game.masterVolume * 100) + "%", skin);
		this.zoomSlider = new Slider(0.01f, 2, 0.01f, false, skin);
		this.zoomSlider.setValue(game.zoom);
		this.zoomLabel = new Label("Zoom: " + (int)(game.zoom * 100) + "%", skin);
		this.back= new TextButton("Back", skin, "small");
		this.back.setTransform(true);
		
		resume.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	escapeTable.setVisible(!escapeTable.isVisible());
            }
        });

		options.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	optionsTable.setVisible(!optionsTable.isVisible());
            	escapeTable.setVisible(!escapeTable.isVisible());
            }
        });
		
		exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
                game.setScreen(new MainMenuScreen(game));
            }
        });
		
		back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	optionsTable.setVisible(!optionsTable.isVisible());
            	escapeTable.setVisible(!escapeTable.isVisible());
            	
            }
        });
		
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
		
		this.escapeTable = new Table();
		escapeTable.setDebug(Constants.DEBUG);
		escapeTable.setVisible(false);
		
		escapeTable.add(resume).center().row();
		escapeTable.add(options).center().row();
		escapeTable.add(exit).center().row();
		
		
		this.optionsTable = new Table();
		optionsTable.setDebug(Constants.DEBUG);
		optionsTable.setVisible(false);
		optionsTable.setTouchable(Touchable.enabled);
		
		optionsTable.add(volumeSlider);
		optionsTable.row();
		optionsTable.add(volumeLabel).colspan(2).center();
		optionsTable.row();
		optionsTable.add(zoomSlider);
		optionsTable.row();
		optionsTable.add(zoomLabel);
		optionsTable.row();
		optionsTable.add(back).align(Align.left);
		optionsTable.row();
		
		//stage.addActor(escapeTable);
		//stage.addActor(optionsTable);
		
		
	}
	
	@Override
	public void act(float delta) { 
		super.act(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			optionsTable.setVisible(!optionsTable.isVisible());

		}
		

		
	}
	
}
