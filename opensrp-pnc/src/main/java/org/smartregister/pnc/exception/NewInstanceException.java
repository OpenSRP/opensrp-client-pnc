package org.smartregister.pnc.exception;

/**
 * Created by Syed Owais Ali - ekigamba@ona.io on 2019-11-29
 */

public class NewInstanceException extends RuntimeException {

    public NewInstanceException() {
    }

    public NewInstanceException(String message) {
        super(message);
    }
}
