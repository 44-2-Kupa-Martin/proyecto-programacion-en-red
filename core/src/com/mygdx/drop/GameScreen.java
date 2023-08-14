package com.mygdx.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTile.BlendMode;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets.FontId;
import com.mygdx.drop.Assets.MusicId;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.actors.HUD;
import com.mygdx.drop.actors.Inventory;
import com.mygdx.drop.game.DebugBox;
import com.mygdx.drop.game.Player;
import com.mygdx.drop.game.RainbowTile;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.RainbowTile.Definition;

public class GameScreen implements Screen {
	private World world;
	private Drop game;
	private OrthographicCamera gameCamera;
	private HUD hud;
	private ScreenViewport hudViewport;
	private ExtendViewport gameViewport;
	private Stage hudStage;
	private Player player;
	private DebugBox box;
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
		this.box = world.createEntity(new DebugBox.Definition(0, 5, 5, 5));
		this.player = world.createEntity(new Player.Definition(0,0));
		this.hud = new HUD(player, world);
		hudStage.addActor(hud);
		game.assets.get(MusicId.GameScreen_rain).setLooping(true);
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
		
		player.update(gameCamera);
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
		game.batch.draw(game.heldItem.texture, Gdx.input.getX() - 15, hudViewport.getScreenHeight() - Gdx.input.getY() - 15, 30, 30);
	}
}
