package dev.ferex.zomsim.objectives;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dev.ferex.zomsim.screens.GameScreen;

public class ObjectivePointer extends Sprite {
    private final GameScreen screen;

    public final Vector2 angle = new Vector2(0, 0);
    public boolean visible = true;

    public ObjectivePointer(GameScreen screen) {
        this.screen = screen;

        set(new Sprite(new Texture(Gdx.files.internal("sprites/objectives/objectivepointer.png"))));
        setBounds(0, 0, 10, 10);
        setOriginCenter();
        setScale(0.5f);
    }

    public void update(float delta) {
        if(screen.player.trackedObjective instanceof RescueObjective && screen.player.rescueObjective.inProgress) {
            float deltaX = screen.entityHandler.rescueExit.getX() - screen.player.getX();
            float deltaY = screen.entityHandler.rescueExit.getY() - screen.player.getY();
            setOffset(deltaX, deltaY);
            visible = true;
        }
        else if(screen.player.trackedObjective instanceof EscapeObjective) {
            float deltaX = screen.entityHandler.escapeExit.getX() - screen.player.getX();
            float deltaY = screen.entityHandler.escapeExit.getY() - screen.player.getY();
            setOffset(deltaX, deltaY);
            visible = true;
        }
        else visible = false;

        setRotation(angle.angleDeg());
    }

    private void setOffset(float deltaX, float deltaY) {
        angle.x = deltaX;
        angle.y = deltaY;

        float xOffset = (float) Math.cos(angle.angleRad()) * 10;
        float yOffset = (float) Math.sin(angle.angleRad()) * 10;

        setPosition(screen.player.b2body.getPosition().x - screen.player.getWidth() / 2 + xOffset,
                screen.player.b2body.getPosition().y - screen.player.getHeight() / 2 + yOffset);
    }

    public void draw(Batch batch) {
        if(!visible) return;

        super.draw(batch);
    }
}
