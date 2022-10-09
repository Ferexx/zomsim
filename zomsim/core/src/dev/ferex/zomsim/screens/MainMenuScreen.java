package dev.ferex.zomsim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import dev.ferex.zomsim.ZomSim;

public class MainMenuScreen extends MenuScreen {
    final Music menuMusic;

    public MainMenuScreen() {
        final ZomSim game = ZomSim.getInstance();
        menuMusic = game.assetManager.get("audio/music/MenuMusic.mp3", Music.class);
        menuMusic.setLooping(true);
        menuMusic.play();

        final Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = font;
        final Label titleLabel = new Label("Agent Zombie", titleLabelStyle);
        titleLabel.setSize(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT / 6.0f);
        titleLabel.setPosition(ZomSim.V_WIDTH / 4.0f, ZomSim.V_HEIGHT - (ZomSim.V_HEIGHT / 3.0f));

        final Skin playButtonSkin = new Skin(Gdx.files.internal("skins/biological-attack/skin/biological-attack-ui.json"));
        final ImageTextButton playButton = new ImageTextButton("Play", playButtonSkin, "gasmask");
        playButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameScreen.clearInstance();
                game.setScreen(GameScreen.getInstance());
                GameScreen.getInstance().loadLevel("maps/level1/level1.tmx");
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
        stage.addActor(table);
    }

    @Override
    public void show() {
        menuMusic.play();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        ZomSim game = ZomSim.getInstance();
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
        menuMusic.dispose();
        stage.dispose();
    }
}
