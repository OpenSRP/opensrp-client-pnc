package org.smartregister.pnc.exception;


import androidx.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncCloseEventProcessException extends Exception {

    public PncCloseEventProcessException() {
        super("Could not process this Maternity Close Event");
    }

    public PncCloseEventProcessException(@NonNull String message) {
        super("Could not process this Maternity Close Event because " + message);
    }

}
