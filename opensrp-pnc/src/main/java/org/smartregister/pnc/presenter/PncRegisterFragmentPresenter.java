package org.smartregister.pnc.presenter;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.pnc.contract.PncRegisterFragmentContract;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterFragmentPresenter implements PncRegisterFragmentContract.Presenter {

    private WeakReference<PncRegisterFragmentContract.View> viewReference;
    private PncRegisterFragmentContract.Model model;

    public PncRegisterFragmentPresenter(PncRegisterFragmentContract.View view, PncRegisterFragmentContract.Model model) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
    }

    @Override
    public void processViewConfigurations() {
        // Do nothing since we don't have process views
    }

    @Override
    public void initializeQueries(String mainCondition) {
        getView().initializeQueryParams("ec_client", null, null);
        getView().initializeAdapter();

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    protected PncRegisterFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {

            return null;
        }
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getDueFilterCondition() {
        return "DUE_ONLY";
    }

    public void setModel(PncRegisterFragmentContract.Model model) {
        this.model = model;
    }
}
