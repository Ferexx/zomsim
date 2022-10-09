package dev.ferex.zomsim.objectives;

import com.badlogic.gdx.math.Vector2;
import dev.ferex.zomsim.characters.Player;

public class EscapeObjective extends BasicObjective {
    public Vector2 escapeLocation;
    private boolean inProgress = false;

    public EscapeObjective() {
        super(ObjectiveType.ESCAPE);
    }

    @Override
    public String toString() {
        return "Escape the level";
    }

    public void start() {
        Player.getInstance().showObjectivePointer(escapeLocation);
        inProgress = true;
    }

    public void setEscapeLocation(final Vector2 location) {
        this.escapeLocation = location;
    }

    public boolean isInProgress() {
        return inProgress;
    }
}
