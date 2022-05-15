package dev.ferex.zomsim.characters;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.pathfinding.SteeringUtils;
import dev.ferex.zomsim.screens.GameScreen;

import java.util.concurrent.ThreadLocalRandom;

public class Survivor extends BasicCharacter implements Steerable<Vector2> {
    private final GameScreen screen;

    public enum State { IDLE, MOVING }
    public State currentState = State.IDLE;
    public State previousState = State.IDLE;
    private float stateTimer = 0;

    public boolean toDestroy = false;
    public boolean destroyed = false;

    boolean tagged = false;
    float boundingRadius;
    float maxLinearSpeed = 500, maxLinearAcceleration = 5000;
    float maxAngularSpeed = 30, maxAngularAcceleration = 5;
    float zeroLinearSpeedThreshold = 1;

    private final Animation idle;
    private final Animation move;

    SteeringBehavior<Vector2> behaviour;
    SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());

    public Survivor(final GameScreen screen, World world, int xPos, int yPos) {
        super(world, xPos + 4, yPos + 4, 50, ZomSim.PLAYER_BIT);
        this.screen = screen;

        b2body.setUserData(this);

        int randomDegree = ThreadLocalRandom.current().nextInt(0, 360 + 1);
        b2body.setTransform(b2body.getPosition(), (float) (randomDegree * (Math.PI / 180)));
        setRotation(randomDegree);

        setBounds(0, 0, 10, 10);
        setOriginCenter();

        TextureAtlas animations = new TextureAtlas("sprites/survivor/Survivor.atlas");
        idle = new Animation(1/20f, animations.findRegions("survivor-idle_flashlight"));
        move = new Animation(1/20f, animations.findRegions("survivor-move_flashlight"));
    }

    @Override
    public void update(float delta) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));

        if(behaviour != null) {
            behaviour.calculateSteering(steeringOutput);
            applySteering(delta);
            Vector2 angle = new Vector2(0, 0);
            angle.x = screen.player.b2body.getPosition().x - b2body.getPosition().x;
            angle.y = screen.player.b2body.getPosition().y - b2body.getPosition().y;
            setRotation(angle.angleDeg());
            b2body.setTransform(b2body.getPosition(), angle.angleRad());
        }

        if(toDestroy && !destroyed) {
            screen.world.destroyBody(b2body);
            destroyed = true;
        }
    }

    public void chasePlayer() {
        Arrive<Vector2> arriveSB = new Arrive<>(this, screen.player)
                .setTimeToTarget(0.01f)
                .setArrivalTolerance(2f)
                .setDecelerationRadius(10);
        setBehaviour(arriveSB);
    }

    public void destroy() {
        toDestroy = true;
    }

    @Override
    public void draw(Batch batch) {
        if(!destroyed) {
            super.draw(batch);
        }
    }

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

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region = new TextureRegion();
        switch (currentState) {
            case IDLE:
                region = (TextureRegion) idle.getKeyFrame(stateTimer, true);
                break;
            case MOVING:
                region = (TextureRegion) move.getKeyFrame(stateTimer, true);
                break;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if(Math.abs(b2body.getLinearVelocity().x) > 0 || Math.abs(b2body.getLinearVelocity().y) > 0)
            return State.MOVING;
        return State.IDLE;
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
