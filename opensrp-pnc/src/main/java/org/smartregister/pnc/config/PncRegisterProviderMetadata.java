package org.smartregister.pnc.config;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface PncRegisterProviderMetadata {

    @NonNull
    String getClientFirstName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getClientMiddleName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getClientLastName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getDob(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getGA(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getPatientID(@NonNull Map<String, String> columnMaps);
}
