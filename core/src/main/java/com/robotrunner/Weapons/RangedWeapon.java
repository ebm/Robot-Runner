package com.robotrunner.Weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.robotrunner.Entities.Entity;
import com.robotrunner.Entities.PlayerPackage.Player;
import com.robotrunner.Entities.PlayerPackage.PlayerState;
import com.robotrunner.World.Listener;
import com.robotrunner.States.MyGame;

import static com.robotrunner.World.Constants.*;

import java.util.ArrayList;

public class RangedWeapon extends Weapon {
    public float bulletSpeed;
    public float bulletRadius;
    ArrayList<BulletClass> totalBullets;
    /*public RangedWeapon(MyGame myGame, float bulletSpeed, float attackDamage, float bulletRadius, float attacksPerSecond) {
        this.myGame = myGame;

        this.bulletSpeed = bulletSpeed;
        this.attackDamage = attackDamage;
        this.attacksPerSecond = attacksPerSecond;
        this.bulletRadius = bulletRadius;

        totalBullets = new ArrayList<>();
    }*/
    public RangedWeapon(MyGame myGame, Entity user) {
        super(myGame, user);

        this.bulletSpeed = 20 * METERS_PER_PIXEL;
        this.attackDamage = 3;
        this.attacksPerSecond = 10;
        this.bulletRadius = 0.2f * METERS_PER_PIXEL;

        totalBullets = new ArrayList<>();
    }
    public RangedWeapon(MyGame myGame, Entity user, float bulletSpeed, float attackDamage, float attacksPerSecond, float bulletRadius, Sound fireSound) {
        super(myGame, user);

        this.bulletSpeed = bulletSpeed;
        this.attackDamage = attackDamage;
        this.attacksPerSecond = attacksPerSecond;
        this.bulletRadius = bulletRadius;
        this.attackSound = fireSound;

        totalBullets = new ArrayList<>();
    }
    public float[] unitsOnScreen() {
        float idealRatio = VISIBLE_HORIZONTAL_TILES / VISIBLE_VERTICAL_TILES;
        float currentRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        if (idealRatio < currentRatio) {
            return new float[] {VISIBLE_VERTICAL_TILES * currentRatio, VISIBLE_VERTICAL_TILES};
        } else {
            return new float[] {VISIBLE_HORIZONTAL_TILES, VISIBLE_HORIZONTAL_TILES / currentRatio};
        }
    }
    @Override
    public void attack(Vector2 target) {
        if (attacksPerSecond == 0 || (myGame.timePassed - lastUse) > 1 / attacksPerSecond) {
            BulletClass newBullet = new BulletClass(myGame, this);

            float diffX = target.x - newBullet.body.getPosition().x;
            float diffY = target.y - newBullet.body.getPosition().y;
            float hypotenuse = (float) Math.sqrt(diffX * diffX + diffY * diffY);

            newBullet.body.setLinearVelocity(bulletSpeed / hypotenuse * diffX,bulletSpeed / hypotenuse * diffY);
            totalBullets.add(newBullet);
            lastUse = myGame.timePassed;
            if (attackSound != null) attackSound.play(0.2f);
        }
    }
    public void attack(Vector2 spawn, Vector2 start, Vector2 target) {
        if (attacksPerSecond == 0 || (myGame.timePassed - lastUse) > 1 / attacksPerSecond) {
            BulletClass newBullet;
            newBullet = new BulletClass(myGame, this, spawn);

            float diffX = target.x - start.x;
            float diffY = target.y - start.y;
            float hypotenuse = (float) Math.sqrt(diffX * diffX + diffY * diffY);

            newBullet.body.setLinearVelocity(bulletSpeed / hypotenuse * diffX,bulletSpeed / hypotenuse * diffY);
            totalBullets.add(newBullet);
            lastUse = myGame.timePassed;
            if (attackSound != null) attackSound.play(0.2f);
        }
    }

    public void attackWithAimbot(Entity e) {
        if (attacksPerSecond == 0 || (myGame.timePassed - lastUse) > 1 / attacksPerSecond) {
            BulletClass newBullet = new BulletClass(myGame, this);

            Vector2 entityPosition = e.getBodyCenter();
            Vector2 entityAcceleration = new Vector2(0, 0);

            if (e.contactFeet == 0 && e.body.getGravityScale() > 0) {
                entityAcceleration = new Vector2(0, GRAVITY);
            }
            Vector2 velocityVector = calcVelocityVector(entityPosition, e.body.getLinearVelocity(), entityAcceleration, newBullet.body.getPosition(), bulletSpeed, new Vector2(0, 0));

            newBullet.body.setLinearVelocity(velocityVector);

            totalBullets.add(newBullet);
            lastUse = myGame.timePassed;
        }
        //attack(new Vector2(e.getBodyCenter().x, e.getBodyCenter().y + 0.2f));
    }
    @Override
    public void destroy() {
        while (!totalBullets.isEmpty()) {
            totalBullets.get(0).destroy();
        }
    }

    @Override
    public void render() {
        for (BulletClass bc : totalBullets) {
            bc.render();
        }
        if (!totalBullets.isEmpty() && (myGame.timePassed - totalBullets.get(0).timeOfCreation) > BULLET_EXPIRY_SECONDS) {
            totalBullets.get(0).destroy();
        }
    }
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        if (!beginContact) return;
        Fixture weaponFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = Listener.checkContact(fixtureA, fixtureB, Weapon.class);
        if (collision[0] != null) {
            weaponFixture = collision[0];
            contactFixture = collision[1];
        }
        if (weaponFixture != null) {
            Weapon weapon = (Weapon) weaponFixture.getUserData();
            if (contactFixture.getUserData() == null || weapon.user.getClass() != contactFixture.getUserData().getClass()) {
                if (ClassReflection.isInstance(weapon.user.enemy, contactFixture.getUserData())) {
                    if (weapon.user.getClass() == Player.class) {
                        ((Sound) myGame.assetManager.get("hitmarker.mp3")).play();
                    }
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
                    if (!ClassReflection.isInstance(b1.user.enemy, b2.user)) return;
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
}
