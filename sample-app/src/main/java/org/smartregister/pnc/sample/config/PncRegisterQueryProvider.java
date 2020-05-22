package org.smartregister.pnc.sample.config;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.pnc.config.PncRegisterQueryProviderContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterQueryProvider extends PncRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        if (TextUtils.isEmpty(filters)) {
            return "SELECT object_id, last_interacted_with FROM ec_client_search WHERE register_type = 'maternity' " +
                    "ORDER BY last_interacted_with DESC";
        } else {
            String sql = "SELECT object_id FROM ec_client_search WHERE date_removed IS NULL AND register_type = 'maternity' AND phrase MATCH '%s*'" +
                    "ORDER BY last_interacted_with DESC";
            sql = sql.replace("%s", filters);
            return sql;
        }
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
        return new String[] {
                sqb.countQueryFts("ec_client", null, "register_type = 'maternity'", filters)
        };
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return "SELECT ec_client.id AS _id , ec_client.first_name , ec_client.last_name , '' AS middle_name , ec_client.gender , ec_client.dob , '' AS home_address, maternity_details.conception_date, maternity_details.hiv_status_current, ec_client.relationalid , ec_client.opensrp_id AS register_id , ec_client.last_interacted_with, 'ec_client' as entity_table FROM ec_client INNER JOIN maternity_registration_details maternity_details ON ec_client.base_entity_id = maternity_details.base_entity_id " +
                "WHERE ec_client.id IN (%s) " +
                "ORDER BY ec_client.last_interacted_with DESC";
    }
}
