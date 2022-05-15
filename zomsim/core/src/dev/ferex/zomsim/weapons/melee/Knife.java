package dev.ferex.zomsim.weapons.melee;

import dev.ferex.zomsim.weapons.AmmoType;
import dev.ferex.zomsim.weapons.BasicWeapon;
import dev.ferex.zomsim.weapons.WeaponSlot;
import dev.ferex.zomsim.weapons.WeaponType;

public class Knife extends BasicWeapon {

    public Knife() {
        super(20, 30, WeaponSlot.MELEE, AmmoType.NONE, WeaponType.KNIFE);
    }

    public boolean attack() {
        if(System.currentTimeMillis() - lastAttack < 60000 / attacksPerMinute) return false;
        else lastAttack = System.currentTimeMillis();

        return true;
    }

    public boolean isReloading() {
        return false;
    }
}
