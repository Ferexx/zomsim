package dev.ferex.zomsim.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;

import static dev.ferex.zomsim.world.WorldCreator.createWorld;

public class WorldManager {
    private static WorldManager instance;
    private final World world;
    private TiledMap map;
    public final OrthographicCamera camera;
    private TiledMapRenderer mapRenderer;
    private final Box2DDebugRenderer b2dr;

    public static WorldManager getInstance() {
        if(instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }

    private WorldManager() {
        world = new World(new Vector2(0, 0), true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, ZomSim.V_WIDTH, ZomSim.V_HEIGHT);
        camera.zoom = 0.1f;
        b2dr = new Box2DDebugRenderer();
    }

    public Vector2 loadWorld(final String fileName) {
        map = new TmxMapLoader().load(fileName);
        final Vector2 playerPos = createWorld(world, map);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        return playerPos;
    }

    public void update(float delta) {
        world.step(1/64f, 6, 2);

        camera.position.x = Player.getInstance().b2body.getPosition().x;
        camera.position.y = Player.getInstance().b2body.getPosition().y;
        cameraClamp();
        camera.update();

        mapRenderer.setView(camera);
    }

    public void render() {
        mapRenderer.render();

        if(ZomSim.MODE_DEBUG) {
            b2dr.render(world, camera.combined);
        }

        ZomSim.getInstance().batch.setProjectionMatrix(camera.combined);
    }

    public void dispose() {
        map.dispose();
        b2dr.dispose();
        world.dispose();
    }

    public World getWorld() {
        return world;
    }

    private void cameraClamp() {
        if((camera.position.x - (camera.viewportWidth * camera.zoom) / 2) < 0) {
            camera.position.x = (camera.viewportWidth / 2) * camera.zoom;
        }
        if((camera.position.x + (camera.viewportWidth * camera.zoom) / 2) > map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class)) {
            camera.position.x = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class) - (camera.viewportWidth * camera.zoom) / 2;
        }
        if((camera.position.y - (camera.viewportHeight * camera.zoom) / 2) < 0) {
            camera.position.y = (camera.viewportHeight / 2) * camera.zoom;
        }
        if((camera.position.y + (camera.viewportHeight * camera.zoom) / 2) > map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class)) {
            camera.position.y = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class) - (camera.viewportHeight * camera.zoom) / 2;
        }
    }
}
