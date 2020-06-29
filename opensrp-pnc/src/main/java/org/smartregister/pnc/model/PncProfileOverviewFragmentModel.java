package org.smartregister.pnc.model;


import androidx.annotation.NonNull;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.util.AppExecutors;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncProfileOverviewFragmentModel implements PncProfileOverviewFragmentContract.Model {

    private AppExecutors appExecutors;
    private HashMap<String, String> pncDetails = null;

    public PncProfileOverviewFragmentModel() {
        this.appExecutors = PncLibrary.getInstance().getAppExecutors();
    }

    @Override
    public void fetchPncOverviewDetails(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        appExecutors.diskIO().execute(() -> {
            pncDetails = new HashMap<>();
            pncDetails = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findByBaseEntityId(baseEntityId);

            appExecutors.mainThread().execute(() -> onFetchedCallback.onFetched(pncDetails));
        });
    }
}
