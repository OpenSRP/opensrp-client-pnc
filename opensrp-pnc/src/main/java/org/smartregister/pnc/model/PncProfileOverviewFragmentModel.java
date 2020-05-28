package org.smartregister.pnc.model;


import androidx.annotation.NonNull;

import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncProfileOverviewFragmentModel implements PncProfileOverviewFragmentContract.Model {

    @Override
    public void fetchPncOverviewDetails(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        // Do nothing
    }
}
