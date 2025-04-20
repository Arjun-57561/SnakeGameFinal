import java.awt.*;

public class Snake {
    private final int TILE_SIZE = 25;
    private int[] x, y;
    private int length;
    private char direction = 'R';

    public Snake(int maxSize) {
        try {
            x = new int[maxSize];
            y = new int[maxSize];
            length = 3;
            x[0] = 300;
            y[0] = 300;
        } catch (NegativeArraySizeException e) {
            System.err.println("Snake size cannot be negative: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error initializing snake: " + e.getMessage());
        }
    }

    public void move() {
        try {
            for (int i = length; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }

            switch (direction) {
                case 'U': y[0] -= TILE_SIZE; break;
                case 'D': y[0] += TILE_SIZE; break;
                case 'L': x[0] -= TILE_SIZE; break;
                case 'R': x[0] += TILE_SIZE; break;
                default: throw new IllegalArgumentException("Invalid direction: " + direction);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Snake moved out of bounds: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during snake movement: " + e.getMessage());
        }
    }

    public void grow() {
        try {
            length++;
        } catch (Exception e) {
            System.err.println("Error growing snake: " + e.getMessage());
        }
    }

    public void setDirection(char newDirection) {
        try {
            if ("UDLR".indexOf(newDirection) == -1) {
                throw new IllegalArgumentException("Invalid direction: " + newDirection);
            }
            this.direction = newDirection;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    public int getLength() { return length; }
    public int[] getX() { return x; }
    public int[] getY() { return y; }
    public char getDirection() { return direction; }
}
