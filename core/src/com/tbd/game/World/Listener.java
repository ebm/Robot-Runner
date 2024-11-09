package com.tbd.game.World;

import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.Entities.Entity;
import com.tbd.game.Entities.PlayerPackage.Player;
import com.tbd.game.Items.Item;
import com.tbd.game.States.MyGame;
import com.tbd.game.Weapons.*;

import java.util.ArrayList;

public class Listener implements ContactListener {
    static class ContactVar {
        Fixture fixtureA;
        Fixture fixtureB;
        boolean beginContact;

        public ContactVar(Contact contact, boolean beginContact) {
            fixtureA = contact.getFixtureA();
            fixtureB = contact.getFixtureB();
            this.beginContact = beginContact;
        }
    }
    MyGame myGame;
    ArrayList<ContactVar> allContacts;

    public Listener(MyGame myGame) {
        this.myGame = myGame;

        resetContacts();
    }
    public void resetContacts() {
        allContacts = new ArrayList<>();

    }
    public static Fixture[] checkContact(Fixture fixtureA, Fixture fixtureB, Class<?> c) {
        Fixture objFixture = null;
        Fixture contactFixture = null;
        if (c.isInstance(fixtureA.getUserData())) {
            objFixture = fixtureA;
            contactFixture = fixtureB;
        } else if (c.isInstance(fixtureB.getUserData())) {
            objFixture = fixtureB;
            contactFixture = fixtureA;
        } /*else if (c.isInstance(fixtureA.getBody().getUserData())) {
            obj = fixtureA.getBody().getUserData();
            contactBody = fixtureB.getBody();
        } else if (c.isInstance(fixtureB.getBody().getUserData())) {
            obj = fixtureB.getBody().getUserData();
            contactBody = fixtureA.getBody();
        }*/
        return new Fixture[] {objFixture, contactFixture};
    }
    public void update() {
        while (!allContacts.isEmpty()) {
            ContactVar cv = allContacts.get(0);
            Entity.handleContact(cv.fixtureA, cv.fixtureB, cv.beginContact, myGame);
            Weapon.handleContact(cv.fixtureA, cv.fixtureB, cv.beginContact, myGame);
            Item.handleContact(cv.fixtureA, cv.fixtureB, cv.beginContact, myGame);
            if (!allContacts.isEmpty()) allContacts.remove(0);
        }
    }
    @Override
    public void beginContact(Contact contact) {
        allContacts.add(new ContactVar(contact, true));
        //handlePlayerContact(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        allContacts.add(new ContactVar(contact, false));
        //handlePlayerContact(contact, false);
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
