package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.pojo.PncStillBorn;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class PncStillBornRepository extends BaseRepository implements PncGenericDao<PncStillBorn> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_STILL_BORN + "("
            + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncStillBorn.STILL_BIRTH_CONDITION + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_STILL_BORN
            + "_" + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_STILL_BORN +
            "(" + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(PncStillBorn stillBorn) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID, stillBorn.getMotherBaseEntityId());
        contentValues.put(PncDbConstants.Column.PncStillBorn.STILL_BIRTH_CONDITION, stillBorn.getStillBirthCondition());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.PNC_STILL_BORN, null, contentValues);
        return rows != -1;
    }

    @Override
    public PncStillBorn findOne(PncStillBorn stillBorn) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean delete(PncStillBorn stillBorn) {
        throw new NotImplementedException("");
    }

    @Override
    public List<PncStillBorn> findAll() {
        throw new NotImplementedException("");
    }

    public int count(String baseEntityId) {
        int count = 0;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PncDbConstants.Table.PNC_STILL_BORN + " WHERE " + PncDbConstants.Column.PncBaby.MOTHER_BASE_ENTITY_ID + "='" + baseEntityId + "'", null)) {
            count = cursor.getCount();
        }
        return count;
    }
}
