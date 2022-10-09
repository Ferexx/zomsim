package dev.ferex.zomsim.objectives;

import com.badlogic.gdx.math.Vector2;
import dev.ferex.zomsim.characters.Player;

public class RescueObjective extends BasicObjective {
    private boolean inProgress = false;
    private Vector2 rescueLocation;

    public RescueObjective() {
        super(ObjectiveType.RESCUE);
    }

    @Override
    public String toString() {
        if(complete)
            return "Survivor rescued";
        else
            return "Survivor not rescued";
    }

    public void setRescueLocation(final Vector2 location) {
        this.rescueLocation = location;
    }
    public void start() {
        Player.getInstance().showObjectivePointer(rescueLocation);
        inProgress = true;
    }

    public void end() {
        Player.getInstance().hideObjectivePointer();
        inProgress = false;
    }

    public boolean isInProgress() {
        return inProgress;
    }
}
