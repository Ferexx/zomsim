package dev.ferex.zomsim.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.screens.GameScreen;

public class Bullet extends Sprite {
    private final GameScreen screen;
    public Body body;
    public Fixture fixture;
    public boolean visible = true;
    public boolean toDestroy = false;

    public int damage;

    public Bullet(GameScreen screen, World world, int damage) {
        this.screen = screen;
        this.damage = damage;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(screen.player.b2body.getPosition().x, screen.player.b2body.getPosition().y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setBullet(true);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f);
        fixtureDef.filter.categoryBits = ZomSim.BULLET_BIT;
        fixtureDef.filter.maskBits = ZomSim.DEFAULT_BIT | ZomSim.ENEMY_BIT;

        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        body.setTransform(body.getPosition(), screen.player.b2body.getAngle());
        float angle = screen.player.getRotation();
        body.applyLinearImpulse(new Vector2((float) Math.cos(angle * Math.PI/180) * 50,
                (float) Math.sin(angle  * Math.PI/180) * 50), screen.player.b2body.getWorldCenter(), true);

        set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/bullet.png"))));
        setBounds(0, 0, 25, 11);
        setOriginCenter();
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRotation(angle);
        setScale(0.05f);
    }

    public void update(float delta) {
        if(toDestroy) {
            screen.world.destroyBody(body);
            screen.entityHandler.bullets.removeValue(this, true);
        }
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    @Override
    public void draw(Batch batch) {
        if(!visible) return;
        super.draw(batch);
    }
}
