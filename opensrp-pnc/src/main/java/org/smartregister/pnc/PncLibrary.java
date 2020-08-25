package org.smartregister.pnc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.json.JSONException;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.config.PncFormProcessingTask;
import org.smartregister.pnc.domain.YamlConfig;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.helper.PncRulesEngineHelper;
import org.smartregister.pnc.repository.PncChildRepository;
import org.smartregister.pnc.repository.PncMedicInfoRepository;
import org.smartregister.pnc.repository.PncOtherVisitRepository;
import org.smartregister.pnc.repository.PncPartialFormRepository;
import org.smartregister.pnc.repository.PncRegistrationDetailsRepository;
import org.smartregister.pnc.repository.PncStillBornRepository;
import org.smartregister.pnc.repository.PncVisitChildStatusRepository;
import org.smartregister.pnc.repository.PncVisitInfoRepository;
import org.smartregister.pnc.scheduler.PncVisitScheduler;
import org.smartregister.pnc.utils.AppExecutors;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;
import org.smartregister.pnc.utils.FilePath;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

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
    private PncOtherVisitRepository pncOtherVisitRepository;
    private PncVisitChildStatusRepository pncVisitChildStatusRepository;
    private PncPartialFormRepository pncPartialFormRepository;
    private PncRegistrationDetailsRepository pncRegistrationDetailsRepository;
    private PncMedicInfoRepository pncMedicInfoRepository;
    private EventClientRepository eventClientRepository;
    private PncVisitScheduler pncVisitScheduler;
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

    @NonNull
    public PncMedicInfoRepository getPncMedicInfoRepository() {
        if (pncMedicInfoRepository == null) {
            pncMedicInfoRepository = new PncMedicInfoRepository();
        }

        return pncMedicInfoRepository;
    }

    public PncVisitScheduler getPncVisitScheduler() {
        if (pncVisitScheduler == null) {
            pncVisitScheduler = ConfigurationInstancesHelper.newInstance(getPncConfiguration().getPncVisitScheduler());
        }
        return pncVisitScheduler;
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

    public PncOtherVisitRepository getPncOtherVisitRepository() {
        if (pncOtherVisitRepository == null) {
            pncOtherVisitRepository = new PncOtherVisitRepository();
        }
        return pncOtherVisitRepository;
    }

    public PncVisitChildStatusRepository getPncVisitChildStatusRepository() {
        if (pncVisitChildStatusRepository == null) {
            pncVisitChildStatusRepository = new PncVisitChildStatusRepository();
        }
        return pncVisitChildStatusRepository;
    }

    public PncPartialFormRepository getPncPartialFormRepository() {
        if (pncPartialFormRepository == null) {
            pncPartialFormRepository = new PncPartialFormRepository();
        }
        return pncPartialFormRepository;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository();
        }
        return eventClientRepository;
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

}
