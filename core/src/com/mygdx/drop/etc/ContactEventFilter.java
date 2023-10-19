package com.mygdx.drop.etc;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Null;
import com.mygdx.drop.etc.events.ClassifiedContactEvent;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.listeners.ContactEventListener;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;

/**
 * Filters {@link ContactEvent}s based on the type of its participants. Both <TypeObjectA> and
 * <TypeObjectB> are required to participate
 *
 * @param <TypeObjectA> The type for which the collision handling functions will be called
 * @param <TypeObjectB> The type for which the collision handling functions will be called
 */
public class ContactEventFilter<TypeObjectA extends Entity, TypeObjectB extends Entity> extends ContactEventListener {
	private final Class<TypeObjectA> typeA;
	private final @Null Class<TypeObjectB> typeB;
	private ClassifiedContactEvent<TypeObjectA, TypeObjectB> contactParticipants;

	public ContactEventFilter(Class<TypeObjectA> typeA, Class<TypeObjectB> typeB) {
		assert typeA != null;
		this.typeA = typeA;
		this.typeB = typeB;
	}

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof ContactEvent))
			return false;
		ContactEvent contactEvent = (ContactEvent) event;
		Object ownerFixtureA = contactEvent.getContact().getFixtureA().getBody().getUserData();
		Object ownerFixtureB = contactEvent.getContact().getFixtureB().getBody().getUserData();
		boolean ownerAOfTypeA = typeA.isInstance(ownerFixtureA);
		boolean ownerBOfTypeA = typeA.isInstance(ownerFixtureB);
		boolean typeAInvolved = ownerAOfTypeA || ownerBOfTypeA;
		if (!typeAInvolved)
			return false;

		if (typeB == null) {
			// Ensure objects are in the order of this generic's parameters
			this.contactParticipants = new ClassifiedContactEvent<TypeObjectA, TypeObjectB>(contactEvent, (TypeObjectA) (ownerAOfTypeA ? ownerFixtureA : ownerFixtureB),
					ownerAOfTypeA ? contactEvent.getContact().getFixtureA() : contactEvent.getContact().getFixtureB(),
					(TypeObjectB) (ownerAOfTypeA ? ownerFixtureB : ownerFixtureA),
					ownerAOfTypeA ? contactEvent.getContact().getFixtureB() : contactEvent.getContact().getFixtureA());
			return super.handle(contactEvent);
		}

		boolean ownerAOfTypeB = typeB.isInstance(ownerFixtureA);
		boolean ownerBOfTypeB = typeB.isInstance(ownerFixtureB);
		boolean typeBInvolved = ownerAOfTypeB || ownerBOfTypeB;
		if (!typeBInvolved)
			return false;

		// Ensure objects are in the order of this generic's parameters
		this.contactParticipants = new ClassifiedContactEvent<TypeObjectA, TypeObjectB>(contactEvent, (TypeObjectA) (ownerAOfTypeA ? ownerFixtureA : ownerFixtureB),
				ownerAOfTypeA ? contactEvent.getContact().getFixtureA() : contactEvent.getContact().getFixtureB(),
				(TypeObjectB) (ownerAOfTypeA ? ownerFixtureB : ownerFixtureA),
				ownerAOfTypeA ? contactEvent.getContact().getFixtureB() : contactEvent.getContact().getFixtureA());

		return super.handle(contactEvent);
	}

	public boolean preSolve(ContactEvent event, ClassifiedContactEvent<TypeObjectA, TypeObjectB> participants) { return false; }

	public boolean postSolve(ContactEvent event, ClassifiedContactEvent<TypeObjectA, TypeObjectB> participants) { return false; }

	public boolean beginContact(ContactEvent event, ClassifiedContactEvent<TypeObjectA, TypeObjectB> participants) { return false; }

	public boolean endContact(ContactEvent event, ClassifiedContactEvent<TypeObjectA, TypeObjectB> participants) { return false; }

	@Override
	public boolean preSolve(ContactEvent event) { return preSolve(event, this.contactParticipants); }

	@Override
	public boolean postSolve(ContactEvent event) { return postSolve(event, this.contactParticipants); }

	@Override
	public boolean beginContact(ContactEvent event) { return beginContact(event, this.contactParticipants); }

	@Override
	public boolean endContact(ContactEvent event) { return endContact(event, this.contactParticipants); }

}
