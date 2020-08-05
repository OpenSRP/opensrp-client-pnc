package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.pojo.PncVisitSummary;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncDbConstants.Column.PncVisitChildStatus;
import org.smartregister.pnc.utils.PncDbConstants.Column.PncVisitInfo;
import org.smartregister.pnc.utils.PncDbConstants.Table;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class PncVisitInfoRepository extends BaseRepository implements PncGenericDao<Map<String, String>> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_VISIT_INFO + "("
            + PncDbConstants.Column.PncVisitInfo.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.VISIT_ID + " VARCHAR NULL, "
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


    private static final String INDEX_MOTHER_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_VISIT_INFO
            + "_" + PncVisitInfo.MOTHER_BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_VISIT_INFO +
            "(" + PncVisitInfo.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_VISIT_INFO
            + "_" + PncVisitInfo.VISIT_ID + "_index ON " + PncDbConstants.Table.PNC_VISIT_INFO +
            "(" + PncVisitInfo.VISIT_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_MOTHER_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }

    @Override
    public boolean saveOrUpdate(Map<String, String> data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisitInfo.MOTHER_BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.VISIT_ID, data.get(PncDbConstants.Column.PncVisitInfo.VISIT_ID));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.VISIT_DATE, data.get(PncDbConstants.Column.PncVisitInfo.VISIT_DATE));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.CREATED_AT, System.currentTimeMillis());
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

    @NonNull
    public PncVisitSummary getPncVisitSummaries(@NonNull String motherBaseEntityId, int pageNo) {

        Map<String, Map<String, String>> visitInfoMap = new HashMap<>();
        Map<String, List<Map<String, String>>> visitChildStatusMap = new HashMap<>();
        PncVisitSummary pncVisitSummary = new PncVisitSummary(visitInfoMap, visitChildStatusMap);

        try {
            String[] visitIds = getVisitIds(motherBaseEntityId, pageNo);
            String joinedIds = "'" + StringUtils.join(visitIds, "','") + "'";

            String query = "SELECT *, pvcs._id AS baby_row_id FROM " + Table.PNC_VISIT_INFO + " AS pvi \n" +
                    "LEFT JOIN " + Table.PNC_VISIT_CHILD_STATUS + " AS pvcs ON pvcs." + PncVisitChildStatus.VISIT_ID + " = pvi." + PncVisitInfo.VISIT_ID + " \n" +
                    "WHERE pvi." + PncVisitInfo.ID + " IN (" + joinedIds + ")";

            ArrayList<HashMap<String, String>> data = rawQuery(getReadableDatabase(), query);

            for (HashMap<String, String> record : data) {

                if (!visitInfoMap.containsKey(record.get(PncVisitInfo.VISIT_ID))) {
                    visitInfoMap.put(record.get(PncVisitInfo.VISIT_ID), record);

                    if (record.get("baby_row_id") != null) {
                        List<Map<String, String>> childList = new ArrayList<>();
                        childList.add(record);
                        visitChildStatusMap.put(record.get(PncVisitInfo.VISIT_ID), childList);
                    }
                }
                else {
                    List<Map<String, String>> childList = visitChildStatusMap.get(record.get(PncVisitInfo.VISIT_ID));
                    Objects.requireNonNull(childList).add(record);
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        }

        return pncVisitSummary;
    }

    public int getVisitPageCount(@NonNull String baseEntityId) {
        Cursor mCursor = null;
        int pageCount = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String query = String.format("SELECT count(%s) FROM %s WHERE %s = '%s'"
                    , PncVisitInfo.ID
                    , PncDbConstants.Table.PNC_VISIT_INFO
                    , PncVisitInfo.MOTHER_BASE_ENTITY_ID
                    , baseEntityId
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        int recordCount = mCursor.getInt(0);
                        pageCount = (int) Math.ceil(recordCount / 10d);
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return pageCount;
    }

    public String[] getVisitIds(@NonNull String motherBaseEntityId, int pageNo) {
        ArrayList<String> visitIds = new ArrayList<>();
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            int offset = pageNo * 10;

            String query = String.format(Locale.getDefault(), "SELECT %s FROM %s WHERE %s = '%s' ORDER BY %s DESC LIMIT 10 OFFSET %d "
                    , PncVisitInfo.ID
                    , Table.PNC_VISIT_INFO
                    , PncVisitInfo.MOTHER_BASE_ENTITY_ID
                    , motherBaseEntityId
                    , PncVisitInfo.VISIT_DATE
                    , offset
            );

            if (StringUtils.isNotBlank(motherBaseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        visitIds.add(mCursor.getString(0));
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return visitIds.toArray(new String[0]);
    }

}