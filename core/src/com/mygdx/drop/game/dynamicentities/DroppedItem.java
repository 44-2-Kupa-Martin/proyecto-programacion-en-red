package com.mygdx.drop.game.dynamicentities;

import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.EventManager;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.events.CanPickupEvent;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.items.BowItem;

public class DroppedItem extends BoxEntity implements Drawable {
	public final Item item;
	private float pickupDelay;
	private boolean eventFired;
	
	/**
	 * Creates an entity that holds an {@link Item}
	 * @param x_mt
	 * @param y_mt
	 * @param item The item that was dropped
	 */
	protected DroppedItem(World world, float x_mt, float y_mt, Item item) { 
		super(world, Drop.tlToMt(1), Drop.tlToMt(1), 
			((Supplier<BodyDef>)(()->{
				BodyDef body = new BodyDef();
				body.fixedRotation = true;
				body.position.set(x_mt,y_mt);
				body.type = BodyType.DynamicBody;
				return body;
			})).get(), 
			((Supplier<FixtureDef>)(()->{
				FixtureDef fixture = new FixtureDef();
				fixture.filter.categoryBits = Constants.Category.ITEM.value;
				fixture.filter.maskBits = (short) (Constants.Category.WORLD.value | Constants.Category.SENSOR.value);
				return fixture;
			})).get()
		);
		assert item != null;
		this.item = item;
		this.pickupDelay = 1.5f;
		this.eventFired = false;
		self.applyLinearImpulse(new Vector2(3, 3) /* N-s */, getPosition(), true);
	 }
	
	public boolean canPickUp() { return this.pickupDelay <= 0; }

	@Override
	public boolean update(Viewport viewport) {
		boolean toBeDisposed = super.update(viewport);;
		if (pickupDelay > 0) {
			pickupDelay -= Gdx.graphics.getDeltaTime();
		} else if (!eventFired) {
			CanPickupEvent event = new CanPickupEvent(this); 
			EventManager.fire(event);
			this.eventFired = true;
			toBeDisposed = event.isHandled();
		}
		return toBeDisposed; 
	}
	
	@Override
	public void draw(Viewport viewport) {
		Vector2 coords = getDrawingCoordinates();
		game.batch.draw(item.getTexture(), coords.x, coords.y, getWidth(), getHeight());
	}

	/**
	 * @see Entity.EntityDefinition
	 */
	public static class Definition extends Entity.EntityDefinition<DroppedItem> {
		public Item item;
		
		/**
		 * @see DroppedItem#DroppedItem(World, float, float, Item)
		 */
		public Definition(float x_mt, float y_mt, Item item) { 
			super(x_mt, y_mt);
			this.item = item;
		}

		@Override
		protected DroppedItem createEntity(World world) { return new DroppedItem(world, x,y, item); }
		
	}
}
