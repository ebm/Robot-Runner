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
    /*public Vector2 calculatePrediction(Entity e) {
        System.out.println("====================================");
        System.out.println("User: " + user.getBodyCenter() + ", Radius: " + bulletSpeed);
        System.out.println("Entity fired at: " + e.getBodyCenter() + ", Entity velocity: " + e.body.getLinearVelocity());

        float slope = ((e.body.getLinearVelocity().y + e.getBodyCenter().y) - e.getBodyCenter().y) / ((e.body.getLinearVelocity().x + e.getBodyCenter().x) - e.getBodyCenter().x);
        float yIntercept = e.getBodyCenter().y - slope * e.getBodyCenter().x;
        float intersectX = (float) (Math.sqrt(bulletSpeed * bulletSpeed * (slope * slope + 1) - user.getBodyCenter().x * user.getBodyCenter().x * slope * slope
                + (2 * user.getBodyCenter().x * user.getBodyCenter().y - 2 * yIntercept * user.getBodyCenter().x) * slope - user.getBodyCenter().y * user.getBodyCenter().y
                + 2 * yIntercept * user.getBodyCenter().y - yIntercept * yIntercept) + (user.getBodyCenter().y - yIntercept) * slope + user.getBodyCenter().x) / (slope * slope + 1);

        float intersectY = slope * intersectX + yIntercept;

        float intersectX2 = (float) -(Math.sqrt(bulletSpeed * bulletSpeed * (slope * slope + 1) - user.getBodyCenter().x * user.getBodyCenter().x * slope * slope
                + (2 * user.getBodyCenter().x * user.getBodyCenter().y - 2 * yIntercept * user.getBodyCenter().x) * slope - user.getBodyCenter().y * user.getBodyCenter().y
                + 2 * yIntercept * user.getBodyCenter().y - yIntercept * yIntercept) + (yIntercept - user.getBodyCenter().y) * slope - user.getBodyCenter().x) / (slope * slope + 1);
        float intersectY2 = slope * intersectX2 + yIntercept;

        System.out.println("Intersect: " + new Vector2(intersectX, intersectY) + " | Intersect2: " + new Vector2(intersectX2, intersectY2));

        return new Vector2(intersectX2, intersectY2);
    }*/
    public double solveCubicEquation(double a, double b, double c, double d) {
        System.out.println("Cubic solution");
        return -1;
    }
    public double solveQuadraticEquation(double a, double b, double c) {
        double res1 = (-b+Math.sqrt(b*b-4*a*c))/(2*a);
        double res2 = (-b-Math.sqrt(b*b-4*a*c))/(2*a);

        System.out.println("Quadratic solution: " + res1 + ", " + res2);
        return res2;
    }
    public double solveLinearEquation(double a, double b) {
        System.out.println("Linear solution: " + -b/a);
        return -b/a;
    }
    public void solveQuarticEquation(double A, double B, double C, double D, double E) {
        System.out.println("equation: " + A + "t^4 + " + B + "t^3 + "+ C + "t^2 + "+ D + "t^1 + " + E + " = 0");
        double a = -(3*B*B)/(8*A*A)+C/A;
        double b = (B*B*B)/(8*A*A*A)-(B*C)/(2*A*A)+D/A;
        double c = -(3*B*B*B*B)/(256*A*A*A*A)+(C*B*B)/(16*A*A*A)-(B*D)/(4*A*A)+E/A;

        double res;
        double res1 = -1;
        double res2 = -1;
        double res3 = -1;
        double res4 = -1;
        if (A == 0 && B == 0 && C == 0) {
            res = solveLinearEquation(D, E);
        } else if (A == 0 && B == 0) {
            res = solveQuadraticEquation(C, D, E);
        } else if (A == 0) {
            res = solveCubicEquation(B, C, D, E);
        } else if (b == 0) {
            System.out.println("b=0");
            res1 = -B/(4*A)+Math.sqrt((-a+Math.sqrt(a*a-4*c))/2);
            res2 = -B/(4*A)+Math.sqrt((-a-Math.sqrt(a*a-4*c))/2);
            res3 = -B/(4*A)-Math.sqrt((-a+Math.sqrt(a*a-4*c))/2);
            res4 = -B/(4*A)-Math.sqrt((-a-Math.sqrt(a*a-4*c))/2);

            //System.out.println("Quartic solution: " + res1 + ", " + res2 + ", " + res3 + ", " + res4);
        } else {
            double P = -(a*a)/12-c;
            double Q = -(a*a*a)/108+(a*c)/3-(b*b)/8;
            double R = -Q/2+Math.sqrt((Q*Q)/4+(P*P*P)/27);
            double U = Math.cbrt(R);
            double y =  -5*a/6;
            if (U == 0) {
                y = y - Math.cbrt(Q);
            } else {
                y = y + U - P/(3*U);
            }
            System.out.println("P = " + P + ", Q = " + Q + ", R = " + R + ", U = " + U + ", y = " + y);
            res1 = -B/(4*A)+(+Math.sqrt(a+2*y)+Math.sqrt(-(3*a+2*y+(2*b)/Math.sqrt(a+2*y))))/2;
            res2 = -B/(4*A)+(+Math.sqrt(a+2*y)-Math.sqrt(-(3*a+2*y+(2*b)/Math.sqrt(a+2*y))))/2;
            res3 = -B/(4*A)+(-Math.sqrt(a+2*y)+Math.sqrt(-(3*a+2*y-(2*b)/Math.sqrt(a+2*y))))/2;
            res4 = -B/(4*A)+(-Math.sqrt(a+2*y)-Math.sqrt(-(3*a+2*y-(2*b)/Math.sqrt(a+2*y))))/2;

            System.out.println("Quartic normal solution: " + res1 + ", " + res2 + ", " + res3 + ", " + res4);
        }
        //System.out.println("Quartic solution: " + res);
    }
    public double calcTime(Vector2 playerPosition, Vector2 playerVelocity, Vector2 playerAcceleration, Vector2 bulletPosition, Vector2 bulletAcceleration, double time) {
        double v = bulletSpeed;
        double p = playerAcceleration.y - bulletAcceleration.y;
        double x = playerPosition.x - bulletPosition.x;
        double y = playerPosition.y - bulletPosition.y;
        double a = playerVelocity.x;
        double b = playerVelocity.y;
        System.out.println("v = " + v + ", p = " + p + ", x = " + x + ", y = " + y + ", a = " + a + ", b = " + b);
        //double accuracy = equation(time, v, p, x, y, a, b);
        //System.out.println("Default time accuracy: " + accuracy);
        double A = p*p/4;
        double B = -b*p;
        double C = -p*y+b*b-v*v+a*a;
        double D = 2*b*y+2*x*a;
        double E = x*x+y*y;
        solveQuarticEquation(A, B, C, D, E);

        return time;
        //return secantMethod(Math.max(0.001, time - 1), time + 3, v, p, x, y, a, b);
    }
    public void attackWithAimbot(Entity e) {
        if (attacksPerSecond == 0 || (myGame.timePassed - lastUse) > 1 / attacksPerSecond) {
            BulletClass newBullet = new BulletClass(myGame, this);

            float time = (float) (Math.sqrt((e.getBodyCenter().x * e.getBodyCenter().x - 2 * newBullet.body.getPosition().x
                    * e.getBodyCenter().x + newBullet.body.getPosition().x * newBullet.body.getPosition().x + e.getBodyCenter().y
                    * e.getBodyCenter().y - 2 * newBullet.body.getPosition().y * e.getBodyCenter().y + newBullet.body.getPosition().y
                    * newBullet.body.getPosition().y) * bulletSpeed * bulletSpeed - e.body.getLinearVelocity().y
                    * e.body.getLinearVelocity().y * e.getBodyCenter().x * e.getBodyCenter().x + (2 * e.body.getLinearVelocity().y
                    * e.body.getLinearVelocity().y * newBullet.body.getPosition().x + 2 * e.body.getLinearVelocity().y
                    * e.body.getLinearVelocity().x * e.getBodyCenter().y - 2 * e.body.getLinearVelocity().y * e.body.getLinearVelocity().x
                    * newBullet.body.getPosition().y) * e.getBodyCenter().x - e.body.getLinearVelocity().y * e.body.getLinearVelocity().y
                    * newBullet.body.getPosition().x * newBullet.body.getPosition().x + (2 * e.body.getLinearVelocity().y
                    * e.body.getLinearVelocity().x * newBullet.body.getPosition().y - 2 * e.body.getLinearVelocity().y
                    * e.body.getLinearVelocity().x * e.getBodyCenter().y) * newBullet.body.getPosition().x - e.body.getLinearVelocity().x
                    * e.body.getLinearVelocity().x * e.getBodyCenter().y * e.getBodyCenter().y + 2 * e.body.getLinearVelocity().x
                    * e.body.getLinearVelocity().x * newBullet.body.getPosition().y * e.getBodyCenter().y - e.body.getLinearVelocity().x
                    * e.body.getLinearVelocity().x * newBullet.body.getPosition().y * newBullet.body.getPosition().y)
                    + e.body.getLinearVelocity().x * e.getBodyCenter().x - e.body.getLinearVelocity().x* newBullet.body.getPosition().x
                    + e.body.getLinearVelocity().y * e.getBodyCenter().y - e.body.getLinearVelocity().y * newBullet.body.getPosition().y)
                    / (bulletSpeed * bulletSpeed - e.body.getLinearVelocity().x * e.body.getLinearVelocity().x
                    - e.body.getLinearVelocity().y * e.body.getLinearVelocity().y);
            //System.out.println("OG Time: " + time);
            //Vector2 playerAcceleration = new Vector2(0, 0);
            //if (e.getClass() == Player.class) {
            //    if (((Player) e).contactFeet == 0) {
            //        playerAcceleration = new Vector2(0, GRAVITY);
            //    }
            //}
            //time = (float) calcTime(e.getBodyCenter(), e.body.getLinearVelocity(), playerAcceleration, newBullet.body.getPosition(), new Vector2(0, 0), time);
            float horizontalBulletVelocity = e.body.getLinearVelocity().x - (newBullet.body.getPosition().x - e.getBodyCenter().x) / time;
            float verticalBulletVelocity = e.body.getLinearVelocity().y - (newBullet.body.getPosition().y - e.getBodyCenter().y) / time;

            //float horizontalBulletVelocity = e.body.getLinearVelocity().x - (newBullet.body.getPosition().x - e.getBodyCenter().x) / time;
            //float verticalBulletVelocity = e.body.getLinearVelocity().y + 0.5f * playerAcceleration.y * time - (newBullet.body.getPosition().y - e.getBodyCenter().y) / time;

            newBullet.body.setLinearVelocity(horizontalBulletVelocity, verticalBulletVelocity);
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
