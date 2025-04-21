public interface GameInterface {
    void startGame() throws GameInitializationException;
    void move();
    void checkCollision();
    void checkFood();
    void gameOver();
}
