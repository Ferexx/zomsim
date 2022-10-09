package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import dev.ferex.zomsim.world.WorldManager;

public abstract class BasicWorldItem extends Sprite implements Interactable {
    protected final Rectangle bounds;
    public final Body body;
    protected final BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/FreakyHand.fnt"), false);

    protected final Fixture fixture;

    public boolean drawingAnnotation = false, visible = true;

    public BasicWorldItem(int xPos, int yPos, int width, int height) {
        bounds = new Rectangle(xPos, yPos, width, height);

        font.getData().setScale(0.05f);

        final BodyDef bodyDef = new BodyDef();
        final FixtureDef fixtureDef = new FixtureDef();
        final PolygonShape shape = new PolygonShape();
        bodyDef.position.set(xPos + width / 2f, yPos + height / 2f);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = WorldManager.getInstance().getWorld().createBody(bodyDef);

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
