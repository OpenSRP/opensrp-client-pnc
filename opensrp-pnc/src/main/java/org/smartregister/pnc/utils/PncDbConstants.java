package org.smartregister.pnc.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface PncDbConstants {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    interface KEY {
        String ID = "_id";
        String FIRST_NAME = "first_name";
        String MIDDLE_NAME = "middle_name";
        String LAST_NAME = "last_name";
        String DOB = "dob";

        String REGISTER_ID = "register_id";
        String BASE_ENTITY_ID = "base_entity_id";
        String GA = "ga";
        String CONCEPTION_DATE = "conception_date";

        String TABLE = "ec_client";
        String OPENSRP_ID = "opensrp_id";
        String LAST_INTERACTED_WITH = "last_interacted_with";
        String DATE_REMOVED = "date_removed";
    }

    interface Column {

        interface Client {
            String ID = "_id";
            String PHOTO = "photo";
            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String BASE_ENTITY_ID = "base_entity_id";
            String DOB = "dob";
            String OPENSRP_ID = "opensrp_id";
            String RELATIONALID = "relationalid";
            String NATIONAL_ID = "national_id";
            String GENDER = "gender";
        }

        interface PncDetails {
            String ID = "_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String PENDING_OUTCOME = "pending_outcome";
            String PARA = "para";
            String GRAVIDA = "gravida";
            String RECORDED_AT = "recorded_at";
            String CONCEPTION_DATE = "conception_date";
            String HIV_STATUS = "hiv_status";
            String EVENT_DATE = "event_date";
            String CREATED_AT = "created_at";
        }

        interface PncBaby {
            String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String DISCHARGED_ALIVE = "discharged_alive";
            String CHILD_REGISTERED = "child_registered";
            String BIRTH_RECORD = "birth_record_date";
            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String DOB = "dob";
            String GENDER = "gender";
            String BIRTH_WEIGTH_ENTERED = "birth_weight_entered";
            String BIRTH_WEIGHT = "birth_weight";
            String BIRTH_HEIGHT_ENTERED = "birth_height_entered";
            String APGAR = "apgar";
            String FIRST_CRY = "first_cry";
            String COMPLICATIONS = "complications";
            String COMPLICATIONS_OTHER = "complications_other";
            String CARE_MGT = "care_mgt";
            String CARE_MGT_SPECIFY = "care_mgt_specify";
            String REF_LOCATION = "referral_location";
            String BF_FIRST_HOUR = "bf_first_hour";
            String NVP_ADMINISTRATION = "nvp_administration";
            String CHILD_HIV_STATUS = "child_hiv_status";
        }

        interface PncVisit {
            String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String CREATED_AT = "created_at";
            String PERIOD = "pnc_visit_period";
            String FIRST_VISIT_CHECK = "pnc_first_visit_check";
            String OUTSIDE_FACILITY = "visits_outside_facility";
            String OUTSIDE_FACILITY_NUMBER = "visits_outside_facility_number";
            String OTHER_VISIT_DATE = "other_pnc_visit_date_group";
            String COMPLICATIONS = "pnc_complications";
            String COMPLICATIONS_OTHER = "pnc_complications_other";
            String STATUS_C_SECTION = "status_csection";
            String EPISOTOMY_TEAR_STATUS = "episotomy_tear_status";
            String LOCHIA_STATUS = "lochia_status";
            String LOCHIA_STATUS_OTHER = "lochia_status_other";
            String UTERUS_STATUS = "uterus_status";
            String UTERUS_STATUS_OTHER = "uterus_status_other";
            String INTERVENTION_GIVEN = "intervention_given";
            String INTERVENTION_GIVEN_TEXT = "intervention_given_text";
            String REFERRED_OUT = "referred_out";
            String REFERRED_OUT_SPECIFY = "referred_out_specify";
            String BREAST_FEEDING = "breastfeeding";
            String NOT_BREAST_FEEDING_REASON = "not_breastfeeding_reason";
            String VIT_A = "vit_a";
            String VIT_A_NOT_GIVING_REASON = "vit_a_not_given_reason";
            String FP_COUNSEL = "fp_counsel";
            String FP_METHOD = "fp_method";
            String FP_METHOD_OTHER = "fp_method_other";
        }

        interface PncVisitChildStatus {
            String PARENT_RELATION_ID = "parent_relation_id";
            String CHILD_RELATION_ID = "child_relation_id";
            String BABY_AGE = "baby_age";
            String BABY_FIRST_NAME = "baby_first_name";
            String BABY_LAST_NAME = "baby_last_name";
            String BABY_STATUS = "baby_status";
            String DATE_OF_DEATH_BABY = "date_of_death_baby";
            String PLACE_OF_DEATH_BABY = "place_of_death_baby";
            String CAUSE_OF_DEATH_BABY = "cause_of_death_baby";
            String DEATH_FOLLOW_UP_BABY = "death_follow_up_baby";
            String BABY_BREAST_FEEDING = "breastfeeding";
            String BABY_NOT_BREAST_FEEDING_REASON = "not_breastfeeding_reason";
            String BABY_DANGER_SIGNS = "baby_danger_signs";
            String BABY_DANGER_SIGNS_OTHER = "baby_danger_signs_other";
            String BABY_REFERRED_OUT = "baby_referred_out";
            String BABY_HIV_EXPOSED = "baby_hiv_exposed";
            String MOTHER_BABY_PAIRING = "mother_baby_pairing";
            String BABY_HIV_TREATMENT = "baby_hiv_treatment";
            String NOT_ART_PAIRING_REASON = "not_art_pairing_reason";
            String NOT_ART_PAIRING_REASON_OTHER = "not_art_pairing_reason_other";
            String BABY_DBS = "baby_dbs";
            String BABY_CARE_MGMT = "baby_care_mgt";
        }

        interface PncStillBorn {
            String STILL_BIRTH_CONDITION = "still_birth_condition";
        }
    }

    interface Table {
        String EC_CLIENT = "ec_client";
        String PNC_DETAILS = "pnc_details";
        String PNC_REGISTRATION_DETAILS = "pnc_registration_details";
        String PNC_OUTCOME_FORM = "pnc_outcome_form";
        String PNC_BABY = "pnc_baby";
        String PNC_STILL_BORN = "pnc_still_born";
        String PNC_VISIT_INFO = "pnc_visit_info";
        String PNC_VISIT_CHILD_STATUS = "pnc_visit_child_status";
    }
}
