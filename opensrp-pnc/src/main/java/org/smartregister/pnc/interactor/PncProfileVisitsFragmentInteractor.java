package org.smartregister.pnc.interactor;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileVisitsFragmentContract;
import org.smartregister.util.AppExecutors;

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
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                /*final List<OpdVisitSummary> summaries = PncLibrary.getInstance().getOpdVisitSummaryRepository().getOpdVisitSummaries(baseEntityId, pageNo);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onVisitsLoadedCallback.onVisitsLoaded(summaries);
                    }
                });*/
            }
        });
    }

    @Override
    public void fetchVisitsPageCount(@NonNull final String baseEntityId, @NonNull final OnFetchVisitsPageCountCallback onFetchVisitsPageCountCallback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                /*final int visitsPageCount = PncLibrary.getInstance().getOpdVisitSummaryRepository().getVisitPageCount(baseEntityId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onFetchVisitsPageCountCallback.onFetchVisitsPageCount(visitsPageCount);
                    }
                });*/
            }
        });
    }

    @Nullable
    public PncProfileVisitsFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}