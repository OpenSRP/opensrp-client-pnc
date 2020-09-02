package org.smartregister.pnc.exception;


import android.support.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncCloseEventProcessException extends Exception {

    public PncCloseEventProcessException() {
        super("Could not process this Pnc Close Event");
    }

    public PncCloseEventProcessException(@NonNull String message) {
        super("Could not process this Pnc Close Event because " + message);
    }

}
