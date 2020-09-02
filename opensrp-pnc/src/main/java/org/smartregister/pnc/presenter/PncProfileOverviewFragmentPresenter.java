package org.smartregister.pnc.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.domain.YamlConfig;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.model.PncProfileOverviewFragmentModel;
import org.smartregister.pnc.repository.PncMedicInfoRepository;
import org.smartregister.pnc.utils.FilePath;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncFactsUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncProfileOverviewFragmentPresenter implements PncProfileOverviewFragmentContract.Presenter {

    private PncProfileOverviewFragmentModel model;
    private WeakReference<PncProfileOverviewFragmentContract.View> view;

    public PncProfileOverviewFragmentPresenter(@NonNull PncProfileOverviewFragmentContract.View view) {
        this.view = new WeakReference<>(view);
        model = new PncProfileOverviewFragmentModel();
    }

    @Override
    public void loadOverviewFacts(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        model.fetchPncOverviewDetails(baseEntityId, pncMedicInfo -> {
            loadOverviewDataAndDisplay(pncMedicInfo, onFinishedCallback);

            // Update the client map
            CommonPersonObjectClient commonPersonObjectClient = getProfileView().getActivityClientMap();
            if (commonPersonObjectClient != null) {
                commonPersonObjectClient.getColumnmaps().putAll(pncMedicInfo);
                commonPersonObjectClient.getDetails().putAll(pncMedicInfo);
            }
        });
    }

    @Override
    public void loadOverviewDataAndDisplay(@NonNull HashMap<String, String> pncMedicInfo, @NonNull final OnFinishedCallback onFinishedCallback) {
        ArrayList<Pair<YamlConfigWrapper, Facts>> yamlConfigListGlobal = new ArrayList<>();
        Facts facts = new Facts();
        setDataFromRegistration(pncMedicInfo, facts);

        try {
            generateYamlConfigList(pncMedicInfo.get(PncConstants.KeyConstants.BASE_ENTITY_ID), facts, yamlConfigListGlobal);
        } catch (IOException ioException) {
            Timber.e(ioException);
        }

        onFinishedCallback.onFinished(facts, yamlConfigListGlobal);
    }

    private void generateYamlConfigList(String motherBaseEntityId, @NonNull Facts facts, ArrayList<Pair<YamlConfigWrapper, Facts>> items) throws IOException {

        int count = PncLibrary.getInstance().getPncStillBornRepository().countBabyStillBorn(motherBaseEntityId);
        facts.put(PncConstants.BABY_COUNT_STILLBORN, String.valueOf(count));
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.PNC_PROFILE_OVERVIEW);

        Iterable<Object> ruleObjectsObjectIterable = Lists.newArrayList(ruleObjects); // creates a deep copy
        for (Object ruleObject : ruleObjectsObjectIterable) {

            YamlConfig yamlConfig = (YamlConfig) ruleObject;


            if (yamlConfig.getGroup() != null) {
                items.add(new Pair<>(new YamlConfigWrapper(yamlConfig.getGroup(),
                        null, null, null), facts));
            }

            if (yamlConfig.getSubGroup() != null) {
                if (PncConstants.LIVE_BIRTHS_SECTION.equals(yamlConfig.getSubGroup())) {
                    generateLiveBirths(motherBaseEntityId, items);
                } else {
                    items.add(new Pair<>(new YamlConfigWrapper(null,
                            yamlConfig.getSubGroup(), null, yamlConfig.getRelevance()), facts));
                }
            }

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            if (configItems != null) {

                for (YamlConfigItem configItem : configItems) {
                    String relevance = configItem.getRelevance();
                    if (relevance != null && PncLibrary.getInstance().getPncRulesEngineHelper()
                            .getRelevance(facts, relevance)) {
                        YamlConfigWrapper yamlConfigWrapper = new YamlConfigWrapper(null,
                                null, configItem, null);
                        items.add(new Pair<>(yamlConfigWrapper, facts));
                    }
                }
            }
        }
    }

    @Override
    public void setDataFromRegistration(@NonNull HashMap<String, String> pncMedicInfo, @NonNull Facts facts) {
        for (String property : pncMedicInfo.keySet()) {
            PncFactsUtil.putNonNullFact(facts, property, pncMedicInfo.get(property));
        }

        String prevHivStatus = pncMedicInfo.get(PncMedicInfoRepository.Property.hiv_status_previous.name());
        String currentHivStatus = pncMedicInfo.get(PncMedicInfoRepository.Property.hiv_status_current.name());
        String hivStatus = StringUtils.isNotBlank(currentHivStatus) ? currentHivStatus : prevHivStatus;
        PncFactsUtil.putNonNullFact(facts, PncConstants.FactKey.ProfileOverview.HIV_STATUS, hivStatus);
    }

    private void generateLiveBirths(@NonNull String motherBaseEntityId, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {

        try {

            List<HashMap<String, String>> childRecords = PncLibrary.getInstance().getPncChildRepository().findAllByMotherBaseEntityId(motherBaseEntityId);

            for (HashMap<String, String> record : childRecords) {

                Facts facts = new Facts();

                Iterable<Object> ruleObjects = PncLibrary.getInstance().readYaml(FilePath.FILE.PNC_PROFILE_OVERVIEW_LIVE_BIRTH);

                for (Map.Entry<String, String> entry : record.entrySet()) {
                    PncFactsUtil.putNonNullFact(facts, entry.getKey(), entry.getValue());
                }

                for (Object ruleObject : ruleObjects) {

                    YamlConfig yamlConfig = (YamlConfig) ruleObject;

                    if (yamlConfig.getSubGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null, null), facts));
                    }

                    List<YamlConfigItem> configItems = yamlConfig.getFields();

                    if (configItems != null) {

                        for (YamlConfigItem configItem : configItems) {
                            String relevance = configItem.getRelevance();
                            if (relevance != null && PncLibrary.getInstance().getPncRulesEngineHelper().getRelevance(facts, relevance)) {
                                YamlConfigWrapper yamlConfigWrapper = new YamlConfigWrapper(null, null, configItem, null);
                                items.add(new Pair<>(yamlConfigWrapper, facts));
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    private Iterable<Object> loadFile(@NonNull String filename) throws IOException {
        return PncLibrary.getInstance().readYaml(filename);
    }

    public void setClient(@NonNull CommonPersonObjectClient client) {
        // Do nothing
    }

    @Nullable
    @Override
    public PncProfileOverviewFragmentContract.View getProfileView() {
        PncProfileOverviewFragmentContract.View view = this.view.get();
        if (view != null) {
            return view;
        }

        return null;
    }

    @Nullable
    @Override
    public String getString(int stringId) {
        if (getProfileView() != null) {
            return getProfileView().getString(stringId);
        }

        return null;
    }
}
