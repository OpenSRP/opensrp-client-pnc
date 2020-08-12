package org.smartregister.pnc.config;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.JsonFormUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.pnc.utils.PncJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.CONCEPT;
import static org.smartregister.util.JsonFormUtils.ENCOUNTER;
import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.JsonFormUtils.OPENMRS_ENTITY;
import static org.smartregister.util.JsonFormUtils.OPENMRS_ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.JsonFormUtils.addObservation;
import static org.smartregister.util.JsonFormUtils.addToJSONObject;
import static org.smartregister.util.JsonFormUtils.formatDate;
import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static org.smartregister.util.JsonFormUtils.getFieldValue;
import static org.smartregister.util.JsonFormUtils.getJSONObject;
import static org.smartregister.util.JsonFormUtils.getString;

public class PncCloseFormProcessing implements PncFormProcessingTask {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    @Override
    public List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {

        try {
            ArrayList<Event> eventList = new ArrayList<>();
            JSONObject jsonFormObject = new JSONObject(jsonString);

            JSONArray fieldsArray = PncUtils.generateFieldsFromJsonForm(jsonFormObject);
            FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());

            JSONObject metadata = jsonFormObject.getJSONObject(METADATA);

            String baseEntityId = PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID);
            String entityTable = PncUtils.getIntentValue(data, PncConstants.IntentKey.ENTITY_TABLE);
            Event closePncEvent = JsonFormUtils.createEvent(fieldsArray, metadata, formTag, baseEntityId, eventType, entityTable);
            PncJsonFormUtils.tagSyncMetadata(closePncEvent);
            eventList.add(closePncEvent);

            if ("Woman died".equals(getFieldValue(fieldsArray, "pnc_close_reason"))) {

                addSaveReportDeceasedObservations(fieldsArray, closePncEvent);
                updateMetadata(metadata, closePncEvent);

                String encounterDateField = getFieldValue(fieldsArray, "date_of_death");
                Date encounterDate = new Date();
                String encounterDateTimeString = null;
                if (StringUtils.isNotBlank(encounterDateField)) {
                    encounterDateTimeString = formatDate(encounterDateField);
                    Date dateTime = formatDate(encounterDateField, false);
                    if (dateTime != null) {
                        encounterDate = dateTime;
                    }
                }

                createDeathEventObject(PncLibrary.getInstance().context().applicationContext(), closePncEvent.getProviderId(), closePncEvent.getLocationId(), closePncEvent.getEntityType(), PncLibrary.getInstance().eventClientRepository(), encounterDate, encounterDateTimeString, closePncEvent, eventType, entityTable);

                ContentValues values = new ContentValues();
                values.put(PncConstants.KeyConstants.DOD, encounterDateField);
                values.put(PncConstants.KeyConstants.DATE_REMOVED, PncUtils.getTodaysDate());
                updateChildFTSTables(values, baseEntityId);

                updateDateOfRemoval(baseEntityId, encounterDateTimeString);

            }

            return eventList;
        }
        catch (Exception ex) {
            Timber.e(ex);
            return new ArrayList<>();
        }
    }

    private void addSaveReportDeceasedObservations(JSONArray fields, Event event) {
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            String value = getString(jsonObject, VALUE);
            if (StringUtils.isNotBlank(value)) {
                addObservation(event, jsonObject);
            }
        }
    }

    public static void updateChildFTSTables(ContentValues values, String entityId) {
        //Update REGISTER and FTS Tables
        String tableName = PncDbConstants.Table.EC_CLIENT;
        AllCommonsRepository allCommonsRepository = PncLibrary.getInstance().context().allCommonsRepositoryobjects(tableName);
        if (allCommonsRepository != null) {
            allCommonsRepository.update(tableName, values, entityId);
            PncLibrary.getInstance().context().allCommonsRepositoryobjects(tableName).updateSearch(Arrays.asList(new String[]{entityId}));
        }
    }

    private void updateDateOfRemoval(String baseEntityId, String dateOfRemovalString) {

        ContentValues contentValues = new ContentValues();

        if (dateOfRemovalString != null) {
            contentValues.put(PncConstants.KeyConstants.DATE_REMOVED, dateOfRemovalString);
        }

        PncLibrary.getInstance().context().getEventClientRepository().getWritableDatabase()
                .update(PncDbConstants.Table.EC_CLIENT, contentValues, PncConstants.KeyConstants.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    private void updateMetadata(JSONObject metadata, Event event) {
        if (metadata != null) {
            Iterator<?> keys = metadata.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jsonObject = getJSONObject(metadata, key);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                    if (entityVal != null) {
                        if (entityVal.equals(CONCEPT)) {
                            addToJSONObject(jsonObject, KEY, key);
                            addObservation(event, jsonObject);
                        } else if (entityVal.equals(ENCOUNTER)) {
                            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                            if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                Date eDate = formatDate(value, false);
                                if (eDate != null) {
                                    event.setEventDate(eDate);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void createDeathEventObject(Context context, String providerId, String locationId, String entityId, EventClientRepository db, Date encounterDate, String encounterDateTimeString, Event event, String encounterType, String entityTable) throws JSONException {
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));


        //Update client to deceased
        JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
        client.put(FormEntityConstants.Person.deathdate.name(), encounterDateTimeString);
        client.put(FormEntityConstants.Person.deathdate_estimated.name(), false);
        client.put(PncConstants.JsonFormKeyConstants.DEATH_DATE_APPROX, false);

        db.addorUpdateClient(entityId, client);

        //Add Death Event for child to flag for Server delete
        db.addEvent(event.getBaseEntityId(), eventJson);

        //Update Child Entity to include death date
        Event updateChildDetailsEvent = getEvent(providerId, locationId, entityId, encounterType, encounterDate, entityTable);

        addMetaData(context, updateChildDetailsEvent, new Date());

        JSONObject eventJsonUpdateChildEvent = new JSONObject(JsonFormUtils.gson.toJson(updateChildDetailsEvent));

        db.addEvent(entityId, eventJsonUpdateChildEvent); //Add event to flag server update
    }

    @SuppressLint("MissingPermission")
    public static Event addMetaData(Context context, Event event, Date start) {
        Map<String, String> metaFields = new HashMap<>();
        metaFields.put("deviceid", "163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Calendar calendar = Calendar.getInstance();

        String end = DATE_TIME_FORMAT.format(calendar.getTime());

        Obs obs = new Obs();
        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(DATE_TIME_FORMAT.format(start));
        obs.setFieldType("concept");
        obs.setFieldDataType("start");
        event.addObs(obs);

        obs = new Obs();
        obs.setFieldCode("163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(end);
        obs.setFieldDataType("end");
        event.addObs(obs);

        String deviceId = "";
        try {

            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = mTelephonyManager.getSimSerialNumber(); //Already handled by native form

        } catch (SecurityException e) {
            Timber.e(e, "JsonFormUtils --> MissingPermission --> getSimSerialNumber");
        }
        obs = new Obs();
        obs.setFieldCode("163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(deviceId);
        obs.setFieldDataType("deviceid");
        event.addObs(obs);

        return event;
    }

    private Event getEvent(String providerId, String locationId, String entityId, String
            encounterType, Date encounterDate, String childType) {
        Event event = (Event) new Event().withBaseEntityId(entityId) //should be different for main and subform
                .withEventDate(encounterDate).withEventType(encounterType).withLocationId(locationId)
                .withProviderId(providerId).withEntityType(childType)
                .withFormSubmissionId(generateRandomUUIDString()).withDateCreated(new Date());

        PncJsonFormUtils.tagSyncMetadata(event);

        return event;
    }
}

