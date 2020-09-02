package org.smartregister.pnc.presenter;

import android.support.annotation.NonNull;

import org.smartregister.domain.FetchStatus;
import org.smartregister.pnc.R;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterActivityPresenter extends BasePncRegisterActivityPresenter implements PncRegisterActivityContract.InteractorCallBack {

    public PncRegisterActivityPresenter(PncRegisterActivityContract.View view, PncRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        if (registerParams.getFormTag() == null) {
            registerParams.setFormTag(PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences()));
        }

        List<PncEventClient> pncEventClientList = model.processRegistration(jsonString, registerParams.getFormTag());
        if (pncEventClientList == null || pncEventClientList.isEmpty()) {
            return;
        }

        registerParams.setEditMode(false);
        interactor.saveRegistration(pncEventClientList, jsonString, registerParams, this);
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null) {
            getView().displayShortToast(R.string.no_unique_id);
        }
    }

    @NonNull
    @Override
    public PncRegisterActivityContract.Interactor createInteractor() {
        return new PncRegisterActivityInteractor();
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }
}
