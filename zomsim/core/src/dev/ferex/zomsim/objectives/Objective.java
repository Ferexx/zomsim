package dev.ferex.zomsim.objectives;

public interface Objective {
    ObjectiveType getType();
    boolean isComplete();
    String toString();

}
