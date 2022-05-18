package dev.ferex.zomsim.screens;

import astra.core.Scheduler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import dev.ferex.zomsim.ZomSim;

public class PauseScreen extends MenuScreen {
    private GameScreen screen;


    public PauseScreen(final ZomSim game, final GameScreen screen) {
        super(game);
        this.screen = screen;

        final Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = font;
        final Label titleLabel = new Label("Paused", titleLabelStyle);
        titleLabel.setSize(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT / 6.0f);
        titleLabel.setPosition(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT - (ZomSim.V_HEIGHT / 3.0f));

        final Skin playButtonSkin = new Skin(Gdx.files.internal("skins/biological-attack/skin/biological-attack-ui.json"));
        final ImageTextButton playButton = new ImageTextButton("Resume", playButtonSkin, "gasmask");
        playButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(screen);
                screen.controls.paused = false;
                dispose();
            }
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        final ImageTextButton quitButton = new ImageTextButton("Quit", playButtonSkin, "gasmask");
        quitButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Scheduler.shutdown();
                Gdx.app.exit();
                dispose();
            }
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        table.add(titleLabel).expandX().padTop(100);
        table.row();
        table.add(playButton).expandX().padTop(50);
        table.row();
        table.add(quitButton).expandX().padTop(50);
        stage.addActor(table);
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        super.render(delta);

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(screen);
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
        stage.dispose();
    }
}
