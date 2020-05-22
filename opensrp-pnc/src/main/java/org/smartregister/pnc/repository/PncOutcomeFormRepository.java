package org.smartregister.pnc.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncOutcomeFormDao;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class PncOutcomeFormRepository extends BaseRepository implements PncOutcomeFormDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.MATERNITY_OUTCOME_FORM + "("
            + PncDbConstants.Column.PncOutcomeForm.ID + " INTEGER NOT NULL PRIMARY KeyConstants AUTOINCREMENT,"
            + PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncOutcomeForm.FORM + " TEXT NOT NULL, "
            + PncDbConstants.Column.PncOutcomeForm.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.MATERNITY_OUTCOME_FORM
            + "_" + PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.MATERNITY_OUTCOME_FORM +
            "(" + PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{
            PncDbConstants.Column.PncOutcomeForm.ID,
            PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID,
            PncDbConstants.Column.PncOutcomeForm.FORM,
            PncDbConstants.Column.PncOutcomeForm.CREATED_AT};

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.pnc.pojo.PncOutcomeForm maternityOutcomeForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID, maternityOutcomeForm.getBaseEntityId());
        contentValues.put(PncDbConstants.Column.PncOutcomeForm.FORM, maternityOutcomeForm.getForm());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(PncDbConstants.Column.PncOutcomeForm.CREATED_AT, maternityOutcomeForm.getCreatedAt());
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.MATERNITY_OUTCOME_FORM, null, contentValues);
        return rows != -1;
    }

    @Nullable
    @Override
    public org.smartregister.pnc.pojo.PncOutcomeForm findOne(@NonNull org.smartregister.pnc.pojo.PncOutcomeForm maternityOutcomeForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(PncDbConstants.Table.MATERNITY_OUTCOME_FORM
                , columns
                , PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityOutcomeForm.getBaseEntityId()}
                , null
                , null
                , null);

        if (cursor.getCount() == 0) {
            return null;
        }

        org.smartregister.pnc.pojo.PncOutcomeForm diagnosisAndTreatmentForm = null;
        if (cursor.moveToNext()) {
            diagnosisAndTreatmentForm = new org.smartregister.pnc.pojo.PncOutcomeForm(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            cursor.close();
        }

        return diagnosisAndTreatmentForm;
    }

    @Override
    public boolean delete(@NonNull org.smartregister.pnc.pojo.PncOutcomeForm maternityOutcomeForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(PncDbConstants.Table.MATERNITY_OUTCOME_FORM
                , PncDbConstants.Column.PncOutcomeForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityOutcomeForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<org.smartregister.pnc.pojo.PncOutcomeForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
