package org.smartregister.pnc.provider;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.pnc.config.PncRegisterQueryProviderContract;

public class PncRegisterQueryProviderTest extends PncRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        return null;
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        return new String[0];
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return "";
    }
}
