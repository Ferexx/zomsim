package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.screens.GameScreen;

public class RescueExit extends BasicWorldItem implements InteractableInterface {

    public RescueExit(GameScreen screen, int xPos, int yPos, int width, int height) {
        super(screen, xPos, yPos, width, height);
        setCategoryFilter(ZomSim.OBJECTIVE_BIT);

        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();
        set(new Sprite(new Texture(Gdx.files.internal("sprites/objectives/rescuezone.png"))));
        setPosition(xPos - (getWidth() / 4), yPos - (getHeight() / 4));
        setScale(0.5f);
    }

    public void interact() {
        if(screen.player.rescueObjective.inProgress && Vector2.dst(screen.player.b2body.getPosition().x, screen.player.b2body.getPosition().y, screen.entityHandler.rescueSurvivor.b2body.getPosition().x, screen.entityHandler.rescueSurvivor.b2body.getPosition().y) < 10) {
            screen.player.rescueObjective.complete = true;
            screen.player.rescueObjective.inProgress = false;
            screen.entityHandler.rescueSurvivor.destroy();
            destroy();
        }
    }

    public void playerTouching(boolean touching) {
        drawingAnnotation = touching;
    }

    public void destroy() {
        body.destroyFixture(fixture);
        drawingAnnotation = false;
        visible = false;
    }

    @Override
    public void draw(Batch batch) {
        if(!visible) return;

        super.draw(batch);
        if(drawingAnnotation)
            font.draw(batch, "Press E\n to rescue", getX() + getWidth() / 2, getY() + getHeight() / 2);
    }
}
