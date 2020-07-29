package org.smartregister.pnc.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public FetchRegistrationDataTask(@NonNull WeakReference<Context> contextWeakReference, @NonNull OnTaskComplete onTaskComplete){
        this.contextWeakReference = contextWeakReference;
        this.onTaskComplete = onTaskComplete;
    }

    @Nullable
    protected String doInBackground(String... params) {
        Map<String, String> detailsMap = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findByBaseEntityId(params[0]);
        detailsMap.put(PncJsonFormUtils.OPENSRP_ID, detailsMap.get(PncConstants.KeyConstants.OPENSRP_ID));
        return PncReverseJsonFormUtils.prepareJsonEditPncRegistrationForm(detailsMap, Arrays.asList(PncJsonFormUtils.OPENSRP_ID, PncConstants.JsonFormKeyConstants.BHT_ID), contextWeakReference.get());
    }

    protected void onPostExecute(@Nullable String jsonForm) {
        onTaskComplete.onSuccess(jsonForm);
    }

    public interface OnTaskComplete {

        void onSuccess(@Nullable String jsonForm);
    }
}