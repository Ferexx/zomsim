package dev.ferex.zomsim.weapons;

public abstract class BasicWeapon implements Weapon {
    public final int damage;
    public final int attacksPerMinute;
    public final WeaponSlot weaponSlot;
    public final AmmoType ammoType;
    public final WeaponType weaponType;
    public long lastAttack;


    public BasicWeapon(int damage, int attacksPerMinute, WeaponSlot weaponSlot, AmmoType ammoType, WeaponType weaponType) {
        this.damage = damage;
        this.attacksPerMinute = attacksPerMinute;
        this.weaponSlot = weaponSlot;
        this.ammoType = ammoType;
        this.weaponType = weaponType;
    }

    public abstract boolean isReloading();
}
