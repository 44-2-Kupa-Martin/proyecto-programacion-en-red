package com.mygdx.drop.etc.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;

public class ClassifiedContactEvent<SelfType extends Entity, OtherType extends Entity> extends ContactEvent<SelfType> {
	public final SelfType self;
	public final Fixture selfFixture;
	public final OtherType other;
	public final Fixture otherFixture;
	public ClassifiedContactEvent(ContactEvent<?> event, SelfType self, Fixture selfFixture, OtherType other, Fixture otherFixture) {
		super(self, event.contact, event.eventType, event.manifold, event.contactImpulse);
		this.self = self;
		this.selfFixture = selfFixture;
		this.other = other;
		this.otherFixture = otherFixture;
	}
}
