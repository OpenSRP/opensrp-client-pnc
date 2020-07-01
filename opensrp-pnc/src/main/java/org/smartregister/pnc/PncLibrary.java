package org.smartregister.pnc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

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
import org.smartregister.pnc.repository.PncChildRepository;
import org.smartregister.pnc.repository.PncOtherDetailsRepository;
import org.smartregister.pnc.repository.PncRegistrationDetailsRepository;
import org.smartregister.pnc.repository.PncStillBornRepository;
import org.smartregister.pnc.repository.PncVisitChildStatusRepository;
import org.smartregister.pnc.repository.PncVisitInfoRepository;
import org.smartregister.pnc.utils.AppExecutors;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;
import org.smartregister.pnc.utils.FilePath;
import org.smartregister.pnc.utils.PncConstants;
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
import java.util.Date;
import java.util.HashMap;
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
    private PncChildRepository pncChildRepository;
    private PncStillBornRepository pncStillBornRepository;
    private PncVisitInfoRepository pncVisitInfoRepository;
    private PncVisitChildStatusRepository pncVisitChildStatusRepository;
    private PncRegistrationDetailsRepository pncRegistrationDetailsRepository;
    private PncOtherDetailsRepository pncOtherDetailsRepository;
    private AppExecutors appExecutors;

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
    public PncRegistrationDetailsRepository getPncRegistrationDetailsRepository() {
        if (pncRegistrationDetailsRepository == null) {
            pncRegistrationDetailsRepository = new PncRegistrationDetailsRepository();
        }

        return pncRegistrationDetailsRepository;
    }

    public PncChildRepository getPncChildRepository() {
        if (pncChildRepository == null) {
            pncChildRepository = new PncChildRepository();
        }
        return pncChildRepository;
    }

    public PncStillBornRepository getPncStillBornRepository() {
        if (pncStillBornRepository == null) {
            pncStillBornRepository = new PncStillBornRepository();
        }
        return pncStillBornRepository;
    }

    public PncVisitInfoRepository getPncVisitInfoRepository() {
        if (pncVisitInfoRepository == null) {
            pncVisitInfoRepository = new PncVisitInfoRepository();
        }
        return pncVisitInfoRepository;
    }

    public PncVisitChildStatusRepository getPncVisitChildStatusRepository() {
        if (pncVisitChildStatusRepository == null) {
            pncVisitChildStatusRepository = new PncVisitChildStatusRepository();
        }
        return pncVisitChildStatusRepository;
    }

    @NonNull
    public PncOtherDetailsRepository getPncOtherDetailsRepository() {
        if (pncOtherDetailsRepository == null) {
            pncOtherDetailsRepository = new PncOtherDetailsRepository();
        }
        return pncOtherDetailsRepository;
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
    public List<Event> processPncForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        HashMap<String, Class<? extends PncFormProcessingTask>> pncFormProcessingTasks = getPncConfiguration().getPncFormProcessingTasks();
        List<Event> eventList = new ArrayList<>();
        if (pncFormProcessingTasks.get(eventType) != null) {
            PncFormProcessingTask pncFormProcessingTask = ConfigurationInstancesHelper.newInstance(pncFormProcessingTasks.get(eventType));
            eventList = pncFormProcessingTask.processPncForm(eventType, jsonString, data);
        }
        return eventList;
    }

    @NonNull
    public List<Event> processPncCloseForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        ArrayList<Event> eventList = new ArrayList<>();
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONArray fieldsArray = PncUtils.generateFieldsFromJsonForm(jsonFormObject);
        FormTag formTag = PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences());

        String baseEntityId = PncUtils.getIntentValue(data, PncConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = PncUtils.getIntentValue(data, PncConstants.IntentKey.ENTITY_TABLE);
        Event closePncEvent = JsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, eventType, entityTable);
        PncJsonFormUtils.tagSyncMetadata(closePncEvent);
        eventList.add(closePncEvent);

        return eventList;
    }

    @VisibleForTesting
    @NonNull
    protected Date getDateNow() {
        return new Date();
    }

    public AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }
        return appExecutors;
    }
}
