package dev.ferex.zomsim.characters.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import dev.ferex.zomsim.screens.GameScreen;

public class LevelGraph implements IndexedGraph<Tile> {
    private final GameScreen screen;
    final TileHeuristic heuristic = new TileHeuristic();
    public final Array<Tile> tiles = new Array<>();
    public final Array<TileConnection> connections = new Array<>();

    final ObjectMap<Tile, Array<Connection<Tile>>> connectionsMap = new ObjectMap<>();

    private int lastTileIndex = 0;

    public LevelGraph(GameScreen screen) {
        this.screen = screen;
    }

    public void addTile(Tile tile) {
        tile.index = lastTileIndex++;
        tiles.add(tile);
    }

    public void connectTiles(Tile fromTile, Tile toTile) {
        final TileConnection connection = new TileConnection(screen, fromTile, toTile);
        if(!connectionsMap.containsKey(fromTile))
            connectionsMap.put(fromTile, new Array<Connection<Tile>>());
        connectionsMap.get(fromTile).add(connection);
        connections.add(connection);
    }

    public void disconnectTiles(Tile fromTile, Tile toTile) {
        final TileConnection connection = new TileConnection(screen, fromTile, toTile);
        if(connectionsMap.containsKey(fromTile))
            connectionsMap.get(fromTile).removeValue(connection, false);
        connections.removeValue(connection, false);
    }

    public GraphPath<Tile> findPath(Tile startTile, Tile endTile) {
        final GraphPath<Tile> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startTile, endTile, heuristic, path);
        return path;
    }

    @Override
    public int getIndex(Tile tile) {
        return tile.index;
    }

    @Override
    public int getNodeCount() {
        return lastTileIndex;
    }

    @Override
    public Array<Connection<Tile>> getConnections(Tile fromNode) {
        if(connectionsMap.containsKey(fromNode))
            return connectionsMap.get(fromNode);
        return new Array<>(0);
    }

    public Tile getTile(int x, int y) {
        for(Tile tile : tiles) {
            if(tile.contains(x, y))
                return tile;
        }
        return null;
    }

    public Tile getTile(Vector2 position) {
        for(Tile tile : tiles) {
            if(tile.contains((int) position.x, (int) position.y)) {
                return tile;
            }
        }
        return null;
    }
}
