package org.smartregister.pnc.presenter;

import android.support.annotation.NonNull;

import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.pojo.RegisterParams;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterActivityPresenter extends BasePncRegisterActivityPresenter implements PncRegisterActivityContract.InteractorCallBack {

    public PncRegisterActivityPresenter(PncRegisterActivityContract.View view, PncRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        // Do nothing
    }

    @Override
    public void onNoUniqueId() {
        // Do nothing
    }


    @Override
    public void onRegistrationSaved(boolean isEdit) {
        // Do nothing
    }

}
