package org.acme.exception;

public abstract class DBSearchException extends RuntimeException {

    public DBSearchException() {
        super();
    }

    public DBSearchException(String message) {
        super(message);
    }

    public DBSearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBSearchException(Throwable cause) {
        super(cause);
    }
}
