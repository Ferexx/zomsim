package dev.ferex.zomsim.characters;

import astra.core.Agent;
import astra.term.ListTerm;
import astra.term.Primitive;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.pathfinding.LevelGraph;
import dev.ferex.zomsim.characters.pathfinding.SteeringUtils;
import dev.ferex.zomsim.characters.pathfinding.Tile;
import dev.ferex.zomsim.world.WorldManager;
import dev.ferex.zomsim.world.interactable.WeaponSpawn;
import zomlink.ZombieEvent;
import zomlink.ZombieListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class BasicZombie extends BasicCharacter implements ZombieInterface, ZombieListener, Steerable<Vector2> {
    public Agent agent;
    public LevelGraph levelGraph;

    public static final double RANGE_HEARING = 50;
    public static final float RANGE_SIGHT = 80;
    public static final int ATTACK_DAMAGE = 10;
    public static final double ATTACK_TIME = 0.75;
    public boolean canAttack = false;
    private float stateTimer = 0;
    private Tile nextTile;
    private final Queue<Tile> currentPath = new Queue<>();

    private final Animation<TextureAtlas.AtlasRegion> idle;
    private final Animation<TextureAtlas.AtlasRegion> move;
    private final Animation<TextureAtlas.AtlasRegion> attack;

    private final RayCastCallback rayCastCallback;
    private float closestFraction;
    private Fixture closestFixture;
    private boolean seeingPlayer = false;
    private boolean previouslySeeingPlayer = false;

    boolean tagged = false;
    float boundingRadius;
    float maxLinearSpeed = 500, maxLinearAcceleration = 5000;
    float maxAngularSpeed = 30, maxAngularAcceleration = 5;
    float zeroLinearSpeedThreshold = 1;

    SteeringBehavior<Vector2> behaviour;
    SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());

    public BasicZombie(int xPos, int yPos){
        super(xPos + 4, yPos + 4, 50, ZomSim.ENEMY_BIT);
        levelGraph = EntityHandler.getInstance().levelGraph;

        b2body.setUserData(this);
        bodyFixture.setUserData("zombie_body");
        meleeFixture.setUserData("zombie_melee");

        final int randomDegree = ThreadLocalRandom.current().nextInt(0, 360 + 1);
        b2body.setTransform(b2body.getPosition(), (float) (randomDegree * (Math.PI / 180)));
        setRotation(randomDegree);

        setBounds(0, 0, 100, 100);

        final TextureAtlas animations = new TextureAtlas("sprites/zombies/basic/BasicZombie.atlas");
        idle = new Animation<>(1/17f, animations.findRegions("skeleton-idle"));
        move = new Animation<>(1/17f, animations.findRegions("skeleton-move"));
        attack = new Animation<>(1/9f, animations.findRegions("skeleton-attack"));

        setScale(0.1f);
        setOriginCenter();

        rayCastCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if(fixture.getUserData() == "player_melee")
                    return -1;
                if(fixture.getUserData() == "zombie_melee")
                    return -1;
                if(fraction < closestFraction || closestFraction == 0) {
                    closestFraction = fraction;
                    closestFixture = fixture;
                }
                return 1;
            }
        };
    }

    @Override
    public void update(float delta) {
        Player player = Player.getInstance();
        World world = WorldManager.getInstance().getWorld();
        EntityHandler entityHandler = EntityHandler.getInstance();

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));

        if(currentPath.notEmpty() && nextTile != null && Vector2.dst(b2body.getPosition().x, b2body.getPosition().y, nextTile.x + 4, nextTile.y + 4) < 0.5)
            reachNextTile();

        if(canAttack) {
            facePlayer();
            if(stateTimer - ATTACK_TIME > 0) {
                player.takeDamage(ATTACK_DAMAGE);
                stateTimer = 0;
            }
        }

        if(health <= 0) {
            world.destroyBody(b2body);
            entityHandler.zombies.removeValue(this, true);
            player.killObjective.addKill();
        }

        float distance = Vector2.dst(b2body.getPosition().x, b2body.getPosition().y, player.b2body.getPosition().x, player.b2body.getPosition().y);
        if(stateTimer % 10 < 0.01 && stateTimer > 1 && distance < 50) {
            if(b2body.getPosition().x < player.b2body.getPosition().x) distance *= -1;
            long soundID = ZomSim.getInstance().assetManager.get("audio/sounds/zombie_growl.wav", Sound.class).play();
            ZomSim.getInstance().assetManager.get("audio/sounds/zombie_growl.wav", Sound.class).setPan(soundID, distance / 50,  1/(Math.abs(distance) / 50));
        }

        if(b2body.getPosition().dst(player.b2body.getPosition()) <= RANGE_SIGHT) {
            for (int i = -60; i <= 60; i++) {
                closestFraction = 0;
                closestFixture = null;
                float x = (float) (Math.cos(b2body.getAngle() + (i * Math.PI / 180)) * RANGE_SIGHT);
                float y = (float) (Math.sin(b2body.getAngle() + (i * Math.PI / 180)) * RANGE_SIGHT);
                world.rayCast(rayCastCallback, new Vector2(b2body.getPosition().x, b2body.getPosition().y), new Vector2(b2body.getPosition().x + x, b2body.getPosition().y + y));
                if (closestFixture != null && closestFixture.getUserData() == "player") {
                    facePlayer();
                    seeingPlayer = true;
                    break;
                }
                seeingPlayer = false;
            }
            if (seeingPlayer && !previouslySeeingPlayer) {
                addEvent("enteredVision");
                previouslySeeingPlayer = true;
            }
            if (!seeingPlayer && previouslySeeingPlayer) {
                String[] array = {String.valueOf(player.b2body.getPosition().x), String.valueOf(player.b2body.getPosition().y)};
                addEvent("leftVision", new Array<>(array));
                previouslySeeingPlayer = false;
            }
        }

        if(behaviour != null) {
            behaviour.calculateSteering(steeringOutput);
            applySteering(delta);
        }
    }

    public void canAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    // ====================== [ STEERING ] ========================

    private void applySteering(float delta) {
        boolean anyAccelerations = false;

        if(!steeringOutput.linear.isZero()) {
            Vector2 force = steeringOutput.linear.scl(delta * 75);
            b2body.applyForceToCenter(force, true);
            anyAccelerations = true;
        }

        if(steeringOutput.angular != 0) {
            b2body.applyTorque(steeringOutput.angular * delta, true);
            anyAccelerations = true;
        }

        if(anyAccelerations) {
            Vector2 velocity = b2body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            if(currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                b2body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
            }

            if(b2body.getAngularVelocity() > maxAngularSpeed) {
                b2body.setAngularVelocity(maxAngularSpeed);
            }
        }
    }

    public boolean chasePlayer() {
        try {
            Arrive<Vector2> arriveSB = new Arrive<>(this, Player.getInstance())
                    .setTimeToTarget(0.01f)
                    .setArrivalTolerance(2f)
                    .setDecelerationRadius(10);
            setBehaviour(arriveSB);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void chaseTile() {
        Arrive<Vector2> arriveSB = new Arrive<>(this, nextTile)
                .setTimeToTarget(0.01f)
                .setArrivalTolerance(0.5f)
                .setDecelerationRadius(10);
        setBehaviour(arriveSB);
    }

    public boolean goToClosestWeapon() {
        EntityHandler entityHandler = EntityHandler.getInstance();
        try {
            WeaponSpawn closestWeapon = null;
            float closestDistance = 10000;
            for (WeaponSpawn weapon : entityHandler.weapons) {
                float distance = Vector2.dst(b2body.getPosition().x, b2body.getPosition().y, weapon.getBoundingRectangle().x + 4, weapon.getBoundingRectangle().y + 4);
                if (distance < closestDistance) {
                    closestWeapon = weapon;
                    closestDistance = distance;
                }
            }

            assert closestWeapon != null;
            return moveTo(entityHandler.levelGraph.getTile(closestWeapon.body.getPosition()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean goToClosestZombie() {
        EntityHandler entityHandler = EntityHandler.getInstance();
        try {
            BasicZombie closestZombie = null;
            float closestDistance = 10000;
            for (BasicZombie zombie : entityHandler.zombies) {
                if (!zombie.equals(this)) {
                    float distance = Vector2.dst(b2body.getPosition().x, b2body.getPosition().y, zombie.getPosition().x + 4, zombie.getPosition().y + 4);
                    if (distance < closestDistance) {
                        closestZombie = zombie;
                        closestDistance = distance;
                    }
                }
            }

            assert closestZombie != null;
            return moveTo(entityHandler.levelGraph.getTile(closestZombie.b2body.getPosition()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean goToLocation(float x, float y) {
        return moveTo(EntityHandler.getInstance().levelGraph.getTile((int) x, (int) y));
    }

    public boolean goToPlayer() {
        return moveTo(EntityHandler.getInstance().levelGraph.getTile(Player.getInstance().b2body.getPosition()));
    }

    public void facePlayer() {
        Player player = Player.getInstance();
        Vector2 angle = new Vector2(0, 0);
        angle.x = player.b2body.getPosition().x - b2body.getPosition().x;
        angle.y = player.b2body.getPosition().y - b2body.getPosition().y;
        setRotation(angle.angleDeg());
        b2body.setTransform(b2body.getPosition(), angle.angleRad());
    }

    public boolean stop() {
        behaviour = null;
        b2body.setLinearVelocity(new Vector2(0, 0));
        currentPath.clear();
        currentState = CharacterState.IDLE;
        return true;
    }

    // ====================== [ AGENT ] ========================

    public void addEvent(String eventName) {
        ZombieEvent event = new ZombieEvent(Primitive.newPrimitive(eventName));
        agent.addEvent(event);
    }

    public void addEvent(String eventName, Array<String> args) {
        ListTerm arguments = new ListTerm();
        for(String arg : args) {
            arguments.add(Primitive.newPrimitive(arg));
        }
        ZombieEvent event = new ZombieEvent(Primitive.newPrimitive(eventName), arguments);
        agent.addEvent(event);
    }

    @Override
    public boolean handle(String type, Object[] params) {
        return true;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public LinkedList<Float> getLocation() {
        return new LinkedList<>(Arrays.asList(b2body.getPosition().x, b2body.getPosition().y));
    }

    @Override
    public int getPlayerHealth() {
        return Player.getInstance().health;
    }

    // ====================== [ PATHFINDING ] ========================

    public boolean moveTo(Tile goal) {
        Tile start = levelGraph.getTile(b2body.getPosition());
        GraphPath<Tile> path = levelGraph.findPath(start, goal);
        if(path.getCount() > 0) {
            currentPath.clear();
            for (int i = 1; i < path.getCount(); i++) {
                currentPath.addLast(path.get(i));
            }
            b2body.setTransform(new Vector2(start.x + 4, start.y + 4), b2body.getAngle());
            moveToNextTile();
            return true;
        }
        return false;
    }

    public void moveToNextTile() {
        nextTile = currentPath.first();
        b2body.setTransform(b2body.getPosition(), new Vector2(b2body.getPosition().x + nextTile.x + 4, b2body.getPosition().y + nextTile.y + 4).angleRad());
        chaseTile();
    }

    public void reachNextTile() {
        currentPath.removeFirst();
        if(currentPath.isEmpty()) {
            reachGoal();
        } else {
            moveToNextTile();
        }
    }

    public void reachGoal() {
        b2body.setLinearVelocity(new Vector2(0, 0));
        currentState = CharacterState.IDLE;
        addEvent("destinationReached");
    }

    // ====================== [ SPRITE DRAWING ] ========================

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region = new TextureRegion();
        switch (currentState) {
            case IDLE:
                region = (TextureRegion) idle.getKeyFrame(stateTimer, true);
                break;
            case RUNNING:
                region = (TextureRegion) move.getKeyFrame(stateTimer, true);
                break;
            case ATTACKING:
                region = (TextureRegion) attack.getKeyFrame(stateTimer, true);
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    public CharacterState getState() {
        if(canAttack) return CharacterState.ATTACKING;
        if(Math.abs(b2body.getLinearVelocity().x) > 0 || Math.abs(b2body.getLinearVelocity().y) > 0)
            return CharacterState.RUNNING;
        return CharacterState.IDLE;
    }

    public void draw(Batch batch) {
        super.draw(batch);
        if(ZomSim.MODE_DEBUG) {
            ShapeRenderer renderer = new ShapeRenderer();
            for (Tile tile : currentPath) {
                tile.draw(renderer);
            }
            float x = (float) (Math.cos(b2body.getAngle()) * RANGE_SIGHT);
            float y = (float) (Math.sin(b2body.getAngle()) * RANGE_SIGHT);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            Vector3 coordinates = WorldManager.getInstance().camera.project(new Vector3(b2body.getPosition().x, b2body.getPosition().y, 0));
            Vector3 coordinates2 = WorldManager.getInstance().camera.project(new Vector3(b2body.getPosition().x + x, b2body.getPosition().y + y, 0));
            renderer.line(new Vector2(coordinates.x, coordinates.y), new Vector2(coordinates2.x, coordinates2.y));
            renderer.end();
        }
    }

    @Override
    public Vector2 getLinearVelocity() {
        return b2body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return b2body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        this.zeroLinearSpeedThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return b2body.getPosition();
    }

    @Override
    public float getOrientation() {
        return b2body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {
        b2body.setTransform(b2body.getPosition(), orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return SteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return SteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    public Body getBody() {
        return b2body;
    }

    public void setBehaviour(SteeringBehavior<Vector2> behaviour) {
        this.behaviour = behaviour;
    }

    public SteeringBehavior<Vector2> getBehaviour() {
        return behaviour;
    }
}
