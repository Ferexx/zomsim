package dev.ferex.zomsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.world.WorldManager;

public class Controls {

    private static Controls instance;
    private final Player player;

    private Controls() {
        player = Player.getInstance();
    }

    public static Controls getInstance() {
        if (instance == null) {
            instance = new Controls();
        }
        return instance;
    }

    public void update(float delta) {
        //player.b2body.applyLinearImpulse(player.b2body.getLinearVelocity().rotateDeg(180), player.b2body.getWorldCenter(), true);

        int x = 0, y = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            x *= 2;
            y *= 2;
        }
        if (x != 0 || y != 0) player.move(new Vector2(x, y));

        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.interact();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            player.reload();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            player.equipWeapon(WeaponSlot.PRIMARY);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            player.equipWeapon(WeaponSlot.SECONDARY);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            player.equipWeapon(WeaponSlot.MELEE);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ZomSim.getInstance().pause();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            ZomSim.MODE_DEBUG = !ZomSim.MODE_DEBUG;
        }

        if (Gdx.input.isTouched()) {
            player.attack();
            player.isAttacking = true;
        } else {
            player.isAttacking = false;
        }

        final Vector2 angle = new Vector2(0,0);
        final Vector3 coordinates = WorldManager.getInstance().camera.project(player.getVector3Position());
        angle.x = Gdx.input.getX() - coordinates.x;
        angle.y = Gdx.graphics.getHeight() - Gdx.input.getY() - coordinates.y + (player.getHeight() / 2);

        player.face(angle);
    }
}
