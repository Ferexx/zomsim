package dev.ferex.zomsim.objectives;

public class KillObjective extends BasicObjective {
    public int numberOfKills;
    public int killed = 0;

    public KillObjective(int numberOfKills) {
        super(ObjectiveType.KILL);
        this.numberOfKills = numberOfKills;
    }

    public void addKill() {
        if(++killed >= numberOfKills) complete = true;
    }

    @Override
    public String toString() {
        return killed + " / " + numberOfKills + " zombies killed";
    }
}
