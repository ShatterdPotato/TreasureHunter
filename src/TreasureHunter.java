import java.awt.*;
import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private Shop shop;
    private boolean hardMode;
    private static boolean easyMode;
    private static boolean samuraiMode;
    private static OutputWindow window = new OutputWindow();


    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        easyMode = false;
    }

    public static boolean isEasyMode(){
        return (easyMode);
    }

    public static boolean isSamuraiMode(){
        return samuraiMode;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }
    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        window.addTextToWindow("Welcome to TREASURE HUNTER!", Color.black);
        window.addTextToWindow("Going hunting for the big treasure, eh?",Color.black);
        window.addTextToWindow("What's your name, Hunter?", Color.black);
        String name = SCANNER.nextLine().toLowerCase();
        Color orange = new Color(240, 80, 20); // RGB!
        window.addTextToWindow("Which mode? (Easy[e], Normal [n], or Hard [h]): ", orange);
        

        // set hunter instance variable
        hunter = new Hunter(name, 20);
        shop =  new Shop(100); //set to 100 for now

        System.out.print("Which mode? (Easy[e], Normal [n], or Hard [h]): ");
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("h")) {
            hardMode = true;
        }   else if (hard.equals("test")) {
            shop.setupTestMode(hunter);
        }   else if (hard.equals("e")) {
            easyMode = true;
        }   else if (hard.equals("test lose")) {
             // Start with low gold for testing
            hunter.changeGold(-15); // Results in 5 gold total
            hardMode = true; // Higher chance of losing brawls
        }   else if (hard.equals("s")) {
            samuraiMode = true;
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        if (easyMode) {
            hunter.changeGold(20);
            markdown = 1;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        while (!choice.equals("x") && !currentTown.checkGameOver() && !hunter.checkWin()) {
            window.clear();
            window.addTextToWindow(currentTown.getLatestNews(), Color.black);
            window.addTextToWindow(hunter.infoString(), Color.black);
            window.addTextToWindow(currentTown.infoString(), Color.black);
            window.addTextToWindow("\n(B)uy something at the shop.", Color.black);
            window.addTextToWindow("(S)ell something at the shop.", Color.black);
            window.addTextToWindow("(D)ig for gold.", Color.black);
            window.addTextToWindow("(E)xplore surrounding terrain.", Color.black);
            window.addTextToWindow("(M)ove on to a different town.", Color.black);
            window.addTextToWindow("(L)ook for trouble!", Color.black);
            window.addTextToWindow("Give up the hunt and e(X)it.", Color.black);
            window.addTextToWindow("(H)unt for treasure.", Color.black);
            window.addTextToWindow("\nWhat's your next move? ", Color.black);
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("e")) {
            window.addTextToWindow(currentTown.getTerrain().infoString(), Color.black);
            currentTown.updateLatestNews();
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                window.addTextToWindow(currentTown.getLatestNews(), Color.black);
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("x")) {
            window.addTextToWindow("Fare thee well, " + hunter.getHunterName() + "!",Color.black);
        } else if (choice.equals("h")) {
            currentTown.huntForTreasure();
            if (hunter.checkWin()) {
                window.addTextToWindow("Congratulations, you have found the last of the three treasures! You win!!", Color.CYAN);
            }
        } else if (choice.equals("d")) {
            currentTown.digForGold();
        }  else {
            window.addTextToWindow("Yikes! That's an invalid option! Try again.", Color.red);
        }
    }
}