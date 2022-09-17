package xyz.mauwh.featherchat.exception;

public final class DataEntityAccessException extends RuntimeException {
    public DataEntityAccessException(Throwable err) {
        super(err);
    }

    public DataEntityAccessException(String message, Throwable err) {
        super(message, err);
    }

    public DataEntityAccessException(String message) {
        super(message);
    }
}
