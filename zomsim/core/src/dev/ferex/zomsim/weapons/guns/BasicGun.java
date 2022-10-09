package dev.ferex.zomsim.weapons.guns;

import com.badlogic.gdx.audio.Sound;
import dev.ferex.zomsim.EntityHandler;
import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.BasicWeapon;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public abstract class BasicGun extends BasicWeapon {
    protected int reserveAmmo;
    protected int magazineSize;
    protected int bulletsInMagazine;

    protected final int reloadTimeMs;
    protected int reloadProgress = 0;
    protected boolean isReloading = false;

    protected Sound shootSound;
    protected Sound reloadSound;

    public BasicGun(int damage, int attacksPerMinute, int reserveAmmo, int magazineSize, int bulletsInMagazine, int reloadTimeMs, WeaponSlot weaponSlot, AmmoType ammoType, WeaponType weaponType) {
        super(damage, attacksPerMinute, weaponSlot, ammoType, weaponType);
        this.reserveAmmo = reserveAmmo;
        this.magazineSize = magazineSize;
        this.bulletsInMagazine = bulletsInMagazine;
        this.reloadTimeMs = reloadTimeMs;
    }

    @Override
    public int getAmmoInMagazine() {
        return bulletsInMagazine;
    }

    @Override
    public int getReserveAmmo() {
        return reserveAmmo;
    }

    public void attack() {
        if(bulletsInMagazine <= 0 || isReloading) return;

        if(System.currentTimeMillis() - lastAttack < 60000 / attacksPerMinute) return;
        else lastAttack = System.currentTimeMillis();

        bulletsInMagazine--;
        EntityHandler.getInstance().addBullet(damage);
        shootSound.play();
    }

    public void reload() {
        if(reserveAmmo > 0 && !isReloading && bulletsInMagazine < magazineSize) {
            isReloading = true;
            reloadSound.play();
        }
    }

    public void update(float delta) {
        if(isReloading) {
            reloadProgress += delta;

            if(reloadProgress >= reloadTimeMs) {
                endReload();
            }
        }
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

    public void addAmmo(int amount) {
        reserveAmmo += amount;
    }
}
