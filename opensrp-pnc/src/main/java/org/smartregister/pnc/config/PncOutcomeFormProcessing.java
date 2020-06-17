package org.smartregister.pnc.config;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.pojo.PncStillBorn;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.pnc.utils.PncJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.gson;

public class PncOutcomeFormProcessing implements PncFormProcessingTask {


    @Override
    public List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        if (eventType.equals(PncConstants.EventTypeConstants.PNC_OUTCOME)) {
            return processPncOutcomeForm(jsonString, data);
        }
        return new ArrayList<>();
    }

    public List<Event> processPncOutcomeForm(@NonNull String jsonString, @NonNull Intent data) throws JSONException {
        List<Event> eventList = new ArrayList<>();

        JSONObject jsonFormObject = new JSONObject(jsonString);

        String baseEntityId = PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID);

        JSONArray fieldsArray = PncUtils.generateFieldsFromJsonForm(jsonFormObject);

        String steps = jsonFormObject.optString(JsonFormConstants.COUNT);

        int numOfSteps = Integer.parseInt(steps);

        for (int j = 0; j < numOfSteps; j++) {

            JSONObject step = jsonFormObject.optJSONObject(JsonFormConstants.STEP.concat(String.valueOf(j + 1)));

            String title = step.optString(JsonFormConstants.STEP_TITLE);

            if (PncConstants.JsonFormStepNameConstants.LIVE_BIRTHS.equals(title)) {
                HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn = PncUtils.buildRepeatingGroup(step, PncConstants.JsonFormKeyConstants.LIVE_BIRTHS);

                //buildChildRegEvent
                PncLibrary.getInstance().getAppExecutors().diskIO().execute(() -> {

                    processBabiesBorn(gson.toJson(buildRepeatingGroupBorn), baseEntityId);

                    List<PncEventClient> childEvents = buildChildRegistrationEvents(buildRepeatingGroupBorn, baseEntityId, jsonFormObject);
                    if (!childEvents.isEmpty()) {
                        PncUtils.savePncChild(childEvents);
                    }
                });

                if (!buildRepeatingGroupBorn.isEmpty()) {
                    String strGroup = gson.toJson(buildRepeatingGroupBorn);

                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, PncConstants.JsonFormKeyConstants.BABIES_BORN_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                }
            } else if (PncConstants.JsonFormStepNameConstants.STILL_BIRTHS.equals(title)) {
                HashMap<String, HashMap<String, String>> buildRepeatingGroupStillBorn = PncUtils.buildRepeatingGroup(step, PncConstants.JsonFormKeyConstants.BABIES_STILLBORN);
                if (!buildRepeatingGroupStillBorn.isEmpty()) {
                    String strGroup = gson.toJson(buildRepeatingGroupStillBorn);
                    processStillBorn(strGroup, baseEntityId);
                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, PncConstants.JsonFormKeyConstants.BABIES_STILL_BORN_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                }
            }
        }

        FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());

        Event pncOutcomeEvent = PncJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, PncConstants.EventTypeConstants.PNC_OUTCOME, "");
        PncJsonFormUtils.tagSyncMetadata(pncOutcomeEvent);
        eventList.add(pncOutcomeEvent);


        Event closePncEvent = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, baseEntityId, PncConstants.EventTypeConstants.PNC_CLOSE, "");
        PncJsonFormUtils.tagSyncMetadata(closePncEvent);
        closePncEvent.addDetails(PncConstants.JsonFormKeyConstants.VISIT_END_DATE, PncUtils.convertDate(new Date(), PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closePncEvent);

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(pncOutcomeEvent, keyValues, false);

        PncRegistrationDetails pncDetails = new PncRegistrationDetails(baseEntityId, pncOutcomeEvent.getEventDate(), keyValues);
        pncDetails.setCreatedAt(new Date());

        PncLibrary.getInstance().getPncRegistrationDetailsRepository().saveOrUpdate(pncDetails);

        return eventList;

    }

    private void saveOutcome(String baseEntityId) {
        HashMap<String, String> keyValues = new HashMap<>();

        PncRegistrationDetails pncDetails = new PncRegistrationDetails(baseEntityId, new Date(), keyValues);
        pncDetails.setCreatedAt(new Date());
        PncLibrary.getInstance().getPncRegistrationDetailsRepository().saveOrUpdate(pncDetails);
    }

    private void processBabiesBorn(@Nullable String strBabiesBorn, @NonNull String baseEntityId) {
        if (StringUtils.isNotBlank(strBabiesBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strBabiesBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonChildObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncChild pncChild = new PncChild();
                    pncChild.setMotherBaseEntityId(baseEntityId);
                    pncChild.setDischargedAlive(jsonChildObject.optString("discharged_alive"));
                    pncChild.setChildRegistered(jsonChildObject.optString("child_registered"));
                    pncChild.setBirthRecordDate(jsonChildObject.optString("birth_record_date"));
                    pncChild.setFirstName(jsonChildObject.optString("baby_first_name"));
                    pncChild.setLastName(jsonChildObject.optString("baby_last_name"));
                    pncChild.setDob(jsonChildObject.optString("baby_dob"));
                    pncChild.setGender(jsonChildObject.optString("baby_gender"));
                    pncChild.setWeightEntered(jsonChildObject.optString("birth_weight_entered"));
                    pncChild.setWeight(jsonChildObject.optString("birth_weight"));
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
                    pncChild.setEventDate(PncUtils.convertDate(new Date(), PncDbConstants.DATE_FORMAT));
                    PncLibrary.getInstance().getPncChildRepository().saveOrUpdate(pncChild);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void processStillBorn(@Nullable String strStillBorn, @NonNull String baseEntityId) {
        if (StringUtils.isNotBlank(strStillBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strStillBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonTestObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncStillBorn maternityStillBorn = new PncStillBorn();
                    maternityStillBorn.setMotherBaseEntityId(baseEntityId);
                    maternityStillBorn.setStillBirthCondition(jsonTestObject.optString("stillbirth_condition"));
                    maternityStillBorn.setEventDate(PncUtils.convertDate(new Date(), PncDbConstants.DATE_FORMAT));
                    PncLibrary.getInstance().getPncStillBornRepository().saveOrUpdate(maternityStillBorn);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues, boolean appendOnNewline) {
        List<org.smartregister.clientandeventmodel.Obs> obs = event.getObs();

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

    @NonNull
    private List<PncEventClient> buildChildRegistrationEvents(HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn, String baseEntityId, JSONObject jsonFormObject) {
        FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());

        String strBabiesBorn = gson.toJson(buildRepeatingGroupBorn);

        List<PncEventClient> childRegEventList = new ArrayList<>();

        if (StringUtils.isNotBlank(strBabiesBorn)) {

            try {
                JSONObject jsonObject = new JSONObject(strBabiesBorn);

                Iterator<String> repeatingGroupKeys = jsonObject.keys();

                HashMap<String, String> motherDetails = PncUtils.getPncClient(baseEntityId);

                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonChildObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    String dischargedAlive = jsonChildObject.optString(PncConstants.JsonFormKeyConstants.DISCHARGED_ALIVE);
                    if (StringUtils.isNotBlank(dischargedAlive) && dischargedAlive.equalsIgnoreCase("yes")) {
                        String entityId = PncJsonFormUtils.generateRandomUUIDString();
                        JSONArray fields = populateChildFieldArray(jsonChildObject, motherDetails);
                        if (fields != null) {
                            Client baseClient = JsonFormUtils.createBaseClient(fields, formTag, entityId);
                            baseClient.addRelationship(PncConstants.MOTHER, baseEntityId);
                            baseClient.setRelationalBaseEntityId(baseEntityId);
                            Event childRegEvent = PncJsonFormUtils.createEvent(fields, jsonFormObject.optJSONObject(METADATA)
                                    , formTag, entityId, childRegistrationEvent(), "");
                            PncJsonFormUtils.tagSyncMetadata(childRegEvent);
                            childRegEventList.add(new PncEventClient(baseClient, childRegEvent));
                        }
                    }

                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        return childRegEventList;
    }

    @Nullable
    private JSONArray getChildFormFields() {
        if (StringUtils.isNotBlank(getChildFormName())) {
            JSONObject formJsonObject = PncUtils.getFormUtils().getFormJson(getChildFormName());
            if (formJsonObject != null) {
                return FormUtils.getMultiStepFormFields(formJsonObject);
            }
        }
        return null;
    }

    @NonNull
    private String childRegistrationEvent() {
        return PncConstants.EventTypeConstants.BIRTH_REGISTRATION;
    }

    @NonNull
    protected String getChildFormName() {
        return "";
    }

    @Nullable
    private JSONArray populateChildFieldArray(JSONObject pncBabyBorn, HashMap<String, String> motherDetails) throws JSONException {
        JSONArray jsonArray = getChildFormFields();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String key = jsonObject.optString(JsonFormConstants.KEY);
                String childKeyToKeyValue = childFormKeyToKeyMap().get(key);
                if (pncBabyBorn.has(key)) {
                    jsonObject.put(JsonFormConstants.VALUE, pncBabyBorn.optString(key));
                } else if (StringUtils.isNotBlank(childKeyToKeyValue) && pncBabyBorn.has(childKeyToKeyValue)) {
                    jsonObject.put(JsonFormConstants.VALUE, pncBabyBorn.optString(childKeyToKeyValue));
                } else if (key.equalsIgnoreCase(childOpensrpId())) {
                    jsonObject.put(JsonFormConstants.VALUE, PncUtils.getNextUniqueId());
                } else if (otherRequiredFields().contains(key)) {
                    jsonObject.put(JsonFormConstants.VALUE, motherDetails.get(childKeyToColumnMap().get(key) == null ? key : childKeyToColumnMap().get(key)));
                }
            }
            PncJsonFormUtils.lastInteractedWith(jsonArray);
            return jsonArray;
        }
        return null;
    }

    protected Set<String> otherRequiredFields() {
        return childKeyToColumnMap().keySet();
    }

    protected String childOpensrpId() {
        return PncConstants.JsonFormKeyConstants.ZEIR_ID;
    }

    public HashMap<String, String> childFormKeyToKeyMap() {
        return new HashMap<>();
    }

    public HashMap<String, String> childKeyToColumnMap() {
        return new HashMap<>();
    }


}
