package org.smartregister.pnc.presenter;

import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.utils.PncConstants;


public class PncFormFragmentPresenter extends JsonWizardFormFragmentPresenter {

    public PncFormFragmentPresenter(BasePncFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }


    @Override
    public void addFormElements() {
        super.addFormElements();

    }

    @Override
    protected boolean moveToNextWizardStep() {
        if (!TextUtils.isEmpty(mStepDetails.optString(JsonFormConstants.NEXT))) {
            JsonFormFragment next = BasePncFormFragment.getFormFragment(mStepDetails.optString(PncConstants.JsonFormExtraConstants.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
            return true;
        }
        return false;
    }

}
