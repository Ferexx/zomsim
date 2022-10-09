package dev.ferex.zomsim.objectives;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dev.ferex.zomsim.characters.Player;

public class ObjectivePointer extends Sprite {

    private final Vector2 objectivePosition;
    public final Vector2 angle = new Vector2(0, 0);

    public ObjectivePointer(Vector2 position) {
        this.objectivePosition = position;
        set(new Sprite(new Texture(Gdx.files.internal("sprites/objectives/objectivepointer.png"))));
        setBounds(0, 0, 10, 10);
        setOriginCenter();
        setScale(0.5f);
    }

    public void update() {
        final Player player = Player.getInstance();
        final float deltaX = objectivePosition.x - player.getX();
        final float deltaY = objectivePosition.y - player.getY();
        setOffset(player, deltaX, deltaY);
        setRotation(angle.angleDeg());
    }

    private void setOffset(final Player player, final float deltaX, final float deltaY) {
        angle.x = deltaX;
        angle.y = deltaY;

        final float xOffset = (float) Math.cos(angle.angleRad()) * 10;
        final float yOffset = (float) Math.sin(angle.angleRad()) * 10;

        setPosition(player.b2body.getPosition().x - player.getWidth() / 2 + xOffset,
                player.b2body.getPosition().y - player.getHeight() / 2 + yOffset);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }
}
