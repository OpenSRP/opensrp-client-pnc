package org.smartregister.pnc.contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.pojo.PncBaseDetails;

import java.util.List;

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

        void loadOverviewDataAndDisplay(@NonNull PncBaseDetails maternityDetails, @NonNull final OnFinishedCallback onFinishedCallback);

        void setDataFromRegistration(@NonNull PncBaseDetails maternityDetails, @NonNull Facts facts);

        void setClient(@NonNull CommonPersonObjectClient client);

        @Nullable
        View getProfileView();

        @Nullable
        String getString(@StringRes int stringId);

        interface OnFinishedCallback {

            void onFinished(@Nullable Facts facts, @Nullable List<YamlConfigWrapper> yamlConfigListGlobal);
        }
    }

    interface Model {

        void fetchMaternityOverviewDetails(@NonNull String baseEntityId, @NonNull OnFetchedCallback onFetchedCallback);

        interface OnFetchedCallback {

            void onFetched(@NonNull PncBaseDetails maternityDetails);
        }
    }
}
