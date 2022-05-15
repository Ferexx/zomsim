package dev.ferex.zomsim.objectives;

public abstract class BasicObjective {
    public ObjectiveType type;
    public boolean complete = false;

    public BasicObjective(ObjectiveType type) {
        this.type = type;
    }

    public abstract String toString();
}
