package org.smartregister.pnc.utils;

import org.smartregister.AllConstants;

public class PncConstants extends AllConstants {

    public static final String SEX = "Sex";
    public static final String CONFIG = "pnc_register";
    public static final String MOTHER = "mother";
    public static final String GLOBAL = "global";

    public interface IntentKey {
        String BASE_ENTITY_ID = "base-entity-id";
        String CLIENT_OBJECT = "common_person_object_client";
        String ENTITY_TABLE = "entity_table";
    }

    public interface Event {

        interface PncRegistration {

            String CONCEPTION_DATE = "conception_date";
            String PARA = "parity";
            String GRAVIDA = "gravidity";
            String PREVIOUS_HIV_STATUS = "hiv_status_previous";
            String CURRENT_HIV_STATUS = "hiv_status_current";
        }
    }

    public interface FactKey {

        interface ProfileOverview {
            String PREGNANCY_STATUS = "pregnancy_status";
            String GRAVIDA = "gravida";
            String PARA = "para";
            String GESTATION_WEEK = "gestation_week";
            String INTAKE_TIME = "intake_time";
            String HIV_STATUS = "hiv_status";
            String CURRENT_HIV_STATUS = "current_hiv_status";
        }
    }

    public interface JsonFormField {
        String MOTHER_HIV_STATUS = "mother_hiv_status";
    }

    public interface JsonFormWidget {
        String MULTI_SELECT_DRUG_PICKER = "multi_select_drug_picker";
    }

    public static class JsonFormKeyConstants {
        public static final String OPTIONS = "options";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DOB = "dob";
        public static final String DOB_UNKNOWN = "dob_unknown";

        public static final String AGE_ENTERED = "age_entered";
        public static final String DOB_ENTERED = "dob_entered";
        public static final String HOME_ADDRESS_WIDGET_KEY = "home_address";
        public static final String VILLAGE_ADDRESS_WIDGET_KEY = "village";

        public static final String SERVICE_FEE = "service_fee";
        public static final String VISIT_ID = "visitId";
        public static final String DOSAGE = "dosage";
        public static final String DURATION = "duration";
        public static final String ID = "ID";
        public static final String VISIT_END_DATE = "visit_end_date";

        public static final String BHT_ID = "bht_mid";
        public static final String HOME_ADDRESS = "home_address";
        public static final String ENCOUNTER_TYPE = "encounter_type";
        public static final String ENTITY_ID = "entity_id";
        public static final String AGE = "age";
        public static final String PNC_EDIT_FORM_TITLE = "Update Pnc Registration";
        public static final String FORM_TITLE = "title";
        public static final String OPENSRP_ID = "opensrp_id";
        public static final String LIVE_BIRTHS = "baby_alive_group";
        public static final String OTHER_VISIT_GROUP = "other_pnc_visit_date_group";
        public static final String CHILD_STATUS_GROUP = "child_status";
        public static final String BABIES_STILLBORN = "baby_stillborn_group";
        public static final String DISCHARGED_ALIVE = "discharged_alive";
        public static final String ZEIR_ID = "zeir_id";
        public static final String BABIES_BORN_MAP = "babies_born_map";
        public static final String BABIES_STILL_BORN_MAP = "babies_still_born_map";
        public static final String OTHER_VISIT_MAP = "other_visit_map";
        public static final String CHILD_STATUS_MAP = "child_status_map";
        public static final String OUTCOME_SUBMITTED = "outcome_submitted";
        public static final String BABY_COUNT_ALIVE = "baby_count_alive";


        public static final String CHILD_REGISTERED = "child_registered";
        public static final String BIRTH_RECORD = "birth_record_date";
        public static final String BABY_FIRST_NAME = "baby_first_name";
        public static final String BABY_LAST_NAME = "baby_last_name";
        public static final String BABY_DOB = "baby_dob";
        public static final String BABY_GENDER = "baby_gender";
        public static final String BIRTH_WEIGHT_ENTERED = "birth_weight_entered";
        public static final String BIRTH_WEIGHT = "birth_weight";
        public static final String BIRTH_HEIGHT_ENTERED = "birth_height_entered";
        public static final String APGAR = "apgar";
        public static final String BABY_FIRST_CRY = "baby_first_cry";
        public static final String BABY_COMPLICATIONS = "baby_complications";
        public static final String BABY_COMPLICATIONS_OTHER = "baby_complications_other";
        public static final String BABY_CARE_MGMT = "baby_care_mgt";
        public static final String BABY_CARE_MGMT_SPECIFY = "baby_care_mgt_specify";
        public static final String BABY_REF_LOCATION = "baby_referral_location";
        public static final String BF_FIRST_HOUR = "bf_first_hour";
        public static final String CHILD_HIV_STATUS = "child_hiv_status";
        public static final String NVP_ADMINISTRATION = "nvp_administration";

        public static final String STILL_BIRTH_CONDITION = "stillbirth_condition";
    }

    public static class JsonFormExtraConstants {
        public static final String NEXT = "next";
        public static final String JSON = "json";
        public static final String ID = "id";
    }

    public static class JsonFormStepNameConstants {
        public static final String LIVE_BIRTHS = "Live Births";
        public static final String STILL_BIRTHS = "Still Births";
        public static final String PNC_VISIT_INFO = "PNC Visit information";
        public static final String PNC_VISIT_CHILD_STATUS = "Child's Status";
    }

    public static class OpenMrsConstants {
        public static final String ENTITY = "openmrs_entity";
        public static final String ENTITY_ID = "openmrs_entity_id";
    }

    public static final class KeyConstants {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String PHOTO = "photo";
        public static final String LOOK_UP = "look_up";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String OPENSRP_ID = "opensrp_id";
        public static final String RELATIONALID = "relationalid";
        public static final String GENDER = "gender";
    }

    public static class EntityConstants {
        public static final String PERSON = "person";
    }

    public static class BooleanIntConstants {
        public static final int TRUE = 1;
    }

    public static final class FormActivityConstants {
        public static final String ENABLE_ON_CLOSE_DIALOG = "EnableOnCloseDialog";
    }

    public static final class EventTypeConstants {
        public static final String PNC_REGISTRATION = "PNC Registration";
        public static final String PNC_OUTCOME = "PNC Medic Information";
        public static final String UPDATE_PNC_REGISTRATION = "Update Pnc Registration";
        public static final String PNC_CLOSE = "Pnc Close";
        public static final String BIRTH_REGISTRATION = "Birth Registration";
        public static final String PNC_VISIT = "PNC Visit";
    }

    public interface ColumnMapKey {
        String REGISTER_ID = "register_id";
        String PENDING_OUTCOME = "pending_outcome";
    }

    public interface DateFormat {
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    }

    public interface Form {
        String PNC_REGISTRATION = "pnc_registration";
        String PNC_MEDIC_INFORMATION = "pnc_medic_information";
        String PNC_VISIT = "pnc_visit";
        String PNC_CLOSE = "pnc_close";
    }

    public interface FormValue {
        String IS_DOB_UNKNOWN = "isDobUnknown";
        String IS_ENROLLED_IN_MESSAGES = "isEnrolledInSmsMessages";
        String OTHER = "other";
    }

    public interface RegisterType {
        String PNC = "pnc";
    }

    public interface ClientMapKey {
        String GENDER = "gender";
    }

    public interface FormGlobalConstants {
        String DELIVERY_DATE = "delivery_date";
        String BABY_DOB = "baby_dob";
        String PNC_VISIT_PERIOD = "pnc_visit_period";
        String BABY_AGE = "baby_age";
        String HIV_STATUS_PREVIOUS = "hiv_status_previous";
        String HIV_STATUS_CURRENT = "hiv_status_current";
        String BABY_COMPLICATIONS = "baby_complications";
    }
}
