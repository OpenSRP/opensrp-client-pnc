package org.smartregister.pnc.config;

import android.content.Intent;

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
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.utils.PncConstants;
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
        return eventList;

    }

    private void saveOutcome(String baseEntityId) {
        HashMap<String, String> keyValues = new HashMap<>();

        PncRegistrationDetails pncDetails = new PncRegistrationDetails(baseEntityId, new Date(), keyValues);
        pncDetails.setCreatedAt(new Date());
        PncLibrary.getInstance().getPncRegistrationDetailsRepository().saveOrUpdate(pncDetails);
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
