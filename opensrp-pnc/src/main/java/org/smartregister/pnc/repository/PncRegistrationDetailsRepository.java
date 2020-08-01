package org.smartregister.pnc.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.HashMap;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegistrationDetailsRepository extends PncDetailsRepository {

    private static final String TABLE = PncDbConstants.Table.PNC_REGISTRATION_DETAILS;

    private String[] propertyNames;

    public static void createTable(@NonNull SQLiteDatabase database) {
        String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE + "("
                + PncDbConstants.Column.PncDetails.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
                + PncDbConstants.Column.PncDetails.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), "
                + PncDbConstants.Column.PncDetails.EVENT_DATE + " DATETIME NOT NULL, ";

        for (PncRegistrationDetails.Property column: PncRegistrationDetails.Property.values()) {
            CREATE_TABLE_SQL += column.name() + " VARCHAR, ";
        }

        CREATE_TABLE_SQL += "UNIQUE(" + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL("CREATE INDEX " + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + "_" + TABLE
                + " ON " + TABLE + " (" + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + ")");

        database.execSQL("CREATE INDEX " + PncDbConstants.Column.PncDetails.EVENT_DATE + "_" + TABLE
                + " ON " + TABLE + " (" + PncDbConstants.Column.PncDetails.EVENT_DATE + ")");
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public String[] getPropertyNames() {
        if (propertyNames == null) {
            PncRegistrationDetails.Property[] properties = PncRegistrationDetails.Property.values();
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
                        "SELECT *, pvi.created_at AS latest_visit_date, prd.base_entity_id as base_entity_id, prd._id AS _id FROM " + PncDbConstants.KEY.TABLE + " AS ec \n" +
                                "LEFT JOIN " + PncDbConstants.Table.PNC_REGISTRATION_DETAILS + " AS prd ON prd." + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " = ec." + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " \n" +
                                "LEFT JOIN " + PncDbConstants.Table.PNC_VISIT_INFO + " AS pvi ON pvi." + PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID + " = prd." + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " \n" +
                                "WHERE ec." + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " = '" + baseEntityId + "'").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }
}