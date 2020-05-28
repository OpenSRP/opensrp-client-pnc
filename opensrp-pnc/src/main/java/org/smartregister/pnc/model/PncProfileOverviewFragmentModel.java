package org.smartregister.pnc.model;


import androidx.annotation.NonNull;

import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.util.AppExecutors;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncProfileOverviewFragmentModel implements PncProfileOverviewFragmentContract.Model {

    public PncProfileOverviewFragmentModel() {
        AppExecutors appExecutors = new AppExecutors();
    }

    @Override
    public void fetchPncOverviewDetails(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        // Do nothing
    }
}
