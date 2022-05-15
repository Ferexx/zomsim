package dev.ferex.zomsim.weapons;

public abstract class BasicWeapon {
    public int damage;
    public int attacksPerMinute;
    public long lastAttack;

    public WeaponSlot weaponSlot;
    public AmmoType ammoType;
    public WeaponType weaponType;


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
