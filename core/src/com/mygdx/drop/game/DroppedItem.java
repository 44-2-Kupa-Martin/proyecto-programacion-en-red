package com.mygdx.drop.game;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;

public class DroppedItem extends BoxEntity {
	private final Item droppedItem;
	protected DroppedItem(World world, float x, float y, Item item) { 
		super(world, Drop.tlToMt(1), Drop.tlToMt(1), 
			((Supplier<BodyDef>)(()->{
				BodyDef body = new BodyDef();
				body.fixedRotation = true;
				body.position.set(x,y);
				body.type = BodyType.DynamicBody;
				return body;
			})).get(), 
			((Supplier<FixtureDef>)(()->{
				FixtureDef fixture = new FixtureDef();
				fixture.filter.categoryBits = Constants.Category.OTHER.value;
				return fixture;
			})).get()
		);
		this.droppedItem = item;
		self.applyLinearImpulse(new Vector2(3, 3) /* N-s */, getPosition(), true);
	 }
	@Override
	public void dispose() {}
	@Override
	public boolean update(Camera camera) { return false; }
	@Override
	public void draw(Camera camera) {
		Vector2 coords = getDrawingCoordinates();
		game.batch.draw(droppedItem.getTexture(), coords.x, coords.y, getWidth(), getHeight());
	}

	public static class Definition extends Entity.EntityDefinition<DroppedItem> {
		public Item item;
		public Definition(float x, float y, Item item) { 
			super(x, y);
			this.item = item;
		}

		@Override
		protected DroppedItem createEntity(World world) { return new DroppedItem(world, x,y, item); }
		
	}
}
