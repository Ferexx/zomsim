package dev.ferex.zomsim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.ferex.zomsim.screens.MainMenuScreen;

public class ZomSim extends Game {
	public static final int V_WIDTH = 1920;
	public static final int V_HEIGHT = 1080;

	public static final int TOTAL_WEAPONS = 20;
	public static final int TOTAL_ZOMBIES = 50;

	public static boolean MODE_DEBUG = false;

	public static final short DEFAULT_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short WEAPON_BIT = 4;
	public static final short ENEMY_BIT = 8;
	public static final short BULLET_BIT = 16;
	public static final short MELEE_BIT = 32;
	public static final short OBJECTIVE_BIT = 64;

	public SpriteBatch batch;
	public BitmapFont font;
	public AssetManager assetManager;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();

		assetManager = new AssetManager();
		assetManager.load("audio/music/MenuMusic.mp3", Music.class);
		assetManager.load("audio/sounds/stonefootsteps.ogg", Sound.class);
		assetManager.load("audio/sounds/ak47_shoot.wav", Sound.class);
		assetManager.load("audio/sounds/ak47_reload.wav", Sound.class);
		assetManager.load("audio/sounds/pistol_shoot.wav", Sound.class);
		assetManager.load("audio/sounds/pistol_reload.wav", Sound.class);
		assetManager.load("audio/sounds/shotgun_shoot.wav", Sound.class);
		assetManager.load("audio/sounds/shotgun_reload.wav", Sound.class);
		assetManager.load("audio/sounds/zombie_growl.wav", Sound.class);
		assetManager.load("audio/sounds/player_hurt.wav", Sound.class);
		assetManager.finishLoading();

		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
		assetManager.update();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		assetManager.dispose();
	}
}
