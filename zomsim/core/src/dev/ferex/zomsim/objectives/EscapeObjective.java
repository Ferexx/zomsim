package dev.ferex.zomsim.objectives;

public class EscapeObjective extends BasicObjective {
    public final int escapeLocationX, escapeLocationY;
    public boolean inProgress = false;

    public EscapeObjective(int x, int y) {
        super(ObjectiveType.ESCAPE);
        escapeLocationX = x;
        escapeLocationY = y;
    }

    @Override
    public String toString() {
        return "Escape the level";
    }
}
