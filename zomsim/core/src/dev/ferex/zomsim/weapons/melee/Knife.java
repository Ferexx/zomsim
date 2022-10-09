package dev.ferex.zomsim.weapons.melee;

import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.BasicWeapon;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Knife extends BasicWeapon {

    public Knife() {
        super(20, 30, WeaponSlot.MELEE, AmmoType.NONE, WeaponType.KNIFE);
    }

    @Override
    public WeaponType getType() {
        return WeaponType.KNIFE;
    }

    @Override
    public AmmoType getAmmoType() {
        return null;
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.MELEE;
    }

    @Override
    public int getAmmoInMagazine() {
        return -1;
    }

    @Override
    public int getReserveAmmo() {
        return -1;
    }

    public void attack() {
        if(System.currentTimeMillis() - lastAttack < 60000 / attacksPerMinute) return;
        else {
            lastAttack = System.currentTimeMillis();
            // TODO: Melee attack
        }
    }

    @Override
    public void update(float delta) {}

    @Override
    public void reload() {}

    @Override
    public boolean isReloading() {
        return false;
    }

    @Override
    public void addAmmo(int amount) {}
}
