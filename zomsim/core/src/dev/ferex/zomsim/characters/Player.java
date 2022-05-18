package dev.ferex.zomsim.characters;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.pathfinding.SteeringUtils;
import dev.ferex.zomsim.objectives.*;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.BasicWeapon;
import dev.ferex.zomsim.weapons.Bullet;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.guns.BasicGun;
import dev.ferex.zomsim.weapons.guns.Rifle;
import dev.ferex.zomsim.weapons.melee.Knife;
import dev.ferex.zomsim.world.interactable.InteractableInterface;
import dev.ferex.zomsim.world.interactable.WeaponSpawn;

import java.util.concurrent.ThreadLocalRandom;

public class Player extends BasicCharacter implements Location<Vector2> {
    private float stateTimer = 0;
    public InteractableInterface inContactWith;
    public BasicZombie inContactWithZombie;

    public BasicWeapon primaryWeapon = null;
    public BasicWeapon secondaryWeapon = null;
    public BasicWeapon meleeWeapon = new Knife();
    public WeaponSlot activeWeaponSlot = WeaponSlot.MELEE;
    public boolean isAttacking = false;

    public CollectObjective collectObjective = new CollectObjective(ThreadLocalRandom.current().nextInt(4, 8 + 1));
    public RescueObjective rescueObjective = new RescueObjective();
    public KillObjective killObjective = new KillObjective(ThreadLocalRandom.current().nextInt(5, 10 + 1));
    public EscapeObjective escapeObjective;
    public BasicObjective trackedObjective = rescueObjective;

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


    public Player(World world, int xPos, int yPos, GameScreen screen) {
        super(screen, world, xPos, yPos, 100, ZomSim.PLAYER_BIT);
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

    public void interact() {
        if(inContactWith != null)
            inContactWith.interact();
    }

    @Override
    public void update(float delta) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));

        final BasicWeapon activeWeapon = getActiveWeapon();
        if(activeWeapon.isReloading()) {
            final BasicGun activeGun = (BasicGun) activeWeapon;
            activeGun.reloadProgress += delta * 1000;
            if(activeGun.reloadProgress > activeGun.reloadTimeMs)
                activeGun.endReload();
        }

        if(isAttacking && !getActiveWeapon().isReloading()) {
            switch (activeWeaponSlot) {
                case PRIMARY:
                    if(primaryWeapon.attack()) {
                        screen.entityHandler.bullets.add(new Bullet(screen, world, primaryWeapon.damage));
                    }
                    break;
                case SECONDARY:
                    if(secondaryWeapon.attack()) {
                        screen.entityHandler.bullets.add(new Bullet(screen, world, secondaryWeapon.damage));
                    }
                    break;
                case MELEE:
                    if(meleeWeapon.attack()) {
                        if(inContactWithZombie != null)
                            inContactWithZombie.health -= meleeWeapon.damage;
                    }
            }
        }

        if(collectObjective.complete && killObjective.complete && rescueObjective.complete && escapeObjective == null) {
            escapeObjective = screen.entityHandler.escapeObjective;
            trackedObjective = escapeObjective;
            escapeObjective.inProgress = true;
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

    public BasicWeapon getActiveWeapon() {
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

    public void addWeapon(BasicWeapon weapon) {
        switch(weapon.weaponSlot) {
            case PRIMARY:
                if(primaryWeapon != null) {
                    screen.entityHandler.weapons.add(new WeaponSpawn(screen, (int) getX(), (int) getY(), 8, 8, primaryWeapon));
                }
                primaryWeapon = weapon;
                break;
            case SECONDARY:
                if(secondaryWeapon != null) {
                    screen.entityHandler.weapons.add(new WeaponSpawn(screen, (int) getX(), (int) getY(), 8, 8, secondaryWeapon));
                }
                secondaryWeapon = weapon;
                break;
            case MELEE:
                if(meleeWeapon != null) {
                    screen.entityHandler.weapons.add(new WeaponSpawn(screen, (int) getX(), (int) getY(), 8, 8, meleeWeapon));
                }
                meleeWeapon = weapon;
        }
    }

    public void reload() {
        if(activeWeaponSlot != WeaponSlot.MELEE) {
            ((BasicGun) getActiveWeapon()).beginReload();
        }
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
        switch(type) {
            case RIFLE:
                if(primaryWeapon != null && primaryWeapon.ammoType == AmmoType.RIFLE) {
                    ((BasicGun) primaryWeapon).reserveAmmo += amount;
                    return true;
                }
                break;
            case SHOTGUN:
                if(primaryWeapon != null && primaryWeapon.ammoType == AmmoType.SHOTGUN) {
                    ((BasicGun) primaryWeapon).reserveAmmo += amount;
                    return true;
                }
                break;
            case PISTOL:
                if(secondaryWeapon != null && secondaryWeapon.ammoType == AmmoType.PISTOL) {
                    ((BasicGun) secondaryWeapon).reserveAmmo += amount;
                    return true;
                }
        }
        return false;
    }

    public void takeDamage(int damage) {
        health -= damage;
        screen.game.assetManager.get("audio/sounds/player_hurt.wav", Sound.class).play();
    }

}
