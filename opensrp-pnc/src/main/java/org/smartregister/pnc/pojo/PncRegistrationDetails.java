package org.smartregister.pnc.pojo;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegistrationDetails extends PncBaseDetails {

    // This enables us to easily change what is and can be saved
    public enum Property {
        dob_entered,
        dob_unknown,
        age_calculated,
        age_entered,
        dob_calculated,
        age,
        phone_number,
        alt_name,
        alt_phone_number,
        educ_level,
        marital_status,
        occupation,
        occupation_other,
        religion
    }

    public PncRegistrationDetails(@NonNull String baseEntityId, @NonNull Date eventDate, @NonNull HashMap<String, String> properties) {
        super(baseEntityId, eventDate, properties);
    }

}
