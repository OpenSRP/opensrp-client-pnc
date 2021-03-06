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
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.pnc.utils.PncJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.gson;

public class PncMedicInfoFormProcessing implements PncFormProcessingTask {


    @Override
    public List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        if (eventType.equals(PncConstants.EventTypeConstants.PNC_MEDIC_INFO)) {
            return processPncMedicInfoForm(jsonString, data);
        }
        return new ArrayList<>();
    }

    public List<Event> processPncMedicInfoForm(@NonNull String jsonString, @NonNull Intent data) throws JSONException {
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

                if (!buildRepeatingGroupBorn.isEmpty()) {
                    String[] ids = PncUtils.generateNIds(buildRepeatingGroupBorn.size());
                    int count = 0;
                    for (Map.Entry<String, HashMap<String, String>> entrySet : buildRepeatingGroupBorn.entrySet()) {
                        entrySet.getValue().put(PncDbConstants.Column.PncBaby.BASE_ENTITY_ID, ids[count]);
                        count++;
                    }

                    createChild(jsonFormObject, baseEntityId, buildRepeatingGroupBorn);

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

        Event pncMedicInfoEvent = PncJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, PncConstants.EventTypeConstants.PNC_MEDIC_INFO, "");
        PncJsonFormUtils.tagSyncMetadata(pncMedicInfoEvent);
        eventList.add(pncMedicInfoEvent);
        return eventList;

    }

    protected void createChild(JSONObject jsonFormObject, String baseEntityId, HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn) {
        PncLibrary.getInstance().getAppExecutors().diskIO().execute(() -> {

            List<PncEventClient> childEvents = buildChildRegistrationEvents(buildRepeatingGroupBorn, baseEntityId, jsonFormObject);
            if (!childEvents.isEmpty()) {
                saveAndProcessChildEvents(childEvents);
            }
        });
    }

    protected void saveAndProcessChildEvents(@NonNull List<PncEventClient> pncEventClients) {
        PncUtils.processEvents(pncEventClients);
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

                HashMap<String, String> motherDetails = PncLibrary.getInstance().getPncRepository().getClientWithRegistrationDetails(baseEntityId);

                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonChildObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    String dischargedAlive = jsonChildObject.optString(PncConstants.JsonFormKeyConstants.DISCHARGED_ALIVE);
                    if (!jsonChildObject.optBoolean(PncConstants.JsonFormField.GENERATED_GRP, false)) {
                        String entityId = jsonChildObject.optString(PncDbConstants.Column.PncBaby.BASE_ENTITY_ID);
                        JSONArray fields = populateChildFieldArray(jsonChildObject, motherDetails);
                        if ("yes".equalsIgnoreCase(dischargedAlive) && fields != null) {
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
    public String childRegistrationEvent() {
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
