package org.smartregister.pnc.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.pnc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class ConfigHelper {

    public static RegisterConfiguration defaultRegisterConfiguration(@Nullable Context context) {
        if (context == null) {
            return null;
        }

        RegisterConfiguration config = new RegisterConfiguration();
        config.setEnableAdvancedSearch(false);
        config.setEnableFilterList(false);
        config.setEnableSortList(false);
        config.setSearchBarText(context.getString(R.string.search_name_or_id));
        config.setEnableJsonViews(false);

        List<Field> filers = new ArrayList<>();
        config.setFilterFields(filers);

        List<Field> sortFields = new ArrayList<>();
        config.setSortFields(sortFields);

        return config;
    }
}