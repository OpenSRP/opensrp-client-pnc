package org.smartregister.pnc.sample.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.interactor.BasePncRegisterActivityInteractor;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class PncRegisterActivityInteractor extends BasePncRegisterActivityInteractor {

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, @NonNull final PncRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final List<PncEventClient> pncEventClientList, final String jsonString
            , final RegisterParams registerParams, @NonNull final PncRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(pncEventClientList, jsonString, registerParams);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(registerParams.isEditMode());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void saveRegistration(@NonNull List<PncEventClient> pncEventClients, @NonNull String jsonString,
                                  @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (int i = 0; i < pncEventClients.size(); i++) {
                try {

                    PncEventClient pncEventClient = pncEventClients.get(i);
                    Client baseClient = pncEventClient.getClient();
                    Event baseEvent = pncEventClient.getEvent();

                    if (baseClient != null) {
                        JSONObject clientJson = new JSONObject(PncJsonFormUtils.gson.toJson(baseClient));
                        if (params.isEditMode()) {
                            try {
                                PncJsonFormUtils.mergeAndSaveClient(baseClient);
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        } else {
                            getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                        }
                    }

                    addEvent(params, currentFormSubmissionIds, baseEvent);
                    updateOpenSRPId(jsonString, params, baseClient);
                    addImageLocation(jsonString, i, baseClient, baseEvent);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
    private void addImageLocation(String jsonString, int i, Client baseClient, Event baseEvent) {
        if (baseClient != null && baseEvent != null) {
            String imageLocation = null;
            if (i == 0) {
                imageLocation = PncJsonFormUtils.getFieldValue(jsonString, PncConstants.KeyConstants.PHOTO);
            } else if (i == 1) {
                imageLocation =
                        PncJsonFormUtils.getFieldValue(jsonString, PncJsonFormUtils.STEP2, PncConstants.KeyConstants.PHOTO);
            }

            if (StringUtils.isNotBlank(imageLocation)) {
                PncJsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }

    private void updateOpenSRPId(@NonNull String jsonString, @NonNull RegisterParams params, @Nullable Client baseClient) {
        if (params.isEditMode()) {
            // Unassign current OPENSRP ID
            if (baseClient != null) {
                try {
                    String newOpenSRPId = baseClient.getIdentifier(PncJsonFormUtils.OPENSRP_ID).replace("-", "");
                    String currentOpenSRPId = PncJsonFormUtils.getString(jsonString, PncJsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
                        //OPENSRP ID was changed
                        getUniqueIdRepository().open(currentOpenSRPId);
                    }
                } catch (Exception e) {//might crash if M_ZEIR
                    Timber.d(e, "RegisterInteractor --> unassign opensrp id");
                }
            }

        } else {
            if (baseClient != null) {
                String opensrpId = baseClient.getIdentifier(PncJsonFormUtils.ZEIR_ID);
                //mark OPENSRP ID as used
                getUniqueIdRepository().close(opensrpId);
            }
        }
    }

    private void addEvent(RegisterParams params, List<String> currentFormSubmissionIds, @Nullable Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(PncJsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            currentFormSubmissionIds
                    .add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
        }
    }
}
