package org.smartregister.pnc;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.config.PncFormProcessingTask;
import org.smartregister.pnc.domain.YamlConfig;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.helper.PncRulesEngineHelper;
import org.smartregister.pnc.repository.PncOutcomeDetailsRepository;
import org.smartregister.pnc.repository.PncOutcomeFormRepository;
import org.smartregister.pnc.repository.PncRegistrationDetailsRepository;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;
import org.smartregister.pnc.utils.FilePath;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;

import static org.smartregister.pnc.utils.PncJsonFormUtils.METADATA;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncLibrary {

    private static PncLibrary instance;
    private final Context context;
    private final Repository repository;
    private PncConfiguration pncConfiguration;
    private ECSyncHelper syncHelper;

    private UniqueIdRepository uniqueIdRepository;
    private PncOutcomeDetailsRepository pncOutcomeDetailsRepository;
    private PncRegistrationDetailsRepository pncRegistrationDetailsRepository;
    private PncOutcomeFormRepository pncOutcomeFormRepository;

    private Compressor compressor;
    private int applicationVersion;
    private int databaseVersion;

    private Yaml yaml;

    private PncRulesEngineHelper pncRulesEngineHelper;

    protected PncLibrary(@NonNull Context context, @NonNull PncConfiguration pncConfiguration
            , @NonNull Repository repository, int applicationVersion, int databaseVersion) {
        this.context = context;
        this.pncConfiguration = pncConfiguration;
        this.repository = repository;
        this.applicationVersion = applicationVersion;
        this.databaseVersion = databaseVersion;

        // Initialize configs processor
        initializeYamlConfigs();
    }

    public static void init(Context context, @NonNull Repository repository, @NonNull PncConfiguration pncConfiguration
            , int applicationVersion, int databaseVersion) {
        if (instance == null) {
            instance = new PncLibrary(context, pncConfiguration, repository, applicationVersion, databaseVersion);
        }
    }

    public static PncLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance does not exist!!! Call "
                    + PncLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class");
        }
        return instance;
    }


    public static int getGestationAgeInWeeks(@NonNull String conceptionDateString) {
        DateTimeFormatter SQLITE_DATE_DF = DateTimeFormat.forPattern("dd-MM-yyyy");
        LocalDate conceptionDate = SQLITE_DATE_DF.withOffsetParsed().parseLocalDate(conceptionDateString);
        Weeks weeks = Weeks.weeksBetween(conceptionDate, LocalDate.now());
        return weeks.getWeeks();
    }

    @NonNull
    public Context context() {
        return context;
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    @NonNull
    public PncOutcomeDetailsRepository getPncOutcomeDetailsRepository() {
        if (pncOutcomeDetailsRepository == null) {
            pncOutcomeDetailsRepository = new PncOutcomeDetailsRepository();
        }
        return pncOutcomeDetailsRepository;
    }

    @NonNull
    public PncRegistrationDetailsRepository getPncRegistrationDetailsRepository() {
        if (pncRegistrationDetailsRepository == null) {
            pncRegistrationDetailsRepository = new PncRegistrationDetailsRepository();
        }

        return pncRegistrationDetailsRepository;
    }

    @NonNull
    public PncOutcomeFormRepository getPncOutcomeFormRepository() {
        if (pncOutcomeFormRepository == null) {
            pncOutcomeFormRepository = new PncOutcomeFormRepository();
        }
        return pncOutcomeFormRepository;
    }

    @NonNull
    public Repository getRepository() {
        return repository;
    }


    @NonNull
    public ECSyncHelper getEcSyncHelper() {
        if (syncHelper == null) {
            syncHelper = ECSyncHelper.getInstance(context().applicationContext());
        }
        return syncHelper;
    }

    @NonNull
    public PncConfiguration getPncConfiguration() {
        return pncConfiguration;
    }

    @NonNull
    public Compressor getCompressor() {
        if (compressor == null) {
            compressor = new Compressor(context().applicationContext());
        }

        return compressor;
    }

    @NonNull
    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
    }


    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }

    private void initializeYamlConfigs() {
        Constructor constructor = new Constructor(YamlConfig.class);
        TypeDescription customTypeDescription = new TypeDescription(YamlConfig.class);
        customTypeDescription.addPropertyParameters(YamlConfigItem.GENERIC_YAML_ITEMS, YamlConfigItem.class);
        constructor.addTypeDescription(customTypeDescription);
        yaml = new Yaml(constructor);
    }

    @NonNull
    public Iterable<Object> readYaml(@NonNull String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                context.applicationContext().getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    @NonNull
    public PncRulesEngineHelper getPncRulesEngineHelper() {
        if (pncRulesEngineHelper == null) {
            pncRulesEngineHelper = new PncRulesEngineHelper();
        }

        return pncRulesEngineHelper;
    }

    @NonNull
    public List<Event> processPncOutcomeForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        ArrayList<Class<? extends PncFormProcessingTask>> maternityFormProcessingTasks = getPncConfiguration().getMaternityFormProcessingTasks();

        for (Class<? extends PncFormProcessingTask> maternityFormProcessingTaskClass: maternityFormProcessingTasks) {
            PncFormProcessingTask maternityFormProcessingTask = ConfigurationInstancesHelper.newInstance(maternityFormProcessingTaskClass);
            maternityFormProcessingTask.processMaternityForm(eventType, jsonString, data);
        }

        ArrayList<Event> eventList = new ArrayList<>();
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONArray fieldsArray = PncUtils.generateFieldsFromJsonForm(jsonFormObject);

        FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());

        String baseEntityId = PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = PncUtils.getIntentValue(data, PncConstants.IntentKey.ENTITY_TABLE);
        Event maternityOutcomeEvent = PncJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, eventType, entityTable);
        eventList.add(maternityOutcomeEvent);

        Event closeMaternityEvent = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, baseEntityId, PncConstants.EventTypeConstants.MATERNITY_CLOSE, "");
        PncJsonFormUtils.tagSyncMetadata(closeMaternityEvent);
        closeMaternityEvent.addDetails(PncConstants.JsonFormKeyConstants.VISIT_END_DATE, PncUtils.convertDate(new Date(), PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closeMaternityEvent);

        return eventList;
    }

    @NonNull
    public List<Event> processMaternityCloseForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        ArrayList<Event> eventList = new ArrayList<>();
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONArray fieldsArray = PncUtils.generateFieldsFromJsonForm(jsonFormObject);
        FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());

        String baseEntityId = PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = PncUtils.getIntentValue(data, PncConstants.IntentKey.ENTITY_TABLE);
        Event closeMaternityEvent = JsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, eventType, entityTable);
        PncJsonFormUtils.tagSyncMetadata(closeMaternityEvent);
        eventList.add(closeMaternityEvent);

        return eventList;
    }

    public String maternityLookUpQuery() {
        String lookUpQueryForChild = "select id as _id, %s, %s, %s, %s, %s, %s, zeir_id as %s, null as national_id from ec_child where [condition] ";
        lookUpQueryForChild = String.format(lookUpQueryForChild, PncConstants.KeyConstants.RELATIONALID, PncConstants.KeyConstants.FIRST_NAME,
                PncConstants.KeyConstants.LAST_NAME, PncConstants.KeyConstants.GENDER, PncConstants.KeyConstants.DOB, PncConstants.KeyConstants.BASE_ENTITY_ID, PncDbConstants.KEY.OPENSRP_ID);
        String lookUpQueryForMother = "select id as _id, %s, %s, %s, %s, %s, %s, register_id as %s, nrc_number as national_id from ec_mother where [condition] ";
        lookUpQueryForMother = String.format(lookUpQueryForMother, PncConstants.KeyConstants.RELATIONALID, PncConstants.KeyConstants.FIRST_NAME,
                PncConstants.KeyConstants.LAST_NAME, PncConstants.KeyConstants.GENDER, PncConstants.KeyConstants.DOB, PncConstants.KeyConstants.BASE_ENTITY_ID, PncConstants.KeyConstants.OPENSRP_ID);
        String lookUpQueryForOpdOrMaternityClient = "select id as _id, %s, %s, %s, %s, %s, %s, %s, national_id from ec_client where [condition] ";
        lookUpQueryForOpdOrMaternityClient = String.format(lookUpQueryForOpdOrMaternityClient, PncConstants.KeyConstants.RELATIONALID, PncConstants.KeyConstants.FIRST_NAME,
                PncConstants.KeyConstants.LAST_NAME, PncConstants.KeyConstants.GENDER, PncConstants.KeyConstants.DOB, PncConstants.KeyConstants.BASE_ENTITY_ID, PncConstants.KeyConstants.OPENSRP_ID);
        return lookUpQueryForChild + " union all " + lookUpQueryForMother + " union all " + lookUpQueryForOpdOrMaternityClient;
    }

    /**
     * This method enables us to configure how-long ago we should consider a valid check-in so that
     * we enable the next step which is DIAGNOSE & TREAT. This method returns the latest date that a check-in
     * should be so that it can be considered for moving to DIAGNOSE & TREAT
     *
     * @return Date
     */
    @NonNull
    public Date getLatestValidCheckInDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return calendar.getTime();
    }

    public boolean isPatientInTreatedState(@NonNull String strVisitEndDate) {
        Date visitEndDate = PncUtils.convertStringToDate(PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, strVisitEndDate);
        if (visitEndDate != null) {
            return isPatientInTreatedState(visitEndDate);
        }

        return false;
    }

    public boolean isPatientInTreatedState(@NonNull Date visitEndDate) {
        // Get the midnight of that day when the visit happened
        Calendar date = Calendar.getInstance();
        date.setTime(visitEndDate);
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, 1);
        return getDateNow().before(date.getTime());
    }

    @VisibleForTesting
    @NonNull
    protected Date getDateNow() {
        return new Date();
    }
}
