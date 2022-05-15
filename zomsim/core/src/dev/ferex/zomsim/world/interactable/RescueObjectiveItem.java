package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.graphics.g2d.Batch;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.screens.GameScreen;

public class RescueObjectiveItem extends BasicWorldItem implements InteractableInterface {

    public RescueObjectiveItem(GameScreen screen, int xPos, int yPos, int width, int height) {
        super(screen, xPos, yPos, width, height);
        setCategoryFilter(ZomSim.OBJECTIVE_BIT);

        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();
        setPosition(xPos - (getWidth() / 4), yPos - (getHeight() / 4));
        setScale(0.3f);
    }

    public void interact() {
        screen.player.rescueObjective.inProgress = true;
        screen.entityHandler.rescueSurvivor.chasePlayer();
        destroy();
    }

    public void playerTouching(boolean touching) {
        drawingAnnotation = touching;
    }

    public void destroy() {
        body.destroyFixture(fixture);
        visible = false;
        drawingAnnotation = false;
    }

    @Override
    public void draw(Batch batch) {
        if(!visible) return;
        if(drawingAnnotation)
            font.draw(batch, "Press E\n to rescue", getX() + getWidth() / 2, getY() + getHeight());
    }
}
