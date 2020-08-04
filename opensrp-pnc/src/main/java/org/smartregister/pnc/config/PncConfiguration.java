package org.smartregister.pnc.config;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.scheduler.PncVisitScheduler;
import org.smartregister.pnc.scheduler.VisitScheduler;
import org.smartregister.pnc.utils.PncConstants;

import java.util.HashMap;

/**
 * This is the object used to configure any configurations added to Pnc. We mostly use objects that are
 * instantiated using {@link org.smartregister.pnc.utils.ConfigurationInstancesHelper} which means
 * that the constructors of any of the classes should not have any parameters
 * <p>
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncConfiguration {

    private Builder builder;

    private PncConfiguration(@NonNull Builder builder) {
        this.builder = builder;

        setDefaults();
    }

    private void setDefaults() {

        if (builder.visitScheduler == null) {
            builder.visitScheduler = new PncVisitScheduler();
        }

        if (builder.pncRegisterProviderMetadata == null) {
            builder.pncRegisterProviderMetadata = BasePncRegisterProviderMetadata.class;
        }

        if (!builder.pncFormProcessingClasses.containsKey(PncConstants.EventTypeConstants.PNC_OUTCOME)) {
            builder.pncFormProcessingClasses.put(PncConstants.EventTypeConstants.PNC_OUTCOME, PncOutcomeFormProcessing.class);
        }

        if (!builder.pncFormProcessingClasses.containsKey(PncConstants.EventTypeConstants.PNC_VISIT)) {
            builder.pncFormProcessingClasses.put(PncConstants.EventTypeConstants.PNC_VISIT, PncVisitFormProcessing.class);
        }

        if (!builder.pncFormProcessingClasses.containsKey(PncConstants.EventTypeConstants.PNC_CLOSE)) {
            builder.pncFormProcessingClasses.put(PncConstants.EventTypeConstants.PNC_CLOSE, PncCloseFormProcessing.class);
        }
    }

    @NonNull
    public <T extends VisitScheduler> T getPncVisitScheduler() {
        return (T) builder.visitScheduler;
    }

    @Nullable
    public PncMetadata getPncMetadata() {
        return builder.pncMetadata;
    }

    @NonNull
    public Class<? extends PncRegisterProviderMetadata> getPncRegisterProviderMetadata() {
        return builder.pncRegisterProviderMetadata;
    }

    @Nullable
    public Class<? extends PncRegisterRowOptions> getPncRegisterRowOptions() {
        return builder.pncRegisterRowOptions;
    }

    @NonNull
    public Class<? extends PncRegisterQueryProviderContract> getPncRegisterQueryProvider() {
        return builder.pncRegisterQueryProvider;
    }

    @Nullable
    public Class<? extends PncRegisterSwitcher> getPncRegisterSwitcher() {
        return builder.pncRegisterSwitcher;
    }

    public HashMap<String, Class<? extends PncFormProcessingTask>> getPncFormProcessingTasks() {
        return builder.pncFormProcessingClasses;
    }

    public int getMaxCheckInDurationInMinutes() {
        return builder.maxCheckInDurationInMinutes;
    }

    public boolean isBottomNavigationEnabled() {
        return builder.isBottomNavigationEnabled;
    }

    public static class Builder {

        @Nullable
        private VisitScheduler visitScheduler;

        @Nullable
        private Class<? extends PncRegisterProviderMetadata> pncRegisterProviderMetadata;

        @Nullable
        private Class<? extends PncRegisterRowOptions> pncRegisterRowOptions;

        @NonNull
        private Class<? extends PncRegisterQueryProviderContract> pncRegisterQueryProvider;

        @Nullable
        private Class<? extends PncRegisterSwitcher> pncRegisterSwitcher;

        @NonNull
        private HashMap<String, Class<? extends PncFormProcessingTask>> pncFormProcessingClasses = new HashMap<>();

        private boolean isBottomNavigationEnabled;

        private PncMetadata pncMetadata;
        private int maxCheckInDurationInMinutes = 24 * 60;

        public Builder(@NonNull Class<? extends PncRegisterQueryProviderContract> pncRegisterQueryProvider) {
            this.pncRegisterQueryProvider = pncRegisterQueryProvider;
        }

        public Builder setPncVisitScheduler(@Nullable VisitScheduler visitScheduler) {
            this.visitScheduler = visitScheduler;
            return this;
        }

        public Builder setPncRegisterProviderMetadata(@Nullable Class<? extends PncRegisterProviderMetadata> pncRegisterProviderMetadata) {
            this.pncRegisterProviderMetadata = pncRegisterProviderMetadata;
            return this;
        }

        public Builder setPncRegisterRowOptions(@Nullable Class<? extends PncRegisterRowOptions> pncRegisterRowOptions) {
            this.pncRegisterRowOptions = pncRegisterRowOptions;
            return this;
        }

        public Builder setPncRegisterSwitcher(@Nullable Class<? extends PncRegisterSwitcher> pncRegisterSwitcher) {
            this.pncRegisterSwitcher = pncRegisterSwitcher;
            return this;
        }

        public Builder setBottomNavigationEnabled(boolean isBottomNavigationEnabled) {
            this.isBottomNavigationEnabled = isBottomNavigationEnabled;
            return this;
        }

        public Builder setPncMetadata(@NonNull PncMetadata pncMetadata) {
            this.pncMetadata = pncMetadata;
            return this;
        }

        public Builder setMaxCheckInDurationInMinutes(int durationInMinutes) {
            this.maxCheckInDurationInMinutes = durationInMinutes;
            return this;
        }

        public Builder addPncFormProcessingTask(@NonNull String eventType, @NonNull Class<? extends PncFormProcessingTask> pncFormProcessingTask) {
            this.pncFormProcessingClasses.put(eventType, pncFormProcessingTask);
            return this;
        }

        public PncConfiguration build() {
            return new PncConfiguration(this);
        }

    }

}
