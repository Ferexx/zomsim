package dev.ferex.zomsim.world;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import dev.ferex.zomsim.characters.BasicZombie;
import dev.ferex.zomsim.characters.ZombieInterface;
import dev.ferex.zomsim.screens.GameScreen;
import dev.ferex.zomsim.weapons.Bullet;
import dev.ferex.zomsim.world.interactable.Gate;
import dev.ferex.zomsim.world.interactable.InteractableInterface;

public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
    private final GameScreen game;

    public ContactListener(GameScreen game) {
        this.game = game;
    }

    @Override
    public void beginContact(Contact contact) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();

        if(fixtureA.getUserData() == "player" || fixtureB.getUserData() == "player") {
            final Fixture player = fixtureA.getUserData() == "player" ? fixtureA : fixtureB;
            final Fixture object = player == fixtureA ? fixtureB : fixtureA;

            if(object.getUserData() == "zombie_melee")
                ((ZombieInterface) object.getBody().getUserData()).canAttack(true);

            if(object.getUserData() != null && InteractableInterface.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractableInterface) object.getUserData()).playerTouching(true);
                game.player.inContactWith = (InteractableInterface) object.getUserData();
            }
        }

        if(fixtureA.getUserData() == "player_melee" || fixtureB.getUserData() == "player_melee") {
            final Fixture player_melee = fixtureA.getUserData() == "player_melee" ? fixtureA : fixtureB;
            final Fixture object = player_melee == fixtureA ? fixtureB : fixtureA;

            if(object.getUserData() != null && object.getUserData() == "zombie_body") {
                game.player.inContactWithZombie = (BasicZombie) object.getBody().getUserData();
            }
        }

        if(fixtureA.getUserData() != null && Bullet.class.isAssignableFrom(fixtureA.getUserData().getClass()) ||
                fixtureB.getUserData() != null && Bullet.class.isAssignableFrom(fixtureB.getUserData().getClass())) {
            final Fixture bullet = Bullet.class.isAssignableFrom(fixtureA.getUserData().getClass()) ? fixtureA : fixtureB;
            final Fixture object = bullet == fixtureA ? fixtureB : fixtureA;

            if(object.getUserData() == "wall") {
                ((Bullet) bullet.getUserData()).toDestroy = true;
            }
            if(object.getUserData() == "zombie_body") {
                ((Bullet) bullet.getUserData()).toDestroy = true;
                ((BasicZombie) object.getBody().getUserData()).health -= ((Bullet) bullet.getUserData()).damage;
            }
            if(object.getUserData() != null && Gate.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Bullet) bullet.getUserData()).toDestroy = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();

        if(fixtureA.getUserData() == "player" || fixtureB.getUserData() == "player") {
            final Fixture player = fixtureA.getUserData() == "player" ? fixtureA : fixtureB;
            final Fixture object = player == fixtureA ? fixtureB : fixtureA;

            if(object.getUserData() == "zombie_melee")
                ((ZombieInterface) object.getBody().getUserData()).canAttack(false);

            if(object.getUserData() != null && InteractableInterface.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractableInterface) object.getUserData()).playerTouching(false);
                game.player.inContactWith = null;
            }
        }

        if(fixtureA.getUserData() == "player_melee" || fixtureB.getUserData() == "player_melee") {
            final Fixture player_melee = fixtureA.getUserData() == "player_melee" ? fixtureA : fixtureB;
            final Fixture object = player_melee == fixtureA ? fixtureB : fixtureA;

            if(object.getUserData() != null && BasicZombie.class.isAssignableFrom(object.getUserData().getClass())) {
                game.player.inContactWithZombie = null;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
