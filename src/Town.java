import java.awt.*;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean hasBeenDug;
    private  boolean treasureHunted;
    private String townTreasure;
    private String lastBrawlWon;
    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        String treasureOfTheTown = "";
        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";
        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        assignTreasure();
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += breakItem(item);
            }
            hasBeenDug = false;
            treasureHunted = false;
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }
    public void assignTreasure() {
        String[] treasures = {"crown", "trophy", "gem", "dust"};
        this.townTreasure = treasures[(int) (Math.random() * treasures.length)];
    }
    public void huntForTreasure() {
        if (townTreasure != null) {
            TreasureHunter.window.addTextToWindow("You begin searching for treasure...", Color.black);
            TreasureHunter.window.addTextToWindow("Ah ha! You found a " + townTreasure + "!", Color.black);
            hunter.addTreasure(townTreasure);
            printMessage = "You found a " + townTreasure + "!";
        } else {
            printMessage = "There's nothing left to find in this town.";
        }
        townTreasure = null;
    }

    public void digForGold() {
        if (!hasBeenDug) {
            if (hunter.hasItemInKit("shovel")) {
                hasBeenDug = true;
                double rand = Math.random();
                if (rand < .50) {
                    digResult(true);
                } else {
                    digResult(false);
                }
            }   else {
                TreasureHunter.window.addTextToWindow("You don't got no shovel! What're ya gonna do claw yur way to gold?!" , Color.YELLOW);
            }
        }   else {
            TreasureHunter.window.addTextToWindow("You've dug this place bone dry!!! Go dig elsewhere!!!", Color.red);
        }
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (TreasureHunter.isEasyMode()) {
                if (Math.random() + 0.1 > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += Colors.YELLOW + "\nYou won the brawl and receive " + goldDiff + " gold." + Colors.RED;
                    hunter.changeGold(goldDiff);
                    lastBrawlWon = "won";
                }
                else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                    hunter.changeGold(-goldDiff);
                    lastBrawlWon = "lost";
                }
            }   else {
                if (Math.random() > noTroubleChance || hunter.hasItemInKit("sword")) {
                    if (hunter.hasItemInKit("sword")) {
                        printMessage += "Woah is that a sword?! Ok bud, you win, I ain't want no trouble with you.";
                    }
                    else {
                        printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";

                    }
                    printMessage += Colors.YELLOW + "\nYou won the brawl and receive " + goldDiff + " gold." + Colors.RED;
                    hunter.changeGold(goldDiff);
                    lastBrawlWon = "won";
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                    hunter.changeGold(-goldDiff);
                    lastBrawlWon = "lost";
                }
            }
            printMessage += Colors.RESET;
        }
    }

    public void updateLatestNews() {
        if (lastBrawlWon == null) {
            printMessage += "";
        }   else if (lastBrawlWon.equals("won")) {
            printMessage = "The town carries you with a higher level of respect as you wont the brawl.";
        }   else if (lastBrawlWon.equals("lost")) {
            printMessage = "You've become the talk of the town, not for a good reason though because you " + Colors.RED + "lost " + Colors.RESET + "the brawl.";
        }
    }
    public boolean checkGameOver() {
        // Check if game is over due to negative gold
        if (hunter.getGold() < 0) {
            TreasureHunter.window.addTextToWindow(Colors.RED + "GAME OVER!", Color.red);
            TreasureHunter.window.addTextToWindow("You lost a brawl and couldn't pay your debt." + Colors.RESET, Color.black);
            return true;
        }
        return false;
    }
    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        int rnd = (int) (Math.random() * 6) + 1 ; //changed the random calculation to be easier to change
        switch (rnd) {
            case 1:
                return new Terrain("Mountains", "Rope");
            case 2:
                return new Terrain("Ocean", "Boat");
            case 3:
                return new Terrain("Plains", "Horse");
            case 4:
                return new Terrain("Desert", "Water");
            case 5:
                return new Terrain("Jungle", "Machete");
            default:
                return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (TreasureHunter.isEasyMode()){
            return (false);
        }
        double rand = Math.random();
        return (rand < 0.5);

    }

    private String breakItem(String item) {
        switch (item) {
            case "water":
                return "Seems like you drank the last drop of your water. Should've bought more!";
            case "rope":
                return Colors.RED + "SNAP!" + Colors.RESET + "Looks like your rope finally gave out. Better restock!";
            case "machete":
                return "You accidentally dropped your machete into a cavern. Looks like you'll need another one!";
            case "horse":
                return "You got cornered by a pack of hyenas. You escaped with your life...but not your horse :(";
            case "boat":
                return "Just as you reach ashore, you crash into a pile of rocks. Looks like this boat wont be sailing the open seas anymore.";
            case "boots":
                return "The soles of your boots gave out and its almost as if you dont even have boots on. Get a new pair!!!";
            default:
                return "";
        }
    }

    private void digResult(boolean struckGold) {
        if (struckGold) {
            int goldAmt  = (int) (Math.random() * 20) + 1;
            TreasureHunter.window.addTextToWindow("You dug up " + Colors.formatGold(goldAmt + " gold!"), Color.black);
            hunter.changeGold(goldAmt);
        }   else {
            TreasureHunter.window.addTextToWindow("You dug but found only dirt.", Color.black);
        }
    }
}