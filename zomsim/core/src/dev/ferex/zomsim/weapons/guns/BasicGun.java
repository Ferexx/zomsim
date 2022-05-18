package dev.ferex.zomsim.weapons.guns;

import com.badlogic.gdx.audio.Sound;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.BasicWeapon;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class BasicGun extends BasicWeapon {
    public int reserveAmmo;
    public int magazineSize;
    public int bulletsInMagazine;

    public final int reloadTimeMs;
    public int reloadProgress = 0;
    public boolean isReloading = false;

    public Sound shootSound;
    public Sound reloadSound;

    public BasicGun(int damage, int attacksPerMinute, int reserveAmmo, int magazineSize, int bulletsInMagazine, int reloadTimeMs, WeaponSlot weaponSlot, AmmoType ammoType, WeaponType weaponType) {
        super(damage, attacksPerMinute, weaponSlot, ammoType, weaponType);
        this.reserveAmmo = reserveAmmo;
        this.magazineSize = magazineSize;
        this.bulletsInMagazine = bulletsInMagazine;
        this.reloadTimeMs = reloadTimeMs;
    }

    public boolean attack() {
        if(bulletsInMagazine <= 0) return false;

        if(System.currentTimeMillis() - lastAttack < 60000 / attacksPerMinute) return false;
        else lastAttack = System.currentTimeMillis();

        bulletsInMagazine--;

        shootSound.play();

        return true;
    }

    public boolean beginReload() {
        if(reserveAmmo <= 0 || isReloading || bulletsInMagazine == magazineSize) return false;
        isReloading = true;
        reloadSound.play();
        return true;
    }

    public void endReload() {
        if(reserveAmmo > magazineSize - bulletsInMagazine) {
            reserveAmmo -= magazineSize - bulletsInMagazine;
            bulletsInMagazine += magazineSize - bulletsInMagazine;
        }
        else if(reserveAmmo > 0) {
            bulletsInMagazine += reserveAmmo;
            reserveAmmo = 0;
        }

        isReloading = false;
        reloadProgress = 0;
    }

    public boolean isReloading() {
        return isReloading;
    }
}
