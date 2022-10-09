package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.weapons.Weapon;
import dev.ferex.zomsim.world.WorldManager;

public class WeaponSpawn extends BasicWorldItem {
    public Weapon weapon;

    public WeaponSpawn(int xPos, int yPos, int width, int height, Weapon weapon) {
        super(xPos, yPos, width, height);
        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();

        this.weapon = weapon;
        setCategoryFilter(ZomSim.WEAPON_BIT);

        switch (weapon.getType()) {
            case PISTOL:
                set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/glock.png"))));
                setPosition(xPos - 2, yPos - 1);
                setScale(0.3f);
                break;
            case RIFLE:
                set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/ak47.png"))));
                setPosition(xPos - 18, yPos - 3);
                setScale(0.2f);
                break;
            case SHOTGUN:
                set(new Sprite(new Texture(Gdx.files.internal("sprites/guns/pump.png"))));
                setPosition(xPos - 15, yPos - 13);
                setScale(0.2f);
                break;
        }
    }

    public void interact() {
        Player.getInstance().addWeapon(weapon);
        destroy();
    }

    public void playerTouching(boolean touching) {
        drawingAnnotation = touching;
    }

    public void destroy() {
        WorldManager.getInstance().getWorld().destroyBody(body);
        drawingAnnotation = false;
        visible = false;
        EntityHandler.getInstance().weapons.removeValue(this, false);
    }


    @Override
    public void draw(Batch batch) {
        if(!visible) return;

        super.draw(batch);
        if(drawingAnnotation)
            font.draw(batch, "Press E\nto equip", getX() + getWidth() / 2, getY() + getHeight());
    }
}
