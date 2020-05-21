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
    public ContentValues createValuesFor(@NonNull PncBaseDetails maternityDetails) {
        ContentValues contentValues = new ContentValues();
        if (maternityDetails.getId() != 0) {
            contentValues.put(PncDbConstants.Column.PncDetails.ID, maternityDetails.getId());
        }

        if (maternityDetails.getCreatedAt() != null) {
            contentValues.put(PncDbConstants.Column.PncDetails.CREATED_AT, PncUtils.convertDate(maternityDetails.getCreatedAt(), PncDbConstants.DATE_FORMAT));
        }

        if (maternityDetails.getEventDate() != null) {
            contentValues.put(PncDbConstants.Column.PncDetails.EVENT_DATE, PncUtils.convertDate(maternityDetails.getEventDate(), PncDbConstants.DATE_FORMAT));
        }

        contentValues.put(PncDbConstants.Column.PncDetails.BASE_ENTITY_ID, maternityDetails.getBaseEntityId());
        for (String column: getPropertyNames()) {
            contentValues.put(column, maternityDetails.getProperties().get(column));
        }

        return contentValues;
    }

    @Override
    public boolean saveOrUpdate(@NonNull PncBaseDetails maternityDetails) {
        ContentValues contentValues = createValuesFor(maternityDetails);

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertWithOnConflict(getTableName(), null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return recordId != -1;
    }

    @Nullable
    @Override
    public PncBaseDetails findOne(@NonNull PncBaseDetails maternityDetails) {
        PncBaseDetails details = null;
        if (maternityDetails.getBaseEntityId() != null) {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();

            Cursor cursor = sqLiteDatabase.query(getTableName(), getColumns(), PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " = ?",
                    new String[]{maternityDetails.getBaseEntityId()}, null, null, null, "1");
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
        PncBaseDetails maternityDetails = new PncBaseDetails();

        maternityDetails.setId(cursor.getInt(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.ID)));
        maternityDetails.setBaseEntityId(cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.BASE_ENTITY_ID)));
        maternityDetails.setEventDate(PncUtils.convertStringToDate(PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.EVENT_DATE))));
        maternityDetails.setCreatedAt(PncUtils.convertStringToDate(PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(PncDbConstants.Column.PncDetails.CREATED_AT))));

        for (String column: getPropertyNames()) {
            int colIndex = cursor.getColumnIndex(column);
            if (colIndex != -1) {
                maternityDetails.put(column, cursor.getString(colIndex));
            }
        }

        return maternityDetails;
    }

    public abstract String[] getPropertyNames();

    @Override
    public boolean delete(PncBaseDetails maternityDetails) {
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
