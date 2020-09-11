package org.smartregister.pnc.tasks;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncReverseJsonFormUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;

public class FetchRegistrationDataTask extends AsyncTask<String, Void, String> {

    private WeakReference<Context> contextWeakReference;
    private OnTaskComplete onTaskComplete;

    public FetchRegistrationDataTask(@NonNull WeakReference<Context> contextWeakReference, @NonNull OnTaskComplete onTaskComplete) {
        this.contextWeakReference = contextWeakReference;
        this.onTaskComplete = onTaskComplete;
    }

    @Nullable
    protected String doInBackground(String... params) {
        Map<String, String> clientMap = PncLibrary.getInstance().getPncRepository().getClientWithRegistrationDetails(params[0]);
        if (clientMap != null) {
            clientMap.put(PncJsonFormUtils.OPENSRP_ID, clientMap.get(PncConstants.KeyConstants.OPENSRP_ID));
            return PncReverseJsonFormUtils.prepareJsonEditPncRegistrationForm(clientMap, Arrays.asList(PncJsonFormUtils.OPENSRP_ID, PncConstants.JsonFormKeyConstants.BHT_ID, PncConstants.JsonFormKeyConstants.SEX), contextWeakReference.get());
        }
        return null;
    }

    protected void onPostExecute(@Nullable String jsonForm) {
        onTaskComplete.onSuccess(jsonForm);
    }

    public interface OnTaskComplete {

        void onSuccess(@Nullable String jsonForm);
    }
}