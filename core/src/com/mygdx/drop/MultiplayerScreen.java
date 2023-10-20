package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MultiplayerScreen implements Screen {
	
	private final Drop game;
	private final Stage stage;
	private final OrthographicCamera camera;
	private final Skin skin;
	private final Viewport viewport;
	private final Label ipLabel;
	private final Label portLabel;
	private final TextButton backButton;
	private final TextButton playButton;
	private final TextField ipField;
	private final TextField portField;
	
	public MultiplayerScreen(Drop game){
		this.game = game;
		this.camera = new OrthographicCamera();
		this.viewport = new ScreenViewport(camera);
		this.stage = new Stage(viewport);
		camera.setToOrtho(false, 800, 480);
		skin = Assets.Skins.Glassy_glassy.get();
		
		ipLabel = new Label("Server IP:", skin);
		portLabel = new Label("Server Port:", skin);
		
		playButton = new TextButton("Play", skin, "small");
		playButton.setTransform(true);
		
		backButton = new TextButton("Back to Main Menu", skin, "small");
		backButton.setTransform(true);
		
		ipField = new TextField("", skin);
		portField = new TextField("",skin);
		
		
		
		// Add a click listener to the "Play" button
		//TODO Link this with the actual server I suppose
		playButton.addListener(new ClickListener() {
			//TODO possibly a memory leak, previous screen was not disposed
			@Override
			public void clicked(InputEvent event, float x, float y) {
			game.setScreen(new GameScreen(game));
			
			}
		});
		
		// Add a click listener to the "Back" button
		backButton.addListener(new ClickListener() {
			//TODO possibly a memory leak, previous screen was not disposed
			@Override
			public void clicked(InputEvent event, float x, float y) { 
			game.setScreen(new MainMenuScreen(game)); 
			dispose();
			}
		});
		
		ipField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                try {
                	int ipAdress;
                    ipAdress = Integer.parseInt(textField.getText());
                    game.ipAdress = ipAdress;
                } catch (NumberFormatException e) {
                    //TODO This is bad, cry about it.
                }
            }
        });
		
		portField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                try {
                	int portAdress;
                	portAdress = Integer.parseInt(textField.getText());
                    game.portAdress = portAdress;
                } catch (NumberFormatException e) {
                    //TODO This is bad, cry about it.
                }
            }
        });
		
		Table serverTable = new Table();
		serverTable.setFillParent(true);
		serverTable.center().top();
		
		serverTable.add(ipLabel);
		serverTable.add(ipField).row();
		
		serverTable.add(portLabel);
		serverTable.add(portField).row();
		
		
		Table buttonsTable = new Table();
		buttonsTable.setFillParent(true);
		
		buttonsTable.add(playButton).row();;
		buttonsTable.add(backButton);
		
		stage.addActor(serverTable);
		stage.addActor(buttonsTable);
		
		
		
	}


	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		
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
