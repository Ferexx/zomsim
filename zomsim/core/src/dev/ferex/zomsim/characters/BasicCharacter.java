package dev.ferex.zomsim.characters;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.ferex.zomsim.ZomSim;

public abstract class BasicCharacter extends Sprite {
    public World world;
    public Body b2body;
    public int health;
    public Fixture bodyFixture;
    public Fixture meleeFixture;

    public BasicCharacter(World world, int xPos, int yPos, int health, short categoryBit) {
        this.world = world;
        this.health = health;

        BodyDef bdef = new BodyDef();
        bdef.linearDamping = 10f;
        bdef.position.set(xPos, yPos);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = this.world.createBody(bdef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
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

        EdgeShape meleeRange = new EdgeShape();
        meleeRange.set(new Vector2(6, -3), new Vector2(6, 3));
        fixtureDef.filter.categoryBits = ZomSim.MELEE_BIT;
        fixtureDef.shape = meleeRange;
        fixtureDef.isSensor = true;

        meleeFixture = b2body.createFixture(fixtureDef);
    }

    public abstract void update(float delta);
}
