package zomlink;

import java.util.LinkedList;

public interface ZombieListener {
    boolean handle(String type, Object[] params);

    // ABILITIES
    boolean chasePlayer();
    boolean goToClosestWeapon();
    boolean goToClosestZombie();
    boolean goToLocation(float x, float y);
    boolean stop();

    // DATA
    int getHealth();
    LinkedList<Float> getLocation();
    int getPlayerHealth();
}
