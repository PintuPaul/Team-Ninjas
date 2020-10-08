public class Position {
    private int x;
    private int y;
    private int prevX;
    private int prevY;
    private char playerIcon;

    public Position(int x, int y, int prevX, int prevY, char playerIcon) {
        this.x = x;
        this.y = y;
        this.prevX = prevX;
        this.prevY = prevY;
        this.playerIcon = playerIcon;
    }

    public Position(int x, int y, char playerIcon) {
        this.x = x;
        this.y = y;
        this.playerIcon = playerIcon;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPrevX() {
        return prevX;
    }

    public void setPrevX(int prevX) {
        this.prevX = prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public void setPrevY(int prevY) {
        this.prevY = prevY;
    }

    public char getPlayerIcon() {
        return playerIcon;
    }
}
