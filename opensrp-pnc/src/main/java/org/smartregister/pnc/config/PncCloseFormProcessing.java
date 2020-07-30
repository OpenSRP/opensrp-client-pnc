package org.smartregister.pnc.config;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.pnc.utils.PncJsonFormUtils.METADATA;

public class PncCloseFormProcessing implements PncFormProcessingTask {
    @Override
    public List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        ArrayList<Event> eventList = new ArrayList<>();
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONArray fieldsArray = PncUtils.generateFieldsFromJsonForm(jsonFormObject);
        FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());

        String baseEntityId = PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = PncUtils.getIntentValue(data, PncConstants.IntentKey.ENTITY_TABLE);
        Event closePncEvent = JsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, eventType, entityTable);
        PncJsonFormUtils.tagSyncMetadata(closePncEvent);
        eventList.add(closePncEvent);

        return eventList;
    }
}
