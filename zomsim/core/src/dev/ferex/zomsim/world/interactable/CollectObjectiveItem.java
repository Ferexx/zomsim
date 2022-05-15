package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.screens.GameScreen;

public class CollectObjectiveItem extends BasicWorldItem implements InteractableInterface {

    public CollectObjectiveItem(GameScreen screen, int xPos, int yPos, int width, int height) {
        super(screen, xPos, yPos, width, height);
        setCategoryFilter(ZomSim.OBJECTIVE_BIT);

        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();
        set(new Sprite(new Texture(Gdx.files.internal("sprites/objectives/gear.png"))));
        setPosition(xPos - (getWidth() / 4), yPos - (getHeight() / 4));
        setScale(0.3f);
    }

    public void interact() {
        screen.player.collectObjective.acquireItem();
        destroy();
    }

    public void playerTouching(boolean touching) {
        drawingAnnotation = touching;
    }

    public void destroy() {
        body.destroyFixture(fixture);
        visible = false;
        drawingAnnotation = false;
        screen.entityHandler.fetchObjectiveItems.removeValue(this, false);
    }

    @Override
    public void draw(Batch batch) {
        if(!visible) return;

        super.draw(batch);
        if(drawingAnnotation)
            font.draw(batch, "Press E\n to pick up", getX() + getWidth() / 2, getY() + getHeight());
    }
}
