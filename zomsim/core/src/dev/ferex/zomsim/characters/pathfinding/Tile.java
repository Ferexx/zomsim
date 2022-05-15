package dev.ferex.zomsim.characters.pathfinding;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dev.ferex.zomsim.screens.GameScreen;

public class Tile implements Location<Vector2> {
    private GameScreen screen;
    public int x, y;
    public int index;

    public Tile(GameScreen screen, int x, int y) {
        this.screen = screen;
        this.x = x;
        this.y = y;
    }

    public void draw(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0, 0, 0 , 1);
        Vector3 coordinates = screen.camera.project(new Vector3(x + 3, y + 3, 0));
        renderer.rect(coordinates.x, coordinates.y, 16, 16);
        renderer.end();
    }

    public boolean contains(int x, int y) {
        return (new Rectangle(this.x, this.y, 8, 8).contains(x, y));
    }

    @Override
    public boolean equals(Object o) {
        return this.x == ((Tile) o).x && this.y == ((Tile) o).y;
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(x + 4, y + 4);
    }

    @Override
    public float getOrientation() {
        return 0;
    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return SteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return SteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return this;
    }
}
