package dev.ferex.zomsim.weapons;

public abstract class BasicWeapon {
    public final int damage;
    public final int attacksPerMinute;
    public long lastAttack;

    public final WeaponSlot weaponSlot;
    public final AmmoType ammoType;
    public final WeaponType weaponType;


    public BasicWeapon(int damage, int attacksPerMinute, WeaponSlot weaponSlot, AmmoType ammoType, WeaponType weaponType) {
        this.damage = damage;
        this.attacksPerMinute = attacksPerMinute;
        this.weaponSlot = weaponSlot;
        this.ammoType = ammoType;
        this.weaponType = weaponType;
    }

    public abstract boolean attack();
    public abstract boolean isReloading();
}
