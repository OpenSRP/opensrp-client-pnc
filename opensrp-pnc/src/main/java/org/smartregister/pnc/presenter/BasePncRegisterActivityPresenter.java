package org.smartregister.pnc.presenter;


import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.interactor.BasePncRegisterActivityInteractor;
import org.smartregister.pnc.pojo.PncOutcomeForm;
import org.smartregister.pnc.utils.PncConstants;

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
    public void saveOutcomeForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        if (data != null) {
            jsonString = data.getStringExtra(PncConstants.JsonFormExtraConstants.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(PncConstants.EventTypeConstants.MATERNITY_OUTCOME)) {
            try {
                List<Event> maternityOutcomeAndCloseEvent = PncLibrary.getInstance().processPncOutcomeForm(eventType, jsonString, data);
                interactor.saveEvents(maternityOutcomeAndCloseEvent, this);
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

        form = null;
        try {
            form = model.getFormAsJson(formName, entityId, locationId, injectedFieldValues);
            // Todo: Enquire if we have to save a session of the outcome form to be continued later
            if (formName.equals(PncConstants.Form.MATERNITY_OUTCOME)) {
                interactor.fetchSavedMaternityOutcomeForm(entityId, entityTable, this);
                return;
            }

        } catch (JSONException e) {
            Timber.e(e);
        }

        // The form will be started directly for forms that do not have saved sessions
        startFormActivity(entityId, entityTable, form);
    }

    @Override
    public void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable PncOutcomeForm diagnosisAndTreatmentForm, @NonNull String caseId, @Nullable String entityTable) {
        try {
            if (diagnosisAndTreatmentForm != null) {
                form = new JSONObject(diagnosisAndTreatmentForm.getForm());
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

            getView().startFormActivityFromFormJson(form, intentKeys);
        }
    }

    @Override
    public void onUniqueIdFetched(@NonNull Triple<String, String, String> triple, @NonNull String entityId) {
        startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), null, null);
    }
}