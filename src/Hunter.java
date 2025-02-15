import java.awt.*;
import java.util.Objects;

/**
 * Hunter Class<br /><br />
 * This class represents the treasure hunter character (the player) in the Treasure Hunt game.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Hunter {
    //instance variables
    private String hunterName;
    private String[] kit;
    private int gold;
    private String[] treasureInv;
    /**
     * The base constructor of a Hunter assigns the name to the hunter and an empty kit.
     *
     * @param hunterName The hunter's name.
     * @param startingGold The gold the hunter starts with.
     */
    public Hunter(String hunterName, int startingGold) {
        this.hunterName = Color.GREEN + hunterName + Color.RESET;
        if (TreasureHunter.isSamuraiMode()){
            kit = new String[8];
        } else {
            kit = new String[7];
        }
        gold = startingGold;
        treasureInv = new String[3];
    }

    //Accessors
    public String getHunterName() {
        return hunterName;
    }

    /**
     * Updates the amount of gold the hunter has.
     *
     * @param modifier Amount to modify gold by.
     */
    public void changeGold(int modifier) {
        gold += modifier;
    }

    /**
     * Buys an item from a shop.
     *
     * @param item The item the hunter is buying.
     * @param costOfItem The cost of the item.
     * @return true if the item is successfully bought.
     */
    public boolean buyItem(String item, int costOfItem) {
        if (TreasureHunter.isSamuraiMode()) {
            if (!hasItemInKit("sword")) {
                gold -= costOfItem;
            }
            addItem(item);
            return true;
        }
        if (costOfItem == 0 || gold < costOfItem || hasItemInKit(item)) {
            return false;
        }
        gold -= costOfItem;
        addItem(item);
        return true;
    }


    /**
     * The Hunter is selling an item to a shop for gold.<p>
     * This method checks to make sure that the seller has the item and that the seller is getting more than 0 gold.
     *
     * @param item The item being sold.
     * @param buyBackPrice the amount of gold earned from selling the item
     * @return true if the item was successfully sold.
     */
    public boolean sellItem(String item, int buyBackPrice) {
        if (buyBackPrice <= 0 || !hasItemInKit(item)) {
            return false;
        }
        gold += buyBackPrice;
        removeItemFromKit(item);
        return true;
    }

    /**
     * Removes an item from the kit by setting the index of the item to null.
     *
     * @param item The item to be removed.
     */
    public void removeItemFromKit(String item) {
        int itmIdx = findItemInKit(item);

        // if item is found
        if (itmIdx >= 0) {
            kit[itmIdx] = null;
        }
    }

    /**
     * Checks to make sure that the item is not already in the kit.
     * If not, it assigns the item to an index in the kit with a null value ("empty" position).
     *
     * @param item The item to be added to the kit.
     * @return true if the item is not in the kit and has been added.
     */
    public boolean addItem(String item) {
        if (!hasItemInKit(item)) {
            int idx = emptyPositionInKit();
            kit[idx] = item;
            return true;
        }
        return false;
    }
    public boolean addTreasure(String treasure) {
        if (Objects.equals(treasure, "dust")){
            return false;
        }
        else if (!hasTreasureAlready(treasure)) {
            int idx = emptyPositionInTreasure();
            treasureInv[idx] = treasure;
            return true;
        }
        return false;
    }

    /**
     * Checks if the kit Array has the specified item.
     *
     * @param item The search item
     * @return true if the item is found.
     */
    public boolean hasItemInKit(String item) {
        for (String tmpItem : kit) {
            if (item.equals(tmpItem)) {
                // early return
                return true;
            }
        }
        return false;
    }
    public boolean hasTreasureAlready(String treasure) {
        for (String tmpTreasure : treasureInv ) {
            if (treasure.equals(tmpTreasure)) {
                TreasureHunter.window.addTextToWindow("Already has " + treasure, Color.yellow);
                return true;
            }
        }
        return false;
    }

     /**
     * Returns a printable representation of the inventory, which
     * is a list of the items in kit, with a space between each item.
     *
     * @return The printable String representation of the inventory.
     */
    public String getInventory() {
        String printableKit = "";
        String space = " ";

        for (String item : kit) {
            if (item != null) {
                printableKit += item + space;
            }
        }
        return Color.pink + printableKit + Color.RESET;
    }
    public String getTreasureInv() {
        String printableTreasureInv = "";
        String space = " ";
        for (String treasure : treasureInv) {
            if (treasure != null) {
                printableTreasureInv += treasure + space;
            }
            else if (treasure == null){
                printableTreasureInv += "and nothing else";
            }
        }
        return Color.YELLOW + printableTreasureInv;
    }

    /**
     * @return A string representation of the hunter.
     */
    public String infoString() {
        String str = hunterName + " has " + Colors.formatGold(gold + " gold") ;
        if (!kitIsEmpty()) {
            str += " and " + getInventory();
        }
        if (!treasureInvIsEmpty()) {
            str += " and " + getTreasureInv();
        }
        return str;
    }
    public boolean checkWin() {
        if (findTreasureInTreasureInv("crown") != -1 && findTreasureInTreasureInv("trophy") != -1 && findTreasureInTreasureInv("gem") != -1) {
            return true;
        }
        return false;
    }
    /**
     * Searches kit Array for the index of the specified value.
     *
     * @param item String to look for.
     * @return The index of the item, or -1 if not found.
     */
    private int findItemInKit(String item) {
        for (int i = 0; i < kit.length; i++) {
            String tmpItem = kit[i];

            if (item.equals(tmpItem)) {
                return i;
            }
        }
        return -1;
    }
    private int findTreasureInTreasureInv(String treasure) {
        for (int i = 0; i < treasureInv.length; i++) {
            String tmpTreasure = treasureInv[i];
            if (treasure.equals(tmpTreasure)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Check if the kit is empty - meaning all elements are null.
     *
     * @return true if kit is completely empty.
     */
    private boolean kitIsEmpty() {
        for (String string : kit) {
            if (string != null) {
                return false;
            }
        }
        return true;
    }
    private boolean treasureInvIsEmpty() {
        for (String string : treasureInv){
            if (string != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the first index where there is a null value.
     *
     * @return index of empty index, or -1 if not found.
     */
    private int emptyPositionInKit() {
        for (int i = 0; i < kit.length; i++) {
            if (kit[i] == null) {
                return i;
            }
        }
        return 0;
    }
    private int emptyPositionInTreasure() {
        for (int i = 0; i < treasureInv.length; i++) {
            if (treasureInv[i] == null) {
                return i;
            }
        }
        return 0;
    }

    public int getGold() {
        return gold;
    }
}