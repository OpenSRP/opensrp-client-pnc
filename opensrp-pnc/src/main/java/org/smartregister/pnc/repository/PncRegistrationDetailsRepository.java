package org.smartregister.pnc.repository;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.HashMap;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegistrationDetailsRepository extends BaseRepository {

    private static final String TABLE = PncDbConstants.Table.PNC_REGISTRATION_DETAILS;

    public HashMap<String, String> findByBaseEntityId(@NonNull String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "select * from " + getTableName() +
                                " where " + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " = '" + baseEntityId + "' limit 1").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }

    public HashMap<String, String> findWithMedicInfoByBaseEntityId(@NonNull String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "select * from " + getTableName() + " prd " +
                                " left join " + PncMedicInfoRepository.TABLE + " pmi on " + "prd." + PncDbConstants.Column.PncMedicInfo.BASE_ENTITY_ID + " = " + "pmi." + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID +
                                " where prd." + PncDbConstants.Column.PncDetails.BASE_ENTITY_ID + " = '" + baseEntityId + "' limit 1").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }

    public String getTableName() {
        return TABLE;
    }

}