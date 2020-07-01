package org.smartregister.pnc.interactor;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.PncRegisterQueryProviderContract;
import org.smartregister.pnc.contract.PncProfileActivityContract;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.utils.AppExecutors;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileInteractor implements PncProfileActivityContract.Interactor {

    private AppExecutors appExecutors;

    public PncProfileInteractor() {
        appExecutors = PncLibrary.getInstance().getAppExecutors();
    }

    @Override
    public void fetchSavedDiagnosisAndTreatmentForm(@NonNull final String baseEntityId, @NonNull final String entityTable) {
        // Do nothing
    }

    @Override
    public void saveRegistration(final @NonNull PncEventClient pncEventClient, final @NonNull String jsonString
            , final @NonNull RegisterParams registerParams, final @NonNull PncProfileActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(pncEventClient, jsonString, registerParams);
                final CommonPersonObjectClient client = retrieveUpdatedClient(pncEventClient.getEvent().getBaseEntityId());


                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(client, registerParams.isEditMode());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Nullable
    @Override
    public CommonPersonObjectClient retrieveUpdatedClient(@NonNull String baseEntityId) {
        PncRegisterQueryProviderContract queryProviderContract = ConfigurationInstancesHelper.newInstance(PncLibrary.getInstance().getPncConfiguration().getPncRegisterQueryProvider());
        String query = queryProviderContract.mainSelectWhereIDsIn();


        String joinedIds = "'" + baseEntityId + "'";
        query = query.replace("%s", joinedIds);

        CommonRepository commonRepository = PncLibrary.getInstance().context().commonrepository(PncDbConstants.Table.EC_CLIENT);
        Cursor cursor = commonRepository.rawCustomQueryForAdapter(query);

        if (cursor != null && cursor.moveToFirst()) {
            CommonPersonObject commonPersonObject = commonRepository.getCommonPersonObjectFromCursor(cursor);
            String name = commonPersonObject.getColumnmaps().get(PncDbConstants.KEY.FIRST_NAME)
                    + " " + commonPersonObject.getColumnmaps().get(PncDbConstants.KEY.LAST_NAME);
            CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(),
                    commonPersonObject.getDetails(), name);
            client.setColumnmaps(commonPersonObject.getDetails());

            return client;
        }

        return null;
    }

    private void saveRegistration(@NonNull PncEventClient pncEventClient, @NonNull String jsonString
            , @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();
            try {

                Client baseClient = pncEventClient.getClient();
                Event baseEvent = pncEventClient.getEvent();

                if (baseClient != null && params.isEditMode()) {
                    try {
                        PncJsonFormUtils.mergeAndSaveClient(baseClient);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

                String formSubmissionId = addEvent(params, baseEvent);

                if (formSubmissionId != null) {
                    currentFormSubmissionIds.add(formSubmissionId);
                }

                updateOpenSRPId(jsonString, params, baseClient);
                addImageLocation(jsonString, baseClient, baseEvent);
            } catch (Exception e) {
                Timber.e(e);
            }

            long lastSyncTimeStamp = PncLibrary.getInstance().context().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            PncLibrary.getInstance().getClientProcessorForJava().processClient(PncLibrary.getInstance().getEcSyncHelper().getEvents(currentFormSubmissionIds));
            PncLibrary.getInstance().context().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void addImageLocation(@NonNull String jsonString, @Nullable Client baseClient, @Nullable Event baseEvent) {
        if (baseClient != null || baseEvent != null) {
            String imageLocation = PncJsonFormUtils.getFieldValue(jsonString, PncConstants.KeyConstants.PHOTO);
            if (StringUtils.isNotBlank(imageLocation)) {
                PncJsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }

    private void updateOpenSRPId(@NonNull String jsonString, @NonNull RegisterParams params, @Nullable Client baseClient) {
        if (params.isEditMode() && baseClient != null) {
            // Unassign current OPENSRP ID
            try {
                String newOpenSRPId = baseClient.getIdentifier(PncJsonFormUtils.OPENSRP_ID).replace("-", "");
                String currentOpenSRPId = PncJsonFormUtils.getString(jsonString, PncJsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                if (!newOpenSRPId.equals(currentOpenSRPId)) {
                    //OPENSRP ID was changed
                    PncLibrary.getInstance().getUniqueIdRepository().open(currentOpenSRPId);
                }
            } catch (Exception e) {//might crash if M_ZEIR
                Timber.d(e);
            }
        }

    }

    @Nullable
    private String addEvent(RegisterParams params, Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(PncJsonFormUtils.gson.toJson(baseEvent));
            PncLibrary.getInstance().getEcSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            return eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString());
        }

        return null;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        // do nothing
    }
}
