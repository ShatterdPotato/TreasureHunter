import java.awt.*;
import java.util.Scanner;

/**
 * The Shop class controls the cost of the items in the Treasure Hunt game. <p>
 * The Shop class also acts as a go between for the Hunter's buyItem() method. <p>
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Shop {
    // constants
    private static final int WATER_COST = 2;
    private static final int ROPE_COST = 4;
    private static final int MACHETE_COST = 6;
    private static final int HORSE_COST = 12;
    private static final int BOAT_COST = 20;
    private static final int BOOT_COST = 10;
    private static final int SHOVEL_COST = 8;
    private static final int SWORD_COST = 0;

    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private double markdown;
    private Hunter customer;

    /**
     * The Shop constructor takes in a markdown value and leaves customer null until one enters the shop.
     *
     * @param markdown Percentage of markdown for selling items in decimal format.
     */
    public Shop(double markdown) {
        this.markdown = markdown;
        customer = null; // customer is set in the enter method
    }
    public void setupTestMode(Hunter hunter) {
        customer = hunter;
        // Give hunter enough gold to have 100 after buying everything
        hunter.changeGold(80);
        // Add all items
        hunter.addItem("water");
        hunter.addItem("rope");
        hunter.addItem("machete");
        hunter.addItem("horse");
        hunter.addItem("boat");
        hunter.addItem("boots");
        hunter.addItem("shovel");
    }

    /**
     * Method for entering the shop.
     *
     * @param hunter the Hunter entering the shop
     * @param buyOrSell String that determines if hunter is "B"uying or "S"elling
     * @return a String to be used for printing in the latest news
     */
    public String enter(Hunter hunter, String buyOrSell) {
        customer = hunter;
        if (buyOrSell.equals("b")) {
            TreasureHunter.window.addTextToWindow("Welcome to the shop! We have the finest wares in town.", Color.BLACK);
            TreasureHunter.window.addTextToWindow("Currently we have the following items:",Color.black);
            TreasureHunter.window.addTextToWindow(inventory(), Color.BLACK);
            System.out.print("What're you lookin' to buy? ");
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, true);
            if (TreasureHunter.isSamuraiMode() && item.equals("sword")) {
                System.out.print("It'll cost you " +Colors.formatGold(cost + " gold. ") + "Buy it (y/n)? ");
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    buyItem(item);
                }
            }   else if (cost == 0) {
                TreasureHunter.window.addTextToWindow("We ain't got none of those.",Color.black);
            }   else {
                if (hunter.hasItemInKit("sword")) {
                    System.out.print("Is that a s-sword?! You know what, its on the house, no cost at all b-bud! (y/n)");
                }   else {
                    System.out.print("It'll cost you " + Colors.formatGold(cost + " gold. ") + "Buy it (y/n)? ");
                }
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    buyItem(item);
                }
            }
        } else {
            TreasureHunter.window.addTextToWindow("What're you lookin' to sell? ", Color.BLACK);
            System.out.print("You currently have the following items: " + customer.getInventory());
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, false);
            if (cost == 0) {
                TreasureHunter.window.addTextToWindow("We don't want none of those.", Color.black);
            } else {
                System.out.print("It'll get you " + Colors.formatGold( cost + " gold. ") + "Sell it (y/n)? ");
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    sellItem(item);
                }
            }
        }
        return "You left the shop";
    }

    /**
     * A method that returns a string showing the items available in the shop
     * (all shops sell the same items).
     *
     * @return the string representing the shop's items available for purchase and their prices.
     */
    public String inventory() {
        String str = "Water: " + Colors.formatGold(WATER_COST + " gold\n");
        str += "Rope: " + Colors.formatGold( ROPE_COST + " gold\n");
        str += "Machete: " + Colors.formatGold(MACHETE_COST + " gold\n");
        str += "Horse: " + Colors.formatGold(HORSE_COST + " gold\n");
        str += "Boat: " + Colors.formatGold(BOAT_COST + " gold\n");
        str += "Boots: " + Colors.formatGold(BOOT_COST + " gold\n");
        str += "Shovel: " + Colors.formatGold(SHOVEL_COST + " gold\n");
        if (TreasureHunter.isSamuraiMode()) {
            str += "Sword: " + Colors.formatGold(SWORD_COST + " gold\n");
        }
        return str;
    }

    /**
     * A method that lets the customer (a Hunter) buy an item.
     *
     * @param item The item being bought.
     */
    public void buyItem(String item) {
        int costOfItem = checkMarketPrice(item, true);
        if (customer.buyItem(item, costOfItem)) {
            TreasureHunter.window.addTextToWindow("Ye' got yerself a " + item + ". Come again soon.", Color.black);
        } else {
            TreasureHunter.window.addTextToWindow("Hmm, either you don't have enough gold or you've already got one of those!", Color.black);
        }
    }

    /**
     * A pathway method that lets the Hunter sell an item.
     *
     * @param item The item being sold.
     */
    public void sellItem(String item) {
        int buyBackPrice = checkMarketPrice(item, false);
        if (customer.sellItem(item, buyBackPrice)) {
            TreasureHunter.window.addTextToWindow("Pleasure doin' business with you.", Color.black);
        } else {
            TreasureHunter.window.addTextToWindow("Stop stringin' me along!", Color.black);
        }
    }

    /**
     * Determines and returns the cost of buying or selling an item.
     *
     * @param item The item in question.
     * @param isBuying Whether the item is being bought or sold.
     * @return The cost of buying or selling the item based on the isBuying parameter.
     */
    public int checkMarketPrice(String item, boolean isBuying) {
        if (isBuying) {
            return getCostOfItem(item);
        } else {
            return getBuyBackCost(item);
        }
    }

    /**
     * Checks the item entered against the costs listed in the static variables.
     *
     * @param item The item being checked for cost.
     * @return The cost of the item or 0 if the item is not found.
     */
    public int getCostOfItem(String item) {
        switch (item) {
            case "water":
                return WATER_COST;
            case "rope":
                return ROPE_COST;
            case "machete":
                return MACHETE_COST;
            case "horse":
                return HORSE_COST;
            case "boat":
                return BOAT_COST;
            case "boots":
                return BOOT_COST;
            case "shovel":
                return SHOVEL_COST;
            case "sword":
                if (TreasureHunter.isSamuraiMode()) {
                    return SWORD_COST;
                }
                return 0;
            default:
                return 0;
        }
    }

    /**
     * Checks the cost of an item and applies the markdown.
     *
     * @param item The item being sold.
     * @return The sell price of the item.
     */
    public int getBuyBackCost(String item) {
        int cost = (int) (getCostOfItem(item) * markdown);
        return cost;
    }
}