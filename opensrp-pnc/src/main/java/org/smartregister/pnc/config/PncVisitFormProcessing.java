package org.smartregister.pnc.config;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.smartregister.pnc.utils.PncJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.gson;

public class PncVisitFormProcessing implements PncFormProcessingTask {


    @Override
    public List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        if (eventType.equals(PncConstants.EventTypeConstants.PNC_VISIT)) {
            return processPncVisitForm(jsonString, data);
        }
        return new ArrayList<>();
    }

    public List<Event> processPncVisitForm(@NonNull String jsonString, @NonNull Intent data) throws JSONException {
        List<Event> eventList = new ArrayList<>();

        JSONObject jsonFormObject = new JSONObject(jsonString);

        String baseEntityId = PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID);

        JSONArray fieldsArray = PncUtils.generateFieldsFromJsonForm(jsonFormObject);

        String steps = jsonFormObject.optString(JsonFormConstants.COUNT);

        int numOfSteps = Integer.parseInt(steps);

        for (int j = 0; j < numOfSteps; j++) {

            JSONObject step = jsonFormObject.optJSONObject(JsonFormConstants.STEP.concat(String.valueOf(j + 1)));

            String title = step.optString(JsonFormConstants.STEP_TITLE);

            if (PncConstants.JsonFormStepNameConstants.PNC_VISIT_INFO.equals(title)) {
                HashMap<String, HashMap<String, String>> buildRepeatingOtherVisits = PncUtils.buildRepeatingGroup(step, PncConstants.JsonFormKeyConstants.OTHER_VISIT_GROUP);
                if (!buildRepeatingOtherVisits.isEmpty()) {
                    String strGroup = gson.toJson(buildRepeatingOtherVisits);
                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, PncConstants.JsonFormKeyConstants.OTHER_VISIT_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                }
            } else if (PncConstants.JsonFormStepNameConstants.PNC_VISIT_CHILD_STATUS.equals(title)) {
                HashMap<String, HashMap<String, String>> buildRepeatingGroupStillBorn = PncUtils.buildRepeatingGroup(step, PncConstants.JsonFormKeyConstants.CHILD_STATUS_GROUP);
                if (!buildRepeatingGroupStillBorn.isEmpty()) {
                    String strGroup = gson.toJson(buildRepeatingGroupStillBorn);
                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, PncConstants.JsonFormKeyConstants.CHILD_STATUS_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                }
            }
        }

        FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());
        Event pncVisitEvent = PncJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, PncConstants.EventTypeConstants.PNC_VISIT, "");
        PncJsonFormUtils.tagSyncMetadata(pncVisitEvent);
        eventList.add(pncVisitEvent);
        return eventList;
    }
}
