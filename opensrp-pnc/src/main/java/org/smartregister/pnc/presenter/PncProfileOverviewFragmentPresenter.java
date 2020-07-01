package org.smartregister.pnc.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.domain.YamlConfig;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.model.PncProfileOverviewFragmentModel;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.utils.FilePath;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncFactsUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        model.fetchPncOverviewDetails(baseEntityId, pncDetails -> {
            loadOverviewDataAndDisplay(pncDetails, onFinishedCallback);

            // Update the client map
            CommonPersonObjectClient commonPersonObjectClient = getProfileView().getActivityClientMap();
            if (commonPersonObjectClient != null) {
                commonPersonObjectClient.getColumnmaps().putAll(pncDetails);
                commonPersonObjectClient.getDetails().putAll(pncDetails);
            }
        });
    }

    @Override
    public void loadOverviewDataAndDisplay(@NonNull HashMap<String, String> pncDetails, @NonNull final OnFinishedCallback onFinishedCallback) {
        List<YamlConfigWrapper> yamlConfigListGlobal = new ArrayList<>();
        Facts facts = new Facts();
        setDataFromRegistration(pncDetails, facts);

        try {
            generateYamlConfigList(facts, yamlConfigListGlobal);
        } catch (IOException ioException) {
            Timber.e(ioException);
        }

        onFinishedCallback.onFinished(facts, yamlConfigListGlobal);
    }

    private void generateYamlConfigList(@NonNull Facts facts, @NonNull List<YamlConfigWrapper> yamlConfigListGlobal) throws IOException {

        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.PNC_PROFILE_OVERVIEW);

        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;

            YamlConfig yamlConfig = (YamlConfig) ruleObject;
            if (yamlConfig.getGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(yamlConfig.getGroup(), null, null));
            }

            if (yamlConfig.getSubGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null));
            }

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            if (configItems != null) {

                for (YamlConfigItem configItem : configItems) {
                    String relevance = configItem.getRelevance();
                    if (relevance != null && PncLibrary.getInstance().getPncRulesEngineHelper()
                            .getRelevance(facts, relevance)) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }
            }

            if (valueCount > 0) {
                yamlConfigListGlobal.addAll(yamlConfigList);
            }

            if ("live_births".equals(yamlConfig.getSubGroup())) {
                addLiveBirths(facts.get("base_entity_id"), yamlConfigListGlobal, facts);
            }
            else if ("stillbirths".equals(yamlConfig.getSubGroup())) {

                yamlConfigListGlobal.add(new YamlConfigWrapper(null, "stillbirths", null));

                int count = PncLibrary.getInstance().getPncStillBornRepository().count(facts.get("base_entity_id"));
                facts.put("baby_count_stillborn", String.valueOf(count));
                yamlConfigListGlobal.add(getConfigItem("Number of babies stillborn: {baby_count_stillborn}", count > 0));
            }
        }
    }

    @Override
    public void setDataFromRegistration(@NonNull HashMap<String, String> pncDetails, @NonNull Facts facts) {
        for (String property: pncDetails.keySet()) {
            PncFactsUtil.putNonNullFact(facts, property, pncDetails.get(property));
        }

        /*
        PncFactsUtil.putNonNullFact(facts, PncConstants.FactKey.ProfileOverview.INTAKE_TIME, pncDetails.getRecordedAt());
        PncFactsUtil.putNonNullFact(facts, PncConstants.FactKey.ProfileOverview.GRAVIDA, pncDetails.get(PncDetails.Property));
        PncFactsUtil.putNonNullFact(facts, PncConstants.FactKey.ProfileOverview.PARA, pncDetails.getPara());
        */

        /*int pncWeeks = 0;
        String conceptionDate = pncDetails.get(PncRegistrationDetails.Property.conception_date.name());

        if (!TextUtils.isEmpty(conceptionDate)) {
            pncWeeks = PncLibrary.getGestationAgeInWeeks(conceptionDate);
        }

        PncFactsUtil.putNonNullFact(facts, PncConstants.FactKey.ProfileOverview.GESTATION_WEEK, "" + pncWeeks);*/

        String prevHivStatus = pncDetails.get(PncRegistrationDetails.Property.hiv_status_previous.name());
        String currentHivStatus = pncDetails.get(PncRegistrationDetails.Property.hiv_status_current.name());
        String hivStatus = StringUtils.isNotBlank(currentHivStatus) ? currentHivStatus : prevHivStatus;
        PncFactsUtil.putNonNullFact(facts, PncConstants.FactKey.ProfileOverview.HIV_STATUS, hivStatus);
    }

    private void addLiveBirths(@NonNull String baseEntityId, @NonNull List<YamlConfigWrapper> yamlConfigListGlobal, @NonNull Facts facts) {
        List<PncChild> childs = PncLibrary.getInstance().getPncChildRepository().findAll(baseEntityId);

        yamlConfigListGlobal.add(new YamlConfigWrapper(null, "live_births", null));

        facts.put("baby_count_alive", String.valueOf(childs.size()));
        yamlConfigListGlobal.add(getConfigItem("Number of babies born alive: {baby_count_alive}"));

        for (int i = 0; i < childs.size(); i++) {
            PncChild child = childs.get(i);

            String subGroup = "yes".equalsIgnoreCase(child.getDischargedAlive()) ? child.getFirstName() + " " + child.getLastName() + " # " + (i + 1) : "Baby born but not discharged alive # " + (i + 1) ;
            yamlConfigListGlobal.add(new YamlConfigWrapper(null, subGroup, null));

            facts.put("baby_discharge_alive_" + i, child.getDischargedAlive());
            yamlConfigListGlobal.add(getConfigItem("Baby discharges status: {baby_discharge_alive_" + i + "}"));

            if ("no".equalsIgnoreCase(child.getChildRegistered())) {
                facts.put("baby_full_name_" + i, child.getFirstName() + " " + child.getLastName());
                yamlConfigListGlobal.add(getConfigItem("Baby Name: {baby_full_name_" + i + "}"));
            }

            facts.put("baby_gender_" + i, child.getGender());
            yamlConfigListGlobal.add(getConfigItem("Child's gender: {baby_gender_" + i + "}"));

            facts.put("baby_birth_weight_entered_" + i, child.getWeightEntered());
            yamlConfigListGlobal.add(getConfigItem("Birth weight (gm): {baby_birth_weight_entered_" + i + "}"));

            facts.put("baby_birth_height_entered_" + i, child.getHeightEntered());
            yamlConfigListGlobal.add(getConfigItem("Birth length (cm): {baby_birth_height_entered_" + i + "}"));

            facts.put("baby_apgar_" + i, child.getApgar());
            yamlConfigListGlobal.add(getConfigItem("APGAR score: {baby_apgar_" + i + "}"));

            if (StringUtils.isNotBlank(child.getComplications()) || StringUtils.isNotBlank(child.getComplicationsOther())) {
                String complications = StringUtils.isNoneBlank(child.getComplications()) ? child.getComplications() : child.getComplicationsOther();
                facts.put("baby_complications_" + i, complications);
                yamlConfigListGlobal.add(getConfigItem("Newborn care complications: {baby_complications_" + i + "}", true));
            }

            if (StringUtils.isNotBlank(child.getCareMgt()) || StringUtils.isNotBlank(child.getCareMgtSpecify())) {
                String babyCareMgmt = StringUtils.isNoneBlank(child.getCareMgt()) ? child.getCareMgt() : child.getCareMgtSpecify();
                facts.put("baby_care_mgmt_" + i, babyCareMgmt);
                yamlConfigListGlobal.add(getConfigItem("Newborn Routine Care & Management: {baby_care_mgmt_" + i + "}"));
            }

            facts.put("baby_referral_location_" + i, child.getRefLocation());
            yamlConfigListGlobal.add(getConfigItem("Referral location: {baby_referral_location_" + i + "}"));

            facts.put("bf_first_hour_" + i, child.getBfFirstHour());
            yamlConfigListGlobal.add(getConfigItem("Breastfeeding initiated within 60 mins: {bf_first_hour_" + i + "}"));

            facts.put("child_hiv_status_" + i, child.getBfFirstHour());
            yamlConfigListGlobal.add(getConfigItem("Child's HIV status: {child_hiv_status_" + i + "}"));

            facts.put("nvp_administration_" + i, child.getBfFirstHour());
            yamlConfigListGlobal.add(getConfigItem("NVP Administration Started: {nvp_administration_" + i + "}"));
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

    private YamlConfigWrapper getConfigItem(String template) {
        return getConfigItem(template, false);
    }

    private YamlConfigWrapper getConfigItem(String template, boolean isRedFont) {
        return new YamlConfigWrapper(null, null, new YamlConfigItem(template, null, isRedFont ? "yes" : null));
    }
}
