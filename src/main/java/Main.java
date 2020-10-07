import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        // initiate terminal
        Terminal terminal = initateTerminal();


        Position positionPlayer = new Position(5, 5);
        int playerX = positionPlayer.getX();
        int playerY = positionPlayer.getY();
        final char player = 'X';

        terminal.setCursorPosition(positionPlayer.getX(), positionPlayer.getY());
        terminal.setForegroundColor(TextColor.ANSI.CYAN);
        terminal.putCharacter(player);
        terminal.flush();

        Position[] monsterPos = new Position[3];
        final char monster = 'Ö';
        int x = 6;
        int y = 1;
        for (int i = 0; i < monsterPos.length; i++) {
            monsterPos[i] = new Position(x, y);
            terminal.setCursorPosition(monsterPos[i].getX(), monsterPos[i].getY());
            terminal.setForegroundColor(TextColor.ANSI.RED);
            terminal.putCharacter(monster);
            x = x + 5;
            y = y + 5;
        }
        terminal.flush();

/*        Random randomX = new Random(5);
        Random randomY = new Random(5);
        Position[] points = new Position[3];
        int p,q ;
        for (int i=0; i< points.length ; i++) {
            p = randomX.nextInt() ;
            q = randomY.nextInt() ;
            terminal.setCursorPosition(p,q);
            terminal.setForegroundColor(TextColor.ANSI.GREEN);
            terminal.putCharacter('Ä');
            points[i] = new Position(p,q);
        }
        terminal.flush();*/

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

            int prevX = playerX;
            int prevY = playerY;

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

       /*     for (Position point : points) {
                if (point.getX() == prevX && point.getY() == prevY) {
                    playerX +=5 ;
                }
            }*/


            boolean crashIntoObsticle = false;
            for (Position ps : monsterPos) {
                System.out.println("Player : " + playerX + "," + playerY);
                System.out.println("ps.get() = " + ps.getX() + "," + ps.getY());
                if (ps.getX() == prevX && ps.getY() == prevY) {
                    crashIntoObsticle = true;
                }
            }

            if (crashIntoObsticle) {
                continueReadingInput = false;
                System.out.println("quit");
                terminal.close();
            } else {
                terminal.setCursorPosition(prevX, prevY);
                terminal.putCharacter(' ');

                terminal.setCursorPosition(playerX, playerY);
                terminal.setForegroundColor(TextColor.ANSI.CYAN);
                terminal.putCharacter(player);
            }
            terminal.flush();

            int monsterX, monsterY, prevMonsterX, prevMonsterY, pos = 0;
            Position[] tempPos = new Position[3];

            for (Position ps : monsterPos) {
                monsterY = ps.getY();
                monsterX = ps.getX();
                prevMonsterX = monsterX;
                prevMonsterY = monsterY;

                if (ps.getX() < playerX) {
                    monsterX = ++monsterX;
                }
                if (ps.getY() < playerY) {
                    monsterY = ++monsterY;
                }
                if (ps.getX() > playerX) {
                    monsterX = --monsterX;
                }
                if (ps.getY() > playerY) {
                    monsterY = --monsterY;
                }
                terminal.setCursorPosition(prevMonsterX, prevMonsterY);
                terminal.putCharacter(' ');


                terminal.setCursorPosition(monsterX, monsterY);
                terminal.setForegroundColor(TextColor.ANSI.RED);
                terminal.putCharacter(monster);
                terminal.flush();

                tempPos[pos] = new Position(monsterX, monsterY);
                pos++;
            }
            monsterPos = tempPos;
        }
    }
    private static Terminal initateTerminal() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        return  terminal;
    }
}
