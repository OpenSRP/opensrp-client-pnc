package org.smartregister.pnc.contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.pojo.PncVisitSummary;

import java.util.List;


/**
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 *
 */
public interface PncProfileVisitsFragmentContract {

    interface Presenter {

        @Nullable
        PncProfileVisitsFragmentContract.View getProfileView();

        void onDestroy(boolean isChangingConfiguration);

        void loadVisits(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        void loadPageCounter(@NonNull String baseEntityId);

        void populateWrapperDataAndFacts(@NonNull PncVisitSummary pncVisitSummary, @NonNull List<Pair<YamlConfigWrapper, Facts>> items);

        void onNextPageClicked();

        void onPreviousPageClicked();


        interface OnFinishedCallback {

            void onFinished(@NonNull PncVisitSummary pncVisitSummary, @NonNull List<Pair<YamlConfigWrapper, Facts>> items);
        }

        interface OnVisitsLoadedCallback {

            void onVisitsLoaded(@NonNull PncVisitSummary pncVisitSummary);
        }
    }

    interface View {

        String getString(@StringRes int resId);

        void showPageCountText(@NonNull String pageCounter);

        void showNextPageBtn(boolean show);

        void showPreviousPageBtn(boolean show);

        void displayVisits(@NonNull PncVisitSummary pncVisitSummary, @NonNull List<Pair<YamlConfigWrapper, Facts>> items);

        @Nullable
        String getClientBaseEntityId();

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit);

        void fetchVisits(@NonNull String baseEntityId, int pageNo, @NonNull Presenter.OnVisitsLoadedCallback onVisitsLoadedCallback);

        void fetchVisitsPageCount(@NonNull String baseEntityId, @NonNull OnFetchVisitsPageCountCallback onTotalVisitCountCallback);

        interface OnFetchVisitsPageCountCallback {

            void onFetchVisitsPageCount(int visitsPageCount);
        }
    }
}