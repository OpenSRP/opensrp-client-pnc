package org.smartregister.pnc.config;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 26-03-2020.
 */
public interface PncFormProcessingTask {

    void processMaternityForm(@NonNull String eventType, String jsonString, @Nullable Intent data);
}
