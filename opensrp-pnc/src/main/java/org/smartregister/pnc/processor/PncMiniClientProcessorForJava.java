package org.smartregister.pnc.processor;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.exception.PncCloseEventProcessException;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.pojo.PncOtherVisit;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.pojo.PncStillBorn;
import org.smartregister.pnc.repository.PncMedicInfoRepository;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.formatDate;

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
            eventTypes.add(PncConstants.EventTypeConstants.PNC_MEDIC_INFO);
            eventTypes.add(PncConstants.EventTypeConstants.PNC_CLOSE);
            eventTypes.add(PncConstants.EventTypeConstants.PNC_VISIT);
            eventTypes.add(PncConstants.EventTypeConstants.DEATH);
        }

        return eventTypes;
    }

    @Override
    public boolean canProcess(@NonNull String eventType) {
        return getEventTypes().contains(eventType);
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception {

        Event event = eventClient.getEvent();
        String eventType = event.getEventType();

        switch (eventType) {
            case PncConstants.EventTypeConstants.PNC_REGISTRATION:
            case PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION:
                ArrayList<EventClient> eventClients = new ArrayList<>();
                eventClients.add(eventClient);
                processClient(eventClients);

                HashMap<String, String> keyValues = new HashMap<>();
                generateKeyValuesFromEvent(event, keyValues, true);

                PncRegistrationDetails pncDetails = new PncRegistrationDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
                pncDetails.setCreatedAt(new Date());

                PncLibrary.getInstance().getPncRegistrationDetailsRepository().saveOrUpdate(pncDetails);

                processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                break;
            case PncConstants.EventTypeConstants.PNC_CLOSE:
                if (eventClient.getClient() == null) {
                    throw new PncCloseEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), PncConstants.EventTypeConstants.PNC_CLOSE));
                }
                processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                unsyncEvents.add(event);
                break;
            case PncConstants.EventTypeConstants.PNC_MEDIC_INFO:
                processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                processPncMedicInfo(eventClient);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                break;
            case PncConstants.EventTypeConstants.PNC_VISIT:
                processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                processPncVisit(eventClient);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                break;
            case PncConstants.EventTypeConstants.DEATH:
                processDeathEvent(eventClient);
                processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                unsyncEvents.add(event);
                break;
            default:
                break;
        }
    }

    private void processDeathEvent(@NonNull EventClient eventClient) {
        Event event = eventClient.getEvent();
        String entityId = event.getBaseEntityId();

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues, true);

        String encounterDateField = keyValues.get("date_of_death");

        ContentValues values = new ContentValues();
        values.put(PncConstants.KeyConstants.DOD, encounterDateField);
        values.put(PncConstants.KeyConstants.DATE_REMOVED, PncUtils.getTodaysDate());

        //Update REGISTER and FTS Tables
        AllCommonsRepository allCommonsRepository = PncLibrary.getInstance().context().allCommonsRepositoryobjects(PncDbConstants.Table.EC_CLIENT);
        if (allCommonsRepository != null) {
            allCommonsRepository.update(PncDbConstants.Table.EC_CLIENT, values, entityId);
            PncLibrary.getInstance().context().allCommonsRepositoryobjects(PncDbConstants.Table.EC_CLIENT).updateSearch(Arrays.asList(new String[]{entityId}));
        }

        // update date of removal
        ContentValues contentValues = new ContentValues();

        String encounterDateTimeString = null;
        if (StringUtils.isNotBlank(encounterDateField)) {
            try {
                encounterDateTimeString = formatDate(encounterDateField);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (encounterDateTimeString != null) {
            contentValues.put(PncConstants.KeyConstants.DATE_REMOVED, encounterDateTimeString);
        }

        PncLibrary.getInstance().context().getEventClientRepository().getWritableDatabase().update(PncDbConstants.Table.EC_CLIENT, contentValues, PncConstants.KeyConstants.BASE_ENTITY_ID + " = ?", new String[]{entityId});
    }

    private void processPncMedicInfo(@NonNull EventClient eventClient) {
        Event event = eventClient.getEvent();

        String motherBaseEntityId = event.getBaseEntityId();

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);

        PncRegistrationDetails pncDetails = new PncRegistrationDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
        pncDetails.setCreatedAt(new Date());

        PncMedicInfoRepository repo = PncLibrary.getInstance().getPncMedicInfoRepository();
        repo.saveOrUpdate(pncDetails);


        String strStillBorn = keyValues.get(PncConstants.JsonFormKeyConstants.BABIES_STILL_BORN_MAP);
        processStillBorn(strStillBorn, event, motherBaseEntityId);
        String strBabiesBorn = keyValues.get(PncConstants.JsonFormKeyConstants.BABIES_BORN_MAP);
        processBabiesBorn(strBabiesBorn, event, motherBaseEntityId);
    }

    private void processPncVisit(@NonNull EventClient eventClient) {
        Event event = eventClient.getEvent();

        String motherBaseEntityId = event.getBaseEntityId();

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);
        keyValues.put(PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID, motherBaseEntityId);

        String strOtherVisit = keyValues.get(PncConstants.JsonFormKeyConstants.OTHER_VISIT_MAP);
        processOtherVisits(strOtherVisit, motherBaseEntityId, keyValues.get(PncDbConstants.Column.PncVisitInfo.VISIT_ID));
        keyValues.put(PncDbConstants.Column.PncVisitInfo.OTHER_VISIT_DATE, strOtherVisit);

        String strChildStatus = keyValues.get(PncConstants.JsonFormKeyConstants.CHILD_STATUS_MAP);
        processVisitChildStatus(strChildStatus, keyValues.get(PncDbConstants.Column.PncVisitInfo.VISIT_ID));

        PncLibrary.getInstance().getPncVisitInfoRepository().saveOrUpdate(keyValues);
    }

    private void processOtherVisits(@Nullable String strOtherVisit, @NonNull String baseEntityId, @NonNull String visitId) {
        if (StringUtils.isNotBlank(strOtherVisit)) {
            try {
                JSONObject jsonObject = new JSONObject(strOtherVisit);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonOtherObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncOtherVisit pncOtherVisit = new PncOtherVisit();
                    pncOtherVisit.setVisitId(visitId);
                    pncOtherVisit.setVisitDate(jsonOtherObject.optString(PncConstants.JsonFormField.OTHER_PNC_VISIT_DATE));
                    pncOtherVisit.setMotherBaseEntityId(baseEntityId);
                    PncLibrary.getInstance().getPncOtherVisitRepository().saveOrUpdate(pncOtherVisit);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void processBabiesBorn(@Nullable String strBabiesBorn, @NonNull Event event, @NonNull String motherBaseEntityId) {
        if (StringUtils.isNotBlank(strBabiesBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strBabiesBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonChildObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncChild pncChild = new PncChild();
                    pncChild.setMotherBaseEntityId(motherBaseEntityId);
                    pncChild.setBaseEntityId(jsonChildObject.optString(PncDbConstants.Column.PncBaby.BASE_ENTITY_ID));
                    pncChild.setDischargedAlive(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.DISCHARGED_ALIVE));
                    pncChild.setChildRegistered(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.CHILD_REGISTERED));
                    pncChild.setBirthRecordDate(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_RECORD));
                    pncChild.setFirstName(jsonChildObject.optString(PncDbConstants.Column.PncBaby.BABY_FIRST_NAME));
                    pncChild.setLastName(jsonChildObject.optString(PncDbConstants.Column.PncBaby.BABY_LAST_NAME));
                    pncChild.setDob(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_DOB));
                    pncChild.setGender(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_GENDER));
                    pncChild.setWeightEntered(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_WEIGHT_ENTERED));
                    pncChild.setWeight(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_WEIGHT));
                    pncChild.setHeightEntered(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_HEIGHT_ENTERED));
                    pncChild.setApgar(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.APGAR));
                    pncChild.setFirstCry(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_FIRST_CRY));
                    pncChild.setComplications(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_COMPLICATIONS));
                    pncChild.setComplicationsOther(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_COMPLICATIONS_OTHER));
                    pncChild.setCareMgt(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_CARE_MGMT));
                    pncChild.setCareMgtSpecify(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_CARE_MGMT_SPECIFY));
                    pncChild.setRefLocation(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_REF_LOCATION));
                    pncChild.setBfFirstHour(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BF_FIRST_HOUR));
                    pncChild.setChildHivStatus(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.CHILD_HIV_STATUS));
                    pncChild.setNvpAdministration(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.NVP_ADMINISTRATION));
                    pncChild.setEventDate(PncUtils.convertDate(event.getEventDate().toDate(), PncDbConstants.DATE_FORMAT));
                    if (StringUtils.isNotBlank(pncChild.getMotherBaseEntityId()))
                        PncLibrary.getInstance().getPncChildRepository().saveOrUpdate(pncChild);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void processStillBorn(@Nullable String strStillBorn, @NonNull Event event, String motherBaseEntityId) {
        if (StringUtils.isNotBlank(strStillBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strStillBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonTestObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncStillBorn pncStillBorn = new PncStillBorn();
                    pncStillBorn.setMotherBaseEntityId(motherBaseEntityId);
                    pncStillBorn.setStillBirthCondition(jsonTestObject.optString(PncConstants.JsonFormKeyConstants.STILL_BIRTH_CONDITION));
                    pncStillBorn.setEventDate(PncUtils.convertDate(event.getEventDate().toDate(), PncDbConstants.DATE_FORMAT));
                    PncLibrary.getInstance().getPncStillBornRepository().saveOrUpdate(pncStillBorn);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void processVisitChildStatus(@Nullable String strChildStatus, String visitId) {
        if (StringUtils.isNotBlank(strChildStatus)) {
            try {
                JSONObject jsonObject = new JSONObject(strChildStatus);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();

                while (repeatingGroupKeys.hasNext()) {
                    String key = repeatingGroupKeys.next();
                    JSONObject jsonChildObject = jsonObject.optJSONObject(key);
                    Map<String, String> data = new HashMap<>();
                    data.put(PncDbConstants.Column.PncVisitChildStatus.VISIT_ID, visitId); //button that opens vaccine card should have clients' id as the value
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BASE_ENTITY_ID, jsonChildObject.optString("open_vaccine_card")); //button that opens vaccine card should have clients' id as the value
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_AGE, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_AGE));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_FIRST_NAME, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_FIRST_NAME));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_LAST_NAME, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_LAST_NAME));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_STATUS, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_STATUS));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.DATE_OF_DEATH_BABY, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.DATE_OF_DEATH_BABY));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.PLACE_OF_DEATH_BABY, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.PLACE_OF_DEATH_BABY));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.CAUSE_OF_DEATH_BABY, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.CAUSE_OF_DEATH_BABY));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.DEATH_FOLLOW_UP_BABY, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.DEATH_FOLLOW_UP_BABY));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_BREAST_FEEDING, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_BREAST_FEEDING));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_NOT_BREAST_FEEDING_REASON, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_NOT_BREAST_FEEDING_REASON));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS_OTHER, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS_OTHER));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_REFERRED_OUT, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_REFERRED_OUT));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_EXPOSED, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_EXPOSED));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.MOTHER_BABY_PAIRING, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.MOTHER_BABY_PAIRING));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_TREATMENT, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_TREATMENT));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON_OTHER, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON_OTHER));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_DBS, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_DBS));
                    data.put(PncDbConstants.Column.PncVisitChildStatus.BABY_CARE_MGMT, jsonChildObject.optString(PncDbConstants.Column.PncVisitChildStatus.BABY_CARE_MGMT));
                    PncLibrary.getInstance().getPncVisitChildStatusRepository().saveOrUpdate(data);
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
        List<Obs> obs = event.getObs();
        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();
            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);
                if (!TextUtils.isEmpty(value)) {
                    if (humanReadableValues.size() > 1) {
                        value = humanReadableValues.toString();
                    }
                    keyValues.put(key, value);
                    continue;
                }
            }
            List<Object> values = observation.getValues();
            if (values.size() > 0) {
                String value = (String) values.get(0);
                if (!TextUtils.isEmpty(value)) {
                    if (values.size() > 1) {
                        value = values.toString();
                    }
                    keyValues.put(key, value);
                    continue;
                }
            }
        }
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        return true;
    }
}