package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.pnc.dao.PncGenericDao;
import org.smartregister.pnc.pojo.PncOtherVisit;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class PncOtherVisitRepository extends BaseRepository implements PncGenericDao<PncOtherVisit> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + PncDbConstants.Table.PNC_OTHER_VISIT + "("
            + PncDbConstants.Column.PncOtherVisit.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + PncDbConstants.Column.PncOtherVisit.MOTHER_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncOtherVisit.VISIT_ID + " VARCHAR NOT NULL, "
            + PncDbConstants.Column.PncOtherVisit.VISIT_DATE + " VARCHAR NOT NULL )";

    private static final String INDEX_MOTHER_BASE_ENTITY_ID = "CREATE INDEX " + PncDbConstants.Table.PNC_OTHER_VISIT
            + "_" + PncDbConstants.Column.PncOtherVisit.MOTHER_BASE_ENTITY_ID + "_index ON " + PncDbConstants.Table.PNC_OTHER_VISIT +
            "(" + PncDbConstants.Column.PncOtherVisit.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_MOTHER_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull PncOtherVisit pncOtherVisit) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PncDbConstants.Column.PncOtherVisit.MOTHER_BASE_ENTITY_ID, pncOtherVisit.getMotherBaseEntityId());
        contentValues.put(PncDbConstants.Column.PncOtherVisit.VISIT_ID, pncOtherVisit.getVisitId());
        contentValues.put(PncDbConstants.Column.PncOtherVisit.VISIT_DATE, pncOtherVisit.getVisitDate());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(PncDbConstants.Table.PNC_OTHER_VISIT, null, contentValues);
        return rows != -1;
    }

    @Override
    public PncOtherVisit findOne(PncOtherVisit pncOtherVisit) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean delete(PncOtherVisit pncOtherVisit) {
        throw new NotImplementedException("");
    }

    @Override
    public List<PncOtherVisit> findAll() {
        throw new NotImplementedException("");
    }
}
