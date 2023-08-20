package com.mygdx.drop.etc.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.drop.game.World;

/**
 * Event for handling collisions between fixtures
 */
public class ContactEvent extends Event<World> {
	public final Type eventType;
	private Contact contact;
	private Manifold manifold;
	private ContactImpulse contactImpulse;
	
	public ContactEvent(Type eventType) {
		this.eventType = eventType;
	}
	public Contact getContact() { return contact; }
	public void setContact(Contact contact) { this.contact = contact; }
	public Manifold getManifold() { return manifold; }
	public void setManifold(Manifold manifold) { this.manifold = manifold; }
	public ContactImpulse getContactImpulse() { return contactImpulse; }
	public void setContactImpulse(ContactImpulse contactImpulse) { this.contactImpulse = contactImpulse; }
	
	public enum Type {
		preSolve,
		postSolve,
		beginContact,
		endContact;
	}
}

