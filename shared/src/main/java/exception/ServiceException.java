package exception;

public class ServiceException extends Exception {
    int statusCode;

    public ServiceException (int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
