package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncDbConstants.Column.PncBaby;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class PncChildRepository extends BaseRepository implements PncGenericDao<PncChild> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_BABY + "("
            + PncBaby.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + PncBaby.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncBaby.MOTHER_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncBaby.DISCHARGED_ALIVE + " VARCHAR NULL, "
            + PncBaby.CHILD_REGISTERED + " VARCHAR NULL, "
            + PncBaby.BIRTH_RECORD + " VARCHAR NULL, "
            + PncBaby.BABY_FIRST_NAME + " VARCHAR NULL, "
            + PncBaby.BABY_LAST_NAME + " VARCHAR NULL, "
            + PncBaby.BABY_DOB + " VARCHAR NULL, "
            + PncBaby.GENDER + " VARCHAR NULL, "
            + PncBaby.BIRTH_WEIGHT_ENTERED + " VARCHAR NULL, "
            + PncBaby.BIRTH_WEIGHT + " VARCHAR NULL, "
            + PncBaby.BIRTH_HEIGHT_ENTERED + " VARCHAR NULL, "
            + PncBaby.APGAR + " VARCHAR NULL, "
            + PncBaby.FIRST_CRY + " VARCHAR NULL, "
            + PncBaby.COMPLICATIONS + " VARCHAR NULL, "
            + PncBaby.COMPLICATIONS_OTHER + " VARCHAR NULL, "
            + PncBaby.CARE_MGT + " VARCHAR NULL, "
            + PncBaby.CARE_MGT_SPECIFY + " VARCHAR NULL, "
            + PncBaby.REF_LOCATION + " VARCHAR NULL, "
            + PncBaby.BF_FIRST_HOUR + " VARCHAR NULL, "
            + PncBaby.NVP_ADMINISTRATION + " VARCHAR NULL, "
            + PncBaby.CHILD_HIV_STATUS + " VARCHAR NULL, "
            + PncBaby.EVENT_DATE + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_BABY
            + "_" + PncBaby.BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_BABY +
            "(" + PncBaby.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_MOTHER_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_BABY
            + "_" + PncBaby.MOTHER_BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_BABY +
            "(" + PncBaby.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_MOTHER_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(PncChild pncChild) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncBaby.BASE_ENTITY_ID, pncChild.getBaseEntityId());
        contentValues.put(PncBaby.MOTHER_BASE_ENTITY_ID, pncChild.getMotherBaseEntityId());
        contentValues.put(PncBaby.DISCHARGED_ALIVE, pncChild.getDischargedAlive());
        contentValues.put(PncBaby.CHILD_REGISTERED, pncChild.getChildRegistered());
        contentValues.put(PncBaby.BIRTH_RECORD, pncChild.getBirthRecordDate());
        contentValues.put(PncBaby.BABY_FIRST_NAME, pncChild.getFirstName());
        contentValues.put(PncBaby.BABY_LAST_NAME, pncChild.getLastName());
        contentValues.put(PncBaby.BABY_DOB, pncChild.getDob());
        contentValues.put(PncBaby.GENDER, pncChild.getGender());
        contentValues.put(PncBaby.BIRTH_WEIGHT_ENTERED, pncChild.getWeightEntered());
        contentValues.put(PncBaby.BIRTH_WEIGHT, pncChild.getWeight());
        contentValues.put(PncBaby.BIRTH_HEIGHT_ENTERED, pncChild.getHeightEntered());
        contentValues.put(PncBaby.APGAR, pncChild.getApgar());
        contentValues.put(PncBaby.FIRST_CRY, pncChild.getFirstCry());
        contentValues.put(PncBaby.COMPLICATIONS, pncChild.getComplications());
        contentValues.put(PncBaby.COMPLICATIONS_OTHER, pncChild.getComplicationsOther());
        contentValues.put(PncBaby.CARE_MGT, pncChild.getCareMgt());
        contentValues.put(PncBaby.CARE_MGT_SPECIFY, pncChild.getCareMgtSpecify());
        contentValues.put(PncBaby.REF_LOCATION, pncChild.getRefLocation());
        contentValues.put(PncBaby.BF_FIRST_HOUR, pncChild.getBfFirstHour());
        contentValues.put(PncBaby.NVP_ADMINISTRATION, pncChild.getNvpAdministration());
        contentValues.put(PncBaby.CHILD_HIV_STATUS, pncChild.getChildHivStatus());
        contentValues.put(PncBaby.EVENT_DATE, pncChild.getEventDate());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.PNC_BABY, null, contentValues);
        return rows != -1;
    }

    @Override
    public PncChild findOne(PncChild pncChild) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean delete(PncChild pncChild) {
        throw new NotImplementedException("");
    }

    @Override
    public List<PncChild> findAll() {
        throw new NotImplementedException("");
    }

    public List<PncChild> findAll(@NonNull String motherBaseEntityId) {
        List<PncChild> data = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PncDbConstants.Table.PNC_BABY + " WHERE " + PncBaby.MOTHER_BASE_ENTITY_ID + "='" + motherBaseEntityId + "'", null)) {
            while (cursor.moveToNext()) {
                data.add(fillUp(cursor));
            }
        }

        return data;
    }

    public List<HashMap<String, String>> findAllByMotherBaseEntityId(@NonNull String motherBaseEntityId) {
        List<HashMap<String, String>> data = new ArrayList<>();
        try {
            String query = "SELECT pb.* FROM " + PncDbConstants.Table.PNC_BABY + " AS pb " +
                    "WHERE pb." + PncBaby.MOTHER_BASE_ENTITY_ID + " = '" + motherBaseEntityId + "'";
            data = rawQuery(getReadableDatabase(), query);
        }
        catch (Exception ex) {
            Timber.e(ex);
        }

        return data;
    }

    private PncChild fillUp(Cursor cursor) {
        PncChild pncChild = new PncChild();

        pncChild.setBaseEntityId(cursor.getString(cursor.getColumnIndex(PncBaby.BASE_ENTITY_ID)));
        pncChild.setMotherBaseEntityId(cursor.getString(cursor.getColumnIndex(PncBaby.MOTHER_BASE_ENTITY_ID)));
        pncChild.setDischargedAlive(cursor.getString(cursor.getColumnIndex(PncBaby.DISCHARGED_ALIVE)));
        pncChild.setChildRegistered(cursor.getString(cursor.getColumnIndex(PncBaby.CHILD_REGISTERED)));
        pncChild.setBirthRecordDate(cursor.getString(cursor.getColumnIndex(PncBaby.BIRTH_RECORD)));
        pncChild.setFirstName(cursor.getString(cursor.getColumnIndex(PncBaby.BABY_FIRST_NAME)));
        pncChild.setLastName(cursor.getString(cursor.getColumnIndex(PncBaby.BABY_LAST_NAME)));
        pncChild.setDob(cursor.getString(cursor.getColumnIndex(PncBaby.BABY_DOB)));
        pncChild.setGender(cursor.getString(cursor.getColumnIndex(PncBaby.GENDER)));
        pncChild.setWeightEntered(cursor.getString(cursor.getColumnIndex(PncBaby.BIRTH_WEIGHT_ENTERED)));
        pncChild.setWeight(cursor.getString(cursor.getColumnIndex(PncBaby.BIRTH_WEIGHT)));
        pncChild.setHeightEntered(cursor.getString(cursor.getColumnIndex(PncBaby.BIRTH_HEIGHT_ENTERED)));
        pncChild.setApgar(cursor.getString(cursor.getColumnIndex(PncBaby.APGAR)));
        pncChild.setFirstCry(cursor.getString(cursor.getColumnIndex(PncBaby.FIRST_CRY)));
        pncChild.setComplications(cursor.getString(cursor.getColumnIndex(PncBaby.COMPLICATIONS)));
        pncChild.setComplicationsOther(cursor.getString(cursor.getColumnIndex(PncBaby.COMPLICATIONS_OTHER)));
        pncChild.setCareMgt(cursor.getString(cursor.getColumnIndex(PncBaby.CARE_MGT)));
        pncChild.setCareMgtSpecify(cursor.getString(cursor.getColumnIndex(PncBaby.CARE_MGT_SPECIFY)));
        pncChild.setRefLocation(cursor.getString(cursor.getColumnIndex(PncBaby.REF_LOCATION)));
        pncChild.setBfFirstHour(cursor.getString(cursor.getColumnIndex(PncBaby.BF_FIRST_HOUR)));
        pncChild.setNvpAdministration(cursor.getString(cursor.getColumnIndex(PncBaby.NVP_ADMINISTRATION)));
        pncChild.setChildHivStatus(cursor.getString(cursor.getColumnIndex(PncBaby.CHILD_HIV_STATUS)));
        pncChild.setEventDate(cursor.getString(cursor.getColumnIndex(PncBaby.EVENT_DATE)));

        return pncChild;
    }
}
