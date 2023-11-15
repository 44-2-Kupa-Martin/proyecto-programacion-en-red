package com.mygdx.drop.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.MainMenuScreen;

public class GameMenu extends Table{

	private final Drop game;
	private final TextButton resume, options, exit, back;
	private Table optionsTable;
	private final Slider volumeSlider, zoomSlider;
	private final Label volumeLabel, zoomLabel;
	private final Skin skin;
	private final Stage stage;
	private final HUD hud;	
	
	
	public GameMenu(Drop game, Stage stage, HUD hud) {
		
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
		this.stage = stage;
		this.hud = hud;
		
//		optionsTable.setVisible(false);
//		optionsTable.setFillParent(true);
//		optionsTable.setDebug(Constants.DEBUG);
//		optionsTable.setTouchable(Touchable.enabled);
		
		
		
		resume.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	stage.getActors().get(0).remove();
				stage.addActor(hud);
            }
        });

		options.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
//            	optionsTable.setVisible(!optionsTable.isVisible());
//            	GameMenu.this.setVisible(!GameMenu.this.isVisible());
            	
            	GameMenu.this.clear();
            	GameMenu.this.add(volumeSlider);
            	GameMenu.this.row();
            	GameMenu.this.add(volumeLabel).colspan(2).center();
            	GameMenu.this.row();
            	GameMenu.this.add(zoomSlider);
            	GameMenu.this.row();
            	GameMenu.this.add(zoomLabel);
            	GameMenu.this.row();
            	GameMenu.this.add(back).align(Align.left);
            	GameMenu.this.row(); 
            }
        });
		
		exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	game.getScreen().dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });
	
		back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	GameMenu.this.clear();
            	GameMenu.this.add(resume).center().row();
        		GameMenu.this.add(options).center().row();
        		GameMenu.this.add(exit).center().row();
            	
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
		
		GameMenu.this.setDebug(Constants.DEBUG);
		
		GameMenu.this.add(resume).center().row();
		GameMenu.this.add(options).center().row();
		GameMenu.this.add(exit).center().row();
		GameMenu.this.setFillParent(true);
		
		
		
		
		//stage.addActor(GameMenu.this);
		//stage.addActor(optionsTable);
		
		
	}
	
//	@Override
//	public void act(float delta) { 
//		super.act(delta);
//		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
//			optionsTable.setVisible(!optionsTable.isVisible());
//
//		}
//		
//
//		
//	}
	
}
