package dev.ferex.zomsim.weapons.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.ZomSim;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Pistol extends BasicGun {

    public Pistol() {
        super(15, 60, 0, 8, 8, 2000, WeaponSlot.SECONDARY, AmmoType.PISTOL, WeaponType.PISTOL);

        ZomSim game = ZomSim.getInstance();
        shootSound = game.assetManager.get("audio/sounds/pistol_shoot.wav", Sound.class);
        reloadSound = game.assetManager.get("audio/sounds/pistol_reload.wav", Sound.class);
    }

    @Override
    public WeaponType getType() {
        return WeaponType.PISTOL;
    }

    @Override
    public AmmoType getAmmoType() {
        return AmmoType.PISTOL;
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.SECONDARY;
    }

    @Override
    public void attack() {
        if(bulletsInMagazine <= 0) return;
        if(isReloading()) return;
        if(!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return;

        bulletsInMagazine--;
        EntityHandler.getInstance().addBullet(damage);
        shootSound.play();
    }
}
