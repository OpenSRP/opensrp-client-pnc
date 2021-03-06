package org.smartregister.pnc.config;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.smartregister.pnc.utils.PncJsonFormUtils.METADATA;

public class PncCloseFormProcessing implements PncFormProcessingTask {

    @Override
    public List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {

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

        processWomanDiedEvent(fieldsArray, closePncEvent);

        return eventList;
    }

    protected void processWomanDiedEvent(JSONArray fieldsArray, Event event) throws JSONException {
        if (PncConstants.KeyConstants.WOMAN_DIED.equals(JsonFormUtils.getFieldValue(fieldsArray, PncConstants.JsonFormKeyConstants.PNC_CLOSE_REASON))) {
            event.setEventType(PncConstants.EventTypeConstants.DEATH);
            createDeathEventObject(event, fieldsArray);
        }
    }

    private void createDeathEventObject(@NonNull Event event, @NonNull JSONArray fieldsArray) throws JSONException {
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

        EventClientRepository db = PncLibrary.getInstance().eventClientRepository();

        JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
        String dateOfDeath = JsonFormUtils.getFieldValue(fieldsArray, PncConstants.JsonFormKeyConstants.DATE_OF_DEATH);
        client.put(PncConstants.JsonFormKeyConstants.DEATH_DATE, StringUtils.isNotBlank(dateOfDeath) ? PncUtils.reverseHyphenSeparatedValues(dateOfDeath, "-") : PncUtils.getTodaysDate());
        client.put(FormEntityConstants.Person.deathdate_estimated.name(), false);
        client.put(PncConstants.JsonFormKeyConstants.DEATH_DATE_APPROX, false);

        JSONObject attributes = client.optJSONObject(PncConstants.JsonFormKeyConstants.ATTRIBUTES);
        attributes.put(PncConstants.KeyConstants.DATE_REMOVED, PncUtils.getTodaysDate());

        db.addorUpdateClient(event.getBaseEntityId(), client);

        db.addEvent(event.getBaseEntityId(), eventJson);

        Event updateClientDetailsEvent = (Event) new Event().withBaseEntityId(event.getBaseEntityId())
                .withEventDate(DateTime.now().toDate()).withEventType(PncUtils.metadata().getUpdateEventType()).withLocationId(event.getLocationId())
                .withProviderId(event.getLocationId()).withEntityType(event.getEntityType())
                .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString()).withDateCreated(new Date());

        JSONObject eventJsonUpdateClientEvent = new JSONObject(JsonFormUtils.gson.toJson(updateClientDetailsEvent));

        db.addEvent(event.getBaseEntityId(), eventJsonUpdateClientEvent);
    }
}

