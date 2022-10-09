package dev.ferex.zomsim.weapons;

public interface Weapon {

    WeaponType getType();
    AmmoType getAmmoType();
    WeaponSlot getSlot();
    int getAmmoInMagazine();
    int getReserveAmmo();
    void attack();
    void update(float delta);
    void reload();
    boolean isReloading();
    void addAmmo(int amount);
}
