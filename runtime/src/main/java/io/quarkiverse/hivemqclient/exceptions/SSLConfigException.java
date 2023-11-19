package io.quarkiverse.hivemqclient.exceptions;

public class SSLConfigException extends RuntimeException {
    public SSLConfigException() {
        super();
    }

    public SSLConfigException(String message) {
        super(message);
    }

    public SSLConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public SSLConfigException(Throwable cause) {
        super(cause);
    }
}
