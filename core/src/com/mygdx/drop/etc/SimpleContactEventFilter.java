package com.mygdx.drop.etc;

import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.game.Entity;

/**
 * Filters {@link ContactEvent}s based on the type of its participants.
 *
 * @param <TypeObjectA> The type for which the collision handling functions will be called
 */
public class SimpleContactEventFilter<TypeObjectA extends Entity> extends ContactEventFilter<TypeObjectA, Entity> {

	public SimpleContactEventFilter(Class<TypeObjectA> typeA) { 
		super(typeA, Entity.class);
	}

}
