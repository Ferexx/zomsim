package dev.ferex.zomsim.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.objectives.Objective;
import dev.ferex.zomsim.weapons.Weapon;

import static dev.ferex.zomsim.weapons.WeaponSlot.MELEE;

public class HUD {

    public Stage stage;

    private final Label ammoLabel;
    private final Label healthLabel;
    private final Label primaryWeaponLabel;
    private final Label secondaryWeaponLabel;
    private final Label meleeWeaponLabel;
    private final Label fetchObjectiveLabel;
    private final Label killObjectiveLabel;
    private final Label rescueObjectiveLabel;
    private final Label escapeObjectiveLabel;
    private final Image primaryWeaponImage;
    private final Image secondaryWeaponImage;
    private final SpriteDrawable rifleDrawable;
    private final SpriteDrawable shotgunDrawable;
    private final SpriteDrawable pistolDrawable;
    private final BitmapFont font = new BitmapFont();
    private final Table objectiveTable;

    public HUD() {
        final Viewport viewport = new StretchViewport(ZomSim.V_WIDTH, ZomSim.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ZomSim.getInstance().batch);

        primaryWeaponLabel = new Label("1", new Label.LabelStyle(font, Color.WHITE));
        secondaryWeaponLabel = new Label("2", new Label.LabelStyle(font, Color.WHITE));
        meleeWeaponLabel = new Label("3", new Label.LabelStyle(font, Color.WHITE));
        ammoLabel = new Label(String.format("%02d / %02d", 0, 0), new Label.LabelStyle(font, Color.WHITE));
        healthLabel = new Label(String.format("%03d HP", 100), new Label.LabelStyle(font, Color.WHITE));

        fetchObjectiveLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        killObjectiveLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        rescueObjectiveLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        escapeObjectiveLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));

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
        objectiveTable = new Table();
        topTable.setFillParent(true);
        bottomTable.setFillParent(true);
        objectiveTable.setFillParent(true);

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
        objectiveTable.right();
        objectiveTable.add(fetchObjectiveLabel).padRight(20);
        objectiveTable.row();
        objectiveTable.add(killObjectiveLabel).padRight(20);
        objectiveTable.row();
        objectiveTable.add(rescueObjectiveLabel).padRight(20);

        stage.addActor(topTable);
        stage.addActor(bottomTable);
        stage.addActor(objectiveTable);
    }

    public void addWeapon(Weapon weapon) {
        switch(weapon.getType()) {
            case RIFLE:
                primaryWeaponImage.setDrawable(rifleDrawable);
                break;
            case SHOTGUN:
                primaryWeaponImage.setDrawable(shotgunDrawable);
                break;
            case PISTOL:
                secondaryWeaponImage.setDrawable(pistolDrawable);
                break;
            case KNIFE:
                break;
        }
    }

    public void removeWeapon(Weapon weapon) {
        switch(weapon.getSlot()) {
            case PRIMARY:
                primaryWeaponImage.setDrawable(null);
                break;
            case SECONDARY:
                secondaryWeaponImage.setDrawable(null);
                break;
            case MELEE:
                break;
        }
    }

    public void equipWeapon(Weapon weapon) {
        switch(weapon.getSlot()) {
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

        if (weapon.getSlot() != MELEE) {
            ammoLabel.setText(String.format("%02d / %02d", weapon.getAmmoInMagazine(),
                    weapon.getReserveAmmo()));
        } else {
            ammoLabel.setText("00 / 00");
        }
    }

    public void updateHealth(int health) {
        healthLabel.setText(String.format("%03d HP", health));
    }

    public void updateObjective(Objective objective) {
        switch (objective.getType()) {
            case FETCH:
                if (objective.isComplete()) {
                    fetchObjectiveLabel.setStyle(new Label.LabelStyle(font, Color.GREEN));
                }
                fetchObjectiveLabel.setText(objective.toString());
                break;
            case KILL:
                if (objective.isComplete()) {
                    killObjectiveLabel.setStyle(new Label.LabelStyle(font, Color.GREEN));
                }
                killObjectiveLabel.setText(objective.toString());
            case RESCUE:
                if (objective.isComplete()) {
                    rescueObjectiveLabel.setStyle(new Label.LabelStyle(font, Color.GREEN));
                }
                rescueObjectiveLabel.setText(objective.toString());
        }
    }

    public void finishObjectives() {
        objectiveTable.clear();
        objectiveTable.add(escapeObjectiveLabel);
    }
}
