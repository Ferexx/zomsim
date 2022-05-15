package dev.ferex.zomsim.weapons.guns;

import com.badlogic.gdx.audio.Sound;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Rifle extends BasicGun {

    public Rifle(GameScreen screen) {
        super(20, 600, 0, 30, 30, 3000, WeaponSlot.PRIMARY, AmmoType.RIFLE, WeaponType.RIFLE);
        reloadTimeMs = 3000;

        shootSound = screen.game.assetManager.get("audio/sounds/ak47_shoot.wav", Sound.class);
        reloadSound = screen.game.assetManager.get("audio/sounds/ak47_reload.wav", Sound.class);
    }
}
