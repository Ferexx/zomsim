package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.weapons.AmmoType;

import java.util.concurrent.ThreadLocalRandom;

public class Ammo extends BasicWorldItem {
    private final AmmoType type;
    private final int amount;

    public Ammo(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
        setCategoryFilter(ZomSim.WEAPON_BIT);

        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();

        switch(ThreadLocalRandom.current().nextInt(1, 3 + 1)) {
            case 0:
                set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/pistol-ammo.png"))));
                type = AmmoType.PISTOL;
                amount = ThreadLocalRandom.current().nextInt(5, 12 + 1);
                break;
            case 1:
                set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/rifle-ammo.png"))));
                type = AmmoType.RIFLE;
                amount = ThreadLocalRandom.current().nextInt(15, 30 + 1);
                break;
            case 2:
            default:
                set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/shotgun-ammo.png"))));
                type = AmmoType.SHOTGUN;
                amount = ThreadLocalRandom.current().nextInt(2, 5 + 1);
        }
        setPosition(xPos, yPos);
        setScale(0.5f);
    }

    @Override
    public void interact() {
        if(Player.getInstance().addAmmo(type, amount)) destroy();
    }

    @Override
    public void playerTouching(boolean touching) {
        drawingAnnotation = touching;
    }

    @Override
    public void destroy() {
        body.destroyFixture(fixture);
        drawingAnnotation = false;
        visible = false;
        EntityHandler.getInstance().ammo.removeValue(this, false);
    }

    @Override
    public void draw(Batch batch) {
        if (!visible) return;

        super.draw(batch);
        if (drawingAnnotation) {
            font.draw(batch, "Press E\n to pick up", getX() + getWidth() / 2, getY() + getHeight() / 2);
        }
    }
}
