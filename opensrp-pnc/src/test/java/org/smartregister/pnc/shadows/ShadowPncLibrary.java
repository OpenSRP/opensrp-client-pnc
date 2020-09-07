package org.smartregister.pnc.shadows;


import androidx.annotation.NonNull;

import org.robolectric.annotation.Implements;
import org.smartregister.pnc.PncLibrary;

import java.util.Date;

@Implements(PncLibrary.class)
public class ShadowPncLibrary {

    public static long currentTime;
    public static boolean isTimeSetExplicitly;

    public static void setMockedTime(long time) {
        currentTime = time;
        isTimeSetExplicitly = true;
    }

    @NonNull
    protected Date getDateNow() {
        if (isTimeSetExplicitly) {
            return new Date(currentTime);
        } else {
            return new Date();
        }
    }
}