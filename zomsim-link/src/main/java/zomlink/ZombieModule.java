package zomlink;

import astra.core.Module;
import astra.event.Event;
import astra.gui.GuiEvent;
import astra.gui.GuiEventUnifier;
import astra.reasoner.Unifier;
import astra.term.ListTerm;
import astra.term.Term;

import java.util.LinkedList;

// ANY CHANGES TO THIS REQUIRES MVN INSTALL

public class ZombieModule extends Module {
    private ZombieListener listener;

    static {
        Unifier.eventFactory.put(ZombieEvent.class, new ZombieEventUnifier());
    }

    @EVENT( types = {"string", "list" }, signature="$zombie:", symbols = {} )
    public Event event(Term id, Term args) {
        return new ZombieEvent(id, (ListTerm) args);
    }

    @EVENT( types = {"string"}, signature = "$zombie:", symbols = {} )
    public Event event(Term id) {
        return new ZombieEvent(id);
    }

    @ACTION
    public boolean linkZombie(ZombieListener listener) {
        this.listener = listener;
        return true;
    }

    @ACTION
    public boolean chasePlayer() {
        return listener.chasePlayer();
    }

    @ACTION
    public boolean goToClosestZombie() {
        return listener.goToClosestZombie();
    }

    @ACTION
    public boolean goToClosestWeapon() {
        return listener.goToClosestWeapon();
    }

    @ACTION
    public boolean goToLocation(float x, float y) {
        return listener.goToLocation(x, y);
    }

    @ACTION
    public boolean stop() {
        return listener.stop();
    }

    @TERM
    public int getHealth() {
        return listener.getHealth();
    }

    @TERM
    public LinkedList<Float> getLocation() {
        return listener.getLocation();
    }

    @TERM
    public int getPlayerHealth() {
        return listener.getPlayerHealth();
    }

    @ACTION
    public boolean doAction(String name, ListTerm args) {
        return listener.handle(name, args.toArray());
    }

}
