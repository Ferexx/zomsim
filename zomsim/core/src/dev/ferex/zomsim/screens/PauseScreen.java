package dev.ferex.zomsim.screens;

import astra.core.Scheduler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.ferex.zomsim.ZomSim;

public class PauseScreen implements Screen {
    private ZomSim game;
    private GameScreen screen;

    private Stage stage;
    OrthographicCamera camera;

    public PauseScreen(final ZomSim game, final GameScreen screen) {
        this.game = game;
        this.screen = screen;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, ZomSim.V_WIDTH, ZomSim.V_HEIGHT);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ZombieQueen.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 200;
        parameter.borderWidth = 1;
        parameter.color = Color.BLACK;
        BitmapFont font30 = fontGenerator.generateFont(parameter);
        fontGenerator.dispose();

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = font30;
        Label titleLabel = new Label("Paused", titleLabelStyle);
        titleLabel.setSize(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT / 6.0f);
        titleLabel.setPosition(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT - (ZomSim.V_HEIGHT / 3.0f));

        Skin playButtonSkin = new Skin(Gdx.files.internal("skins/biological-attack/skin/biological-attack-ui.json"));
        ImageTextButton playButton = new ImageTextButton("Resume", playButtonSkin, "gasmask");
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

        ImageTextButton quitButton = new ImageTextButton("Quit", playButtonSkin, "gasmask");
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
        Gdx.gl.glClearColor(256, 256, 256, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        stage.act();
        stage.draw();

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
