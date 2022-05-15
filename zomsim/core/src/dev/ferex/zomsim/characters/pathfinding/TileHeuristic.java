package dev.ferex.zomsim.characters.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class TileHeuristic implements Heuristic<Tile> {
    @Override
    public float estimate(Tile node, Tile endNode) {
        return Vector2.dst(node.x, node.y, endNode.x, endNode.y);
    }
}
