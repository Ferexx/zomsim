package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.characters.pathfinding.Tile;
import dev.ferex.zomsim.world.EventHandler;

public class Gate extends BasicWorldItem {
    public boolean closed = true;
    private boolean vertical;

    public Gate(int xPos, int yPos, int width, int height, boolean vertical) {
        super(xPos, yPos, width, height);
        this.vertical = vertical;
        fixture.setUserData(this);
        setBounds(0, 0, 8, 8);
        setOriginCenter();

        set(new Sprite(new Texture(Gdx.files.internal("sprites/world/gate_closed" + (vertical ? "_vertical.png" : ".png")))));
        setPosition(xPos, yPos);

        fixture.setSensor(false);
    }

    public void playerTouching(boolean touching) {
        drawingAnnotation = touching;
    }

    public void interact() {
        EntityHandler entityHandler = EntityHandler.getInstance();
        closed = !closed;
        if(closed) {
            fixture.setSensor(false);
            setTexture((new Texture(Gdx.files.internal("sprites/world/gate_closed" + (vertical ? "_vertical.png" : ".png")))));
            Array<Connection<Tile>> connections = entityHandler.levelGraph.getConnections(entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2)));
            while(connections.size != 0) {
                Connection<Tile> tileConnection = connections.first();
                entityHandler.levelGraph.disconnectTiles(tileConnection.getFromNode(), tileConnection.getToNode());
                entityHandler.levelGraph.disconnectTiles(tileConnection.getToNode(), tileConnection.getFromNode());
            }
        }
        else {
            fixture.setSensor(true);
            setTexture(new Texture(Gdx.files.internal("sprites/world/gate_open" + (vertical ? "_vertical.png" : ".png"))));

            Tile gateTile = entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2));
            if(vertical) {
                Tile leftTile = entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2) - 8, (int) ((int) bounds.getY() + bounds.getHeight() / 2));
                Tile rightTile = entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2) + 8, (int) ((int) bounds.getY() + bounds.getHeight() / 2));
                entityHandler.levelGraph.connectTiles(gateTile, rightTile);
                entityHandler.levelGraph.connectTiles(rightTile, gateTile);
                entityHandler.levelGraph.connectTiles(gateTile, leftTile);
                entityHandler.levelGraph.connectTiles(leftTile, gateTile);
            } else {
                Tile aboveTile = entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2) + 8);
                Tile belowTile = entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2) - 8);
                entityHandler.levelGraph.connectTiles(gateTile, belowTile);
                entityHandler.levelGraph.connectTiles(belowTile, gateTile);
                entityHandler.levelGraph.connectTiles(gateTile, aboveTile);
                entityHandler.levelGraph.connectTiles(aboveTile, gateTile);
            }
        }
        EventHandler.getInstance().onGateChanged(this);
        setPosition(getX(), getY());
    }

    public void destroy() {

    }

    @Override
    public void draw(Batch batch) {
        if(!visible) return;

        if(drawingAnnotation) {
            if(closed) font.draw(batch, "Press E\nto open", getX() + getWidth() / 2, getY() + getHeight());
            else font.draw(batch, "Press E\nto close", getX() + getWidth() / 2, getY() + getHeight());
        }
        super.draw(batch);
    }
}
