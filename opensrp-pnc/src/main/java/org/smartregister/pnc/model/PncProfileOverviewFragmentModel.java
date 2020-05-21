package org.smartregister.pnc.model;


import androidx.annotation.NonNull;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.pojo.PncBaseDetails;
import org.smartregister.util.AppExecutors;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncProfileOverviewFragmentModel implements PncProfileOverviewFragmentContract.Model {

    private AppExecutors appExecutors;
    private PncBaseDetails maternityDetails = null;

    public PncProfileOverviewFragmentModel() {
        this.appExecutors = new AppExecutors();
    }

    @Override
    public void fetchMaternityOverviewDetails(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        appExecutors.diskIO().execute(new Runnable() {

            @Override
            public void run() {
                maternityDetails = new PncBaseDetails();
                maternityDetails.setBaseEntityId(baseEntityId);
                maternityDetails = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findOne(maternityDetails);

                appExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        onFetchedCallback.onFetched(maternityDetails);
                    }
                });
            }
        });
    }
}
