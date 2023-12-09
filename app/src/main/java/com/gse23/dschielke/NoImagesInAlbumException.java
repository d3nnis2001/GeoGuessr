package com.gse23.dschielke;


public class NoImagesInAlbumException extends Exception {
    public NoImagesInAlbumException(final String message) {
        super(message);
    }

    public NoImagesInAlbumException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public NoImagesInAlbumException(final Throwable cause) {
        super(cause);
    }
}
