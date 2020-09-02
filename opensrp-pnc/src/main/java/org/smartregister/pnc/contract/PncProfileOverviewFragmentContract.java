package org.smartregister.pnc.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.domain.YamlConfigWrapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface PncProfileOverviewFragmentContract {

    interface View {

        @Nullable
        String getString(@StringRes int stringId);

        @Nullable
        CommonPersonObjectClient getActivityClientMap();

    }

    interface Presenter {

        void loadOverviewFacts(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        void loadOverviewDataAndDisplay(@NonNull HashMap<String, String> pncDetails, @NonNull final OnFinishedCallback onFinishedCallback);

        void setDataFromRegistration(@NonNull HashMap<String, String> pncDetails, @NonNull Facts facts);

        void setClient(@NonNull CommonPersonObjectClient client);

        @Nullable
        View getProfileView();

        @Nullable
        String getString(@StringRes int stringId);

        interface OnFinishedCallback {

            void onFinished(@NonNull Facts facts, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items);
        }
    }

    interface Model {

        void fetchPncOverviewDetails(@NonNull String baseEntityId, @NonNull OnFetchedCallback onFetchedCallback);

        interface OnFetchedCallback {

            void onFetched(@NonNull HashMap<String, String> maternityDetails);
        }
    }
}
