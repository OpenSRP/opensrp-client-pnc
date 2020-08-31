package org.smartregister.pnc.presenter;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.interactor.BasePncRegisterActivityInteractor;
import org.smartregister.pnc.pojo.PncPartialForm;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public abstract class BasePncRegisterActivityPresenter implements PncRegisterActivityContract.Presenter, PncRegisterActivityContract.InteractorCallBack {

    protected WeakReference<PncRegisterActivityContract.View> viewReference;
    protected PncRegisterActivityContract.Interactor interactor;
    protected PncRegisterActivityContract.Model model;
    private JSONObject form;

    public BasePncRegisterActivityPresenter(@NonNull PncRegisterActivityContract.View view, @NonNull PncRegisterActivityContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = createInteractor();
        this.model = model;
    }

    @NonNull
    @Override
    public PncRegisterActivityContract.Interactor createInteractor() {
        return new BasePncRegisterActivityInteractor();
    }

    public void setModel(PncRegisterActivityContract.Model model) {
        this.model = model;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy\
        if (!isChangingConfiguration) {
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null && getView() != null) {
            getView().updateInitialsText(initials);
        }
    }

    @Nullable
    protected PncRegisterActivityContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);

        if (getView() != null) {
            getView().displayToast(language + " selected");
        }
    }

    @Override
    public void savePncForm(String eventType, @Nullable Intent data) {
        String jsonString = null;
        if (data != null) {
            jsonString = data.getStringExtra(PncConstants.JsonFormExtraConstants.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(PncConstants.EventType.PNC_MEDIC_INFO) || eventType.equals(PncConstants.EventType.PNC_VISIT)) {
            try {
                List<Event> pncFormEvent = PncLibrary.getInstance().processPncForm(eventType, jsonString, data);
                interactor.saveEvents(pncFormEvent, this);
                PncLibrary.getInstance().getAppExecutors().diskIO().execute(() -> PncLibrary.getInstance().getPncPartialFormRepository().delete(new PncPartialForm(PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID), eventType)));
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void onEventSaved() {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    @Override
    public void startForm(@NonNull String formName, @Nullable String entityId, String metaData
            , @NonNull String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {

        // Todo: Refactor this method to only start the form and move the logic for getting a unique id to another method
        // We are also sure
        if (StringUtils.isBlank(entityId)) {
            //Todo: Check if this metadata is usually null OR can be null at
            Triple<String, String, String> triple = Triple.of(formName, metaData, locationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }


        try {

            form = model.getFormAsJson(formName, entityId, locationId, injectedFieldValues);

            if (formName.equals(PncConstants.Form.PNC_MEDIC_INFO) || formName.equals(PncConstants.Form.PNC_VISIT)) {
                interactor.fetchSavedForm(form.optString(JsonFormConstants.ENCOUNTER_TYPE), entityId, entityTable, this);
                return;
            }

        } catch (JSONException e) {
            Timber.e(e);
        }

        // The form will be started directly for forms that do not have saved sessions
        startFormActivity(entityId, entityTable, form);
    }

    @Override
    public void onFetchedSavedForm(@Nullable PncPartialForm pncPartialForm, @NonNull String caseId, @NonNull String formType, @Nullable String entityTable) {
        try {
            if (pncPartialForm != null) {
                form = new JSONObject(pncPartialForm.getForm());
            }

            startFormActivity(caseId, entityTable, form);
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    private void startFormActivity(@NonNull String entityId, @Nullable String entityTable, @Nullable JSONObject form) {
        if (getView() != null && form != null) {
            HashMap<String, String> intentKeys = new HashMap<>();
            intentKeys.put(PncConstants.IntentKey.BASE_ENTITY_ID, entityId);
            intentKeys.put(PncConstants.IntentKey.ENTITY_TABLE, entityTable);


            getView().startFormActivityFromFormJson(entityId, form, intentKeys);
        }
    }

    @Override
    public void onUniqueIdFetched(@NonNull Triple<String, String, String> triple, @NonNull String entityId) {
        startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), null, null);
    }
}