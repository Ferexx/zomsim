package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import dev.ferex.zomsim.characters.pathfinding.Tile;
import dev.ferex.zomsim.screens.GameScreen;

public class Gate extends BasicWorldItem implements InteractableInterface {
    public boolean closed = true;
    private boolean vertical;

    public Gate(GameScreen screen, int xPos, int yPos, int width, int height, boolean vertical) {
        super(screen, xPos, yPos, width, height);
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
        closed = !closed;
        if(closed) {
            fixture.setSensor(false);
            setTexture((new Texture(Gdx.files.internal("sprites/world/gate_closed" + (vertical ? "_vertical.png" : ".png")))));
            Array<Connection<Tile>> connections = screen.entityHandler.levelGraph.getConnections(screen.entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2)));
            while(connections.size != 0) {
                Connection<Tile> tileConnection = connections.first();
                screen.entityHandler.levelGraph.disconnectTiles(tileConnection.getFromNode(), tileConnection.getToNode());
                screen.entityHandler.levelGraph.disconnectTiles(tileConnection.getToNode(), tileConnection.getFromNode());
            }
        }
        else {
            fixture.setSensor(true);
            setTexture(new Texture(Gdx.files.internal("sprites/world/gate_open" + (vertical ? "_vertical.png" : ".png"))));

            Tile gateTile = screen.entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2));
            if(vertical) {
                Tile leftTile = screen.entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2) - 8, (int) ((int) bounds.getY() + bounds.getHeight() / 2));
                Tile rightTile = screen.entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2) + 8, (int) ((int) bounds.getY() + bounds.getHeight() / 2));
                screen.entityHandler.levelGraph.connectTiles(gateTile, rightTile);
                screen.entityHandler.levelGraph.connectTiles(rightTile, gateTile);
                screen.entityHandler.levelGraph.connectTiles(gateTile, leftTile);
                screen.entityHandler.levelGraph.connectTiles(leftTile, gateTile);
            } else {
                Tile aboveTile = screen.entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2) + 8);
                Tile belowTile = screen.entityHandler.levelGraph.getTile((int) ((int) bounds.getX() + bounds.getWidth() / 2), (int) ((int) bounds.getY() + bounds.getHeight() / 2) - 8);
                screen.entityHandler.levelGraph.connectTiles(gateTile, belowTile);
                screen.entityHandler.levelGraph.connectTiles(belowTile, gateTile);
                screen.entityHandler.levelGraph.connectTiles(gateTile, aboveTile);
                screen.entityHandler.levelGraph.connectTiles(aboveTile, gateTile);
            }
        }
        screen.eventHandler.onGateChanged(this);
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
