package org.smartregister.pnc.contract;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.listener.OnSendActionToFragment;
import org.smartregister.pnc.listener.OngoingTaskCompleteListener;
import org.smartregister.pnc.pojo.OngoingTask;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncOutcomeForm;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.view.contract.BaseProfileContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface PncProfileActivityContract {

    interface Presenter extends BaseProfileContract.Presenter {

        @Nullable
        PncProfileActivityContract.View getProfileView();

        void refreshProfileTopSection(@NonNull Map<String, String> client, String baseEntityId);

        void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient);

        void startFormActivity(@Nullable JSONObject form, @NonNull String caseId, @NonNull String entityTable);

        void savePncCloseForm(@NonNull String eventType, @Nullable Intent data);

        void saveUpdateRegistrationForm(@NonNull String jsonString, @NonNull RegisterParams registerParams);

        @Nullable
        PncEventClient processRegistration(@NonNull String jsonString, @NonNull FormTag formTag);

        void savePncForm(String eventType, @Nullable Intent data);

        void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId);

        boolean hasOngoingTask();

        OngoingTask getOngoingTask();

        boolean setOngoingTask(@NonNull OngoingTask ongoingTask);

        boolean removeOngoingTask(@NonNull OngoingTask ongoingTask);

        boolean addOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener);

        boolean removeOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener);
    }

    interface View extends BaseProfileContract.View {

        void setProfileName(@NonNull String fullName);

        void setProfileID(@NonNull String registerId);

        void setProfileAge(@NonNull String age);

        void setProfileGender(@NonNull String gender);

        void setProfileImage(@NonNull String baseEntityId);

        void openPncVisitForm();

        void openPncMedicInfoForm();

        void openPncCloseForm();

        void startFormActivity(String entityId, @NonNull JSONObject form, @NonNull HashMap<String, String> intentData);

        OnSendActionToFragment getActionListenerForVisitFragment();

        OnSendActionToFragment getActionListenerForProfileOverview();

        @Nullable
        String getString(@StringRes int resId);

        @NonNull
        Context getContext();

        void startActivityForResult(Intent intent, int requestCode);

        @Nullable
        CommonPersonObjectClient getClient();

        void setClient(@NonNull CommonPersonObjectClient client);

        void showMessage(@Nullable String text);

        void closeView();

    }

    interface Interactor {

        void fetchSavedDiagnosisAndTreatmentForm(@NonNull String baseEntityId, @NonNull String entityTable);

        void saveRegistration(@NonNull PncEventClient pncEventClient, @NonNull String jsonString, RegisterParams registerParams, @NonNull PncProfileActivityContract.InteractorCallBack callBack);

        @Nullable
        CommonPersonObjectClient retrieveUpdatedClient(@NonNull String baseEntityId);

        void saveEvents(@NonNull List<Event> events, @NonNull PncProfileActivityContract.InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallBack {

        void onRegistrationSaved(@Nullable CommonPersonObjectClient client, boolean isEdit);

        void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable PncOutcomeForm diagnosisAndTreatmentForm, @NonNull String caseId, @NonNull String entityTable);

        void onEventSaved();
    }
}