package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PncVisitInfoRepository extends BaseRepository implements PncGenericDao<Map<String, String>> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_VISIT_INFO + "("
            + PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.CREATED_AT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.PERIOD + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.FIRST_VISIT_CHECK + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY_NUMBER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.OTHER_VISIT_DATE + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.COMPLICATIONS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.COMPLICATIONS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.STATUS_C_SECTION + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.EPISOTOMY_TEAR_STATUS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.LOCHIA_STATUS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.LOCHIA_STATUS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.UTERUS_STATUS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.UTERUS_STATUS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN_TEXT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.REFERRED_OUT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.REFERRED_OUT_SPECIFY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BREAST_FEEDING + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.NOT_BREAST_FEEDING_REASON + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.VIT_A + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.VIT_A_NOT_GIVING_REASON + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.FP_COUNSEL + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.FP_METHOD + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.FP_METHOD_OTHER + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_VISIT_INFO
            + "_" + PncDbConstants.Column.PncVisit.BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_VISIT_INFO +
            "(" + PncDbConstants.Column.PncVisit.BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(Map<String, String> data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisit.CREATED_AT, System.currentTimeMillis());
        contentValues.put(PncDbConstants.Column.PncVisit.PERIOD, data.get(PncDbConstants.Column.PncVisit.PERIOD));
        contentValues.put(PncDbConstants.Column.PncVisit.FIRST_VISIT_CHECK, data.get(PncDbConstants.Column.PncVisit.FIRST_VISIT_CHECK));
        contentValues.put(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY, data.get(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY));
        contentValues.put(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY_NUMBER, data.get(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY_NUMBER));
        contentValues.put(PncDbConstants.Column.PncVisit.OTHER_VISIT_DATE, data.get(PncDbConstants.Column.PncVisit.OTHER_VISIT_DATE));
        contentValues.put(PncDbConstants.Column.PncVisit.COMPLICATIONS, data.get(PncDbConstants.Column.PncVisit.COMPLICATIONS));
        contentValues.put(PncDbConstants.Column.PncVisit.COMPLICATIONS_OTHER, data.get(PncDbConstants.Column.PncVisit.COMPLICATIONS_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisit.STATUS_C_SECTION, data.get(PncDbConstants.Column.PncVisit.STATUS_C_SECTION));
        contentValues.put(PncDbConstants.Column.PncVisit.EPISOTOMY_TEAR_STATUS, data.get(PncDbConstants.Column.PncVisit.EPISOTOMY_TEAR_STATUS));
        contentValues.put(PncDbConstants.Column.PncVisit.LOCHIA_STATUS, data.get(PncDbConstants.Column.PncVisit.LOCHIA_STATUS));
        contentValues.put(PncDbConstants.Column.PncVisit.LOCHIA_STATUS_OTHER, data.get(PncDbConstants.Column.PncVisit.LOCHIA_STATUS_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisit.UTERUS_STATUS, data.get(PncDbConstants.Column.PncVisit.UTERUS_STATUS));
        contentValues.put(PncDbConstants.Column.PncVisit.UTERUS_STATUS_OTHER, data.get(PncDbConstants.Column.PncVisit.UTERUS_STATUS_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN, data.get(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN));
        contentValues.put(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN_TEXT, data.get(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN_TEXT));
        contentValues.put(PncDbConstants.Column.PncVisit.REFERRED_OUT, data.get(PncDbConstants.Column.PncVisit.REFERRED_OUT));
        contentValues.put(PncDbConstants.Column.PncVisit.REFERRED_OUT_SPECIFY, data.get(PncDbConstants.Column.PncVisit.REFERRED_OUT_SPECIFY));
        contentValues.put(PncDbConstants.Column.PncVisit.BREAST_FEEDING, data.get(PncDbConstants.Column.PncVisit.BREAST_FEEDING));
        contentValues.put(PncDbConstants.Column.PncVisit.NOT_BREAST_FEEDING_REASON, data.get(PncDbConstants.Column.PncVisit.NOT_BREAST_FEEDING_REASON));
        contentValues.put(PncDbConstants.Column.PncVisit.VIT_A, data.get(PncDbConstants.Column.PncVisit.VIT_A));
        contentValues.put(PncDbConstants.Column.PncVisit.VIT_A_NOT_GIVING_REASON, data.get(PncDbConstants.Column.PncVisit.VIT_A_NOT_GIVING_REASON));
        contentValues.put(PncDbConstants.Column.PncVisit.FP_COUNSEL, data.get(PncDbConstants.Column.PncVisit.FP_COUNSEL));
        contentValues.put(PncDbConstants.Column.PncVisit.FP_METHOD, data.get(PncDbConstants.Column.PncVisit.FP_METHOD));
        contentValues.put(PncDbConstants.Column.PncVisit.FP_METHOD_OTHER, data.get(PncDbConstants.Column.PncVisit.FP_METHOD_OTHER));
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.PNC_VISIT_INFO, null, contentValues);
        return rows != -1;
    }

    @Override
    public Map<String, String> findOne(Map<String, String> pncChild) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean delete(Map<String, String> pncChild) {
        throw new NotImplementedException("");
    }

    @Override
    public List<Map<String, String>> findAll() {
        throw new NotImplementedException("");
    }

    public Map<String, String> getLatestVisitByParent(String parentBaseEntityId) {
        Map<String, String> data = null;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PncDbConstants.Table.PNC_VISIT_INFO + " WHERE " + PncDbConstants.Column.PncVisit.CREATED_AT + " = (SELECT MAX(" + PncDbConstants.Column.PncVisit.CREATED_AT +") FROM " + PncDbConstants.Table.PNC_VISIT_INFO + ") AND " + PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID + " = '" + parentBaseEntityId + "'", null);

        if (cursor.getCount() > 0 && cursor.moveToNext()) {
            data = convert(cursor);
            cursor.close();
        }
        return data;
    }

    private Map<String, String> convert(Cursor cursor) {
        Map<String, String> data = new HashMap<>();
        data.put(PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID)));
        data.put(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID)));
        data.put(PncDbConstants.Column.PncVisit.CREATED_AT, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.CREATED_AT)));
        data.put(PncDbConstants.Column.PncVisit.PERIOD, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.PERIOD)));
        data.put(PncDbConstants.Column.PncVisit.FIRST_VISIT_CHECK, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.FIRST_VISIT_CHECK)));
        data.put(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY)));
        data.put(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY_NUMBER, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.OUTSIDE_FACILITY_NUMBER)));
        data.put(PncDbConstants.Column.PncVisit.OTHER_VISIT_DATE, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.OTHER_VISIT_DATE)));
        data.put(PncDbConstants.Column.PncVisit.COMPLICATIONS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.COMPLICATIONS)));
        data.put(PncDbConstants.Column.PncVisit.COMPLICATIONS_OTHER, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.COMPLICATIONS_OTHER)));
        data.put(PncDbConstants.Column.PncVisit.STATUS_C_SECTION, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.STATUS_C_SECTION)));
        data.put(PncDbConstants.Column.PncVisit.EPISOTOMY_TEAR_STATUS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.EPISOTOMY_TEAR_STATUS)));
        data.put(PncDbConstants.Column.PncVisit.LOCHIA_STATUS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.LOCHIA_STATUS)));
        data.put(PncDbConstants.Column.PncVisit.LOCHIA_STATUS_OTHER, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.LOCHIA_STATUS_OTHER)));
        data.put(PncDbConstants.Column.PncVisit.UTERUS_STATUS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.UTERUS_STATUS)));
        data.put(PncDbConstants.Column.PncVisit.UTERUS_STATUS_OTHER, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.UTERUS_STATUS_OTHER)));
        data.put(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN)));
        data.put(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN_TEXT, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.INTERVENTION_GIVEN_TEXT)));
        data.put(PncDbConstants.Column.PncVisit.REFERRED_OUT, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.REFERRED_OUT)));
        data.put(PncDbConstants.Column.PncVisit.REFERRED_OUT_SPECIFY, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.REFERRED_OUT_SPECIFY)));
        data.put(PncDbConstants.Column.PncVisit.BREAST_FEEDING, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.BREAST_FEEDING)));
        data.put(PncDbConstants.Column.PncVisit.NOT_BREAST_FEEDING_REASON, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.NOT_BREAST_FEEDING_REASON)));
        data.put(PncDbConstants.Column.PncVisit.VIT_A, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.VIT_A)));
        data.put(PncDbConstants.Column.PncVisit.VIT_A_NOT_GIVING_REASON, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.VIT_A_NOT_GIVING_REASON)));
        data.put(PncDbConstants.Column.PncVisit.FP_COUNSEL, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.FP_COUNSEL)));
        data.put(PncDbConstants.Column.PncVisit.FP_METHOD, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.FP_METHOD)));
        data.put(PncDbConstants.Column.PncVisit.FP_METHOD_OTHER, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.FP_METHOD_OTHER)));

        return data;
    }
}