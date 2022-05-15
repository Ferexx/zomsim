package dev.ferex.zomsim.objectives;

public class CollectObjective extends BasicObjective {
    public int numberOfItems;
    public int itemsAcquired = 0;

    public CollectObjective(int numberOfItems) {
        super(ObjectiveType.FETCH);
        this.numberOfItems = numberOfItems;
    }

    public void acquireItem() {
        if(++itemsAcquired >= numberOfItems) {
            complete = true;
        }
    }

    @Override
    public String toString() {
        return itemsAcquired + " / " + numberOfItems + " items fetched";
    }
}
