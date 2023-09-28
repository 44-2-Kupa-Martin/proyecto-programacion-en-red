package com.mygdx.drop.etc;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Null;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.handlers.ContactEventHandler;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;

public class ContactEventFilter<TypeObjectA, TypeObjectB> extends ContactEventHandler {
	private final Class<TypeObjectA> typeA;
	private TypeObjectA objectA;
	private final @Null Class<TypeObjectB> typeB;
	private TypeObjectB objectB;
	private ContactEventHandler next;
	
	public ContactEventFilter(Class<TypeObjectA> typeA, Class<TypeObjectB> typeB) {
		assert typeA != null;
		this.typeA = typeA;
		this.typeB = typeB;
	}
	
	@Override
	public boolean handle(Event event) {
		if (!(event instanceof ContactEvent)) 
			return false;
		this.objectA = null;
		this.objectB = null;
		ContactEvent contactEvent = (ContactEvent)event;
		
		Object ownerFixtureA = contactEvent.getContact().getFixtureA().getBody().getUserData();
		Object ownerFixtureB = contactEvent.getContact().getFixtureB().getBody().getUserData();
		boolean ownerAOfTypeA = typeA.isInstance(ownerFixtureA);
		boolean ownerBOfTypeA = typeA.isInstance(ownerFixtureB);
		boolean typeAInvolved = ownerAOfTypeA || ownerBOfTypeA;
		if (!typeAInvolved) 
			return false;
		
		this.objectA = (TypeObjectA)(ownerAOfTypeA ? ownerFixtureA : ownerFixtureB);
		
		if (typeB == null) {
			this.objectB = (TypeObjectB)(ownerAOfTypeA ? ownerFixtureB : ownerFixtureA);
			return super.handle(contactEvent);
		}
		
		boolean ownerAOfTypeB = typeB.isInstance(ownerFixtureA);
		boolean ownerBOfTypeB = typeB.isInstance(ownerFixtureB);
		boolean typeBInvolved = ownerAOfTypeB || ownerBOfTypeB;
		if (!typeBInvolved) 
			return false;
		
		this.objectB = (TypeObjectB)(ownerAOfTypeB ? ownerFixtureA : ownerFixtureB);

		return super.handle(contactEvent); 
	}
	public boolean preSolve(ContactEvent event, TypeObjectA objectA, TypeObjectB objectB) { return false; }
	public boolean postSolve(ContactEvent event, TypeObjectA objectA, TypeObjectB objectB) { return false; }
	public boolean beginContact(ContactEvent event, TypeObjectA objectA, TypeObjectB objectB) { return false; }
	public boolean endContact(ContactEvent event, TypeObjectA objectA, TypeObjectB objectB) { return false; }

	@Override
	public boolean preSolve(ContactEvent event) { return preSolve(event, objectA, objectB); }
	@Override
	public boolean postSolve(ContactEvent event) { return postSolve(event, objectA, objectB); }
	@Override
	public boolean beginContact(ContactEvent event) { return beginContact(event, objectA, objectB); }
	@Override
	public boolean endContact(ContactEvent event) { return endContact(event, objectA, objectB); }

}
