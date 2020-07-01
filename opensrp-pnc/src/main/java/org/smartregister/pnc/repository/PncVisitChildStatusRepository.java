package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;
import java.util.Map;

public class PncVisitChildStatusRepository extends BaseRepository implements PncGenericDao<Map<String, String>> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_VISIT_CHILD_STATUS + "("
            + PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncVisit.BASE_ENTITY_ID + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_AGE + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_STATUS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.DATE_OF_DEATH_BABY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.PLACE_OF_DEATH_BABY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.CAUSE_OF_DEATH_BABY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.DEATH_FOLLOW_UP_BABY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_BREAST_FEEDING + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_NOT_BREAST_FEEDING_REASON + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_DANGER_SIGNS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_DANGER_SIGNS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_REFERRED_OUT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_HIV_EXPOSED + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.MOTHER_BABY_PAIRING + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_HIV_TREATMENT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.NOT_ART_PAIRING_REASON + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.NOT_ART_PAIRING_REASON_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_DBS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncVisit.BABY_CARE_MGMT + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_VISIT_CHILD_STATUS
            + "_" + PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_VISIT_CHILD_STATUS +
            "(" + PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(Map<String, String> data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisit.PARENT_BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID, data.get(PncDbConstants.Column.PncVisit.BASE_ENTITY_ID));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_AGE, data.get(PncDbConstants.Column.PncVisit.BABY_AGE));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_STATUS, data.get(PncDbConstants.Column.PncVisit.BABY_STATUS));
        contentValues.put(PncDbConstants.Column.PncVisit.DATE_OF_DEATH_BABY, data.get(PncDbConstants.Column.PncVisit.DATE_OF_DEATH_BABY));
        contentValues.put(PncDbConstants.Column.PncVisit.PLACE_OF_DEATH_BABY, data.get(PncDbConstants.Column.PncVisit.PLACE_OF_DEATH_BABY));
        contentValues.put(PncDbConstants.Column.PncVisit.CAUSE_OF_DEATH_BABY, data.get(PncDbConstants.Column.PncVisit.CAUSE_OF_DEATH_BABY));
        contentValues.put(PncDbConstants.Column.PncVisit.DEATH_FOLLOW_UP_BABY, data.get(PncDbConstants.Column.PncVisit.DEATH_FOLLOW_UP_BABY));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_BREAST_FEEDING, data.get(PncDbConstants.Column.PncVisit.BABY_BREAST_FEEDING));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_NOT_BREAST_FEEDING_REASON, data.get(PncDbConstants.Column.PncVisit.BABY_NOT_BREAST_FEEDING_REASON));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_DANGER_SIGNS, data.get(PncDbConstants.Column.PncVisit.BABY_DANGER_SIGNS));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_DANGER_SIGNS_OTHER, data.get(PncDbConstants.Column.PncVisit.BABY_DANGER_SIGNS_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_REFERRED_OUT, data.get(PncDbConstants.Column.PncVisit.BABY_REFERRED_OUT));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_HIV_EXPOSED, data.get(PncDbConstants.Column.PncVisit.BABY_HIV_EXPOSED));
        contentValues.put(PncDbConstants.Column.PncVisit.MOTHER_BABY_PAIRING, data.get(PncDbConstants.Column.PncVisit.MOTHER_BABY_PAIRING));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_HIV_TREATMENT, data.get(PncDbConstants.Column.PncVisit.BABY_HIV_TREATMENT));
        contentValues.put(PncDbConstants.Column.PncVisit.NOT_ART_PAIRING_REASON, data.get(PncDbConstants.Column.PncVisit.NOT_ART_PAIRING_REASON));
        contentValues.put(PncDbConstants.Column.PncVisit.NOT_ART_PAIRING_REASON_OTHER, data.get(PncDbConstants.Column.PncVisit.NOT_ART_PAIRING_REASON_OTHER));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_DBS, data.get(PncDbConstants.Column.PncVisit.BABY_DBS));
        contentValues.put(PncDbConstants.Column.PncVisit.BABY_CARE_MGMT, data.get(PncDbConstants.Column.PncVisit.BABY_CARE_MGMT));
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.PNC_VISIT_CHILD_STATUS, null, contentValues);
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