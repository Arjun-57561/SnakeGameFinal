public class Snake {
    private final int TILE_SIZE = 25;
    private int[] x, y; // arrays to store the x and y coordinates of each part of the snake
    private int length;
    private char direction = 'R';

    // Constructor
    public Snake(int maxSize) throws GameInitializationException {
        try {
            if (maxSize <= 0) {
                throw new GameInitializationException("Snake size must be positive");
            }
            
            x = new int[maxSize];
            y = new int[maxSize];
            length = 3; // Initial length set to 3 for easier start
            
            // Initialize snake starting position
            for (int i = 0; i < length; i++) {
                x[i] = 300 - i * TILE_SIZE; // Each segment is offset to the left
                y[i] = 300;
            }
            
            direction = 'R'; // Start moving right
        } catch (NegativeArraySizeException e) {
            throw new GameInitializationException("Snake size cannot be negative: " + e.getMessage(), e);
        } catch (Exception e) {
            if (!(e instanceof GameInitializationException)) {
                throw new GameInitializationException("Error initializing snake: " + e.getMessage(), e);
            } else {
                throw e;
            }
        }
    }

    public void move() {
        try {
            // Move the body
            for (int i = length - 1; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }
            
            // Move the head based on direction
            switch (direction) {
                case 'U': y[0] -= TILE_SIZE; break;
                case 'D': y[0] += TILE_SIZE; break;
                case 'L': x[0] -= TILE_SIZE; break;
                case 'R': x[0] += TILE_SIZE; break;
                default: throw new InvalidMovementException("Invalid direction: " + direction);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Snake moved out of bounds: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during snake movement: " + e.getMessage());
        }
    }

    public void grow() {
        try {
            if (length < x.length - 1) {
                length++;
            } else {
                System.err.println("Snake has reached maximum length");
            }
        } catch (Exception e) {
            System.err.println("Error growing snake: " + e.getMessage());
        }
    }

    public void setDirection(char newDirection) throws InvalidMovementException {
        // Validate direction
        if ("UDLR".indexOf(newDirection) == -1) {
            throw new InvalidMovementException("Invalid direction: " + newDirection);
        }
        
        // Prevent 180-degree turns (which would cause immediate game over)
        boolean validDirectionChange = true;
        switch (newDirection) {
            case 'U': if (direction == 'D') validDirectionChange = false; break;
            case 'D': if (direction == 'U') validDirectionChange = false; break;
            case 'L': if (direction == 'R') validDirectionChange = false; break;
            case 'R': if (direction == 'L') validDirectionChange = false; break;
        }
        
        if (!validDirectionChange) {
            throw new InvalidMovementException("Cannot move in opposite direction");
        }
        
        this.direction = newDirection;
    }

    public int getLength() { return length; }
    public int[] getX() { return x; }
    public int[] getY() { return y; }
    public char getDirection() { return direction; }
}
