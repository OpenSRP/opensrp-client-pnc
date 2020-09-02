package org.smartregister.pnc.config;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.smartregister.clientandeventmodel.Event;

import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 26-03-2020.
 */
public interface PncFormProcessingTask {

    List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException;
}
