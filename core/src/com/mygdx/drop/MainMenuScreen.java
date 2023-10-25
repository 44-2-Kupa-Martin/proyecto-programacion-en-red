package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.drop.game.ServerThread;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;

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
		
		
		game.batch.begin();
		
		skin = Assets.Skins.Glassy_glassy.get();
		
		Label titleLabel = new Label("Placeholder", skin);
		
		TextButton singleplayerButton = new TextButton("Singleplayer",skin);
		TextButton multiplayerButton = new TextButton("Multiplayer",skin);
		TextButton optionsButton = new TextButton("Options",skin);
		TextButton exitButton = new TextButton("Exit",skin);
		
		singleplayerButton.setTransform(true);
		multiplayerButton.setTransform(true);
		optionsButton.setTransform(true);
		exitButton.setTransform(true);
		
		game.batch.end();
		
		titleLabel.setFontScale(2.0f * Gdx.graphics.getWidth() / 800f);
		
		//Add a click listener to singleplayerButton
		singleplayerButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
//            	World world = new World(Constants.WORLD_WIDTH_tl, Constants.WORLD_HEIGHT_tl, new Vector2(0, -10) /* m/s^2 */);
//            	Player player = world.createEntity(new Player.Definition("kupitinchi", 0, 10));
//            	GameScreen gameScreen = new GameScreen(game, player.name, world);
//            	if (Constants.DEBUG) {
//					world.debug.camera = gameScreen.gameCamera;
//				}
//                game.setScreen(gameScreen);
            	Client client = new Client("messi");
            	while (client.notConnected);
            	game.setScreen(new GameScreen(game, "messi", client));
            }
        });
		
		//Add a click listener to multiplayerButton
		multiplayerButton.addListener(new ClickListener(){
	        @Override
		    public void clicked(InputEvent event, float x, float y) {
		        game.serverThread = new ServerThread(Constants.WORLD_WIDTH_tl, Constants.WORLD_HEIGHT_tl, new Vector2(0, -10), 1/60f);
		        game.serverThread.start();
		        Client client = new Client("fullaccess");
		        while (client.notConnected);
	        	game.setScreen(new GameScreen(game, "fullaccess", client));
	        	dispose();
	        }
		});
		
		//Add a click listener to optionsButton
		
		optionsButton.addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	
	        	game.setScreen(new OptionsScreen(game));
	        	dispose();
	        }
		});
		
		//Add a click listener to Exit button
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		
		//TODO Change the resize of buttons so it fits in all window sizes
		
		 //float buttonWidth = Gdx.graphics.getWidth() * 0.1f;
		 //float buttonHeight = buttonWidth * 0.25f;
		
		//Create Table titleTablr
		Table titleTable = new Table();
				
		titleTable.setFillParent(true);
		titleTable.right().top();
		
		//Add label to table
		 
		titleTable.add(titleLabel).padTop(30).padRight(60).center().row();
		titleTable.row().padTop(200);
		
		
		//Create table buttonsTable
		Table buttonsTable = new Table();
		
		buttonsTable.setFillParent(true);
		buttonsTable.center().left();
		
		
		//Add buttons to table
		buttonsTable.add(singleplayerButton).padBottom(20).row();
		buttonsTable.add(multiplayerButton).padBottom(20).row();
		buttonsTable.add(optionsButton).padBottom(20).row();
		buttonsTable.add(exitButton).padBottom(20).row();
		
		//Add tables to stage
		stage.addActor(titleTable);
		stage.addActor(buttonsTable);
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