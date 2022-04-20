package spring.exception;

public class SeasonTicketNotFoundException extends RuntimeException {
    public SeasonTicketNotFoundException(String message)
    {
        super(message);
    }
}
