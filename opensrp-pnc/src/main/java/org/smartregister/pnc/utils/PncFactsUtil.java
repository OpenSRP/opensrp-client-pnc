package org.smartregister.pnc.utils;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jeasy.rules.api.Facts;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-11
 */

public class PncFactsUtil {

    public static void putNonNullFact(@NonNull Facts facts, @NonNull String factKey, @Nullable Object factValue) {
        if (factValue != null) {
            facts.put(factKey, factValue);
        }
    }
}
