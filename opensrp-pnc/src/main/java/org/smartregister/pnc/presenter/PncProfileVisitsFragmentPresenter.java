package org.smartregister.pnc.presenter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.contract.PncProfileVisitsFragmentContract;
import org.smartregister.pnc.domain.YamlConfig;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.interactor.PncProfileVisitsFragmentInteractor;
import org.smartregister.pnc.pojo.PncVisitSummary;
import org.smartregister.pnc.utils.FilePath;
import org.smartregister.pnc.utils.PncFactsUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileVisitsFragmentPresenter implements PncProfileVisitsFragmentContract.Presenter {

    private WeakReference<PncProfileVisitsFragmentContract.View> mProfileView;
    private PncProfileVisitsFragmentContract.Interactor mProfileInteractor;
    private int currentPageNo = 0;
    private int totalPages = 0;

    public PncProfileVisitsFragmentPresenter(@NonNull PncProfileVisitsFragmentContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new PncProfileVisitsFragmentInteractor(this);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (! isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public void loadVisits(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisits(baseEntityId, currentPageNo, pncVisitSummaries -> {
                updatePageCounter();

                List<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();
                populateWrapperDataAndFacts(pncVisitSummaries, items);
                onFinishedCallback.onFinished(pncVisitSummaries, items);
            });

        }
    }

    @Override
    public void loadPageCounter(@NonNull String baseEntityId) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisitsPageCount(baseEntityId, visitsPageCount -> {
                totalPages = visitsPageCount;
                updatePageCounter();
            });
        }
    }

    private void updatePageCounter() {
        String pageCounterTemplate = getString(R.string.current_page_of_total_pages);

        PncProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null && pageCounterTemplate != null) {
            profileView.showPageCountText(String.format(pageCounterTemplate, (currentPageNo + 1), totalPages));

            profileView.showPreviousPageBtn(currentPageNo > 0);
            profileView.showNextPageBtn(currentPageNo < (totalPages -1));
        }
    }

    @Override
    public void populateWrapperDataAndFacts(@NonNull PncVisitSummary pncVisitSummary, @NonNull List<Pair<YamlConfigWrapper, Facts>> items) {

        try {
            for (Map.Entry<String, Map<String, String>> entry: pncVisitSummary.getVisitInfoMap().entrySet()) {

                Iterable<Object> ruleObjects = PncLibrary.getInstance().readYaml(FilePath.FILE.PNC_PROFILE_VISIT);

                Facts facts = generateVisitInfoAndWomanFacts(entry.getValue());

                for (Object ruleObject : ruleObjects) {

                    YamlConfig yamlConfig = (YamlConfig) ruleObject;

                    if (yamlConfig.getGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(yamlConfig.getGroup(), null, null), facts));
                    }

                    if (yamlConfig.getSubGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null), facts));
                    }

                    List<YamlConfigItem> configItems = yamlConfig.getFields();

                    if (configItems != null) {
                        for (YamlConfigItem configItem : configItems) {
                            String relevance = configItem.getRelevance();
                            if (relevance != null && PncLibrary.getInstance().getPncRulesEngineHelper().getRelevance(facts, relevance)) {
                                items.add(new Pair<>( new YamlConfigWrapper(null, null, configItem), facts));
                            }
                        }
                    }
                }

                if (pncVisitSummary.getVisitChildStatusMap().containsKey(entry.getKey())) {
                    generateChild(pncVisitSummary.getVisitChildStatusMap().get(entry.getKey()), items);
                }
            }
        }
        catch (IOException e) {
            Timber.e(e);
        }
    }

    private Facts generateVisitInfoAndWomanFacts(Map<String, String> pncVisitMap) {
        Facts facts = new Facts();

        for (Map.Entry<String, String> entry: pncVisitMap.entrySet()) {
            PncFactsUtil.putNonNullFact(facts, entry.getKey(), entry.getValue());
        }

        return facts;
    }

    private void generateChild(List<Map<String, String>> data, List<Pair<YamlConfigWrapper, Facts>> items) {
        try {

            for (Map<String, String> childData: data) {

                Iterable<Object> ruleObjects = PncLibrary.getInstance().readYaml(FilePath.FILE.PNC_PROFILE_VISIT_ROW);

                Facts facts = new Facts();

                for (Map.Entry<String, String> entry : childData.entrySet()) {
                    PncFactsUtil.putNonNullFact(facts, entry.getKey(), entry.getValue());
                }

                for (Object ruleObject : ruleObjects) {

                    YamlConfig yamlConfig = (YamlConfig) ruleObject;

                    if (yamlConfig.getSubGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null), facts));
                    }

                    List<YamlConfigItem> configItems = yamlConfig.getFields();

                    if (configItems != null) {
                        for (YamlConfigItem configItem : configItems) {
                            String relevance = configItem.getRelevance();
                            if (relevance != null && PncLibrary.getInstance().getPncRulesEngineHelper().getRelevance(facts, relevance)) {
                                items.add(new Pair<>( new YamlConfigWrapper(null, null, configItem), facts));
                            }
                        }
                    }
                }

            }

        } catch (IOException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void onNextPageClicked() {
        if (currentPageNo < totalPages && getProfileView() != null && getProfileView().getClientBaseEntityId() != null) {
            currentPageNo++;

            loadVisits(getProfileView().getClientBaseEntityId(), (pncVisitSummaries, items) -> {
                if (getProfileView() != null) {
                    getProfileView().displayVisits(pncVisitSummaries, items);
                }
            });
        }
    }

    @Override
    public void onPreviousPageClicked() {
        if (currentPageNo > 0 && getProfileView() != null && getProfileView().getClientBaseEntityId() != null) {
            currentPageNo--;

            loadVisits(getProfileView().getClientBaseEntityId(), (pncVisitSummaries, items) -> {
                if (getProfileView() != null) {
                    getProfileView().displayVisits(pncVisitSummaries, items);
                }
            });
        }
    }

    @Nullable
    @Override
    public PncProfileVisitsFragmentContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Nullable
    public String getString(@StringRes int stringId) {
        PncProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null) {
            return profileView.getString(stringId);
        }

        return null;
    }

}