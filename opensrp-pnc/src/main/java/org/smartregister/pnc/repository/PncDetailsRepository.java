package org.smartregister.pnc.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncDetailsDao;
import org.smartregister.pnc.pojo.PncBaseDetails;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.BaseRepository;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public abstract class PncDetailsRepository extends BaseRepository implements PncDetailsDao {

    private String[] columns;

    public abstract String getTableName();

    @NonNull
    public ContentValues createValuesFor(@NonNull PncBaseDetails pncDetails) {
        ContentValues contentValues = new ContentValues();
        if (pncDetails.getId() != 0) {
            contentValues.put(PncDbConstants.Column.PncDetails.ID, pncDetails.getId());
        }

        if (pncDetails.getCreatedAt() != null) {
            contentValues.put(PncDbConstants.Column.PncDetails.CREATED_AT, PncUtils.convertDate(pncDetails.getCreatedAt(), PncDbConstants.DATE_FORMAT));
        }

        if (pncDetails.getEventDate() != null) {
            contentValues.put(PncDbConstants.Column.PncDetails.EVENT_DATE, PncUtils.convertDate(pncDetails.getEventDate(), PncDbConstants.DATE_FORMAT));
        }

        contentValues.put(PncDbConstants.Column.PncDetails.BASE_ENTITY_ID, pncDetails.getBaseEntityId());
        for (String column: getPropertyNames()) {
            contentValues.put(column, pncDetails.getProperties().get(column));
        }

        return contentValues;
    }

    @Override
    public boolean saveOrUpdate(@NonNull PncBaseDetails pncDetails) {
        ContentValues contentValues = createValuesFor(pncDetails);

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertWithOnConflict(getTableName(), null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return recordId != -1;
    }

    @Nullable
    @Override
    public PncBaseDetails findOne(@NonNull PncBaseDetails pncDetails) {
        PncBaseDetails details = null;
        if (pncDetails.getBaseEntityId() != null) {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();

            Cursor cursor = sqLiteDatabase.query(getTableName(), getColumns(), PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " = ?",
                    new String[]{pncDetails.getBaseEntityId()}, null, null, null, "1");
            if (cursor.getCount() == 0) {
                return null;
            }

            if (cursor.moveToNext()) {
                details = convert(cursor);
                cursor.close();
            }

        }
        return details;
    }

    public PncBaseDetails convert(@NonNull Cursor cursor) {
        PncBaseDetails pncDetails = new PncBaseDetails();

        pncDetails.setId(cursor.getInt(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.ID)));
        pncDetails.setBaseEntityId(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.BASE_ENTITY_ID)));
        pncDetails.setEventDate(PncUtils.convertStringToDate(PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.EVENT_DATE))));
        pncDetails.setCreatedAt(PncUtils.convertStringToDate(PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.CREATED_AT))));

        for (String column: getPropertyNames()) {
            int colIndex = cursor.getColumnIndex(column);
            if (colIndex != -1) {
                pncDetails.put(column, cursor.getString(colIndex));
            }
        }

        return pncDetails;
    }

    public abstract String[] getPropertyNames();

    @Override
    public boolean delete(PncBaseDetails pncDetails) {
        throw new NotImplementedException("Not Implemented");
    }

    public boolean delete(@NonNull String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rowsDeleted = sqLiteDatabase.delete(getTableName(), PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " = ?", new String[]{baseEntityId});

        return rowsDeleted > 0;
    }

    @Override
    public List<PncBaseDetails> findAll() {
        throw new NotImplementedException("Not Implemented");
    }

    public String[] getColumns() {
        if (this.columns == null) {
            String[] propertyNames = getPropertyNames();
            String[] columns = new String[propertyNames.length + 4];

            columns[0] = PncDbConstants.Column.PncDetails.ID;
            columns[1] = PncDbConstants.Column.PncDetails.BASE_ENTITY_ID;
            columns[2] = PncDbConstants.Column.PncDetails.CREATED_AT;
            columns[3] = PncDbConstants.Column.PncDetails.EVENT_DATE;

            for (int i = 0; i < propertyNames.length; i++) {
                columns[i + 4] = propertyNames[i];
            }
            this.columns = columns;
        }

        return this.columns;

    }
}
