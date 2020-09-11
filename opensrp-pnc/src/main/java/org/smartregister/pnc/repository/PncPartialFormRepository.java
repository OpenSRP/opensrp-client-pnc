package org.smartregister.pnc.repository;

import android.content.ContentValues;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.pojo.PncPartialForm;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class PncPartialFormRepository extends BaseRepository implements PncGenericDao<PncPartialForm> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_PARTIAL_FORM + "("
            + PncDbConstants.Column.PncPartialForm.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncPartialForm.FORM + " TEXT NOT NULL, "
            + PncDbConstants.Column.PncPartialForm.FORM_TYPE + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncPartialForm.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_PARTIAL_FORM
            + "_" + PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_PARTIAL_FORM +
            "(" + PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{
            PncDbConstants.Column.PncPartialForm.ID,
            PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID,
            PncDbConstants.Column.PncPartialForm.FORM,
            PncDbConstants.Column.PncPartialForm.FORM_TYPE,
            PncDbConstants.Column.PncPartialForm.CREATED_AT};

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull PncPartialForm pncPartialForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID, pncPartialForm.getBaseEntityId());
        contentValues.put(PncDbConstants.Column.PncPartialForm.FORM, pncPartialForm.getForm());
        contentValues.put(PncDbConstants.Column.PncPartialForm.FORM_TYPE, pncPartialForm.getFormType());
        contentValues.put(PncDbConstants.Column.PncPartialForm.CREATED_AT, pncPartialForm.getCreatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.PNC_PARTIAL_FORM, null, contentValues);
        return rows != -1;
    }

    @Nullable
    @Override
    public PncPartialForm findOne(@NonNull PncPartialForm pncPartialForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(PncDbConstants.Table.PNC_PARTIAL_FORM
                , columns
                , PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID + " = ? AND " + PncDbConstants.Column.PncPartialForm.FORM_TYPE + " = ?"
                , new String[]{pncPartialForm.getBaseEntityId(), pncPartialForm.getFormType()}
                , null
                , null
                , null);

        if (cursor == null) {
            return null;
        }

        PncPartialForm partialForm = null;
        if (cursor.moveToNext()) {
            partialForm = new PncPartialForm(
                    cursor.getInt(cursor.getColumnIndex(PncDbConstants.Column.PncPartialForm.ID)),
                    cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID)),
                    cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncPartialForm.FORM)),
                    cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncPartialForm.FORM_TYPE)),
                    cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncPartialForm.CREATED_AT)));
            cursor.close();
        }

        return partialForm;
    }

    @Override
    public boolean delete(@NonNull PncPartialForm pncPartialForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(PncDbConstants.Table.PNC_PARTIAL_FORM
                , PncDbConstants.Column.PncPartialForm.BASE_ENTITY_ID + " = ? AND " + PncDbConstants.Column.PncPartialForm.FORM_TYPE + " = ?"
                , new String[]{pncPartialForm.getBaseEntityId(), pncPartialForm.getFormType()});

        return rows > 0;
    }

    @Override
    public List<PncPartialForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
