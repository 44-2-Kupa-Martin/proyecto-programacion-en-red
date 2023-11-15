package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.drop.Assets.Animations;
import com.mygdx.drop.Assets.Asset;
import com.mygdx.drop.Assets.Textures;
import com.mygdx.drop.actors.GameMenu;
import com.mygdx.drop.actors.HUD;
import com.mygdx.drop.game.PlayerManager;
import com.mygdx.drop.game.PlayerManager.FrameComponent;

public class GameScreen implements Screen, InputProcessor {
	private static Vector2 tempVector = new Vector2();
	private Drop game;
	private HUD hud;
	public OrthographicCamera gameCamera;
	private ScreenViewport hudViewport;
	private ExtendViewport gameViewport;
	private Stage hudStage;
	private String playerName;
	private PlayerManager playerManager;
	private GameMenu gameMenu;
//	private Disposable networkThread;
	public GameScreen(Drop game, String playerName, PlayerManager playerManager) {
		this.game = game;
		this.playerManager = playerManager;
		this.playerName = playerName;
		this.gameCamera = new OrthographicCamera();
		this.gameViewport = new ExtendViewport(Drop.tlToMt(Constants.DEFAULT_FOV_WIDTH_tl), Drop.tlToMt(Constants.DEFAULT_FOV_HEIGHT_tl), gameCamera);
		this.hudViewport = new ScreenViewport();
		InputMultiplexer multiplexer = new InputMultiplexer();
		this.hudStage = new Stage(hudViewport, game.batch);
		multiplexer.addProcessor(hudStage);
		multiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(multiplexer);
		this.hud = new HUD(playerName, playerManager);
		this.gameMenu = new GameMenu(game, hudStage, hud);
		hudStage.addActor(hud);
		
//		Server server = new Server();
//		this.networkThread = server;
//		Client client = new Client();
//		this.networkThread = client;
//		client.sendString("hello from client");
//		hudStage.addActor(hud);
		Assets.Music.rain.get().setLooping(true);
		
		System.out.println(game.ipAdress);
		System.out.println(game.portAdress);
	}

	@Override
	public void show() { Assets.Music.rain.get().play(); }

	@Override
	public void render(float delta) {		
		Assets.Music.rain.get().setVolume(game.masterVolume);
		gameCamera.zoom = game.zoom;
		
		playerManager.step(delta);
		
		updateCameraPosition();
		ScreenUtils.clear(0, 0, 0.2f, 1);
		// All camera manipulations must be done before calling this method
		gameViewport.apply();
		game.batch.setProjectionMatrix(gameCamera.combined);
		
		FrameComponent[] components = playerManager.getFrameData();
		game.batch.begin();
		drawHeldItem();
		for (FrameComponent frameComponent : components) {
			if (frameComponent == null) 
				continue;
			AtlasRegion texture = null;
			Asset<?> asset = Assets.getById(frameComponent.assetId);
			if (asset instanceof Animations) {
				texture = ((Animations)asset).get().get(frameComponent.animationFrameIndex);
			} else {
				texture = ((Textures)asset).get();
			}
			game.batch.draw(texture, frameComponent.x_mt, frameComponent.y_mt, 0, 0, frameComponent.width_mt, frameComponent.height_mt, 1, 1, frameComponent.rotation_deg);
		}		
		
		game.batch.end();
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			
					System.out.println("hice escape");	
						
					hudStage.getActors().get(0).remove();
					hudStage.addActor(gameMenu);
							
			}
		
		//Sacar el stage y hacer devuelta el swap
		
		hudViewport.apply();
		game.batch.setProjectionMatrix(hudViewport.getCamera().combined);
		hudStage.act();
		hudStage.draw();
		
		if (!playerManager.isConnected()) {
			game.setScreen(new MainMenuScreen(game));
			dispose();
		}
		
//		game.batch.begin();
//		drawHeldItem();
//		game.batch.end();		
	}

	@Override
	public void resize(int width, int height) { 
		gameViewport.update(width, height);
		hudViewport.update(width, height, true);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() { 
		hudStage.dispose(); 
		playerManager.closeSession();
		Assets.Music.rain.get().stop();
//		networkThread.dispose(); 
	}
	

	private final void updateCameraPosition() {
		// Make camera follow player and prevent it from going out of bounds
		Vector2 playerPosition_mt = playerManager.getPlayerPosition(playerName);
		Camera camera = gameViewport.getCamera();
		final float cameraYUpperBound = (Drop.tlToMt(playerManager.getWorldHeight()) - camera.viewportHeight) / 2;
		final float cameraYLowerBound = -cameraYUpperBound;
		final float cameraXUpperBound = (Drop.tlToMt(playerManager.getWorldWidth()) - camera.viewportWidth) / 2;
		final float cameraXLowerBound = -cameraXUpperBound;
		final float cameraX = MathUtils.clamp(playerPosition_mt.x, cameraXLowerBound, cameraXUpperBound);
		final float cameraY = MathUtils.clamp(playerPosition_mt.y, cameraYLowerBound, cameraYUpperBound);
		camera.position.set(cameraX, cameraY, 0);
	}
	
	private final void drawHeldItem() {
		if (playerManager.getCursorItem(playerName) == null) 
			return;
		int textureId = playerManager.getCursorItem(playerName).getTextureId();
		game.batch.draw((AtlasRegion) Assets.getById(textureId).get(), Gdx.input.getX() - 15, hudViewport.getScreenHeight() - Gdx.input.getY() - 15, 30, 30);
	}

	@Override
	public boolean keyDown(int keycode) { return playerManager.keyDown(playerName, keycode); }

	@Override
	public boolean keyUp(int keycode) { return playerManager.keyUp(playerName, keycode); }

	@Override
	public boolean keyTyped(char character) { return playerManager.keyTyped(playerName, character); }

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 gameCoordinates = gameViewport.unproject(tempVector.set(screenX, screenY));
		return playerManager.touchDown(playerName, gameCoordinates.x, gameCoordinates.y, pointer, button); 
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { 
		Vector2 gameCoordinates = gameViewport.unproject(tempVector.set(screenX, screenY));
		return playerManager.touchUp(playerName, gameCoordinates.x, gameCoordinates.y, pointer, button); 
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { 
		Vector2 gameCoordinates = gameViewport.unproject(tempVector.set(screenX, screenY));
		return playerManager.touchCancelled(playerName, gameCoordinates.x, gameCoordinates.y, pointer, button); 
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) { 
		Vector2 gameCoordinates = gameViewport.unproject(tempVector.set(screenX, screenY));
		return playerManager.touchDragged(playerName, gameCoordinates.x, gameCoordinates.y, pointer); 
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) { 
		Vector2 gameCoordinates = gameViewport.unproject(tempVector.set(screenX, screenY));
		return playerManager.mouseMoved(playerName, gameCoordinates.x, gameCoordinates.y); 
	}

	@Override
	public boolean scrolled(float amountX, float amountY) { return playerManager.scrolled(playerName, amountX, amountY); }
}
