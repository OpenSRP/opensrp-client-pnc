package org.smartregister.pnc.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncDbConstants.Column.PncMedicInfo;

import java.util.HashMap;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncMedicInfoRepository extends PncDetailsRepository {

    protected static final String TABLE = PncDbConstants.Table.PNC_MEDIC_INFO;

    private static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE + "("
            + PncMedicInfo.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + PncMedicInfo.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncMedicInfo.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), "
            + PncMedicInfo.EVENT_DATE + " DATETIME NOT NULL, ";

    private String[] propertyNames;

    static {
        for (Property column : Property.values()) {
            CREATE_TABLE_SQL += column.name() + " VARCHAR, ";
        }

        CREATE_TABLE_SQL += "UNIQUE(" + PncMedicInfo.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";
    }

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE
            + "_" + PncMedicInfo.BASE_ENTITY_ID + "_index ON " + TABLE +
            "(" + PncMedicInfo.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_EVENT_DATE = "CREATE INDEX " + TABLE
            + "_" + PncMedicInfo.EVENT_DATE + "_index ON " + TABLE +
            "(" + PncMedicInfo.EVENT_DATE + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_EVENT_DATE);
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public String[] getPropertyNames() {
        if (propertyNames == null) {
            Property[] properties = Property.values();
            propertyNames = new String[properties.length];

            for (int i = 0; i < properties.length; i++) {
                propertyNames[i] = properties[i].name();
            }
        }

        return propertyNames;
    }

    public HashMap<String, String> findByBaseEntityId(@NonNull String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "SELECT *, pmi.base_entity_id as base_entity_id, pmi._id AS _id FROM " + PncDbConstants.KEY.TABLE + " AS ec \n" +
                                "LEFT JOIN " + PncDbConstants.Table.PNC_MEDIC_INFO + " AS pmi ON pmi." + PncDbConstants.Column.PncMedicInfo.BASE_ENTITY_ID + " = ec." + PncDbConstants.Column.Client.BASE_ENTITY_ID + " \n" +
                                "WHERE ec." + PncDbConstants.Column.Client.BASE_ENTITY_ID + " = '" + baseEntityId + "'").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }

    public HashMap<String, String> findWithVisitInfoByBaseEntityId(@NonNull String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "SELECT *, MAX(pvi.created_at) AS latest_visit_date, pmi.base_entity_id as base_entity_id, pmi._id AS _id FROM " + PncDbConstants.KEY.TABLE + " AS ec \n" +
                                "LEFT JOIN " + PncDbConstants.Table.PNC_MEDIC_INFO + " AS pmi ON pmi." + PncDbConstants.Column.PncMedicInfo.BASE_ENTITY_ID + " = ec." + PncDbConstants.Column.Client.BASE_ENTITY_ID + " \n" +
                                "LEFT JOIN " + PncDbConstants.Table.PNC_VISIT_INFO + " AS pvi ON pvi." + PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID + " = ec." + PncDbConstants.Column.Client.BASE_ENTITY_ID + " \n" +
                                "WHERE ec." + PncDbConstants.Column.Client.BASE_ENTITY_ID + " = '" + baseEntityId + "'").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }

    public enum Property{

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
        not_art_reasons_other
    }
}