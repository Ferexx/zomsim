package dev.ferex.zomsim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import dev.ferex.zomsim.Controls;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.characters.Player;
import dev.ferex.zomsim.hud.HUD;
import dev.ferex.zomsim.world.EventHandler;
import dev.ferex.zomsim.world.WorldManager;

public class GameScreen implements Screen {

    private static GameScreen instance;

    public final HUD hud;

    private GameScreen() {
        Controls.getInstance();
        EventHandler.getInstance();

        hud = new HUD();
    }

    public static GameScreen getInstance() {
        if (instance == null) {
            instance = new GameScreen();
        }
        return instance;
    }

    public static void clearInstance() {
        if(instance == null) return;
        instance.dispose();
        instance = null;
    }

    public void loadLevel(final String fileName) {
        final Vector2 playerSpawn = WorldManager.getInstance().loadWorld(fileName);
        Player.getInstance();
        Player.getInstance().teleport(playerSpawn);
    }

    @Override
    public void show() {

    }

    public void update(float delta) {
        Controls.getInstance().update(delta);
        Player.getInstance().update(delta);
        EntityHandler.getInstance().update(delta);

        WorldManager.getInstance().update(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        WorldManager.getInstance().render();

        final ZomSim game = ZomSim.getInstance();
        game.batch.begin();
        EntityHandler.getInstance().draw(game.batch);
        Player.getInstance().draw(game.batch);

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(playerDied()) {
            game.setScreen(new DeadScreen());
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
        WorldManager.getInstance().dispose();
    }

    public boolean playerDied() {
        return Player.getInstance().health <= 0;
    }

    public void finishLevel() {
        ZomSim.getInstance().setScreen(new WinScreen());
        dispose();
    }
}
