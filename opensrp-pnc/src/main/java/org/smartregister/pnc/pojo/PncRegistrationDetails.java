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
        home_address,
        village,
        phone_number,
        alt_name,
        alt_phone_number,
        educ_level,
        marital_status,
        occupation,
        occupation_other,
        religion,
        gravidity,
        parity,
        lmp_unknown,
        lmp,
        gest_age,
        ga_weeks_entered,
        ga_days_entered,
        onset_labour,
        onset_labour_time,
        previous_complications,
        previous_complications_other,
        previous_delivery_mode,
        previous_pregnancy_outcomes,
        family_history,
        family_history_other,
        mother_tdv_doses,
        protected_at_birth,
        delivery_date,
        delivery_time,
        delivery_place,
        delivery_person,
        delivery_person_other,
        delivery_mode,
        delivery_mode_other,
        obstretic_complications,
        obstretic_complications_other,
        obstretic_care,
        obstretic_care_other,
        referred_out,
        vit_a,
        discharge_status,
        hiv_status_previous,
        hiv_status_current,
        on_art_treatment,
        art_clinic_number,
        hiv_treatment_start,
        not_art_reason,
        not_art_reasons_other,
        neonatal_death,
        neonatal_death_count,
        surviving_child,
        surviving_child_count,
        stillbirth,
        outcome_submitted
    }

    public PncRegistrationDetails(@NonNull String baseEntityId, @NonNull Date eventDate, @NonNull HashMap<String, String> properties) {
        super(baseEntityId, eventDate, properties);
    }

}
