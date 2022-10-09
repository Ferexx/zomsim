package dev.ferex.zomsim.objectives;

public abstract class BasicObjective implements Objective {
    private final ObjectiveType type;
    protected boolean complete = false;

    public BasicObjective(ObjectiveType type) {
        this.type = type;
    }

    @Override
    public ObjectiveType getType() {
        return type;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
