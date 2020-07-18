package org.smartregister.pnc.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;
import java.util.Map;

public class PncVisitInfoRepository extends BaseRepository implements PncGenericDao<Map<String, String>> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_VISIT_INFO + "("
            + PncDbConstants.Column.PncVisit.BASE_ENTITY_ID + " VARCHAR NULL, "
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
        contentValues.put(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID));
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

}