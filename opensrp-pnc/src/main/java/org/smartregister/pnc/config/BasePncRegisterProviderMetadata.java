package org.smartregister.pnc.config;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.utils.PncDbConstants;
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

    @NonNull
    @Override
    public String getGA(@NonNull Map<String, String> columnMaps) {
        String gaInWeeks = getString(R.string.zero_weeks);
        String conceptionDateString = Utils.getValue(columnMaps, PncDbConstants.KEY.CONCEPTION_DATE, false);

        if (!TextUtils.isEmpty(conceptionDateString)) {
            int intWeeks = PncLibrary.getGestationAgeInWeeks(conceptionDateString);
            String weekString;

            if (intWeeks != 1) {
                weekString = getString(R.string.weeks);
            } else {
                weekString = getString(R.string.week);
            }

            gaInWeeks =  intWeeks + " " + weekString;
        }

        return gaInWeeks;
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

    @NonNull
    private String getString(@StringRes int stringResId) {
        return PncLibrary.getInstance().context().applicationContext().getString(stringResId);
    }
}
