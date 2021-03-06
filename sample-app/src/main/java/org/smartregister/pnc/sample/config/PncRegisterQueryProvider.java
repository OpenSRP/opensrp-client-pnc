package org.smartregister.pnc.sample.config;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.pnc.config.PncRegisterQueryProviderContract;
import org.smartregister.pnc.utils.PncUtils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterQueryProvider extends PncRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        if (StringUtils.isBlank(filters) && StringUtils.isBlank(mainCondition)) {
            return "SELECT object_id, last_interacted_with FROM " + CommonFtsObject.searchTableName(PncUtils.metadata().getTableName()) +
                    " ORDER BY last_interacted_with DESC";
        } else {
            String sql = "SELECT object_id, cast (((julianday('now' ,'localtime')- julianday((substr(pmi.delivery_date, 7) || \"-\" || substr(pmi.delivery_date,4,2) || \"-\" || substr(pmi.delivery_date, 1,2)), 'localtime'))) as INTEGER) as ddNow  FROM " + CommonFtsObject.searchTableName(PncUtils.metadata().getTableName())
                    +" left join pnc_medic_info pmi on object_id = pmi.base_entity_id " +
                    getMainCondition(mainCondition) + " AND phrase MATCH '%s*' " +
                    "ORDER BY last_interacted_with DESC";
            sql = sql.replace("%s", filters);
            return sql;
        }
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
        return new String[]{
                sqb.countQueryFts(PncUtils.metadata().getTableName(), null, null, filters)
        };
    }

    public String getMainCondition(String mainCondition) {
        return " WHERE date_removed IS NULL AND " + mainCondition;
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return "SELECT pmi.base_entity_id AS pmi_base_entity_id, ec_client.id AS _id, ec_client.base_entity_id, ec_client.first_name , ec_client.last_name , '' AS middle_name , ec_client.gender , ec_client.dob , '' AS home_address, pmi.hiv_status_current, ec_client.relationalid , ec_client.opensrp_id AS register_id , ec_client.last_interacted_with, 'ec_client' as entity_table, pmi.delivery_date,(SELECT MAX(pvi.created_at)  FROM pnc_visit_info AS pvi WHERE pvi.mother_base_entity_id = ec_client.base_entity_id) AS latest_visit_date " +
                "FROM ec_client " +
                "LEFT JOIN pnc_medic_info AS pmi ON pmi.base_entity_id = ec_client.base_entity_id " +
                "WHERE ec_client.id IN (%s) " +
                "ORDER BY ec_client.last_interacted_with DESC";
    }
}
