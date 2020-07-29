package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class PncVisitInfoRepository extends BaseRepository implements PncGenericDao<Map<String, String>> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_VISIT_INFO + "("
            + PncDbConstants.Column.PncVisitInfo.PARENT_BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisitInfo.CREATED_AT + " VARCHAR NULL, "
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
            + "_" + PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_VISIT_INFO +
            "(" + PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(Map<String, String> data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncVisitInfo.PARENT_BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisitInfo.PARENT_BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisitInfo.BASE_ENTITY_ID));
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

    @NonNull
    public List<Map<String, Object>> getPncVisitSummaries(@NonNull String motherBaseEntityId, int pageNo) {
        List<Map<String, Object>> data = new ArrayList<>();

        Cursor cursor = null;
        Cursor subCursor = null;
        try {
            String[] visitIds = getVisitIds(motherBaseEntityId, pageNo);
            String joinedIds = "'" + StringUtils.join(visitIds, "','") + "'";

            String query = "SELECT * FROM " + PncDbConstants.Table.PNC_VISIT_INFO + " " +
                    " WHERE " + PncDbConstants.Column.PncVisit.MOTHER_BASE_ENTITY_ID + " ='" + motherBaseEntityId + "'  AND " + PncDbConstants.Column.PncVisit.BASE_ENTITY_ID + " IN (" + joinedIds + ") " +
                    " ORDER BY " + PncDbConstants.Column.PncVisit.CREATED_AT + " DESC";

            cursor = getReadableDatabase().rawQuery(query, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    Map<String, Object> record = convert(cursor);

                    List<Map<String, String>> childData = new ArrayList<>();
                    record.put(PncConstants.CHILD_RECORDS, childData);

                    String subQuery = "SELECT pb.first_name, pb.last_name, pb.dob, pvcs.* FROM pnc_visit_child_status AS pvcs " +
                            "LEFT JOIN pnc_baby AS pb ON pb.base_entity_id = pvcs.child_relation_id " +
                            "WHERE pvcs.parent_relation_id = '" + record.get(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID) + "'";

                    subCursor = getReadableDatabase().rawQuery(subQuery, null);

                    if (subCursor.getCount() > 0) {

                        while (subCursor.moveToNext()) {

                            Map<String, String> childRecord = new HashMap<>();
                            childRecord.put(PncDbConstants.Column.PncBaby.FIRST_NAME, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncBaby.FIRST_NAME)));
                            childRecord.put(PncDbConstants.Column.PncBaby.LAST_NAME, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncBaby.LAST_NAME)));
                            childRecord.put(PncDbConstants.Column.PncBaby.DOB, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncBaby.DOB)));
                            childRecord.put(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.PARENT_RELATION_ID, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.PARENT_RELATION_ID)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.CHILD_RELATION_ID, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.CHILD_RELATION_ID)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_STATUS, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_STATUS)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.DATE_OF_DEATH_BABY, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.DATE_OF_DEATH_BABY)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.PLACE_OF_DEATH_BABY, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.PLACE_OF_DEATH_BABY)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.CAUSE_OF_DEATH_BABY, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.CAUSE_OF_DEATH_BABY)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.DEATH_FOLLOW_UP_BABY, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.DEATH_FOLLOW_UP_BABY)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_BREAST_FEEDING, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_BREAST_FEEDING)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_NOT_BREAST_FEEDING_REASON, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_NOT_BREAST_FEEDING_REASON)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS_OTHER, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS_OTHER)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_REFERRED_OUT, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_REFERRED_OUT)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_EXPOSED, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_EXPOSED)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.MOTHER_BABY_PAIRING, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.MOTHER_BABY_PAIRING)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_TREATMENT, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_TREATMENT)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON_OTHER, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON_OTHER)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_DBS, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_DBS)));
                            childRecord.put(PncDbConstants.Column.PncVisitChildStatus.BABY_CARE_MGMT, subCursor.getString(subCursor.getColumnIndex(PncDbConstants.Column.PncVisitChildStatus.BABY_CARE_MGMT)));

                            childData.add(childRecord);
                        }
                    }

                    data.add(record);
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (subCursor != null) {
                subCursor.close();
            }
        }

        return data;
    }

    public int getVisitPageCount(@NonNull String baseEntityId) {
        Cursor mCursor = null;
        int pageCount = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String query = String.format("SELECT count(%s) FROM %s WHERE %s = '%s'"
                    , PncDbConstants.Column.PncVisit.BASE_ENTITY_ID
                    , PncDbConstants.Table.PNC_VISIT_INFO
                    , PncDbConstants.Column.PncVisit.MOTHER_BASE_ENTITY_ID
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
                    , PncDbConstants.Column.PncVisit.BASE_ENTITY_ID
                    , PncDbConstants.Table.PNC_VISIT_INFO
                    , PncDbConstants.Column.PncVisit.MOTHER_BASE_ENTITY_ID
                    , motherBaseEntityId
                    , PncDbConstants.Column.PncVisit.CREATED_AT
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

    private Map<String, Object> convert(Cursor cursor) {
        Map<String, Object> data = new HashMap<>();
        data.put(PncDbConstants.Column.PncVisit.MOTHER_BASE_ENTITY_ID, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncVisit.MOTHER_BASE_ENTITY_ID)));
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