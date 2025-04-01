import java.awt.*;

public class Snake {
    private final int TILE_SIZE = 25;
    private int[] x, y;
    private int length;
    private char direction = 'R';

    public Snake(int maxSize) {
        x = new int[maxSize];
        y = new int[maxSize];
        length = 3;
        x[0] = 300;
        y[0] = 300;
    }

    public void move() {
        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U': y[0] -= TILE_SIZE; break;
            case 'D': y[0] += TILE_SIZE; break;
            case 'L': x[0] -= TILE_SIZE; break;
            case 'R': x[0] += TILE_SIZE; break;
        }
    }

    public void grow() { length++; }
    public void setDirection(char newDirection) { this.direction = newDirection; }
    public int getLength() { return length; }
    public int[] getX() { return x; }
    public int[] getY() { return y; }
    public char getDirection() { return direction; }
}

