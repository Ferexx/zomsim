package dev.ferex.zomsim.world;

import astra.AgentLoader;
import astra.core.Agent;
import astra.core.Scheduler;
import astra.execution.TestSchedulerStrategy;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.BasicZombie;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.characters.Survivor;
import dev.ferex.zomsim.characters.pathfinding.Tile;
import dev.ferex.zomsim.objectives.EscapeObjective;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.guns.Pistol;
import dev.ferex.zomsim.weapons.guns.Rifle;
import dev.ferex.zomsim.weapons.guns.Shotgun;
import dev.ferex.zomsim.world.interactable.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WorldCreator {

    public WorldCreator(GameScreen screen, World world, TiledMap map) {
        final BodyDef bodyDef = new BodyDef();
        final PolygonShape shape = new PolygonShape();
        final FixtureDef fixtureDef = new FixtureDef();
        Body body;

        world.setContactListener(new ContactListener(screen));

        Scheduler.setStrategy(new TestSchedulerStrategy());
        final AgentLoader loader = new AgentLoader("zomsim/core/src/astra/agents");
        try {
            loader.scan();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        for(MapObject object : map.getLayers().get("Buildings").getObjects()) {
            final Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
            body = world.createBody(bodyDef);

            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef).setUserData("wall");
        }

        for(MapObject object : map.getLayers().get("Interactive Objects").getObjects()) {
            if (object.getProperties().get("type", String.class) != null) {
                final int xPos = object.getProperties().get("x", float.class).intValue();
                final int yPos = object.getProperties().get("y", float.class).intValue();

                if (object.getProperties().get("type", String.class).equals("Gate_Horizontal"))
                    screen.entityHandler.interactables.add(new Gate(screen, xPos, yPos, 8, 5, false));
                if (object.getProperties().get("type", String.class).equals("Gate_Vertical"))
                    screen.entityHandler.interactables.add(new Gate(screen, xPos, yPos, 5, 8, true));
            }
        }

        // This is horrible, horrible, spaghetti code that introduces a lot of loading time to the world
        // Oh well!
        MapObjects objects = map.getLayers().get("Buildings").getObjects();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Art");
        for(int x = 0; x < layer.getWidth(); x++) {
            for(int y = 0; y < layer.getHeight(); y++) {
                boolean isBuildingTile = false;
                for(MapObject object : objects) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    if(rect.contains(new Rectangle(x * layer.getTileWidth(), y * layer.getTileHeight(), layer.getTileWidth(), layer.getTileHeight()))) {
                        isBuildingTile = true;
                    }
                    if(rect.overlaps(new Rectangle(x * layer.getTileWidth(), y * layer.getTileHeight(), layer.getTileWidth(), layer.getTileHeight()))) {
                        isBuildingTile = true;
                    }
                }
                if(!isBuildingTile) {
                    Tile tile = new Tile(screen, x * layer.getTileWidth(), y * layer.getTileHeight());
                    screen.entityHandler.levelGraph.addTile(tile);

                    for (Tile checkTile : screen.entityHandler.levelGraph.tiles) {
                        boolean isGateTile = false;
                        for(MapObject object : map.getLayers().get("Interactive Objects").getObjects()) {
                            if (object.getProperties().get("type", String.class) != null) {
                                int xPos = object.getProperties().get("x", float.class).intValue();
                                int yPos = object.getProperties().get("y", float.class).intValue();

                                if ((xPos == checkTile.x && yPos == checkTile.y) || (xPos == tile.x && yPos == tile.y)) {
                                    isGateTile = true;
                                    break;
                                }
                            }
                        }
                        if(!isGateTile) {
                            if (checkTile.x == tile.x && checkTile.y == tile.y - layer.getTileHeight()) {
                                screen.entityHandler.levelGraph.connectTiles(tile, checkTile);
                                screen.entityHandler.levelGraph.connectTiles(checkTile, tile);
                            }
                            if (checkTile.y == tile.y && checkTile.x == tile.x - layer.getTileWidth()) {
                                screen.entityHandler.levelGraph.connectTiles(tile, checkTile);
                                screen.entityHandler.levelGraph.connectTiles(checkTile, tile);
                            }
                            // This is for diagonal connections. It'll be a pain to do properly because might try walk through the corner of a wall
                            if (checkTile.y == tile.y - layer.getTileHeight() && checkTile.x == tile.x - layer.getTileWidth()) {
                                //screen.entityHandler.levelGraph.connectTiles(tile, checkTile);
                            }
                        }
                    }
                }
            }
        }

        int weaponCount = 0, zombieCount = 0;
        int i = 0;
        final Random r = new Random();
        while(weaponCount < ZomSim.TOTAL_WEAPONS || zombieCount < ZomSim.TOTAL_ZOMBIES) {
            for (MapObject object : map.getLayers().get("Spawns").getObjects()) {
                if (object.getProperties().get("type", String.class) != null) {
                    int xPos = object.getProperties().get("x", float.class).intValue();
                    int yPos = object.getProperties().get("y", float.class).intValue();

                    if (object.getProperties().get("type", String.class).equals("Gun") && r.nextInt() % 100 > 50 && object.getColor() != Color.BLACK && weaponCount < ZomSim.TOTAL_WEAPONS) {
                        if(ThreadLocalRandom.current().nextInt(1, 5 + 1) > 3) {
                            spawnRandomGun(screen, xPos, yPos);
                            System.out.println("Spawned weapon at: " + xPos + " " + yPos);
                        }
                        else {
                            screen.entityHandler.ammo.add(new Ammo(screen, xPos, yPos, 8,8));
                        }
                        weaponCount++;
                        object.setColor(Color.BLACK);
                    }
                    if (object.getProperties().get("type", String.class).equals("Player") && object.getColor() != Color.BLACK) {
                        screen.player = new Player(world, xPos, yPos, screen);
                        object.setColor(Color.BLACK);
                    }
                    if (object.getProperties().get("type", String.class).equals("Zombie") && r.nextInt() % 100 > 50 && object.getColor() != Color.BLACK && zombieCount < ZomSim.TOTAL_ZOMBIES) {
                        BasicZombie zombie = new BasicZombie(screen, world, xPos, yPos);
                        try {
                            Agent agent = loader.createAgent("Zombie", "me" + i++);
                            agent.initialize(new Goal(new Predicate("main", new Term[]{new ListTerm(new Term[]{Primitive.newPrimitive(zombie)})})));
                            Scheduler.schedule(agent);
                            zombie.agent = agent;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("Spawned zombie at: " + xPos + " " + yPos);
                        screen.entityHandler.zombies.add(zombie);
                        object.setColor(Color.BLACK);
                        zombieCount++;
                    }
                }
            }
        }

        try {
            loader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        int fetchCount = ThreadLocalRandom.current().nextInt(8, 12 + 1), fetchesCreated = 0;
        boolean rescueCreated = false, rescueExitCreated = false, levelExitCreated = false;
        while(fetchesCreated < fetchCount || !rescueCreated || !rescueExitCreated || !levelExitCreated) {
            for (MapObject object : map.getLayers().get("Objectives").getObjects()) {
                if (object.getProperties().get("type", String.class) != null) {
                    int xPos = object.getProperties().get("x", float.class).intValue();
                    int yPos = object.getProperties().get("y", float.class).intValue();

                    if (object.getProperties().get("type", String.class).equals("FetchObjective") && fetchesCreated < fetchCount && r.nextInt() % 100 > 60 && object.getColor() != Color.BLACK) {
                        screen.entityHandler.fetchObjectiveItems.add(new CollectObjectiveItem(screen, xPos, yPos, 8, 8));
                        System.out.println("Spawned fetch at: " + xPos + " " + yPos);
                        object.setColor(Color.BLACK);
                        fetchesCreated++;
                    }
                    if (object.getProperties().get("type", String.class).equals("RescueObjective") && r.nextInt() % 100 > 95 && object.getColor() != Color.BLACK && !rescueCreated) {
                        screen.entityHandler.rescueObjectiveItem = new RescueObjectiveItem(screen, xPos, yPos, 8, 8);
                        screen.entityHandler.rescueSurvivor = new Survivor(screen, screen.world, xPos, yPos);
                        System.out.println("Spawned rescue at: " + xPos + " " + yPos);
                        object.setColor(Color.BLACK);
                        rescueCreated = true;
                    }
                    if (object.getProperties().get("type", String.class).equals("RescueExit") && r.nextInt() % 100 > 95 && object.getColor() != Color.BLACK && !rescueExitCreated) {
                        screen.entityHandler.rescueExit = new RescueExit(screen, xPos, yPos, 8, 8);
                        System.out.println("Spawned rescue exit at: " + xPos + " " + yPos);
                        object.setColor(Color.BLACK);
                        rescueExitCreated = true;
                    }
                    if (object.getProperties().get("type", String.class).equals("EscapeObjective") && r.nextInt() % 100 > 70 && object.getColor() != Color.BLACK && !levelExitCreated) {
                        screen.entityHandler.escapeObjective = new EscapeObjective(xPos, yPos);
                        screen.entityHandler.escapeExit = new EscapeExit(screen, xPos, yPos, 8, 8);
                        System.out.println("Spawned escape at: " + xPos + " " + yPos);
                        object.setColor(Color.BLACK);
                        levelExitCreated = true;
                    }
                }
            }
        }
    }

    public void spawnRandomGun(GameScreen screen, int xPos, int yPos) {
        final Random r = new Random();
        int random = r.nextInt(10) + 1;
        if(random > 8) screen.entityHandler.weapons.add(new WeaponSpawn(screen, xPos, yPos, 8, 8, new Shotgun(screen)));
        else if(random > 5) screen.entityHandler.weapons.add(new WeaponSpawn(screen, xPos, yPos, 8, 8, new Rifle(screen)));
        else screen.entityHandler.weapons.add(new WeaponSpawn(screen, xPos, yPos, 8, 8, new Pistol(screen)));
    }
}