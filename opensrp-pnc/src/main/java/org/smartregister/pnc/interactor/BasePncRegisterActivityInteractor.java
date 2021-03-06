package org.smartregister.pnc.interactor;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncPartialForm;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.utils.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class BasePncRegisterActivityInteractor implements PncRegisterActivityContract.Interactor {

    protected AppExecutors appExecutors;

    public BasePncRegisterActivityInteractor() {
        this(PncLibrary.getInstance().getAppExecutors());
    }

    @VisibleForTesting
    BasePncRegisterActivityInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void fetchSavedForm(final @NonNull String formType, final @NonNull String baseEntityId, final @Nullable String entityTable, @NonNull final PncRegisterActivityContract.InteractorCallBack interactorCallBack) {
        appExecutors.diskIO().execute(() -> {
            final PncPartialForm partialForm = PncLibrary
                    .getInstance()
                    .getPncPartialFormRepository()
                    .findOne(new PncPartialForm(baseEntityId, formType));

            appExecutors.mainThread().execute(() -> interactorCallBack.onFetchedSavedForm(partialForm, baseEntityId, formType, entityTable));
        });
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final PncRegisterActivityContract.InteractorCallBack callBack) {
        // Do nothing for now, this will be handled by the class that extends this
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        // Do nothing for now, this will be handled by the class that extends this to nullify the presenter
    }

    @Override
    public void saveRegistration(List<PncEventClient> pncEventClientList, String jsonString, RegisterParams registerParams, PncRegisterActivityContract.InteractorCallBack callBack) {
        // Do nothing for now, this will be handled by the class that extends this
    }

    @Override
    public void saveEvents(@NonNull final List<Event> events, @NonNull final PncRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<String> formSubmissionIds = new ArrayList<>();
                for (Event event : events) {
                    formSubmissionIds.add(event.getFormSubmissionId());
                    saveEventInDb(event);
                }

                processLatestUnprocessedEvents(formSubmissionIds);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onEventSaved();
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }


    private void saveEventInDb(@NonNull Event event) {
        try {
            CoreLibrary.getInstance()
                    .context()
                    .getEventClientRepository()
                    .addEvent(event.getBaseEntityId()
                            , new JSONObject(JsonFormUtils.gson.toJson(event)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void processLatestUnprocessedEvents(List<String> formSubmissionIds) {
        // Process this event
        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);

        try {
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(formSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @NonNull
    public ECSyncHelper getSyncHelper() {
        return PncLibrary.getInstance().getEcSyncHelper();
    }

    @NonNull
    public AllSharedPreferences getAllSharedPreferences() {
        return PncLibrary.getInstance().context().allSharedPreferences();
    }

    @NonNull
    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        return PncLibrary.getInstance().getUniqueIdRepository();
    }

}