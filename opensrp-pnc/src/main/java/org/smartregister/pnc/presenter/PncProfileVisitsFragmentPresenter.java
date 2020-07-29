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
import org.smartregister.pnc.utils.FilePath;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    public void populateWrapperDataAndFacts(@NonNull List<Map<String, Object>> pncVisitSummaries, @NonNull List<Pair<YamlConfigWrapper, Facts>> items) {
        for (Map<String, Object> pncVisitSummary: pncVisitSummaries) {

            try {
                Iterable<Object> ruleObjects = PncLibrary.getInstance().readYaml(FilePath.FILE.PNC_PROFILE_VISIT);

                Facts facts = new Facts();

                for (Object ruleObject : ruleObjects) {

                    YamlConfig yamlConfig = (YamlConfig) ruleObject;

                    if (yamlConfig.getGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(yamlConfig.getGroup(), null, null), facts));
                    }

                    if ("pnc_visit_information".equals(yamlConfig.getSubGroup())) {
                        addVisitInfo(pncVisitSummary, items, facts);
                    }
                    else if ("woman_status".equals(yamlConfig.getSubGroup())) {
                        addWomanStatus(pncVisitSummary, items, facts);
                    }
                    else if ("child_status".equals(yamlConfig.getSubGroup())) {
                        addChildStatus(pncVisitSummary, items,facts);
                    }
                }

            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    private void addVisitInfo(Map<String, Object> data, List<Pair<YamlConfigWrapper, Facts>> items, Facts facts) {

        items.add(new Pair<>(new YamlConfigWrapper(null, "pnc_visit_information", null), facts));

        String keyPeriod = PncDbConstants.Column.PncVisitInfo.PERIOD;
        if (isNotEmpty(keyPeriod, data) && Integer.parseInt((String)data.get(keyPeriod)) > 0) {
            facts.put(keyPeriod, data.get(keyPeriod));
            items.add(new Pair<>(getConfigItem("PNC Visit day: {" + keyPeriod + "}"), facts));
        }

        String firstVisitCheck = PncDbConstants.Column.PncVisitInfo.FIRST_VISIT_CHECK;
        if (isNotEmpty(firstVisitCheck, data)) {
            facts.put(firstVisitCheck, data.get(firstVisitCheck));
            items.add(new Pair<>(getConfigItem("PNC received within 48 hours of delivery?: {" + firstVisitCheck + "}"), facts));
        }

        String outsideFacility = PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY;
        if (isNotEmpty(outsideFacility, data)) {
            facts.put(outsideFacility, data.get(outsideFacility));
            items.add(new Pair<>(getConfigItem("Other visits outside this facility?: {" + outsideFacility + "}"), facts));
        }

        String outsideFacilityNumber = PncDbConstants.Column.PncVisitInfo.OUTSIDE_FACILITY_NUMBER;
        if (isNotEmpty(outsideFacilityNumber, data)) {
            facts.put(outsideFacilityNumber, data.get(outsideFacilityNumber));
            items.add(new Pair<>(getConfigItem("How many visits?: {" + outsideFacilityNumber + "}"), facts));
        }
    }

    private void addWomanStatus(Map<String, Object> data, List<Pair<YamlConfigWrapper, Facts>> items, Facts facts) {

        items.add(new Pair<>(new YamlConfigWrapper(null, "woman_status", null), facts));

        String keyComplications = PncDbConstants.Column.PncVisitInfo.COMPLICATIONS;
        String keyComplicationsOther = PncDbConstants.Column.PncVisitInfo.COMPLICATIONS_OTHER;
        if (isNotEmpty(keyComplications, data) || isNotEmpty(keyComplicationsOther, data)) {
            String value = isNotEmpty(keyComplications, data) ? (String) data.get(keyComplications) : (String) data.get(keyComplicationsOther);
            facts.put(keyComplications, value);
            items.add(new Pair<>(getConfigItem("Postnatal complications: {" + keyComplications + "}", !"none".equalsIgnoreCase(value)), facts));
        }

        String keyCSection = PncDbConstants.Column.PncVisitInfo.STATUS_C_SECTION;
        if (isNotEmpty(keyCSection, data)) {
            facts.put(keyCSection, data.get(keyCSection));
            items.add(new Pair<>(getConfigItem("Status of C-section incision: {" + keyCSection + "}", isNotEmpty(keyCSection, data)), facts));
        }

        String keyTearStatus = PncDbConstants.Column.PncVisitInfo.EPISOTOMY_TEAR_STATUS;
        if (isNotEmpty(keyTearStatus, data)) {
            facts.put(keyTearStatus, data.get(keyTearStatus));
            items.add(new Pair<>(getConfigItem("Condition of episiotomy/tear, if present: {" + keyTearStatus + "}", isNotEmpty(keyTearStatus, data)), facts));
        }

        String keyLochia = PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS;
        String keyLochiaOther = PncDbConstants.Column.PncVisitInfo.LOCHIA_STATUS_OTHER;
        if (isNotEmpty(keyLochia, data) || isNotEmpty(keyLochiaOther, data)) {
            String value = isNotEmpty(keyLochia, data) ? (String) data.get(keyLochia) : (String) data.get(keyLochiaOther);
            facts.put(keyLochia, value);
            items.add(new Pair<>(getConfigItem("Status of the lochia: {" + keyLochia + "}", true), facts));
        }

        String keyUterus = PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS;
        String keyUterusOther = PncDbConstants.Column.PncVisitInfo.UTERUS_STATUS_OTHER;
        if (isNotEmpty(keyUterus, data) || isNotEmpty(keyUterusOther, data)) {
            String value = isNotEmpty(keyUterus, data) ? (String) data.get(keyUterus) : (String) data.get(keyUterusOther);
            facts.put(keyUterus, value);
            items.add(new Pair<>(getConfigItem("Status of the uterus: {" + keyUterus + "}", true), facts));
        }

        String keyIntervention = PncDbConstants.Column.PncVisitInfo.INTERVENTION_GIVEN_TEXT;
        if (isNotEmpty(keyIntervention, data)) {
            facts.put(keyIntervention, data.get(keyIntervention));
            items.add(new Pair<>(getConfigItem("Intervention given: {" + keyIntervention + "}", isNotEmpty(keyIntervention, data)), facts));
        }

        String keyReferred = PncDbConstants.Column.PncVisitInfo.REFERRED_OUT;
        String keyReferredOther = PncDbConstants.Column.PncVisitInfo.REFERRED_OUT_SPECIFY;
        if (isNotEmpty(keyReferred, data) || isNotEmpty(keyReferredOther, data)) {
            String value = isNotEmpty(keyReferred, data) ? (String) data.get(keyReferred) : (String) data.get(keyReferredOther);
            facts.put(keyReferred, value);
            items.add(new Pair<>(getConfigItem("Was the woman referred?: {" + keyReferred + "}"), facts));
        }

        String keyVitA = PncDbConstants.Column.PncVisitInfo.VIT_A;
        if (isNotEmpty(keyVitA, data)) {
            facts.put(keyVitA, data.get(keyVitA));
            items.add(new Pair<>(getConfigItem("Was Vitamin A given?: {" + keyVitA + "}"), facts));
        }

        String keyVitANotGivenReason = PncDbConstants.Column.PncVisitInfo.VIT_A_NOT_GIVING_REASON;
        if (isNotEmpty(keyVitANotGivenReason, data)) {
            facts.put(keyVitANotGivenReason, data.get(keyVitANotGivenReason));
            items.add(new Pair<>(getConfigItem("Provide a reason for not giving Vitamin A: {" + keyVitANotGivenReason + "}"), facts));
        }

        String keyFPCounsel = PncDbConstants.Column.PncVisitInfo.FP_COUNSEL;
        if (isNotEmpty(keyFPCounsel, data)) {
            facts.put(keyFPCounsel, data.get(keyFPCounsel));
            items.add(new Pair<>(getConfigItem("Family planning done?: {" + keyFPCounsel + "}"), facts));
        }

        String keyMethod = PncDbConstants.Column.PncVisitInfo.FP_METHOD;
        String keyMethodOther = PncDbConstants.Column.PncVisitInfo.FP_METHOD_OTHER;
        if (isNotEmpty(keyMethod, data) || isNotEmpty(keyMethodOther, data)) {
            String value = isNotEmpty(keyMethod, data) ? (String) data.get(keyMethod) : (String) data.get(keyMethodOther);
            facts.put(keyMethod, value);
            items.add(new Pair<>(getConfigItem("Family planning methods chosen: {" + keyMethod + "}"), facts));
        }
    }

    private void addChildStatus(Map<String, Object> data, List<Pair<YamlConfigWrapper, Facts>> items, Facts facts) {
        if (data.containsKey(PncConstants.CHILD_RECORDS)) {
            List<Map<String, String>> childRecords = (List<Map<String, String>>) data.get(PncConstants.CHILD_RECORDS);

            items.add(new Pair<>(new YamlConfigWrapper(null, "child_status", null), facts));
            for (int i = 0; i < childRecords.size(); i++) {
                Map<String, String> record = childRecords.get(i);

                String firstName = record.get(PncDbConstants.Column.PncBaby.FIRST_NAME);
                String lastName = record.get(PncDbConstants.Column.PncBaby.LAST_NAME);
                String heading = String.format(Locale.getDefault(), "%s %s", firstName, lastName);
                items.add(new Pair<>(new YamlConfigWrapper(null, heading, null), facts));

                String keyBabyStatus = PncDbConstants.Column.PncVisitChildStatus.BABY_STATUS;
                if (isNotEmpty(keyBabyStatus, record)) {
                    facts.put(keyBabyStatus + "_" + i, record.get(keyBabyStatus));
                    items.add(new Pair<>(getConfigItem("Current Baby's Status: {" + keyBabyStatus + "_" + i + "}", (isNotEmpty(keyBabyStatus, record) || "alive".equalsIgnoreCase(record.get(keyBabyStatus)))), facts));
                }

                String keyDateOfDeath = PncDbConstants.Column.PncVisitChildStatus.DATE_OF_DEATH_BABY;
                if (isNotEmpty(keyDateOfDeath, record)) {
                    facts.put(keyDateOfDeath + "_" + i, record.get(keyDateOfDeath));
                    items.add(new Pair<>(getConfigItem("Date of death: {" + keyDateOfDeath + "_" + i + "}"), facts));
                }

                String keyPlaceOfDeath = PncDbConstants.Column.PncVisitChildStatus.PLACE_OF_DEATH_BABY;
                if (isNotEmpty(keyPlaceOfDeath, record)) {
                    facts.put(keyPlaceOfDeath + "_" + i, record.get(keyPlaceOfDeath));
                    items.add(new Pair<>(getConfigItem("Place of death: {" + keyPlaceOfDeath + "_" + i + "}"), facts));
                }

                String keyCauseOfDeathBaby = PncDbConstants.Column.PncVisitChildStatus.CAUSE_OF_DEATH_BABY;
                if (isNotEmpty(keyCauseOfDeathBaby, record)) {
                    facts.put(keyCauseOfDeathBaby + "_" + i, record.get(keyCauseOfDeathBaby));
                    items.add(new Pair<>(getConfigItem("Cause of death: {" + keyCauseOfDeathBaby + "_" + i + "}"), facts));
                }

                String keyDeathFollowUp = PncDbConstants.Column.PncVisitChildStatus.DEATH_FOLLOW_UP_BABY;
                if (isNotEmpty(keyDeathFollowUp, record)) {
                    facts.put(keyDeathFollowUp + "_" + i, record.get(keyDeathFollowUp));
                    items.add(new Pair<>(getConfigItem("Was the follow up of death conducted by health workers?: {" + keyDeathFollowUp + "_" + i + "}"), facts));
                }

                String keyBreastfeeding = PncDbConstants.Column.PncVisitChildStatus.BABY_BREAST_FEEDING;
                if (isNotEmpty(keyBreastfeeding, record)) {
                    facts.put(keyBreastfeeding + "_" + i, record.get(keyBreastfeeding));
                    items.add(new Pair<>(getConfigItem("Breastfeeding status of the baby: {" + keyBreastfeeding + "_" + i + "}"), facts));
                }

                String keyNotBreastfeeding = PncDbConstants.Column.PncVisitChildStatus.BABY_NOT_BREAST_FEEDING_REASON;
                if (isNotEmpty(keyNotBreastfeeding, record)) {
                    facts.put(keyNotBreastfeeding + "_" + i, record.get(keyNotBreastfeeding));
                    items.add(new Pair<>(getConfigItem("Not breastfeeding reason: {" + keyNotBreastfeeding + "_" + i + "}"), facts));
                }

                String keyDanger = PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS;
                String keyDangerOther = PncDbConstants.Column.PncVisitChildStatus.BABY_DANGER_SIGNS_OTHER;
                if (isNotEmpty(keyDanger, record) || isNotEmpty(keyDangerOther, record)) {
                    String value = isNotEmpty(keyDanger, record) ? record.get(keyDanger) : record.get(keyDangerOther);
                    facts.put(keyDanger + "_" + i, value);
                    items.add(new Pair<>(getConfigItem("Danger signs and complications at visit: {" + keyDanger + "_" + i + "}", (!"none".equalsIgnoreCase(record.get(keyDanger)) || !"none".equalsIgnoreCase(record.get(keyDangerOther)))), facts));
                }

                String keyReferredOut = PncDbConstants.Column.PncVisitChildStatus.BABY_REFERRED_OUT;
                if (isNotEmpty(keyReferredOut, record)) {
                    facts.put(keyReferredOut + "_" + i, record.get(keyReferredOut));
                    items.add(new Pair<>(getConfigItem("Baby referred out?: {" + keyReferredOut + "_" + i + "}"), facts));
                }

                String keyHivExposed = PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_EXPOSED;
                if (isNotEmpty(keyHivExposed, record)) {
                    facts.put(keyHivExposed + "_" + i, record.get(keyHivExposed));
                    items.add(new Pair<>(getConfigItem("Baby exposed to HIV?: {" + keyHivExposed + "_" + i + "}", "yes".equalsIgnoreCase(record.get(keyHivExposed))), facts));
                }

                String keyHivTreatment = PncDbConstants.Column.PncVisitChildStatus.BABY_HIV_TREATMENT;
                if (isNotEmpty(keyHivTreatment, record)) {
                    facts.put(keyHivTreatment + "_" + i, record.get(keyHivTreatment));
                    items.add(new Pair<>(getConfigItem("HIV treatment: {" + keyHivTreatment + "_" + i + "}"), facts));
                }

                String keyMotherBabyPairing = PncDbConstants.Column.PncVisitChildStatus.MOTHER_BABY_PAIRING;
                if (isNotEmpty(keyMotherBabyPairing, record)) {
                    facts.put(keyMotherBabyPairing + "_" + i, record.get(keyMotherBabyPairing));
                    items.add(new Pair<>(getConfigItem("Referred to the ART clinic for mother/infant pairing?: {" + keyMotherBabyPairing + "_" + i + "}"), facts));
                }

                String keyNotArtPairing = PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON;
                String keyNotArtPairingOther = PncDbConstants.Column.PncVisitChildStatus.NOT_ART_PAIRING_REASON_OTHER;
                if (isNotEmpty(keyNotArtPairing, record) || isNotEmpty(keyNotArtPairingOther, record)) {
                    String value = isNotEmpty(keyNotArtPairing, record) ? record.get(keyNotArtPairing) : record.get(keyNotArtPairingOther);
                    facts.put(keyNotArtPairing + "_" + i, value);
                    items.add(new Pair<>(getConfigItem("Mother not referred to the ART clinic for mother/infant pairing reason: {" + keyNotArtPairing + "_" + i + "}", true), facts));
                }

                String keyBabyDbs = PncDbConstants.Column.PncVisitChildStatus.BABY_DBS;
                if (isNotEmpty(keyBabyDbs, record)) {
                    facts.put(keyBabyDbs + "_" + i, record.get(keyBabyDbs));
                    items.add(new Pair<>(getConfigItem("Baby tested at 6 weeks using DBS (Dry Blood Sample)?: {" + keyBabyDbs + "_" + i + "}"), facts));
                }

                String keyCareMgmt = PncDbConstants.Column.PncVisitChildStatus.BABY_CARE_MGMT;
                if (isNotEmpty(keyCareMgmt, record)) {
                    facts.put(keyCareMgmt + "_" + i, record.get(keyCareMgmt));
                    items.add(new Pair<>(getConfigItem("Newborn care & management: {" + keyCareMgmt + "_" + i + "}"), facts));
                }
            }
        }
    }

    private YamlConfigWrapper getConfigItem(String template) {
        return getConfigItem(template, false);
    }

    private YamlConfigWrapper getConfigItem(String template, boolean isRedFont) {
        return new YamlConfigWrapper(null, null, new YamlConfigItem(template, null, isRedFont ? "yes" : null));
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

    private <T extends Object> boolean  isNotEmpty(String key, Map<String, T> record) {
        if (record.get(key) == null) return false;
        return String.format(Locale.getDefault(), "%s", record.get(key)).length() > 0;
    }
}