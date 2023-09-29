package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.drop.Assets.MusicId;
import com.mygdx.drop.actors.HUD;
import com.mygdx.drop.etc.ContactEventFilter;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.handlers.ContactEventHandler;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Arrow;
import com.mygdx.drop.game.dynamicentities.DebugBox;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.dynamicentities.TestEnemy;

public class GameScreen implements Screen {
	private World world;
	private Drop game;
	private OrthographicCamera gameCamera;
	private HUD hud;
	private ScreenViewport hudViewport;
	private ExtendViewport gameViewport;
	private Stage hudStage;
	private Player player;
	public GameScreen(Drop game) {
		this.game = game;
		this.gameCamera = new OrthographicCamera();
		this.gameViewport = new ExtendViewport(Drop.tlToMt(Constants.DEFAULT_FOV_WIDTH_tl), Drop.tlToMt(Constants.DEFAULT_FOV_HEIGHT_tl), gameCamera);
		this.hudViewport = new ScreenViewport();
		InputMultiplexer multiplexer = new InputMultiplexer();
		this.hudStage = new Stage(hudViewport, game.batch);
		multiplexer.addProcessor(hudStage);
		this.world = new World(Constants.WORLD_WIDTH_tl, Constants.WORLD_HEIGHT_tl, new Vector2(0, -10) /* m/s^2 */, gameViewport);
		multiplexer.addProcessor(world);
		Gdx.input.setInputProcessor(multiplexer);
//		world.createEntity(new DebugBox.Definition(0, 15, 5, 5));
		this.player = world.createEntity(new Player.Definition(0,3));
		this.hud = new HUD(player, world);
		TestEnemy enemy = world.createEntity(new TestEnemy.Definition(-5, 1, player));
		TestEnemy enemy2 = world.createEntity(new TestEnemy.Definition(5, 3, player));
		hudStage.addActor(hud);
		game.assets.get(MusicId.GameScreen_rain).setLooping(true);
		game.assets.get(MusicId.GameScreen_rain).setVolume(game.masterVolume);
	}

	@Override
	public void show() { game.assets.get(MusicId.GameScreen_rain).play(); }

	@Override
	public void render(float delta) {
		// TODO: implement zoom
		updateCameraPosition();
		ScreenUtils.clear(0, 0, 0.2f, 1);
		// All camera manipulations must be done before calling this method
		gameViewport.apply();
		game.batch.setProjectionMatrix(gameCamera.combined);
		world.render(gameCamera);		
		
		hudViewport.apply();
		game.batch.setProjectionMatrix(hudViewport.getCamera().combined);
		hudStage.act();
		hudStage.draw();
		
		game.batch.begin();
		drawHeldItem();
		game.batch.end();
		
		world.step();
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
	public void dispose() { world.dispose(); hudStage.dispose(); }

	private final void updateCameraPosition() {
		// Make camera follow player and prevent it from going out of bounds
		Vector2 playerPosition_mt = player.getPosition();
		Camera camera = gameViewport.getCamera();
		final float cameraYUpperBound = (world.worldHeight_mt - camera.viewportHeight) / 2;
		final float cameraYLowerBound = -cameraYUpperBound;
		final float cameraXUpperBound = (world.worldWidth_mt - camera.viewportWidth) / 2;
		final float cameraXLowerBound = -cameraXUpperBound;
		final float cameraX = MathUtils.clamp(playerPosition_mt.x, cameraXLowerBound, cameraXUpperBound);
		final float cameraY = MathUtils.clamp(playerPosition_mt.y, cameraYLowerBound, cameraYUpperBound);
		camera.position.set(cameraX, cameraY, 0);
	}
	
	private final void drawHeldItem() {
		if (game.heldItem == null) 
			return;
		game.batch.draw(game.heldItem.getTexture(), Gdx.input.getX() - 15, hudViewport.getScreenHeight() - Gdx.input.getY() - 15, 30, 30);
	}
}
