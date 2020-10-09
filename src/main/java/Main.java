import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.util.concurrent.ThreadLocalRandom;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // initiate terminal
        Terminal terminal = initiateTerminal();

        // handle terminal
        handleBackground(terminal);

        // create player
        //Position player = new Position(20, 10, 20, 10,'\u263a');
        int randomY = ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getRows()-1);
        int randomX = ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns()-1);
        Position player = new Position(randomX, randomY, randomX, randomY,'\u263a');


        // set the player on the terminal
        printToTerminal(terminal, player.getX(), player.getY(),TextColor.ANSI.CYAN, player.getPlayerIcon() );

        // calculate and display player score
        final int scoreAvoidingMonster= 10;
        final int scoreBooster = 100;
        int playerScore = 0;
        displayScore(terminal, playerScore);

        // create monsters

        List<Position> monsters = new ArrayList<>();
        monsters.add(new Position(ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns()-1), ThreadLocalRandom.current().nextInt(3, terminal.getTerminalSize().getRows()-1), '\u262b'));
        monsters.add(new Position(ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns()-1), ThreadLocalRandom.current().nextInt(3, terminal.getTerminalSize().getRows()-1), '\u262b'));
        monsters.add(new Position(ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns()-1), ThreadLocalRandom.current().nextInt(3, terminal.getTerminalSize().getRows()-1), '\u262b'));

        //monsters.add(new Position(1, 10, 'Ö'));
        //monsters.add(new Position(40, 5, 'Ö'));
        //monsters.add(new Position(40, 15, 'Ö'));

        // set monsters on the terminal
        for (Position monster : monsters) {
            printToTerminal(terminal,monster.getX(),monster.getY(),TextColor.ANSI.RED,monster.getPlayerIcon());
        }

        // Save initial position of monsters for resetting the monster positions
        List<Position> defaultMonsterPosition = new ArrayList<>();
        defaultMonsterPosition.addAll(monsters) ;

        // add boosters
        List<Position> boosters = new ArrayList<>();
        boosters.add(new Position(ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns()-1), ThreadLocalRandom.current().nextInt(3, terminal.getTerminalSize().getRows()-1), '\u2665'));
        boosters.add(new Position(ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns()-1), ThreadLocalRandom.current().nextInt(3, terminal.getTerminalSize().getRows()-1), '\u2665'));
        boosters.add(new Position(ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns()-1), ThreadLocalRandom.current().nextInt(3, terminal.getTerminalSize().getRows()-1), '\u2665'));

        //boosters.add(new Position(15, 5, '¤') );
        //boosters.add(new Position(25, 9, '¤') );
        //boosters.add(new Position(17, 15, '¤') );

        // set boosters on the terminal
        for (Position booster : boosters) {
            printToTerminal(terminal,booster.getX(), booster.getY(), TextColor.ANSI.GREEN, booster.getPlayerIcon());
        }

        // get the initial position of the player
        int playerX = player.getX();
        int playerY = player.getY();

        // initialize win chances
        int winChance = 0;

        // read user input to move the player, move monsters behind the player
        boolean continueReadingInput = true;
        while (continueReadingInput) {

            // user input
            KeyStroke keyStroke;
            do {
                Thread.sleep(5);
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);


            // if user wants to quit
            Character c = keyStroke.getCharacter();
            if (c == Character.valueOf('q')) {
               continueReadingInput = false;
               System.out.println("quit");
               terminal.close();
            }

            // save the current position of the player before moving
            player.setPrevX(playerX);
            player.setPrevY(playerY);

            // fetch previous position of the player
            int prevX = player.getPrevX();
            int prevY = player.getPrevY();

            // move player based on user input
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    playerY++;
                    break;
                case ArrowUp:
                    playerY--;
                    break;
                case ArrowLeft:
                    playerX--;
                    break;
                case ArrowRight:
                    playerX++;
                    break;
                case Enter:
                    playerX = ThreadLocalRandom.current().nextInt(2, terminal.getTerminalSize().getColumns() - 1);
                    playerY = ThreadLocalRandom.current().nextInt(3, terminal.getTerminalSize().getRows() - 1);
                    playerScore = playerScore - 110;
                    if (playerScore <= 0) {
                        String message = "Game Over";
                        for (int i = 0; i < message.length(); i++) {
                            printToTerminal(terminal, i + 35, 11, TextColor.ANSI.RED, message.charAt(i));
                            Thread.sleep(300); // might throw InterruptedException

                        }
                    }
            }
            // check if the player crashes with any of the monsters
            boolean crashIntoObsticle = false;
            for (Position monster : monsters) {
                if (monster.getX() == prevX && monster.getY() == prevY) {
                    crashIntoObsticle = true;
                    break;
                }
            }

            // check if the player hits the boosters
            boolean crashIntoBooster = false;
            for (Position booster : boosters) {
                if (booster.getX() == prevX && booster.getY() == prevY) {
                    crashIntoBooster = true;
                    playerScore = playerScore + scoreBooster;
                    displayScore(terminal,playerScore);
                    break;
                }
            }

            // player hits all boosters , game over, player WON
            if (crashIntoBooster) {
                winChance ++ ;

                if (winChance == boosters.size()) {
                    String message = "You WIN !!! :)  ";
                    for (int i = 0; i < message.length(); i++) {
                        printToTerminal(terminal,i+35,11,TextColor.ANSI.GREEN,message.charAt(i));
                        Thread.sleep(300); // might throw InterruptedException
                    }
                    continueReadingInput = false;
                }
            }

            // player crashed with the monster , game over , player LOST
            if (crashIntoObsticle) {

                // display the message on to the terminal
                String message = "Game Over :( ";
                for (int i = 0; i < message.length(); i++) {
                    printToTerminal(terminal,i+35,11,TextColor.ANSI.RED,message.charAt(i));
                    Thread.sleep(300); // might throw InterruptedException
                }

                continueReadingInput = false;

            } else {

                // clean previous position of the player
                cleanPreviousPosition(terminal,prevX,prevY);

                // set the player to new position
                printToTerminal(terminal,playerX,playerY,TextColor.ANSI.CYAN,player.getPlayerIcon()) ;

                // save new position of the player
                player.setX(playerX);
                player.setY(playerY);

                // player avoided monster, increase points
                playerScore = playerScore + scoreAvoidingMonster;
                displayScore(terminal,playerScore);

            }

            int monsterX, monsterY, prevMonsterX, prevMonsterY, pos = 0;
            List<Position> tempMonsters = new ArrayList<>();

            // move monsters based on player position
            for (Position monster : monsters) {

                // get the current position of monsters
                monsterX = monster.getX();
                monsterY = monster.getY();

                // save the current position of the monster before moving
                prevMonsterX = monsterX;
                prevMonsterY = monsterY;

                // move the monster if player has not hit a booster
                if (! crashIntoBooster) {
                    if (monsterX < playerX) {
                        monsterX = ++monsterX;
                    }
                    if (monsterY < playerY) {
                        monsterY = ++monsterY;
                    }
                    if (monsterX > playerX) {
                        monsterX = --monsterX;
                    }
                    if (monsterY > playerY) {
                        monsterY = --monsterY;
                    }
                }
                // if player hits the booster, reset monster positions
                else
                {
                    monsterX = defaultMonsterPosition.get(pos).getX();
                    monsterY = defaultMonsterPosition.get(pos).getY();
                }

                // clear the previous monster position
                cleanPreviousPosition(terminal,prevMonsterX,prevMonsterY);

                // set the monster to new position
                printToTerminal(terminal,monsterX,monsterY,TextColor.ANSI.RED,monster.getPlayerIcon()) ;

                // save the new monster positions in a temporary list
                Position newMonsterPos = new Position(monsterX, monsterY, '\u262b');
                tempMonsters.add(newMonsterPos);
                pos ++ ;
            }
            // Reset the monster list with new monster positions
            monsters.clear();
            monsters.addAll(tempMonsters);
        }
    }

    // method to initiate the terminal
    private static Terminal initiateTerminal() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        return terminal;
    }

    // method to print player / monster on a particular position on the terminal
    private static void printToTerminal(Terminal terminal, int x, int y, TextColor color, char icon) throws IOException {
        terminal.setCursorPosition(x, y);
        terminal.setForegroundColor(color);
        terminal.putCharacter(icon);
        terminal.flush();
    }

    // clean previous player / monster position on the terminal
    private static void cleanPreviousPosition (Terminal terminal, int x,int y) throws IOException {
        terminal.setCursorPosition(x, y);
        terminal.putCharacter(' ');
        terminal.flush();
    }

    // method to display score
    private static void displayScore(Terminal terminal, int score) throws IOException {
        // If player survives a move, add a point, play a sound
        String message = "Players score: " + score;

        final int textStartPositionX = 60;
        final int textPositionY = 1;
        //Play a little sound when scoring
        // Thread thread = new Thread(new Music());
        // thread.start();
        for (int i = 0; i< message.length(); i++){
            printToTerminal(terminal, textStartPositionX + i, textPositionY, TextColor.ANSI.GREEN,message.charAt(i));
        }

    }

    // method to add header and border to terminal
    private static void handleBackground(Terminal terminal) throws IOException {
        // get size of the terminal
        int rowSize = terminal.getTerminalSize().getRows();
        int colSize = terminal.getTerminalSize().getColumns();

        // draw a border for the terminal
        TextGraphics tGraphics = terminal.newTextGraphics();
        tGraphics.drawRectangle(
                new TerminalPosition(0,0), new TerminalSize(colSize,rowSize), '|');

        // add a header to the terminal
        tGraphics.putString(35,0,"MONSTER GAME");

        terminal.flush();
    }

}
