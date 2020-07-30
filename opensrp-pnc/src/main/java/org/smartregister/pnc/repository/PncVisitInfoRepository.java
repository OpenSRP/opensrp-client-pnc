package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PncVisitInfoRepository extends BaseRepository implements PncGenericDao<Map<String, String>> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_VISIT_INFO + "("
            + PncDbConstants.Column.PncVisitInfo.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.CREATED_AT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.VISIT_DATE + " DATETIME NOT NULL,"
            + PncDbConstants.Column.PncVisitInfo.PERIOD + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.FIRST_VISIT_CHECK + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY_NUMBER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.OTHER_VISIT_DATE + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.COMPLICATIONS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.COMPLICATIONS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.STATUS_C_SECTION + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.EPISOTOMY_TEAR_STATUS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.INTERVENTION_GIVEN + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.INTERVENTION_GIVEN_TEXT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.REFERRED_OUT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.REFERRED_OUT_SPECIFY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.BREAST_FEEDING + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.NOT_BREAST_FEEDING_REASON + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.VIT_A + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.VIT_A_NOT_GIVING_REASON + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.FP_COUNSEL + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.FP_METHOD + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.FP_METHOD_OTHER + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_VISIT_INFO
            + "_" + PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_VISIT_INFO +
            "(" + PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(Map<String, String> data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.CREATED_AT, System.currentTimeMillis());
        contentValues.put(PncDbConstants.Column.PncVisitInfo.VISIT_DATE, data.get(PncDbConstants.Column.PncVisitInfo.VISIT_DATE));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.PERIOD, data.get(PncDbConstants.Column.PncVisitInfo.PERIOD));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.FIRST_VISIT_CHECK, data.get(PncDbConstants.Column.PncVisitInfo.FIRST_VISIT_CHECK));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY, data.get(PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY_NUMBER, data.get(PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY_NUMBER));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.OTHER_VISIT_DATE, data.get(PncDbConstants.Column.PncVisitInfo.OTHER_VISIT_DATE));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.COMPLICATIONS, data.get(PncDbConstants.Column.PncVisitInfo.COMPLICATIONS));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.COMPLICATIONS_OTHER, data.get(PncDbConstants.Column.PncVisitInfo.COMPLICATIONS_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.STATUS_C_SECTION, data.get(PncDbConstants.Column.PncVisitInfo.STATUS_C_SECTION));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.EPISOTOMY_TEAR_STATUS, data.get(PncDbConstants.Column.PncVisitInfo.EPISOTOMY_TEAR_STATUS));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS, data.get(PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS_OTHER, data.get(PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS, data.get(PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS_OTHER, data.get(PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.INTERVENTION_GIVEN, data.get(PncDbConstants.Column.PncVisitInfo.INTERVENTION_GIVEN));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.INTERVENTION_GIVEN_TEXT, data.get(PncDbConstants.Column.PncVisitInfo.INTERVENTION_GIVEN_TEXT));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.REFERRED_OUT, data.get(PncDbConstants.Column.PncVisitInfo.REFERRED_OUT));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.REFERRED_OUT_SPECIFY, data.get(PncDbConstants.Column.PncVisitInfo.REFERRED_OUT_SPECIFY));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.BREAST_FEEDING, data.get(PncDbConstants.Column.PncVisitInfo.BREAST_FEEDING));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.NOT_BREAST_FEEDING_REASON, data.get(PncDbConstants.Column.PncVisitInfo.NOT_BREAST_FEEDING_REASON));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.VIT_A, data.get(PncDbConstants.Column.PncVisitInfo.VIT_A));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.VIT_A_NOT_GIVING_REASON, data.get(PncDbConstants.Column.PncVisitInfo.VIT_A_NOT_GIVING_REASON));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.FP_COUNSEL, data.get(PncDbConstants.Column.PncVisitInfo.FP_COUNSEL));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.FP_METHOD, data.get(PncDbConstants.Column.PncVisitInfo.FP_METHOD));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.FP_METHOD_OTHER, data.get(PncDbConstants.Column.PncVisitInfo.FP_METHOD_OTHER));
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.PNC_VISIT_INFO, null, contentValues);
        return rows != -1;
    }

    public Map<String, String> findOne(String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "SELECT * FROM " + PncDbConstants.Table.PNC_VISIT_INFO + " \n" +
                                "WHERE " + PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID + " = '" + baseEntityId + "'").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public Map<String, String> findOne(Map<String, String> data) {
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