package dev.ferex.zomsim.weapons.guns;

import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Shotgun extends BasicGun {

    public Shotgun() {
        super(50, 20, 0, 5, 5, 5000, WeaponSlot.PRIMARY, AmmoType.SHOTGUN, WeaponType.SHOTGUN);

        ZomSim game = ZomSim.getInstance();
        shootSound = game.assetManager.get("audio/sounds/shotgun_shoot.wav");
        reloadSound = game.assetManager.get("audio/sounds/shotgun_reload.wav");
    }

    @Override
    public WeaponType getType() {
        return WeaponType.SHOTGUN;
    }

    @Override
    public AmmoType getAmmoType() {
        return AmmoType.SHOTGUN;
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.PRIMARY;
    }
}
