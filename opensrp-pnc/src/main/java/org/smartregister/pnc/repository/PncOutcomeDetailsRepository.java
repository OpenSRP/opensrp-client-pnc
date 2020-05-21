package org.smartregister.pnc.repository;


import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.pnc.pojo.PncDetails;
import org.smartregister.pnc.utils.PncDbConstants;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncOutcomeDetailsRepository extends PncDetailsRepository {

    private String[] propertyNames;

    public static void createTable(@NonNull SQLiteDatabase database) {
        String CREATE_TABLE_SQL = "CREATE TABLE " + getTableNameStatic() + "("
                + PncDbConstants.Column.PncDetails.ID + " INTEGER NOT NULL PRIMARY Key AUTOINCREMENT, "
                + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
                + PncDbConstants.Column.PncDetails.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), "
                + PncDbConstants.Column.PncDetails.EVENT_DATE + " DATETIME NOT NULL, ";

        for (PncDetails.Property column: PncDetails.Property.values()) {
            CREATE_TABLE_SQL += column.name() + " VARCHAR, ";
        }

        CREATE_TABLE_SQL += "UNIQUE(" + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL("CREATE INDEX " + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + "_" + getTableNameStatic()
                + " ON " + getTableNameStatic() + " (" + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + ")");

        database.execSQL("CREATE INDEX " + PncDbConstants.Column.PncDetails.EVENT_DATE + "_" + getTableNameStatic()
                + " ON " + getTableNameStatic() + " (" + PncDbConstants.Column.PncDetails.EVENT_DATE + ")");
    }

    @NonNull
    private static String getTableNameStatic() {
        return PncDbConstants.Table.MATERNITY_DETAILS;
    }

    @Override
    public String getTableName() {
        return PncDbConstants.Table.MATERNITY_DETAILS;
    }

    @Override
    public String[] getPropertyNames() {
        if (propertyNames == null) {
            PncDetails.Property[] properties = PncDetails.Property.values();
            propertyNames = new String[properties.length];

            for (int i = 0; i < properties.length; i++) {
                propertyNames[i] = properties[i].name();
            }
        }

        return propertyNames;
    }
}
