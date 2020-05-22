package org.smartregister.pnc.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.pnc.config.PncRegisterQueryProviderContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterQueryProviderTest extends PncRegisterQueryProviderContract {

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
