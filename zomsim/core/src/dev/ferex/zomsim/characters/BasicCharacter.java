package dev.ferex.zomsim.characters;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.world.WorldManager;

public abstract class BasicCharacter extends Sprite {
    public final Body b2body;
    public CharacterState currentState = CharacterState.IDLE;
    public CharacterState previousState = CharacterState.IDLE;
    public int health;
    public final Fixture bodyFixture;
    public final Fixture meleeFixture;

    public BasicCharacter(int xPos, int yPos, int health, short categoryBit) {
        this.health = health;
        World world = WorldManager.getInstance().getWorld();

        final BodyDef bdef = new BodyDef();
        bdef.linearDamping = 10f;
        bdef.position.set(xPos, yPos);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        final FixtureDef fixtureDef = new FixtureDef();
        final CircleShape shape = new CircleShape();
        shape.setRadius(3.5f);
        fixtureDef.filter.categoryBits = categoryBit;
        if(categoryBit == ZomSim.PLAYER_BIT) {
            fixtureDef.filter.maskBits = ZomSim.DEFAULT_BIT | ZomSim.WEAPON_BIT | ZomSim.ENEMY_BIT | ZomSim.OBJECTIVE_BIT | ZomSim.MELEE_BIT;
        }
        if(categoryBit == ZomSim.ENEMY_BIT) {
            fixtureDef.filter.maskBits = ZomSim.DEFAULT_BIT | ZomSim.BULLET_BIT | ZomSim.PLAYER_BIT | ZomSim.MELEE_BIT;
        }
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.95f;
        fixtureDef.density = 1f;
        bodyFixture = b2body.createFixture(fixtureDef);

        final EdgeShape meleeRange = new EdgeShape();
        meleeRange.set(new Vector2(6, -3), new Vector2(6, 3));
        fixtureDef.filter.categoryBits = ZomSim.MELEE_BIT;
        fixtureDef.shape = meleeRange;
        fixtureDef.isSensor = true;

        meleeFixture = b2body.createFixture(fixtureDef);
    }

    public abstract void update(float delta);
}
