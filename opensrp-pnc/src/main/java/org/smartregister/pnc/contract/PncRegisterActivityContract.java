package org.smartregister.pnc.contract;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncPartialForm;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface PncRegisterActivityContract {

    interface View extends BaseRegisterContract.View {

        PncRegisterActivityContract.Presenter presenter();

        void startFormActivityFromFormName(String formName, String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String clientTable);

        void startFormActivityFromFormJson(@NonNull String entityId, @NonNull JSONObject jsonForm, @Nullable HashMap<String, String> parcelableData);
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void saveLanguage(String language);

        void saveForm(String jsonString, @NonNull RegisterParams registerParams);

        void savePncForm(String eventType, @Nullable Intent data);

        void startForm(@NonNull String formName, @Nullable String entityId, String metaData, @NonNull String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable);

        @NonNull
        PncRegisterActivityContract.Interactor createInteractor();
    }

    interface Model {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        List<PncEventClient> processRegistration(String jsonString, FormTag formTag);

        @Nullable
        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws JSONException;

        @Nullable
        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId, @Nullable HashMap<String, String> injectedValues) throws JSONException;

        String getInitials();

    }

    interface Interactor {

        void fetchSavedForm(final @NonNull String formType, final @NonNull String baseEntityId, final @Nullable String entityTable, @NonNull final PncRegisterActivityContract.InteractorCallBack interactorCallBack);

        void getNextUniqueId(Triple<String, String, String> triple, PncRegisterActivityContract.InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);

        void saveRegistration(List<PncEventClient> pncEventClientList, String jsonString, RegisterParams registerParams, PncRegisterActivityContract.InteractorCallBack callBack);

        void saveEvents(@NonNull List<Event> events, @NonNull InteractorCallBack callBack);
    }

    interface InteractorCallBack {

        void onNoUniqueId();

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onRegistrationSaved(boolean isEdit);

        void onEventSaved();

        void onFetchedSavedForm(@Nullable PncPartialForm pncPartialForm, @NonNull String caseId, @NonNull String formType, @Nullable String entityTable);

    }
}