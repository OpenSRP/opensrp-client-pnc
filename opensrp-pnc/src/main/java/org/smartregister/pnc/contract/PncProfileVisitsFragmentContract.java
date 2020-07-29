package org.smartregister.pnc.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.pnc.domain.YamlConfigWrapper;

import java.util.List;
import java.util.Map;


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

        void populateWrapperDataAndFacts(@NonNull List<Map<String, Object>> ancVisitSummaries, @NonNull List<Pair<YamlConfigWrapper, Facts>> items);

        void onNextPageClicked();

        void onPreviousPageClicked();


        interface OnFinishedCallback {

            void onFinished(@NonNull List<Map<String, Object>> ancVisitSummaries, @NonNull List<Pair<YamlConfigWrapper, Facts>> items);
        }

        interface OnVisitsLoadedCallback {

            void onVisitsLoaded(@NonNull List<Map<String, Object>> pncVisitSummaries);
        }
    }

    interface View {

        String getString(@StringRes int resId);

        void showPageCountText(@NonNull String pageCounter);

        void showNextPageBtn(boolean show);

        void showPreviousPageBtn(boolean show);

        void displayVisits(@NonNull List<Map<String, Object>> ancVisitSummaries, @NonNull List<Pair<YamlConfigWrapper, Facts>> items);

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