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
        Position player = new Position(5, 5,5,5,'X');

        // set the player on the terminal
        setPlayer(terminal, player);

        // create monsters
        List<Position> monsters = new ArrayList<>();
        Position monster1 = new Position(10,10,10,10,'Ö');
        Position monster2 = new Position(1,1,1,1,'Ö');
        Position monster3 = new Position(20,10,20,10,'Ö');
        monsters.add(monster1);
        monsters.add(monster2);
        monsters.add(monster3);

        // set monsters on the terminal
        setMonster (terminal, monsters) ;


        // get the initial position of the player
        int playerX = player.getX();
        int playerY = player.getY();


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
            for (int i=0 ; i< monsters.size() ; i++) {
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
                terminal.setCursorPosition(prevX, prevY);
                terminal.putCharacter(' ');

                // move the player to new position
                terminal.setCursorPosition(playerX, playerY);
                terminal.setForegroundColor(TextColor.ANSI.CYAN);
                terminal.putCharacter(player.getPlayerIcon());

                // save new position of the player
                player.setX(playerX);
                player.setY(playerY);
            }
            terminal.flush();

            int monsterX, monsterY, prevMonsterX, prevMonsterY;

/*            // initial position of the monster
            monsterX = monsters.get(0).getX();
            monsterY = monsters.get(0).getY(); */

            for (int j=0 ; j< monsters.size() ; j++) {

                // get the current position of monsters
                monsterX = monsters.get(j).getX();
                monsterY = monsters.get(j).getY();
                System.out.println("monsterX = " + monsterX);
                System.out.println("monsterY = " + monsterY);

                // save the current position of the monster before moving
                Position prevMonsterPos = new Position(monsterX, monsterY) ;
                monsters.set(j, prevMonsterPos);

                // fetch the previous position of the monsters
                prevMonsterX = monsters.get(j).getPrevX();
                prevMonsterY = monsters.get(j).getPrevY();
                System.out.println("prevMonsterX = " + prevMonsterX);
                System.out.println("prevMonsterY = " + prevMonsterY);

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
                terminal.setCursorPosition(prevMonsterX, prevMonsterY);
                terminal.putCharacter(' ');


                terminal.setCursorPosition(monsterX, monsterY);
                terminal.setForegroundColor(TextColor.ANSI.RED);
                terminal.putCharacter(monsters.get(j).getPlayerIcon());
                terminal.flush();

                // save the new position of monsters
                Position newMonsterPos = new Position(monsterX, monsterY,'Ö') ;
                monsters.set(j, newMonsterPos);
                System.out.println("monsterX = " + monsterX);
                System.out.println("monsterY = " + monsterY);

            }
          //  monsters.addAll(tempMonsters);
        }
    }

    // method to initiate the terminal
    private static Terminal initiateTerminal() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        return  terminal;
    }

    // method to set the player on the terminal
    private static void setPlayer(Terminal terminal, Position player) throws IOException {
        terminal.setCursorPosition(player.getX(), player.getY());
        terminal.setForegroundColor(TextColor.ANSI.CYAN);
        terminal.putCharacter(player.getPlayerIcon());
        terminal.flush();
    }

    // method to set the monsters on the terminal
    private static void setMonster(Terminal terminal, List<Position> monsters) throws IOException {
        for (int i=0 ; i<monsters.size() ; i++) {
            terminal.setCursorPosition(monsters.get(i).getX(), monsters.get(i).getY());
            terminal.setForegroundColor(TextColor.ANSI.RED);
            terminal.putCharacter(monsters.get(i).getPlayerIcon());
            terminal.flush();
        }
    }
}
