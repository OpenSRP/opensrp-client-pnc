package org.smartregister.pnc.model;


import android.support.annotation.NonNull;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.utils.AppExecutors;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncProfileOverviewFragmentModel implements PncProfileOverviewFragmentContract.Model {

    private AppExecutors appExecutors;
    private HashMap<String, String> pncMedicInfo = null;

    public PncProfileOverviewFragmentModel() {
        this.appExecutors = PncLibrary.getInstance().getAppExecutors();
    }

    @Override
    public void fetchPncOverviewDetails(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        appExecutors.diskIO().execute(() -> {
            pncMedicInfo = new HashMap<>();
            pncMedicInfo = PncLibrary.getInstance().getPncMedicInfoRepository().findByBaseEntityId(baseEntityId);

            HashMap<String, String> pncRegisterDetails = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findByBaseEntityId(baseEntityId);

            if (pncRegisterDetails != null) {
                pncMedicInfo.putAll(pncRegisterDetails);
            }
            appExecutors.mainThread().execute(() -> onFetchedCallback.onFetched(pncMedicInfo));
        });
    }
}
