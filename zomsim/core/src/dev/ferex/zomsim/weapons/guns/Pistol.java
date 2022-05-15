package dev.ferex.zomsim.weapons.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Pistol extends BasicGun {

    public Pistol(GameScreen screen) {
        super(15, 60, 0, 8, 8, 2000, WeaponSlot.SECONDARY, AmmoType.PISTOL, WeaponType.PISTOL);

        shootSound = screen.game.assetManager.get("audio/sounds/pistol_shoot.wav", Sound.class);
        reloadSound = screen.game.assetManager.get("audio/sounds/pistol_reload.wav", Sound.class);
    }

    public boolean attack() {
        if(bulletsInMagazine <= 0) return false;
        if(!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return false;

        bulletsInMagazine--;

        shootSound.play();

        return true;
    }
}
