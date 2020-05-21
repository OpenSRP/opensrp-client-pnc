package org.smartregister.pnc.model;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterActivityModel implements PncRegisterActivityContract.Model {

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
        }
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
        }
    }

    @Override
    public void saveLanguage(String language) {
        // Do nothing for now
    }

    @Nullable
    @Override
    public String getLocationId(@Nullable String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Nullable
    @Override
    public List<PncEventClient> processRegistration(String jsonString, FormTag formTag) {
        List<PncEventClient> maternityEventClientList = new ArrayList<>();
        PncEventClient maternityEventClient = PncJsonFormUtils.processMaternityRegistrationForm(jsonString, formTag);

        if (maternityEventClient == null) {
            return null;
        }

        maternityEventClientList.add(maternityEventClient);
        return maternityEventClientList;
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws JSONException {
        return getFormAsJson(formName, entityId, currentLocationId, null);
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId,
                                    String currentLocationId, @Nullable HashMap<String, String> injectedValues) throws JSONException {
        JSONObject form = PncUtils.getJsonFormToJsonObject(formName);
        if (form != null) {
            return PncJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, injectedValues);
        }
        return null;
    }

    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }

}
