package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class PncChildRepository extends BaseRepository implements PncGenericDao<PncChild> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_BABY + "("
            + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncBaby.DISCHARGED_ALIVE + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.CHILD_REGISTERED + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.CHILD_REGISTERED + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.BIRTH_RECORD + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.FIRST_NAME + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.LAST_NAME + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.DOB + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.GENDER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.BIRTH_WEIGTH_ENTERED + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.BIRTH_WEIGHT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.BIRTH_HEIGHT_ENTERED + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.APGAR + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.FIRST_CRY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.COMPLICATIONS + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.COMPLICATIONS_OTHER + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.CARE_MGT + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.CARE_MGT_SPECIFY + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.REF_LOCATION + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.BF_FIRST_HOUR + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.NVP_ADMINISTRATION + " VARCHAR NULL, "
            + PncDbConstants.Column.PncBaby.CHILD_HIV_STATUS + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_BABY
            + "_" + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_BABY +
            "(" + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(PncChild pncChild) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID, pncChild.getMotherBaseEntityId());
        contentValues.put(PncDbConstants.Column.PncBaby.DISCHARGED_ALIVE, pncChild.getDischargedAlive());
        contentValues.put(PncDbConstants.Column.PncBaby.CHILD_REGISTERED, pncChild.getChildRegistered());
        contentValues.put(PncDbConstants.Column.PncBaby.BIRTH_RECORD, pncChild.getBirthRecordDate());
        contentValues.put(PncDbConstants.Column.PncBaby.FIRST_NAME, pncChild.getFirstName());
        contentValues.put(PncDbConstants.Column.PncBaby.LAST_NAME, pncChild.getLastName());
        contentValues.put(PncDbConstants.Column.PncBaby.DOB, pncChild.getDob());
        contentValues.put(PncDbConstants.Column.PncBaby.GENDER, pncChild.getGender());
        contentValues.put(PncDbConstants.Column.PncBaby.BIRTH_WEIGTH_ENTERED, pncChild.getWeightEntered());
        contentValues.put(PncDbConstants.Column.PncBaby.BIRTH_WEIGHT, pncChild.getWeight());
        contentValues.put(PncDbConstants.Column.PncBaby.BIRTH_HEIGHT_ENTERED, pncChild.getHeightEntered());
        contentValues.put(PncDbConstants.Column.PncBaby.APGAR, pncChild.getApgar());
        contentValues.put(PncDbConstants.Column.PncBaby.FIRST_CRY, pncChild.getFirstCry());
        contentValues.put(PncDbConstants.Column.PncBaby.COMPLICATIONS, pncChild.getComplications());
        contentValues.put(PncDbConstants.Column.PncBaby.COMPLICATIONS_OTHER, pncChild.getComplicationsOther());
        contentValues.put(PncDbConstants.Column.PncBaby.CARE_MGT, pncChild.getCareMgt());
        contentValues.put(PncDbConstants.Column.PncBaby.CARE_MGT_SPECIFY, pncChild.getCareMgtSpecify());
        contentValues.put(PncDbConstants.Column.PncBaby.REF_LOCATION, pncChild.getRefLocation());
        contentValues.put(PncDbConstants.Column.PncBaby.BF_FIRST_HOUR, pncChild.getBfFirstHour());
        contentValues.put(PncDbConstants.Column.PncBaby.NVP_ADMINISTRATION, pncChild.getNvpAdministration());
        contentValues.put(PncDbConstants.Column.PncBaby.CHILD_HIV_STATUS, pncChild.getChildHivStatus());
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

    public List<PncChild> findAll(@NonNull String baseEntityId) {
        List<PncChild> data = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PncDbConstants.Table.PNC_BABY + " WHERE " + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + "='" + baseEntityId + "'", null)) {
            while (cursor.moveToNext()) {
                data.add(fillUp(cursor));
            }
        }

        return data;
    }

    public int countBaby28DaysOld(String baseEntityId, int howBabyOldInDays) {
        int count;
        String deliveryDays = "delivery_date";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "SELECT CAST(julianday('now') - julianday(datetime(substr(" + PncDbConstants.Column.PncBaby.DOB + ", 7, 4) || '-' || substr(" + PncDbConstants.Column.PncBaby.DOB + ", 4, 2) || '-' || substr(" + PncDbConstants.Column.PncBaby.DOB + ", 1, 2))) AS INTEGER) AS " + deliveryDays + " " +
                "FROM " + PncDbConstants.Table.PNC_BABY + " " +
                "WHERE " + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + " = '" + baseEntityId + "' AND " + deliveryDays + " <= " + howBabyOldInDays + " ";

        try (Cursor cursor = sqLiteDatabase.rawQuery(query, null)) {
            count = cursor.getCount();
        }

        return count;
    }

    private PncChild fillUp(Cursor cursor) {
        PncChild pncChild = new PncChild();

        pncChild.setMotherBaseEntityId(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID)));
        pncChild.setDischargedAlive(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.DISCHARGED_ALIVE)));
        pncChild.setChildRegistered(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.CHILD_REGISTERED)));
        pncChild.setBirthRecordDate(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.BIRTH_RECORD)));
        pncChild.setFirstName(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.FIRST_NAME)));
        pncChild.setLastName(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.LAST_NAME)));
        pncChild.setDob(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.DOB)));
        pncChild.setGender(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.GENDER)));
        pncChild.setWeightEntered(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.BIRTH_WEIGTH_ENTERED)));
        pncChild.setWeight(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.BIRTH_WEIGHT)));
        pncChild.setHeightEntered(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.BIRTH_HEIGHT_ENTERED)));
        pncChild.setApgar(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.APGAR)));
        pncChild.setFirstCry(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.FIRST_CRY)));
        pncChild.setComplications(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.COMPLICATIONS)));
        pncChild.setComplicationsOther(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.COMPLICATIONS_OTHER)));
        pncChild.setCareMgt(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.CARE_MGT)));
        pncChild.setCareMgtSpecify(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.CARE_MGT_SPECIFY)));
        pncChild.setRefLocation(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.REF_LOCATION)));
        pncChild.setBfFirstHour(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.BF_FIRST_HOUR)));
        pncChild.setNvpAdministration(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.NVP_ADMINISTRATION)));
        pncChild.setChildHivStatus(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncBaby.CHILD_HIV_STATUS)));

        return pncChild;
    }
}
