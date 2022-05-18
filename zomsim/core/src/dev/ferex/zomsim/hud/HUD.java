package dev.ferex.zomsim.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.BasicWeapon;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.guns.BasicGun;
import dev.ferex.zomsim.weapons.guns.Pistol;
import dev.ferex.zomsim.weapons.guns.Rifle;
import dev.ferex.zomsim.weapons.guns.Shotgun;

public class HUD {
    private final GameScreen screen;

    public Stage stage;

    private final Label ammoLabel;
    private final Label healthLabel;
    private final Label primaryWeaponLabel;
    private final Label secondaryWeaponLabel;
    private final Label meleeWeaponLabel;
    private final Label objective1Label;
    private final Label objective2Label;
    private final Label objective3Label;
    private final Image primaryWeaponImage;
    private final Image secondaryWeaponImage;
    private final SpriteDrawable rifleDrawable;
    private final SpriteDrawable shotgunDrawable;
    private final SpriteDrawable pistolDrawable;
    private final BitmapFont font = new BitmapFont();

    public HUD(GameScreen screen, SpriteBatch spriteBatch) {
        this.screen = screen;

        final Viewport viewport = new StretchViewport(ZomSim.V_WIDTH, ZomSim.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        primaryWeaponLabel = new Label("1", new Label.LabelStyle(font, Color.WHITE));
        secondaryWeaponLabel = new Label("2", new Label.LabelStyle(font, Color.WHITE));
        meleeWeaponLabel = new Label("3", new Label.LabelStyle(font, Color.WHITE));
        ammoLabel = new Label(String.format("%02d / %02d", 0, 0), new Label.LabelStyle(font, Color.WHITE));
        healthLabel = new Label(String.format("%03d HP", screen.player.health), new Label.LabelStyle(font, Color.WHITE));

        objective1Label = new Label(screen.player.collectObjective.toString(), new Label.LabelStyle(font, Color.WHITE));
        objective2Label = new Label(screen.player.killObjective.toString(), new Label.LabelStyle(font, Color.WHITE));
        objective3Label = new Label(screen.player.rescueObjective.toString(), new Label.LabelStyle(font, Color.WHITE));

        primaryWeaponImage = new Image();
        secondaryWeaponImage = new Image();
        final Image meleeWeaponImage = new Image();

        rifleDrawable = new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("hud/ak47.png"))));
        rifleDrawable.setMinSize(rifleDrawable.getMinWidth() * 2, rifleDrawable.getMinHeight() * 2);
        shotgunDrawable = new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("hud/pump.png"))));
        shotgunDrawable.setMinSize(shotgunDrawable.getMinWidth() * 2, shotgunDrawable.getMinHeight() * 2);
        pistolDrawable = new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("hud/glock.png"))));
        pistolDrawable.setMinSize(pistolDrawable.getMinWidth() * 2, pistolDrawable.getMinHeight() * 2);
        final SpriteDrawable knifeDrawable = new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("hud/knife.png"))));
        knifeDrawable.setMinSize(knifeDrawable.getMinWidth() / 2, knifeDrawable.getMinHeight() / 2);
        meleeWeaponImage.setDrawable(knifeDrawable);

        ammoLabel.setFontScale(5);
        ammoLabel.setAlignment(Align.bottomRight);
        healthLabel.setFontScale(5);
        healthLabel.setAlignment(Align.bottomLeft);

        final Table topTable = new Table();
        final Table bottomTable = new Table();
        final Table rightTable = new Table();
        topTable.setFillParent(true);
        bottomTable.setFillParent(true);
        rightTable.setFillParent(true);

        topTable.top();
        topTable.add(primaryWeaponLabel).padTop(10);
        topTable.add(secondaryWeaponLabel).padTop(10);
        topTable.add(meleeWeaponLabel).padTop(10);
        topTable.row();
        topTable.add(primaryWeaponImage).padTop(5).padRight(70);
        topTable.add(secondaryWeaponImage).padTop(5);
        topTable.add(meleeWeaponImage).padTop(5).padLeft(50);

        bottomTable.bottom();
        bottomTable.add(healthLabel).expandX().padRight(700);
        bottomTable.add(ammoLabel).expandX().padLeft(700);

        font.getData().setScale(2);
        rightTable.right();
        rightTable.add(objective1Label).padRight(20);
        rightTable.row();
        rightTable.add(objective2Label).padRight(20);
        rightTable.row();
        rightTable.add(objective3Label).padRight(20);

        stage.addActor(topTable);
        stage.addActor(bottomTable);
        stage.addActor(rightTable);
    }

    public void update(float delta) {
        final BasicWeapon playerWeapon = screen.player.getActiveWeapon();
        if(playerWeapon.weaponSlot != WeaponSlot.MELEE) {
            ammoLabel.setText(String.format("%02d / %02d", ((BasicGun) playerWeapon).bulletsInMagazine,
                    ((BasicGun) playerWeapon).reserveAmmo));
        }
        else ammoLabel.setText("00 / 00");
        healthLabel.setText(String.format("%03d HP", screen.player.health));

        if(screen.player.primaryWeapon instanceof Rifle && primaryWeaponImage.getDrawable() != rifleDrawable) {
            primaryWeaponImage.setDrawable(rifleDrawable);
        }
        if(screen.player.primaryWeapon instanceof Shotgun && primaryWeaponImage.getDrawable() != shotgunDrawable) {
            primaryWeaponImage.setDrawable(shotgunDrawable);
        }
        if(screen.player.secondaryWeapon instanceof Pistol && secondaryWeaponImage.getDrawable() != pistolDrawable) {
            secondaryWeaponImage.setDrawable(pistolDrawable);
        }

        switch(screen.player.activeWeaponSlot) {
            case PRIMARY:
                primaryWeaponLabel.setStyle(new Label.LabelStyle(font, Color.RED));
                secondaryWeaponLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
                meleeWeaponLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
                break;
            case SECONDARY:
                primaryWeaponLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
                secondaryWeaponLabel.setStyle(new Label.LabelStyle(font, Color.RED));
                meleeWeaponLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
                break;
            case MELEE:
                primaryWeaponLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
                secondaryWeaponLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
                meleeWeaponLabel.setStyle(new Label.LabelStyle(font, Color.RED));
        }

        font.getData().setScale(2);
        if(!screen.player.killObjective.complete || !screen.player.collectObjective.complete || !screen.player.rescueObjective.complete) {
            if (screen.player.collectObjective.complete) {
                objective1Label.setStyle(new Label.LabelStyle(font, Color.GREEN));
            }
            objective1Label.setText(screen.player.collectObjective.toString());
            if (screen.player.killObjective.complete) {
                objective2Label.setStyle(new Label.LabelStyle(font, Color.GREEN));
            }
            objective2Label.setText(screen.player.killObjective.toString());
            if (screen.player.rescueObjective.complete) {
                objective3Label.setStyle(new Label.LabelStyle(font, Color.GREEN));
            }
            objective3Label.setText(screen.player.rescueObjective.toString());
        } else {
            objective1Label.setText("");
            objective2Label.setStyle(new Label.LabelStyle(font, Color.WHITE));
            objective2Label.setText(screen.player.escapeObjective.toString());
            objective3Label.setText("");
        }
    }

    public void onZombieKilled() {

    }
}
