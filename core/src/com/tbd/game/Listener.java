package com.tbd.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.Entities.PlayerPackage.BodyPart;
import com.tbd.game.Entities.Entity;
import com.tbd.game.Entities.PlayerPackage.Player;
import com.tbd.game.Weapons.*;

import java.util.ArrayList;

import static com.tbd.game.Constants.*;

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
    int contactFeet;
    int contactLeftArm;
    int contactRightArm;
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
    public void handlePlayerContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact) {
        BodyPart bp = null;
        Fixture[] collision = checkContact(fixtureA, fixtureB, BodyPart.class);
        if (collision[0] != null) {
            bp = (BodyPart) collision[0].getUserData();
        }
        if (fixtureA.isSensor() && fixtureB.isSensor()) return;
        if (bp != null) {
            if (bp == BodyPart.Feet) {
                if (beginContact) {
                    myGame.player.touchingFloor = true;
                    contactFeet++;
                } else {
                    contactFeet--;
                    if (contactFeet == 0) myGame.player.touchingFloor = false;
                }
            }
            if (bp == BodyPart.LeftArm) {
                if (beginContact) {
                    myGame.player.touchingLeftWall = true;
                    contactLeftArm++;
                } else {
                    contactLeftArm--;
                    if (contactLeftArm == 0) myGame.player.touchingLeftWall = false;
                }
            }
            if (bp == BodyPart.RightArm) {
                if (beginContact) {
                    myGame.player.touchingRightWall = true;
                    contactRightArm++;
                } else {
                    contactRightArm--;
                    if (contactRightArm == 0) myGame.player.touchingRightWall = false;
                }
            }
        }
    }
    public void handleWeaponContact(Fixture fixtureA, Fixture fixtureB) {
        Fixture weaponFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = checkContact(fixtureA, fixtureB, Weapon.class);
        if (collision[0] != null) {
            weaponFixture = collision[0];
            contactFixture = collision[1];
        }
        if (weaponFixture != null) {
            Weapon weapon = (Weapon) weaponFixture.getUserData();
            if (contactFixture.getUserData() == null || weapon.user.getClass() != contactFixture.getUserData().getClass()) {
                if (weapon.user.enemy.isInstance(contactFixture.getUserData())) {
                    //System.out.println(fixtureA.getUserData() + ", " + fixtureB.getUserData() + " | " + contactFixture.getUserData());
                    Entity e = (Entity) contactFixture.getUserData();
                    if (weapon.getClass() == BodyWeapon.class) {
                        ((BodyWeapon) weapon).contactStarted(e);
                    } else e.takeDamage(weapon.attackDamage);
                    if (weapon.getClass() == BulletClass.class) {
                        ((BulletClass) weapon).destroy();
                    }
                    //System.out.println(e.health + ", " + weapon.attackDamage);
                }
                if (fixtureA.getUserData() != null && fixtureB.getUserData() != null && fixtureA.getUserData().getClass() == BulletClass.class && fixtureB.getUserData().getClass() == BulletClass.class) {
                    BulletClass b1 = (BulletClass) fixtureA.getUserData();
                    BulletClass b2 = (BulletClass) fixtureB.getUserData();
                    float b1AttackDamage = b1.attackDamage;
                    float b2AttackDamage = b2.attackDamage;

                    b1.attackDamage -= b2AttackDamage;
                    b2.attackDamage -= b1AttackDamage;
                    if (b1.attackDamage <= 0) b1.destroy();
                    if (b2.attackDamage <= 0) b2.destroy();
                }
            }
        }
    }
    public void handleBulletDestroyed(Fixture fixtureA, Fixture fixtureB) {
        Fixture weaponFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = checkContact(fixtureA, fixtureB, BulletType.class);
        if (collision[0] != null) {
            weaponFixture = collision[0];
            contactFixture = collision[1];
        }
        if (weaponFixture != null && weaponFixture.getBody().getUserData().getClass() == BulletClass.class) {
            if (contactFixture.getUserData() == null || contactFixture.getUserData().getClass() == weaponFixture.getBody().getUserData().getClass()) ((BulletClass) weaponFixture.getBody().getUserData()).destroy();
        }
    }
    public void handleLaser(Fixture fixtureA, Fixture fixtureB) {
        Fixture laserProjectileFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = checkContact(fixtureA, fixtureB, Laser.class);
        if (collision[0] != null) {
            laserProjectileFixture = collision[0];
            contactFixture = collision[1];
        }
        if (laserProjectileFixture != null) {
            Laser l  = (Laser) laserProjectileFixture.getUserData();
            if (l.body == null) {
                l.createBody();
            }
        }
    }
    public void handleBodyWeaponContact(Fixture fixtureA, Fixture fixtureB) {
        Fixture weaponFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = checkContact(fixtureA, fixtureB, BodyWeapon.class);
        if (collision[0] != null) {
            weaponFixture = collision[0];
            contactFixture = collision[1];
        }
        if (weaponFixture != null) {
            BodyWeapon weapon = (BodyWeapon) weaponFixture.getUserData();
            if (contactFixture.getUserData() == null || weapon.user.getClass() != contactFixture.getUserData().getClass()) {
                if (weapon.user.enemy.isInstance(contactFixture.getUserData())) {
                    weapon.contactEnded();
                }
            }
        }
    }
    public void update() {
        while (!allContacts.isEmpty()) {
            ContactVar cv = allContacts.get(0);
            Player.handleContact(cv.fixtureA, cv.fixtureB, cv.beginContact, myGame);
            Weapon.handleContact(cv.fixtureA, cv.fixtureB, cv.beginContact, myGame);
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
