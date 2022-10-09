package dev.ferex.zomsim;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import dev.ferex.zomsim.characters.BasicZombie;
import dev.ferex.zomsim.characters.Survivor;
import dev.ferex.zomsim.characters.pathfinding.LevelGraph;
import dev.ferex.zomsim.characters.pathfinding.TileConnection;
import dev.ferex.zomsim.objectives.EscapeObjective;
import dev.ferex.zomsim.weapons.Bullet;
import dev.ferex.zomsim.world.interactable.*;

public class EntityHandler {

    private static EntityHandler instance;
    public final Array<WeaponSpawn> weapons = new Array<>();
    public final Array<Ammo> ammo = new Array<>();
    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet();
        }
    };
    private final Array<Bullet> activeBullets = new Array<>();
    public final Array<BasicZombie> zombies = new Array<>();
    public final Array<CollectObjectiveItem> fetchObjectiveItems = new Array<>();
    public final Array<Interactable> interactables = new Array<>();
    public RescueObjectiveItem rescueObjectiveItem;
    public Survivor rescueSurvivor;
    public RescueExit rescueExit;
    public EscapeObjective escapeObjective;
    public EscapeExit escapeExit;
    public LevelGraph levelGraph;

    private EntityHandler() {
        levelGraph = new LevelGraph();
    }

    public static EntityHandler getInstance() {
        if (instance == null) {
            instance = new EntityHandler();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public void draw(Batch batch) {
        for(WeaponSpawn spawn : weapons) {
            spawn.draw(batch);
        }
        for(Ammo ammoSpawn : ammo) {
            ammoSpawn.draw(batch);
        }
        for(Bullet bullet : activeBullets) {
            bullet.draw(batch);
        }
        for(BasicZombie zombie : zombies) {
            zombie.draw(batch);
        }
        for(CollectObjectiveItem fetch : fetchObjectiveItems) {
            fetch.draw(batch);
        }
        for(Interactable interactable : interactables) {
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
    }

    public void update(float delta) {
        for(Bullet bullet : activeBullets) {
            bullet.update();
            if(bullet.toDestroy) {
                activeBullets.removeValue(bullet, true);
                bulletPool.free(bullet);
            }
        }

        for(BasicZombie zombie : zombies)
            zombie.update(delta);
        rescueSurvivor.update(delta);
    }

    public void addBullet(int damage) {
        Bullet bullet = bulletPool.obtain();
        bullet.init(damage);
        activeBullets.add(bullet);
    }
}
