package pl.luczak.michal.loginandsignup;

class UserAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE = "User with username: {username} already exists";

    public UserAlreadyExistsException(String username) {
        super(MESSAGE.replace("{username}", username));
    }
}
