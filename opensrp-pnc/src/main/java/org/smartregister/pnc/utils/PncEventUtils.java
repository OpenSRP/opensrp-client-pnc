package org.smartregister.pnc.utils;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.listener.PncEventActionCallBack;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.util.Utils.getAllSharedPreferences;

public class PncEventUtils {

    private AppExecutors appExecutors;
    private PncLibrary pncLibrary;

    public PncEventUtils() {
        this.appExecutors = PncLibrary.getInstance().getAppExecutors();
        this.pncLibrary = PncLibrary.getInstance();
    }

    public void saveEvents(@NonNull final List<Event> events, @NonNull final PncEventActionCallBack callBack) {
        Runnable runnable = () -> {
            List<String> formSubmissionIds = new ArrayList<>();
            for (Event event : events) {
                formSubmissionIds.add(event.getFormSubmissionId());
                saveEventInDb(event);
            }
            processLatestUnprocessedEvents(formSubmissionIds);
            appExecutors.mainThread().execute(() -> callBack.onPncEventSaved());
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
        try {
            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            pncLibrary.getClientProcessorForJava().processClient(pncLibrary.getEcSyncHelper().getEvents(formSubmissionIds));
            PncUtils.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
