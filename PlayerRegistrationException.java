public class PlayerRegistrationException extends Exception {
    public PlayerRegistrationException(String message) {
        super(message);
    }

    public PlayerRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}