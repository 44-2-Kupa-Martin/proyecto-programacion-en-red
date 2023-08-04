package com.mygdx.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.game.Player;
import com.mygdx.drop.game.RainbowTile;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.RainbowTile.Definition;

public class GameScreen implements Screen {
	private World world;
	private Drop game;
	private Player player;
	private Viewport extendViewport;

	public GameScreen(Drop game) {
		this.game = game;
		this.extendViewport = new ExtendViewport(Drop.tlToMt(Constants.DEFAULT_FOV_WIDTH_tl), Drop.tlToMt(Constants.DEFAULT_FOV_HEIGHT_tl));
		this.world = new World(Constants.WORLD_WIDTH_tl, Constants.WORLD_HEIGHT_tl, new Vector2(0, -10) /* m/s^2 */);
		initWorld();
		game.assets.rainMusic.setLooping(true);
	}

	@Override
	public void show() { game.assets.rainMusic.play(); }

	@Override
	public void render(float delta) {
		// TODO: implement zoom
		updateCameraPosition();
		ScreenUtils.clear(0, 0, 0.2f, 1);
		// All camera manipulations must be done before calling this method
		extendViewport.apply();
		game.batch.setProjectionMatrix(extendViewport.getCamera().combined);

		world.render(extendViewport.getCamera());
		game.batch.begin();
		player.draw(extendViewport.getCamera());
		game.batch.end();

		player.update(extendViewport.getCamera());
		world.step();
	}

	@Override
	public void resize(int width, int height) { extendViewport.update(width, height); }

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() { world.dispose(); }

	private final void initWorld() {
		player = world.createEntity(new Player.Definition());
		for (int i = 40; i < 60; i++) {
			for (int j = 20; j < 23; j++) {
				world.createTile(new RainbowTile.Definition(i, j));
			}
		}
	}

	private final void updateCameraPosition() {
		// Make camera follow player and prevent it from going out of bounds
		Vector2 playerPosition_mt = player.getPosition();
		Camera camera = extendViewport.getCamera();
		final float cameraYUpperBound = (Constants.WORLD_HEIGHT_mt - camera.viewportHeight) / 2;
		final float cameraYLowerBound = -cameraYUpperBound;
		final float cameraXUpperBound = (Constants.WORLD_WIDTH_mt - camera.viewportWidth) / 2;
		final float cameraXLowerBound = -cameraXUpperBound;
		final float cameraX = MathUtils.clamp(playerPosition_mt.x, cameraXLowerBound, cameraXUpperBound);
		final float cameraY = MathUtils.clamp(playerPosition_mt.y, cameraYLowerBound, cameraYUpperBound);
		camera.position.set(cameraX, cameraY, 0);
	}

}
