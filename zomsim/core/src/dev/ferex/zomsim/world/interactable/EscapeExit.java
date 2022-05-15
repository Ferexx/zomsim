package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.screens.GameScreen;

public class EscapeExit extends BasicWorldItem implements InteractableInterface {

    public EscapeExit(GameScreen screen, int xPos, int yPos, int width, int height) {
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
        if(screen.player.escapeObjective != null && screen.player.escapeObjective.inProgress) {
            screen.player.escapeObjective.complete = true;
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
        if (!visible) return;

        super.draw(batch);
        if (drawingAnnotation) {
            if(screen.player.escapeObjective != null)
                font.draw(batch, "Press E\n to escape", getX() + getWidth() / 2, getY() + getHeight() / 2);
            else
                font.draw(batch, "Complete all\nobjectives before\nescaping", getX() + getWidth() / 2, getY() + getHeight() / 2);
        }
    }
}

