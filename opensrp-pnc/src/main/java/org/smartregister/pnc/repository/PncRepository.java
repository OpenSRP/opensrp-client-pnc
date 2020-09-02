package org.smartregister.pnc.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import timber.log.Timber;

public class PncRepository extends BaseRepository {

    public HashMap<String, String> getClientWithRegistrationDetails(@Nullable String baseEntityId) {
        if (StringUtils.isNotBlank(baseEntityId)) {
            String tableName = PncUtils.metadata().getTableName();
            ArrayList<HashMap<String, String>> hashMap = CoreLibrary.getInstance().context().getEventClientRepository().rawQuery(PncLibrary.getInstance().getRepository().getReadableDatabase(),
                    "select * from " + tableName + " " +
                            "LEFT JOIN " + PncDbConstants.Table.PNC_REGISTRATION_DETAILS + " AS prd ON prd.base_entity_id = " + tableName + ".base_entity_id " +
                            " where " + tableName + ".id = '" + baseEntityId + "' limit 1");
            if (!hashMap.isEmpty()) {
                return hashMap.get(0);
            }
        }
        return null;
    }

    public HashMap<String, String> getPncDetailsFromQueryProvider(@NonNull String providerQuery) {
        if (StringUtils.isNotBlank(providerQuery)) {
            ArrayList<HashMap<String, String>> hashMap = rawQuery(getReadableDatabase(),
                    providerQuery + " limit 1");
            if (hashMap != null && !hashMap.isEmpty()) {
                return hashMap.get(0);
            }
        }
        return null;
    }


    public void updateLastInteractedWith(@Nullable String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                String tableName = PncUtils.metadata().getTableName();

                String lastInteractedWithDate = String.valueOf(new Date().getTime());

                ContentValues contentValues = new ContentValues();
                contentValues.put(PncConstants.JsonFormKeyConstants.LAST_INTERACTED_WITH, lastInteractedWithDate);

                getWritableDatabase()
                        .update(tableName, contentValues,
                                String.format("%s = ?", PncConstants.KeyConstants.BASE_ENTITY_ID),
                                new String[]{baseEntityId});

                // Update FTS
                CommonRepository commonrepository = PncLibrary.getInstance().context().commonrepository(tableName);

                if (commonrepository.isFts()) {
                    getWritableDatabase()
                            .update(CommonFtsObject.searchTableName(tableName), contentValues, CommonFtsObject.idColumn + " = ?", new String[]{baseEntityId});
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            Timber.e(e);
        }
    }
}
