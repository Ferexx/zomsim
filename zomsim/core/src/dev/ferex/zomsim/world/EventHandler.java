package dev.ferex.zomsim.world;

import com.badlogic.gdx.utils.Array;
import dev.ferex.zomsim.characters.BasicZombie;
import dev.ferex.zomsim.characters.CharacterState;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.world.interactable.Gate;

public class EventHandler {
    private GameScreen screen;
    private float timeElapsed = 0;

    public EventHandler(GameScreen screen) {
        this.screen = screen;
    }

    public void onPlayerWalk() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(screen.player.getX()));
        args.add(String.valueOf(screen.player.getY()));

        for(BasicZombie zombie : screen.entityHandler.zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), screen.player.getX(), screen.player.getY()) < BasicZombie.RANGE_HEARING)
                zombie.addEvent("movementHeard", args);
        }
    }
    public void onPlayerRun() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(screen.player.getX()));
        args.add(String.valueOf(screen.player.getY()));

        for(BasicZombie zombie : screen.entityHandler.zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), screen.player.getX(), screen.player.getY()) < BasicZombie.RANGE_HEARING * 1.5)
                zombie.addEvent("movementHeard", args);
        }
    }
    public void onPlayerAttack() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(screen.player.getX()));
        args.add(String.valueOf(screen.player.getY()));

        for(BasicZombie zombie : screen.entityHandler.zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), screen.player.getX(), screen.player.getY()) < BasicZombie.RANGE_HEARING * 5)
                zombie.addEvent("shotHeard", args);
        }
    }
    public void onPlayerReload() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(screen.player.getX()));
        args.add(String.valueOf(screen.player.getY()));

        for(BasicZombie zombie : screen.entityHandler.zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), screen.player.getX(), screen.player.getY()) < BasicZombie.RANGE_HEARING * 0.5)
                zombie.addEvent("reloadHeard", args);
        }
    }

    public void onGateChanged(Gate gate) {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(gate.getBoundingRectangle().x));
        args.add(String.valueOf(gate.getBoundingRectangle().y));

        for(BasicZombie zombie : screen.entityHandler.zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), screen.player.getX(), screen.player.getY()) < BasicZombie.RANGE_HEARING)
                if(gate.closed)
                    zombie.addEvent("gateOpened", args);
                else
                    zombie.addEvent("gateClosed", args);
        }
    }

    public void update(float delta) {
        timeElapsed += delta;
        if(timeElapsed > 1) {
            if(Math.abs(screen.player.b2body.getLinearVelocity().x) > 5 || Math.abs(screen.player.b2body.getLinearVelocity().y) > 5) {
                onPlayerRun();
            }
            else if(Math.abs(screen.player.b2body.getLinearVelocity().x) > 0 || Math.abs(screen.player.b2body.getLinearVelocity().y) > 0) {
                onPlayerWalk();
            }
            if(screen.player.isAttacking) {
                onPlayerAttack();
            }
            if(screen.player.currentState == CharacterState.RELOADING) {
                onPlayerReload();
            }

            timeElapsed = 0;
        }
    }

    private double getDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2)) + Math.pow(y2 - y1, 2));
    }

}
