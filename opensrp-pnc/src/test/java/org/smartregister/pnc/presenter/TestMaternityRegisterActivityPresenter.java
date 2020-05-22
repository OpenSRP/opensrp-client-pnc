package org.smartregister.pnc.presenter;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.pojo.RegisterParams;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class TestMaternityRegisterActivityPresenter extends BasePncRegisterActivityPresenter {

    public TestMaternityRegisterActivityPresenter(PncRegisterActivityContract.View view, PncRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void onNoUniqueId() {
        // Do nothing
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        // Do nothing
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        // Do nothing
    }

    @Override
    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
        // Do nothing
    }
}
