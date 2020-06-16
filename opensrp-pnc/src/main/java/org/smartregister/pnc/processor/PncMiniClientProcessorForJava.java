package org.smartregister.pnc.processor;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.exception.PncCloseEventProcessException;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.pojo.PncDetails;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncMiniClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {

    private HashSet<String> eventTypes = null;

    public PncMiniClientProcessorForJava(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        if (eventTypes == null) {
            eventTypes = new HashSet<>();
            eventTypes.add(PncConstants.EventTypeConstants.PNC_REGISTRATION);
            eventTypes.add(PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION);
            eventTypes.add(PncConstants.EventTypeConstants.PNC_OUTCOME);
            eventTypes.add(PncConstants.EventTypeConstants.PNC_CLOSE);
        }

        return eventTypes;
    }

    @Override
    public boolean canProcess(@NonNull String eventType) {
        return getEventTypes().contains(eventType);
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception {

        /*if (eventType.equals(PncConstants.EventTypeConstants.PNC_REGISTRATION)
                || eventType.equals(PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION)) {
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        } else if (eventType.equals(PncConstants.EventTypeConstants.PNC_CLOSE)) {
            if (eventClient.getClient() == null) {
                throw new PncCloseEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), PncConstants.EventTypeConstants.PNC_CLOSE));
            }
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            unsyncEvents.add(event);
        } else if (eventType.equals(PncConstants.EventTypeConstants.PNC_OUTCOME)) {
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            processPncOutcome(event);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        }*/


        Event event = eventClient.getEvent();

        String eventType = event.getEventType();

        if (eventType.equals(PncConstants.EventTypeConstants.PNC_REGISTRATION)
                || eventType.equals(PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION)) {
            ArrayList<EventClient> eventClients = new ArrayList<>();
            eventClients.add(eventClient);
            processClient(eventClients);

            //updateRegisterTypeColumn(event, "maternity");

            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues, true);

            PncRegistrationDetails pncDetails = new PncRegistrationDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
            pncDetails.setCreatedAt(new Date());

            PncLibrary.getInstance().getPncRegistrationDetailsRepository().saveOrUpdate(pncDetails);

//            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
//            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        } else if (eventType.equals(PncConstants.EventTypeConstants.PNC_CLOSE)) {
            if (eventClient.getClient() == null) {
                throw new PncCloseEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), PncConstants.EventTypeConstants.PNC_CLOSE));
            }
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            unsyncEvents.add(event);
        } else if (eventType.equals(PncConstants.EventTypeConstants.PNC_MEDIC_INFORMATION)) {

            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());

            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues, true);

            PncRegistrationDetails pncDetails = new PncRegistrationDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
            pncDetails.setCreatedAt(new Date());
        } else if (eventType.equals(PncConstants.EventTypeConstants.PNC_OUTCOME)) {
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            processPncOutcome(eventClient);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        }
    }

    private void processPncOutcome(@NonNull EventClient eventClient) {
        Event event = eventClient.getEvent();
        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);
        String strStillBorn = keyValues.get(PncConstants.JsonFormKeyConstants.BABIES_STILL_BORN_MAP);
        processStillBorn(strStillBorn, event);
        String strBabiesBorn = keyValues.get(PncConstants.JsonFormKeyConstants.BABIES_BORN_MAP);
        processBabiesBorn(strBabiesBorn, event);

        PncDetails maternityDetails = new PncDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
        maternityDetails.setCreatedAt(new Date());
        PncLibrary.getInstance().getPncOtherDetailsRepository().saveOrUpdate(maternityDetails);
    }

    private void processBabiesBorn(@Nullable String strBabiesBorn, @NonNull Event event) {
        if (StringUtils.isNotBlank(strBabiesBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strBabiesBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonChildObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncChild pncChild = new PncChild();
                    pncChild.setMotherBaseEntityId(event.getBaseEntityId());
                    pncChild.setDischargedAlive(jsonChildObject.optString("discharged_alive"));
                    pncChild.setChildRegistered(jsonChildObject.optString("child_registered"));
                    pncChild.setBirthRecordDate(jsonChildObject.optString("birth_record_date"));
                    pncChild.setFirstName(jsonChildObject.optString("baby_first_name"));
                    pncChild.setLastName(jsonChildObject.optString("baby_last_name"));
                    pncChild.setDob(jsonChildObject.optString("baby_dob"));
                    pncChild.setGender(jsonChildObject.optString("baby_gender"));
                    pncChild.setWeightEntered(jsonChildObject.optString("birth_weight_entered"));
                    pncChild.setWeight(jsonChildObject.optString("birth_weight_entered"));
                    pncChild.setHeightEntered(jsonChildObject.optString("birth_height_entered"));
                    pncChild.setApgar(jsonChildObject.optString("apgar"));
                    pncChild.setFirstCry(jsonChildObject.optString("baby_first_cry"));
                    pncChild.setComplications(jsonChildObject.optString("baby_complications"));
                    pncChild.setComplicationsOther(jsonChildObject.optString("baby_complications_other"));
                    pncChild.setCareMgt(jsonChildObject.optString("baby_care_mgt"));
                    pncChild.setCareMgtSpecify(jsonChildObject.optString("baby_care_mgt_specify"));
                    pncChild.setRefLocation(jsonChildObject.optString("baby_referral_location"));
                    pncChild.setBfFirstHour(jsonChildObject.optString("bf_first_hour"));
                    pncChild.setChildHivStatus(jsonChildObject.optString("child_hiv_status"));
                    pncChild.setNvpAdministration(jsonChildObject.optString("nvp_administration"));
                    pncChild.setEventDate(PncUtils.convertDate(event.getEventDate().toDate(), PncDbConstants.DATE_FORMAT));
                    PncLibrary.getInstance().getPncChildRepository().saveOrUpdate(pncChild);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void processStillBorn(@Nullable String strStillBorn, @NonNull Event event) {
        if (StringUtils.isNotBlank(strStillBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strStillBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonTestObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncChild maternityStillBorn = new PncChild();
                    maternityStillBorn.setMotherBaseEntityId(event.getBaseEntityId());
                    maternityStillBorn.setStillBirthCondition(jsonTestObject.optString("stillbirth_condition"));
                    maternityStillBorn.setEventDate(PncUtils.convertDate(event.getEventDate().toDate(), PncDbConstants.DATE_FORMAT));
                    PncLibrary.getInstance().getPncChildRepository().saveOrUpdate(maternityStillBorn);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues, boolean appendOnNewline) {
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);
                value = value != null ? value.trim() : value;

                if (!TextUtils.isEmpty(value)) {
                    if (appendOnNewline && keyValues.containsKey(key)) {
                        String currentValue = keyValues.get(key);
                        keyValues.put(key, value + "\n" + currentValue);
                    } else {
                        keyValues.put(key, value);
                    }
                    continue;
                }
            }

            List<Object> values = observation.getValues();
            if (values.size() > 0) {
                String value = (String) values.get(0);
                value = value != null ? value.trim() : value;

                if (!TextUtils.isEmpty(value)) {
                    if (appendOnNewline && keyValues.containsKey(key)) {
                        String currentValue = keyValues.get(key);
                        keyValues.put(key, value + "\n" + currentValue);
                    } else {
                        keyValues.put(key, value);
                    }
                }
            }
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues) {
        generateKeyValuesFromEvent(event, keyValues, false);
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        // Do nothing for now
        /*if (events != null) {
            for (Event event : events) {
                if (PncConstants.EventType.PNC_CLOSE.equals(event.getEventType())) {
                    // Delete the pnc details
                    // PncLibrary.getInstance().getPncOtherDetailsRepository().delete(event.getBaseEntityId());

                    // Delete the actual client in the pnc table OR REMOVE THE Pnc register type
                    //updateRegisterTypeColumn(event, null);
                }
            }
        }*/
        return true;
    }
}