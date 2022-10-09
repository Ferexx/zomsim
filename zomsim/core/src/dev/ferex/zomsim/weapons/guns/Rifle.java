package dev.ferex.zomsim.weapons.guns;

import com.badlogic.gdx.audio.Sound;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Rifle extends BasicGun {

    public Rifle() {
        super(20, 600, 0, 30, 30, 3000, WeaponSlot.PRIMARY, AmmoType.RIFLE, WeaponType.RIFLE);

        ZomSim game = ZomSim.getInstance();
        shootSound = game.assetManager.get("audio/sounds/ak47_shoot.wav", Sound.class);
        reloadSound = game.assetManager.get("audio/sounds/ak47_reload.wav", Sound.class);
    }


    @Override
    public WeaponType getType() {
        return WeaponType.RIFLE;
    }

    @Override
    public AmmoType getAmmoType() {
        return AmmoType.RIFLE;
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.PRIMARY;
    }
}
