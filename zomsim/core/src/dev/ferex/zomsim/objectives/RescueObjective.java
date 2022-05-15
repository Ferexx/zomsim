package dev.ferex.zomsim.objectives;

public class RescueObjective extends BasicObjective {
    public boolean inProgress = false;

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
}
