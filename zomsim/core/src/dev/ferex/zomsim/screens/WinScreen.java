package dev.ferex.zomsim.screens;

import com.badlogic.gdx.Gdx;
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

public class WinScreen implements Screen {
    private final ZomSim game;
    private final Stage stage;

    OrthographicCamera camera;

    public WinScreen(final ZomSim game) {
        this.game = game;

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
        Label titleLabel = new Label("You Survived", titleLabelStyle);
        titleLabel.setSize(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT / 6.0f);
        titleLabel.setPosition(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT - (ZomSim.V_HEIGHT / 3.0f));

        Skin playButtonSkin = new Skin(Gdx.files.internal("skins/biological-attack/skin/biological-attack-ui.json"));
        ImageTextButton mainMenuButton = new ImageTextButton("Return to Main Menu", playButtonSkin, "gasmask");
        ImageTextButton quitButton = new ImageTextButton("Quit Game", playButtonSkin, "gasmask");

        mainMenuButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        quitButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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
        table.add(mainMenuButton).expandX().padTop(50);
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

        game.batch.begin();
        game.batch.end();
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
