package com.gse23.dschielke;

public class CorruptedExifDataException extends Exception {
    public CorruptedExifDataException(String message) {
        super(message);
    }
    public CorruptedExifDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public CorruptedExifDataException(final Throwable cause) {
        super(cause);
    }
}
