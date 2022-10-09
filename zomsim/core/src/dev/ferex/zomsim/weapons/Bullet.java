package dev.ferex.zomsim.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.world.WorldManager;

public class Bullet extends Sprite implements Pool.Poolable {
    public final Body body;
    public final Fixture fixture;
    public boolean toDestroy = false;

    public int damage;

    public Bullet() {
        Player player = Player.getInstance();

        final BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(player.b2body.getPosition().x, player.b2body.getPosition().y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = WorldManager.getInstance().getWorld().createBody(bodyDef);
        body.setBullet(true);

        final FixtureDef fixtureDef = new FixtureDef();
        final CircleShape shape = new CircleShape();
        shape.setRadius(0.5f);
        fixtureDef.filter.categoryBits = ZomSim.BULLET_BIT;
        fixtureDef.filter.maskBits = ZomSim.DEFAULT_BIT | ZomSim.ENEMY_BIT;

        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/bullet.png"))));
        setBounds(0, 0, 25, 11);
        setOriginCenter();
        setScale(0.05f);
    }

    public void update() {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void init(int damage) {
        this.damage = damage;
        Player player = Player.getInstance();
        body.setTransform(body.getPosition(), player.b2body.getAngle());
        final float angle = player.getRotation();
        body.applyLinearImpulse(new Vector2((float) Math.cos(angle * Math.PI/180) * 50,
                (float) Math.sin(angle  * Math.PI/180) * 50), player.b2body.getWorldCenter(), true);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRotation(angle);
    }

    @Override
    public void reset() {
        body.getPosition().x = 0;
        body.getPosition().y = 0;
        setPosition(0,0);
    }
}
