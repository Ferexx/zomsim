package dev.ferex.zomsim;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import dev.ferex.zomsim.characters.BasicZombie;
import dev.ferex.zomsim.characters.Survivor;
import dev.ferex.zomsim.characters.pathfinding.LevelGraph;
import dev.ferex.zomsim.characters.pathfinding.TileConnection;
import dev.ferex.zomsim.objectives.EscapeObjective;
import dev.ferex.zomsim.objectives.ObjectivePointer;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.Bullet;
import dev.ferex.zomsim.world.interactable.*;

public class EntityHandler {
    private GameScreen screen;

    public Array<WeaponSpawn> weapons = new Array<>();
    public Array<Ammo> ammo = new Array<>();
    public Array<Bullet> bullets = new Array<>();
    public Array<BasicZombie> zombies = new Array<>();
    public Array<CollectObjectiveItem> fetchObjectiveItems = new Array<>();
    public Array<InteractableInterface> interactables = new Array<>();
    public RescueObjectiveItem rescueObjectiveItem;
    public Survivor rescueSurvivor;
    public RescueExit rescueExit;
    public EscapeObjective escapeObjective;
    public EscapeExit escapeExit;
    public ObjectivePointer objectivePointer;
    public LevelGraph levelGraph;

    public EntityHandler(GameScreen screen) {
        this.screen = screen;
        objectivePointer = new ObjectivePointer(screen);
        levelGraph = new LevelGraph(screen);
    }

    public void draw(Batch batch) {
        for(WeaponSpawn spawn : weapons) {
            spawn.draw(batch);
        }
        for(Ammo ammoSpawn : ammo) {
            ammoSpawn.draw(batch);
        }
        for(Bullet bullet : bullets) {
            bullet.draw(batch);
        }
        for(BasicZombie zombie : zombies) {
            zombie.draw(batch);
        }
        for(CollectObjectiveItem fetch : fetchObjectiveItems) {
            fetch.draw(batch);
        }
        for(InteractableInterface interactable : interactables) {
            interactable.draw(batch);
        }
        if(ZomSim.MODE_DEBUG) {
            ShapeRenderer renderer = new ShapeRenderer();
            for (TileConnection connection : levelGraph.connections) {
                connection.draw(renderer);
            }
        }

        rescueObjectiveItem.draw(batch);
        rescueSurvivor.draw(batch);
        rescueExit.draw(batch);
        escapeExit.draw(batch);
        objectivePointer.draw(batch);
    }

    public void update(float delta) {
        for(Bullet bullet : bullets)
            bullet.update(delta);
        for(BasicZombie zombie : zombies)
            zombie.update(delta);
        objectivePointer.update(delta);
        rescueSurvivor.update(delta);
    }
}
