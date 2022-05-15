package dev.ferex.zomsim.weapons.guns;

import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Shotgun extends BasicGun {

    public Shotgun(GameScreen screen) {
        super(50, 20, 0, 5, 5, 5000, WeaponSlot.PRIMARY, AmmoType.SHOTGUN, WeaponType.SHOTGUN);

        shootSound = screen.game.assetManager.get("audio/sounds/shotgun_shoot.wav");
        reloadSound = screen.game.assetManager.get("audio/sounds/shotgun_reload.wav");
    }
}
