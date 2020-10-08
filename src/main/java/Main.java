import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // initiate terminal
        Terminal terminal = initiateTerminal();

        // create player
        Position player = new Position(5, 5, 5, 5, 'X');

        final int pointsAvoidingMonster= 1;

        // set the player on the terminal
        printToTerminal(terminal, player.getX(), player.getY(),TextColor.ANSI.CYAN, player.getPlayerIcon() );
        displayScore(terminal, player.getScore());

        // create monsters
        List<Position> monsters = new ArrayList<>();
        Position monster1 = new Position(10, 10, 10, 10, 'Ö');
        Position monster2 = new Position(1, 1, 1, 1, 'Ö');
        Position monster3 = new Position(20, 10, 20, 10, 'Ö');
        monsters.add(monster1);
        monsters.add(monster2);
        monsters.add(monster3);


        // set monsters on the terminal
        for (int i = 0; i< monsters.size(); i++) {
           printToTerminal(terminal, monsters.get(i).getX(), monsters.get(i).getY(), TextColor.ANSI.RED, monsters.get(i).getPlayerIcon());
        }

        // get the initial position of the player
        int playerX = player.getX();
        int playerY = player.getY();

        // read user input to move the player, move monsters behind the player
        boolean continueReadingInput = true;
        while (continueReadingInput) {

            KeyStroke keyStroke;
            do {
                Thread.sleep(5);
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);

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
            }

            // check if the player crashes with any of the monsters
            boolean crashIntoObsticle = false;
            for (int i = 0; i < monsters.size(); i++) {
                if (monsters.get(i).getX() == prevX && monsters.get(i).getY() == prevY) {
                    crashIntoObsticle = true;
                }
            }

            // player crashed with the monster
            if (crashIntoObsticle) {
                continueReadingInput = false;
                System.out.println("quit");
                terminal.close();
            } else {
                // clean previous position of player
                cleanPreviousPosition(terminal,prevX,prevY);

                // set the player to new position
                printToTerminal(terminal,playerX,playerY,TextColor.ANSI.CYAN,player.getPlayerIcon()) ;

                // save new position of the player
                player.setX(playerX);
                player.setY(playerY);

                // player avoided monster, increase points by 5
                player.setScore(pointsAvoidingMonster);
                displayScore(terminal, player.getScore());
            }
            terminal.flush();

            int monsterX, monsterY, prevMonsterX, prevMonsterY;
            List<Position> tempMonsters = new ArrayList<>();

            for (int j = 0; j < monsters.size(); j++) {

                // get the current position of monsters
                monsterX = monsters.get(j).getX();
                monsterY = monsters.get(j).getY();

                // save the current position of the monster before moving
                prevMonsterX = monsterX;
                prevMonsterY = monsterY;

                // move the monster
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

                // clear the previous monster position
                cleanPreviousPosition(terminal,prevMonsterX,prevMonsterY);

                // set the monster to new position
                printToTerminal(terminal,monsterX,monsterY,TextColor.ANSI.RED,monsters.get(j).getPlayerIcon()) ;

                // save the new monster positions in a temporary list
                Position newMonsterPos = new Position(monsterX, monsterY, 'Ö');
                tempMonsters.add(newMonsterPos);
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

    private static void cleanPreviousPosition (Terminal terminal, int x,int y) throws IOException {
        terminal.setCursorPosition(x, y);
        terminal.putCharacter(' ');
        terminal.flush();
    }

    private static void displayScore(Terminal terminal, int score) throws IOException {
        // If player survives a move, add a point, play a sound
        String message = "Players score: " + score;
        final int textStartPositionX = 50;
        final int textPositionY = 50;

        for (int i = 0; i< message.length(); i++){
            printToTerminal(terminal, textStartPositionX + i, textPositionY, TextColor.ANSI.GREEN,message.charAt(i));
        }

    }


}
