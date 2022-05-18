package dev.ferex.zomsim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import dev.ferex.zomsim.Controls;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.hud.HUD;
import dev.ferex.zomsim.world.EventHandler;
import dev.ferex.zomsim.world.WorldCreator;

public class GameScreen implements Screen {
    public final ZomSim game;
    public final EntityHandler entityHandler = new EntityHandler(this);
    public Player player;
    public final Controls controls;
    public final EventHandler eventHandler;

    // Tiled Variables
    public final TiledMap currentMap;
    private final TiledMapRenderer mapRenderer;

    // Box2D Variables
    public final World world;
    private final Box2DDebugRenderer b2dr;
    public final WorldCreator worldCreator;

    public final OrthographicCamera camera;
    public final HUD hud;

    public GameScreen(ZomSim game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, ZomSim.V_WIDTH, ZomSim.V_HEIGHT);
        camera.zoom = 0.1f;

        currentMap = new TmxMapLoader().load("maps/level1/level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap, 1);

        world = new World(new Vector2(0, 0), true);
        worldCreator = new WorldCreator(this, world, currentMap);
        b2dr = new Box2DDebugRenderer();
        controls = new Controls(this);
        eventHandler = new EventHandler(this);

        hud = new HUD(this, game.batch);
    }

    @Override
    public void show() {

    }

    public void update(float delta) {
        controls.update(delta);

        world.step(1/64f, 6, 2);

        player.update(delta);
        entityHandler.update(delta);
        eventHandler.update(delta);
        hud.update(delta);

        camera.position.x = player.b2body.getPosition().x;
        camera.position.y = player.b2body.getPosition().y;
        cameraClamp();
        camera.update();

        mapRenderer.setView(camera);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        if(ZomSim.MODE_DEBUG) {
            b2dr.render(world, camera.combined);
        }
        //b2dr.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        entityHandler.draw(game.batch);
        player.draw(game.batch);

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(playerDied()) {
            game.setScreen(new DeadScreen(game));
            dispose();
        }
        if(playerSurvived()) {
            game.setScreen(new WinScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        currentMap.dispose();
        b2dr.dispose();
        world.dispose();
    }

    public void cameraClamp() {
        if((camera.position.x - (camera.viewportWidth * camera.zoom) / 2) < 0) {
            camera.position.x = (camera.viewportWidth / 2) * camera.zoom;
        }
        if((camera.position.x + (camera.viewportWidth * camera.zoom) / 2) > currentMap.getProperties().get("width", Integer.class) * currentMap.getProperties().get("tilewidth", Integer.class)) {
            camera.position.x = currentMap.getProperties().get("width", Integer.class) * currentMap.getProperties().get("tilewidth", Integer.class) - (camera.viewportWidth * camera.zoom) / 2;
        }
        if((camera.position.y - (camera.viewportHeight * camera.zoom) / 2) < 0) {
            camera.position.y = (camera.viewportHeight / 2) * camera.zoom;
        }
        if((camera.position.y + (camera.viewportHeight * camera.zoom) / 2) > currentMap.getProperties().get("height", Integer.class) * currentMap.getProperties().get("tileheight", Integer.class)) {
            camera.position.y = currentMap.getProperties().get("height", Integer.class) * currentMap.getProperties().get("tileheight", Integer.class) - (camera.viewportHeight * camera.zoom) / 2;
        }
    }

    public boolean playerDied() {
        return player.health <= 0;
    }
    public boolean playerSurvived() {
        if(player.escapeObjective != null) {
            return player.escapeObjective.complete;
        }
        return false;
    }
}
