package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;

public class CollectObjectiveItem extends BasicWorldItem {

    public CollectObjectiveItem(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
        setCategoryFilter(ZomSim.OBJECTIVE_BIT);

        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();
        set(new Sprite(new Texture(Gdx.files.internal("sprites/objectives/gear.png"))));
        setPosition(xPos - (getWidth() / 4), yPos - (getHeight() / 4));
        setScale(0.3f);
    }

    public void interact() {
        Player.getInstance().collectObjective.acquireItem();
        destroy();
    }

    public void playerTouching(boolean touching) {
        drawingAnnotation = touching;
    }

    public void destroy() {
        body.destroyFixture(fixture);
        visible = false;
        drawingAnnotation = false;
        EntityHandler.getInstance().fetchObjectiveItems.removeValue(this, false);
    }

    @Override
    public void draw(Batch batch) {
        if(!visible) return;

        super.draw(batch);
        if(drawingAnnotation)
            font.draw(batch, "Press E\n to pick up", getX() + getWidth() / 2, getY() + getHeight());
    }
}
