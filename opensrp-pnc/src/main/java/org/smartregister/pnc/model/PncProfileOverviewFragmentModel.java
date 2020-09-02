package org.smartregister.pnc.model;


import android.support.annotation.NonNull;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.PncRegisterQueryProviderContract;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.utils.AppExecutors;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncProfileOverviewFragmentModel implements PncProfileOverviewFragmentContract.Model {

    private AppExecutors appExecutors;
    private PncRegisterQueryProviderContract pncRegisterQueryProvider;

    public PncProfileOverviewFragmentModel() {
        this.appExecutors = PncLibrary.getInstance().getAppExecutors();
    }

    @Override
    public void fetchPncOverviewDetails(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        appExecutors.diskIO().execute(() -> {
            HashMap<String, String> pncRegisterDetails = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findWithMedicInfoByBaseEntityId(baseEntityId);

            pncRegisterQueryProvider = getPncRegisterQueryProvider();

            String query = pncRegisterQueryProvider.mainSelectWhereIDsIn()
                    .replace("%s", "'" + baseEntityId + "'");

            HashMap<String, String> pncDetails = PncLibrary.getInstance().getPncRepository().getPncDetailsFromQueryProvider(query);

            if (pncRegisterDetails != null && pncDetails != null) {
                pncRegisterDetails.putAll(pncDetails);

            }

            appExecutors.mainThread().execute(() -> onFetchedCallback.onFetched(pncRegisterDetails != null ? pncRegisterDetails : new HashMap<>()));
        });
    }

    public PncRegisterQueryProviderContract getPncRegisterQueryProvider() {
        if (pncRegisterQueryProvider == null) {
            pncRegisterQueryProvider = ConfigurationInstancesHelper.newInstance(PncLibrary.getInstance().getPncConfiguration().getPncRegisterQueryProvider());
        }
        return pncRegisterQueryProvider;
    }
}
