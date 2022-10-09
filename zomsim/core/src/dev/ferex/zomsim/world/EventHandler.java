package dev.ferex.zomsim.world;

import com.badlogic.gdx.utils.Array;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.characters.BasicZombie;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.world.interactable.Gate;

public class EventHandler {
    private static EventHandler instance;
    Player player = Player.getInstance();

    public static EventHandler getInstance() {
        if (instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }

    public void onPlayerWalk() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(player.getX()));
        args.add(String.valueOf(player.getY()));

        for(BasicZombie zombie : EntityHandler.getInstance().zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), player.getX(), player.getY()) < BasicZombie.RANGE_HEARING)
                zombie.addEvent("movementHeard", args);
        }
    }
    public void onPlayerRun() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(player.getX()));
        args.add(String.valueOf(player.getY()));

        for(BasicZombie zombie : EntityHandler.getInstance().zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), player.getX(), player.getY()) < BasicZombie.RANGE_HEARING * 1.5)
                zombie.addEvent("movementHeard", args);
        }
    }
    public void onPlayerAttack() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(player.getX()));
        args.add(String.valueOf(player.getY()));

        for(BasicZombie zombie : EntityHandler.getInstance().zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), player.getX(), player.getY()) < BasicZombie.RANGE_HEARING * 5)
                zombie.addEvent("shotHeard", args);
        }
    }
    public void onPlayerReload() {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(player.getX()));
        args.add(String.valueOf(player.getY()));

        for(BasicZombie zombie : EntityHandler.getInstance().zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), player.getX(), player.getY()) < BasicZombie.RANGE_HEARING * 0.5)
                zombie.addEvent("reloadHeard", args);
        }
    }

    public void onGateChanged(Gate gate) {
        final Array<String> args = new Array<>();
        args.add(String.valueOf(gate.getBoundingRectangle().x));
        args.add(String.valueOf(gate.getBoundingRectangle().y));

        for(BasicZombie zombie : EntityHandler.getInstance().zombies) {
            if(getDistance(zombie.getX(), zombie.getY(), player.getX(), player.getY()) < BasicZombie.RANGE_HEARING)
                if(gate.closed)
                    zombie.addEvent("gateOpened", args);
                else
                    zombie.addEvent("gateClosed", args);
        }
    }

    private double getDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2)) + Math.pow(y2 - y1, 2));
    }

}
