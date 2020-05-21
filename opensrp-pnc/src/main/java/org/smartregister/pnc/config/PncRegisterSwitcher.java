package org.smartregister.pnc.config;

import android.content.Context;

import androidx.annotation.NonNull;

import org.smartregister.commonregistry.CommonPersonObjectClient;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-20
 */

public interface PncRegisterSwitcher {

    void switchFromMaternityRegister(@NonNull CommonPersonObjectClient client, @NonNull Context context);

    boolean showRegisterSwitcher(@NonNull CommonPersonObjectClient client);
}
