package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Drop;

public abstract class Entity {
	public final Body self;
	protected float width;
	protected float height;
	
	protected static Drop game;
	protected static GameScreen gameScreen;
	
	protected Entity(float width, float height, float x, float y) {
		assert Drop.game != null : "Entity created before game instance!";
		if (game == null) game = Drop.game;
		
		assert GameScreen.gameScreen != null : "Entity created outside of game screeen!";
		if (gameScreen == null) gameScreen = GameScreen.gameScreen;
		
		this.width = width;
		this.height = height;
		
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(x, y);
		bodyDefinition.fixedRotation = true;
		
		this.self = gameScreen.world.box2dWorld.createBody(bodyDefinition);
		self.setUserData(this);
	}
	
	public abstract boolean update(Camera camera);
	
	public abstract void draw(Camera camera);
}
