package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;

public class RescueExit extends BasicWorldItem {

    public RescueExit(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
        setCategoryFilter(ZomSim.OBJECTIVE_BIT);

        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();
        set(new Sprite(new Texture(Gdx.files.internal("sprites/objectives/rescuezone.png"))));
        setPosition(xPos - (getWidth() / 4), yPos - (getHeight() / 4));
        setScale(0.5f);
    }

    public void interact() {
        Player player = Player.getInstance();
        EntityHandler entityHandler = EntityHandler.getInstance();
        if(player.rescueObjective.isInProgress() && Vector2.dst(player.b2body.getPosition().x, player.b2body.getPosition().y, entityHandler.rescueSurvivor.b2body.getPosition().x, entityHandler.rescueSurvivor.b2body.getPosition().y) < 10) {
            player.rescueObjective.end();
            entityHandler.rescueSurvivor.destroy();
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
