package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import dev.ferex.zomsim.screens.GameScreen;

public abstract class BasicWorldItem extends Sprite {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    public Body body;
    protected GameScreen screen;
    protected BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/FreakyHand.fnt"), false);

    protected Fixture fixture;

    public boolean drawingAnnotation = false, visible = true;

    public BasicWorldItem(GameScreen screen, int xPos, int yPos, int width, int height) {
        this.screen = screen;
        this.world = screen.world;
        this.map = screen.currentMap;
        bounds = new Rectangle(xPos, yPos, width, height);

        font.getData().setScale(0.05f);

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        bodyDef.position.set(xPos + width / 2f, yPos + height / 2f);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);

        shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixture = body.createFixture(fixtureDef);
    }

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }
}
