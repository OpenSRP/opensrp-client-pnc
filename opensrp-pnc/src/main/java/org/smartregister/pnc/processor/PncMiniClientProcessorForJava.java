package org.smartregister.pnc.processor;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

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
        // do nothing
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        // Do nothing for now
        return true;
    }
}