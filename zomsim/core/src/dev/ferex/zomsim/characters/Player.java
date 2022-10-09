package dev.ferex.zomsim.characters;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.pathfinding.SteeringUtils;
import dev.ferex.zomsim.objectives.*;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.Weapon;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.guns.Rifle;
import dev.ferex.zomsim.weapons.melee.Knife;
import dev.ferex.zomsim.world.EventHandler;
import dev.ferex.zomsim.world.interactable.Interactable;
import dev.ferex.zomsim.world.interactable.WeaponSpawn;

import java.util.concurrent.ThreadLocalRandom;

public class Player extends BasicCharacter implements Location<Vector2> {

    private static Player instance;
    private float stateTimer = 0;
    public Interactable inContactWith;
    public BasicZombie inContactWithZombie;

    public Weapon primaryWeapon = null;
    public Weapon secondaryWeapon = null;
    public Weapon meleeWeapon = new Knife();
    public WeaponSlot activeWeaponSlot = WeaponSlot.MELEE;
    public boolean isAttacking = false;

    public CollectObjective collectObjective = new CollectObjective(ThreadLocalRandom.current().nextInt(4, 8 + 1));
    public RescueObjective rescueObjective = new RescueObjective();
    public KillObjective killObjective = new KillObjective(ThreadLocalRandom.current().nextInt(5, 10 + 1));
    public EscapeObjective escapeObjective = new EscapeObjective();

    private ObjectivePointer objectivePointer;

    private final Animation<TextureAtlas.AtlasRegion> knife_idle;
    private final Animation<TextureAtlas.AtlasRegion> pistol_idle;
    private final Animation<TextureAtlas.AtlasRegion> rifle_idle;
    private final Animation<TextureAtlas.AtlasRegion> shotgun_idle;
    private final Animation<TextureAtlas.AtlasRegion> knife_move;
    private final Animation<TextureAtlas.AtlasRegion> pistol_move;
    private final Animation<TextureAtlas.AtlasRegion> rifle_move;
    private final Animation<TextureAtlas.AtlasRegion> shotgun_move;
    private final Animation<TextureAtlas.AtlasRegion> knife_attack;
    private final Animation<TextureAtlas.AtlasRegion> pistol_shoot;
    private final Animation<TextureAtlas.AtlasRegion> rifle_shoot;
    private final Animation<TextureAtlas.AtlasRegion> shotgun_shoot;
    private final Animation<TextureAtlas.AtlasRegion> pistol_reload;
    private final Animation<TextureAtlas.AtlasRegion> rifle_reload;
    private final Animation<TextureAtlas.AtlasRegion> shotgun_reload;


    private Player() {
        super(0, 0, 100, ZomSim.PLAYER_BIT);
        bodyFixture.setUserData("player");
        meleeFixture.setUserData("player_melee");

        TextureAtlas animations = new TextureAtlas("sprites/player/Player.atlas");
        knife_idle = new Animation<>(1/20f, animations.findRegions("survivor-idle_knife"));
        knife_move = new Animation<>(1/20f, animations.findRegions("survivor-move_knife"));
        knife_attack = new Animation<>(1/20f, animations.findRegions("survivor-meleeattack_knife"));

        pistol_idle = new Animation<>(1/20f, animations.findRegions("survivor-idle_handgun"));
        pistol_move = new Animation<>(1/20f, animations.findRegions("survivor-move_handgun"));
        pistol_shoot = new Animation<>(1/20f, animations.findRegions("survivor-shoot_handgun"));
        pistol_reload = new Animation<>(1/15f, animations.findRegions("survivor-reload_handgun"));

        rifle_idle = new Animation<>(1/20f, animations.findRegions("survivor-idle_rifle"));
        rifle_move = new Animation<>(1/20f, animations.findRegions("survivor-move_rifle"));
        rifle_shoot = new Animation<>(1/20f, animations.findRegions("survivor-shoot_rifle"));
        rifle_reload = new Animation<>(1/20f, animations.findRegions("survivor-reload_rifle"));

        shotgun_idle = new Animation<>(1/20f, animations.findRegions("survivor-idle_shotgun"));
        shotgun_move = new Animation<>(1/20f, animations.findRegions("survivor-move_shotgun"));
        shotgun_shoot = new Animation<>(1/20f, animations.findRegions("survivor-shoot_shotgun"));
        shotgun_reload = new Animation<>(1/20f, animations.findRegions("survivor-reload_shotgun"));

        setBounds(0, 0, 10, 10);
        setOriginCenter();
    }

    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }

    public void attack() {
        getActiveWeapon().attack();
    }

    public void move(Vector2 vector2) {
        b2body.setLinearVelocity(vector2);
    }

    public void teleport(Vector2 vector2) {
        b2body.setTransform(vector2, b2body.getAngle());
    }

    public void face(Vector2 angle) {
        setRotation(angle.angleDeg());
        b2body.setTransform(b2body.getPosition(), angle.angleRad());
    }

    public void interact() {
        if(inContactWith != null)
            inContactWith.interact();
    }

    public void showObjectivePointer(Vector2 location) {
        objectivePointer = new ObjectivePointer(location);
    }

    public void hideObjectivePointer() {
        objectivePointer = null;
    }

    public Vector3 getVector3Position() {
        return new Vector3(b2body.getPosition().x, b2body.getPosition().y, 0);
    }

    @Override
    public void update(float delta) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
        if(rescueObjective.isInProgress() || escapeObjective.isInProgress()) {
            objectivePointer.update();
        }

        getActiveWeapon().update(delta);

        if(stateTimer % 1000 < 100) {
            switch (currentState) {
                case RUNNING:
                    EventHandler.getInstance().onPlayerRun();
                    break;
                case WALKING:
                    EventHandler.getInstance().onPlayerWalk();
                    break;
                case ATTACKING:
                    EventHandler.getInstance().onPlayerAttack();
                    break;
                case RELOADING:
                    EventHandler.getInstance().onPlayerReload();
                    break;
            }
        }

        if(collectObjective.isComplete() && killObjective.isComplete() && rescueObjective.isComplete() && !escapeObjective.isInProgress()) {
            escapeObjective.setEscapeLocation(EntityHandler.getInstance().escapeObjective.escapeLocation);
            escapeObjective.start();
        }
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if(rescueObjective.isInProgress() || escapeObjective.isInProgress()) {
            objectivePointer.draw(batch);
        }
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region = new TextureRegion();
        switch (currentState) {
            case RELOADING:
                region = getTextureRegion(region, rifle_reload, shotgun_reload, pistol_reload, knife_idle);
                break;
            case ATTACKING:
                region = getTextureRegion(region, rifle_shoot, shotgun_shoot, pistol_shoot, knife_attack);
                break;
            case WALKING:
            case RUNNING:
                region = getTextureRegion(region, rifle_move, shotgun_move, pistol_move, knife_move);
                break;
            default:
                region = getTextureRegion(region, rifle_idle, shotgun_idle, pistol_idle, knife_idle);
                break;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    private TextureRegion getTextureRegion(TextureRegion region, Animation<TextureAtlas.AtlasRegion> rifle, Animation<TextureAtlas.AtlasRegion> shotgun,
                                           Animation<TextureAtlas.AtlasRegion> pistol, Animation<TextureAtlas.AtlasRegion> knife) {
        switch (activeWeaponSlot) {
            case PRIMARY:
                if(primaryWeapon instanceof Rifle)
                    region = rifle.getKeyFrame(stateTimer, true);
                else
                    region = shotgun.getKeyFrame(stateTimer, true);
                break;
            case SECONDARY:
                region = pistol.getKeyFrame(stateTimer, true);
                break;
            case MELEE:
                region = knife.getKeyFrame(stateTimer, true);
        }
        return region;
    }

    public CharacterState getState() {
        if(getActiveWeapon().isReloading())
            return CharacterState.RELOADING;
        if(isAttacking)
            return CharacterState.ATTACKING;
        if (Math.abs(b2body.getLinearVelocity().x) > 2 || Math.abs(b2body.getLinearVelocity().y) > 2)
            return CharacterState.RUNNING;
        if (Math.abs(b2body.getLinearVelocity().x) > 0 || Math.abs(b2body.getLinearVelocity().y) > 0)
            return CharacterState.WALKING;
        return CharacterState.IDLE;
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
        return this;
    }

    public Weapon getActiveWeapon() {
        switch(activeWeaponSlot) {
            case PRIMARY:
                return primaryWeapon;
            case SECONDARY:
                return secondaryWeapon;
            case MELEE:
                return meleeWeapon;
        }
        return meleeWeapon;
    }

    public void addWeapon(Weapon weapon) {
        switch(weapon.getSlot()) {
            case PRIMARY:
                if(primaryWeapon != null) {
                    EntityHandler.getInstance().weapons.add(new WeaponSpawn((int) getX(), (int) getY(), 8, 8, primaryWeapon));
                }
                primaryWeapon = weapon;
                break;
            case SECONDARY:
                if(secondaryWeapon != null) {
                    EntityHandler.getInstance().weapons.add(new WeaponSpawn((int) getX(), (int) getY(), 8, 8, secondaryWeapon));
                }
                secondaryWeapon = weapon;
                break;
            case MELEE:
                if(meleeWeapon != null) {
                    EntityHandler.getInstance().weapons.add(new WeaponSpawn((int) getX(), (int) getY(), 8, 8, meleeWeapon));
                }
                meleeWeapon = weapon;
        }
    }

    public void reload() {
        getActiveWeapon().reload();
    }

    public void equipWeapon(WeaponSlot slot) {
        switch(slot) {
            case PRIMARY:
                if(primaryWeapon != null)
                    activeWeaponSlot = WeaponSlot.PRIMARY;
                break;
            case SECONDARY:
                if(secondaryWeapon != null)
                    activeWeaponSlot = WeaponSlot.SECONDARY;
                break;
            case MELEE:
                if(meleeWeapon != null)
                    activeWeaponSlot = WeaponSlot.MELEE;
        }
    }

    public boolean addAmmo(AmmoType type, int amount) {
        if(primaryWeapon.getAmmoType() == type) {
            primaryWeapon.addAmmo(amount);
            return true;
        }
        if(secondaryWeapon.getAmmoType() == type) {
            secondaryWeapon.addAmmo(amount);
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        health -= damage;
        ZomSim.getInstance().assetManager.get("audio/sounds/player_hurt.wav", Sound.class).play();
    }

}
