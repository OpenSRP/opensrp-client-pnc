package org.smartregister.pnc.interactor;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileVisitsFragmentContract;
import org.smartregister.pnc.utils.AppExecutors;

import java.util.List;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileVisitsFragmentInteractor implements PncProfileVisitsFragmentContract.Interactor {

    private PncProfileVisitsFragmentContract.Presenter mProfileFrgamentPresenter;
    private AppExecutors appExecutors;

    public PncProfileVisitsFragmentInteractor(@NonNull PncProfileVisitsFragmentContract.Presenter presenter) {
        this.mProfileFrgamentPresenter = presenter;
        appExecutors = PncLibrary.getInstance().getAppExecutors();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfileFrgamentPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit) {
        // Todo: We will have an implementation for refresh view
    }

    @Override
    public void fetchVisits(@NonNull final String baseEntityId, final int pageNo, @NonNull final PncProfileVisitsFragmentContract.Presenter.OnVisitsLoadedCallback onVisitsLoadedCallback) {
        appExecutors.diskIO().execute(() -> {
            final List<Map<String, Object>> summaries = PncLibrary.getInstance().getPncVisitInfoRepository().getPncVisitSummaries(baseEntityId, pageNo);

            appExecutors.mainThread().execute(() -> onVisitsLoadedCallback.onVisitsLoaded(summaries));
        });
    }

    @Override
    public void fetchVisitsPageCount(@NonNull final String baseEntityId, @NonNull final OnFetchVisitsPageCountCallback onFetchVisitsPageCountCallback) {
        appExecutors.diskIO().execute(() -> {
            final int visitsPageCount = PncLibrary.getInstance().getPncVisitInfoRepository().getVisitPageCount(baseEntityId);

            appExecutors.mainThread().execute(() -> onFetchVisitsPageCountCallback.onFetchVisitsPageCount(visitsPageCount));
        });
    }

    @Nullable
    public PncProfileVisitsFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}