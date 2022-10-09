package dev.ferex.zomsim.world.interactable;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Interactable {
    void draw(Batch batch);
    void interact();
    void playerTouching(boolean touching);
    void destroy();
}
