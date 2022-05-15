package dev.ferex.zomsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.screens.PauseScreen;
import dev.ferex.zomsim.weapons.WeaponSlot;

public class Controls {
    private GameScreen screen;
    private Player player;

    public boolean paused = false;

    public Controls(GameScreen screen) {
        this.screen = screen;
        this.player = screen.player;
    }

    public void update(float delta) {
        //player.b2body.applyLinearImpulse(player.b2body.getLinearVelocity().rotateDeg(180), player.b2body.getWorldCenter(), true);

        int x = 0, y = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            x *= 2;
            y *= 2;
        }
        if(x != 0)
            screen.player.b2body.setLinearVelocity((x * 5), screen.player.b2body.getLinearVelocity().y);
        if(y != 0)
            screen.player.b2body.setLinearVelocity(screen.player.b2body.getLinearVelocity().x, (y * 5));

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
            if(!paused) {
                screen.game.setScreen(new PauseScreen(screen.game, screen));
                paused = true;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            ZomSim.MODE_DEBUG = !ZomSim.MODE_DEBUG;
        }

        player.isAttacking = Gdx.input.isTouched();

        Vector2 angle = new Vector2(0,0);
        Vector3 coordinates = screen.camera.project(new Vector3(screen.player.b2body.getPosition().x, screen.player.b2body.getPosition().y, 0));
        angle.x = Gdx.input.getX() - coordinates.x;
        angle.y = Gdx.graphics.getHeight() - Gdx.input.getY() - coordinates.y + (player.getHeight() / 2);

        player.setRotation(angle.angleDeg());
        player.b2body.setTransform(player.b2body.getPosition(), angle.angleRad());
    }
}
