package com.mygdx.drop.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Constants.LayerId;
import com.mygdx.drop.Drop;

public abstract class Tile {
	protected static Drop game;
	private static GameScreen gameScreen;
	
	public final Body self;
	public final int x;
	public final int y;
	
	protected final LayerId layerId;
	protected final TileId tileId;
	
	
	public Tile(LayerId layerId, TileId tileId, int x, int y) {
		assert Drop.game != null : "Tile object created before game instance!";
		game = Drop.game;
		
		assert GameScreen.gameScreen != null : "Tile object created outside of game screeen!";
		if (gameScreen == null) gameScreen = GameScreen.gameScreen;
		
		this.layerId = layerId;
		this.tileId = tileId;
		this.x = x;
		this.y = y;
		
		assert gameScreen.world.tiledMap.getLayers().get(LayerId.WORLD.value) instanceof TiledMapTileLayer : "Cannot cast!"; 
		TiledMapTileLayer layer = (TiledMapTileLayer) gameScreen.world.tiledMap.getLayers().get(LayerId.WORLD.value);
		layer.setCell(x, y, new Cell().setTile(gameScreen.world.tiledMap.getTileSets().getTileSet(layerId.value).getTile(tileId.value)));
		
		BodyDef bodyDefiniton = new BodyDef();
		bodyDefiniton.type = BodyType.StaticBody;
		bodyDefiniton.position.set(Drop.tileToMt(x) - Constants.WORLD_WIDTH_MT/2 + Drop.tileToMt(1)/2, Drop.tileToMt(y) - Constants.WORLD_HEIGHT_MT/2 + Drop.tileToMt(1)/2);
		bodyDefiniton.fixedRotation = true;
		
		this.self = gameScreen.world.box2dWorld.createBody(bodyDefiniton);
		self.setUserData(this);
		
		FixtureDef fixtureDefinition = new FixtureDef();
		PolygonShape box = new PolygonShape();
		
		box.setAsBox(Drop.tileToMt(1)/2, Drop.tileToMt(1)/2);
		fixtureDefinition.shape = box;
		self.createFixture(fixtureDefinition);
		box.dispose();
	}
	
	public static enum TileId {
		RAINBOW;
		
		public final int value;
		
		private TileId() {
			this.value = ordinal();
		}
	}
}
