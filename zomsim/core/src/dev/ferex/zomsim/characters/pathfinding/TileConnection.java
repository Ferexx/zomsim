package dev.ferex.zomsim.characters.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import dev.ferex.zomsim.world.WorldManager;

public class TileConnection implements Connection<Tile> {
    public final Tile fromTile, toTile;

    public TileConnection(Tile fromTile, Tile toTile) {
        this.fromTile = fromTile;
        this.toTile = toTile;
    }

    @Override
    public float getCost() {
        return 0;
    }

    @Override
    public Tile getFromNode() {
        return fromTile;
    }

    @Override
    public Tile getToNode() {
        return toTile;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0 , 1);
        final Vector3 coordinates = WorldManager.getInstance().camera.project(new Vector3(fromTile.x + 4, fromTile.y + 4, 0));
        final Vector3 coordinates2 = WorldManager.getInstance().camera.project(new Vector3(toTile.x + 4, toTile.y + 4, 0));
        shapeRenderer.rectLine(coordinates.x, coordinates.y, coordinates2.x, coordinates2.y, 1);
        shapeRenderer.end();
    }

    @Override
    public boolean equals(Object o) {
        return fromTile.equals(((TileConnection) o).getFromNode()) && toTile.equals(((TileConnection) o).getToNode());
    }
}
