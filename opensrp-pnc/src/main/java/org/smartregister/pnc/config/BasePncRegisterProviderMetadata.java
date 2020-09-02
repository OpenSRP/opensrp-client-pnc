package org.smartregister.pnc.config;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.Utils;

import java.util.Map;

/**
 * This is a metadata class for the RegisterProvider at {@link org.smartregister.pnc.provider.PncRegisterProvider}. Some of the methods avoid null-checking but scream NotNullable
 * because https://github.com/OpenSRP/opensrp-client-core/blob/master/opensrp-app/src/main/java/org/smartregister/util/Utils.java#L208 checks for nulls and replaces them with empty strings
 * <p>
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class BasePncRegisterProviderMetadata implements PncRegisterProviderMetadata {

    @NonNull
    @Override
    public String getClientFirstName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, PncDbConstants.KEY.FIRST_NAME, true);
    }

    @NonNull
    @Override
    public String getClientMiddleName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, PncDbConstants.KEY.MIDDLE_NAME, true);
    }

    @NonNull
    @Override
    public String getClientLastName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, PncDbConstants.KEY.LAST_NAME, true);
    }

    @NonNull
    @Override
    public String getDob(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, PncDbConstants.KEY.DOB, false);
    }

    @Override
    public int getDeliveryDays(@NonNull Map<String, String> columnMaps) {
        return PncUtils.getDeliveryDays(columnMaps.get(PncConstants.FormGlobalConstants.DELIVERY_DATE));
    }

    @NonNull
    @Override
    public String getPatientID(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, PncDbConstants.KEY.REGISTER_ID, true);
    }

    @NonNull
    public String getSafeValue(@Nullable String nullableString) {
        return nullableString == null ? "" : nullableString;
    }
}
