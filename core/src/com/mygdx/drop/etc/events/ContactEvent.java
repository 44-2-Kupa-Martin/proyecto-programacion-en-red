package com.mygdx.drop.etc.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Null;
import com.mygdx.drop.game.World;

/**
 * Event for handling collisions between fixtures
 */
public class ContactEvent extends Event {
	public final World world;
	public final Type eventType;
	public final Contact contact;
	private Manifold manifold;
	private @Null ContactImpulse contactImpulse;
	
	public ContactEvent(World world, Contact contact, Type eventType) {
		assert world != null;
		this.world = world;
		assert eventType != null;
		this.eventType = eventType;
		assert contact != null;
		this.contact = contact;
	}
	
	public World getWorld() { return world; }
	public Contact getContact() { return contact; }
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

