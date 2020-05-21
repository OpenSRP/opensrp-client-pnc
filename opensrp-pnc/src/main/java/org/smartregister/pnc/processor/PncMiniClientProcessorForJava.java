package org.smartregister.pnc.processor;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.exception.PncCloseEventProcessException;
import org.smartregister.pnc.pojo.PncDetails;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
            eventTypes.add(PncConstants.EventType.MATERNITY_REGISTRATION);
            eventTypes.add(PncConstants.EventType.UPDATE_MATERNITY_REGISTRATION);
            eventTypes.add(PncConstants.EventType.MATERNITY_OUTCOME);
            eventTypes.add(PncConstants.EventType.MATERNITY_CLOSE);
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

        if (eventType.equals(PncConstants.EventType.MATERNITY_REGISTRATION)
                || eventType.equals(PncConstants.EventType.UPDATE_MATERNITY_REGISTRATION)) {
            ArrayList<EventClient> eventClients = new ArrayList<>();
            eventClients.add(eventClient);
            processClient(eventClients);

            //updateRegisterTypeColumn(event, "maternity");

            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues, true);

            PncRegistrationDetails maternityDetails = new PncRegistrationDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
            maternityDetails.setCreatedAt(new Date());

            PncLibrary.getInstance().getPncRegistrationDetailsRepository().saveOrUpdate(maternityDetails);
        } else if (eventType.equals(PncConstants.EventType.MATERNITY_CLOSE)) {
            if (eventClient.getClient() == null) {
                throw new PncCloseEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), PncConstants.EventType.MATERNITY_CLOSE));
            }

            unsyncEvents.add(event);
        } else if (eventType.equals(PncConstants.EventType.MATERNITY_OUTCOME)) {
            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues);

            PncDetails maternityDetails = new PncDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
            maternityDetails.setCreatedAt(new Date());
            PncLibrary.getInstance().getPncOutcomeDetailsRepository().saveOrUpdate(maternityDetails);
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
                if (MaternityConstants.EventType.MATERNITY_CLOSE.equals(event.getEventType())) {
                    // Delete the maternity details
                    // MaternityLibrary.getInstance().getMaternityOutcomeDetailsRepository().delete(event.getBaseEntityId());

                    // Delete the actual client in the maternity table OR REMOVE THE Maternity register type
                    //updateRegisterTypeColumn(event, null);
                }
            }
        }*/
        return true;
    }
}