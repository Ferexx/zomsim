package dev.ferex.zomsim.characters;

import dev.ferex.zomsim.characters.pathfinding.Tile;

public interface ZombieInterface {
    boolean moveTo(Tile tile);
    void canAttack(boolean canAttack);
}
