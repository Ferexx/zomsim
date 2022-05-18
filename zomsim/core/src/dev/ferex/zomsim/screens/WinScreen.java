package dev.ferex.zomsim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import dev.ferex.zomsim.ZomSim;

public class WinScreen extends MenuScreen {

    public WinScreen(final ZomSim game) {
        super(game);

        final Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = font;
        final Label titleLabel = new Label("You Survived", titleLabelStyle);
        titleLabel.setSize(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT / 6.0f);
        titleLabel.setPosition(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT - (ZomSim.V_HEIGHT / 3.0f));

        final Skin playButtonSkin = new Skin(Gdx.files.internal("skins/biological-attack/skin/biological-attack-ui.json"));
        final ImageTextButton mainMenuButton = new ImageTextButton("Return to Main Menu", playButtonSkin, "gasmask");
        final ImageTextButton quitButton = new ImageTextButton("Quit Game", playButtonSkin, "gasmask");

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
