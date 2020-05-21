package org.smartregister.pnc.config;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.pnc.pojo.PncMetadata;

import java.util.ArrayList;

/**
 * This is the object used to configure any configurations added to Pnc. We mostly use objects that are
 * instantiated using {@link org.smartregister.pnc.utils.ConfigurationInstancesHelper} which means
 * that the constructors of any of the classes should not have any parameters
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncConfiguration {

    private Builder builder;

    private PncConfiguration(@NonNull Builder builder) {
        this.builder = builder;

        setDefaults();
    }

    private void setDefaults() {
        if (builder.maternityRegisterProviderMetadata == null) {
            builder.maternityRegisterProviderMetadata = BasePncRegisterProviderMetadata.class;
        }
    }

    @Nullable
    public PncMetadata getMaternityMetadata() {
        return builder.maternityMetadata;
    }

    @NonNull
    public Class<? extends PncRegisterProviderMetadata> getMaternityRegisterProviderMetadata() {
        return builder.maternityRegisterProviderMetadata;
    }

    @Nullable
    public Class<? extends PncRegisterRowOptions> getMaternityRegisterRowOptions() {
        return builder.maternityRegisterRowOptions;
    }

    @NonNull
    public Class<? extends PncRegisterQueryProviderContract> getMaternityRegisterQueryProvider() {
        return builder.maternityRegisterQueryProvider;
    }

    @Nullable
    public Class<? extends PncRegisterSwitcher> getMaternityRegisterSwitcher() {
        return builder.maternityRegisterSwitcher;
    }

    public ArrayList<Class<? extends PncFormProcessingTask>> getMaternityFormProcessingTasks() {
        return builder.maternityFormProcessingClasses;
    }

    public int getMaxCheckInDurationInMinutes() {
        return builder.maxCheckInDurationInMinutes;
    }

    public boolean isBottomNavigationEnabled() {
        return builder.isBottomNavigationEnabled;
    }

    public static class Builder {

        @Nullable
        private Class<? extends PncRegisterProviderMetadata> maternityRegisterProviderMetadata;

        @Nullable
        private Class<? extends PncRegisterRowOptions> maternityRegisterRowOptions;

        @NonNull
        private Class<? extends PncRegisterQueryProviderContract> maternityRegisterQueryProvider;

        @Nullable
        private Class<? extends PncRegisterSwitcher> maternityRegisterSwitcher;

        @NonNull
        private ArrayList<Class<? extends PncFormProcessingTask>> maternityFormProcessingClasses = new ArrayList<>();

        private boolean isBottomNavigationEnabled;

        private PncMetadata maternityMetadata;
        private int maxCheckInDurationInMinutes = 24 * 60;

        public Builder(@NonNull Class<? extends PncRegisterQueryProviderContract> maternityRegisterQueryProvider) {
            this.maternityRegisterQueryProvider = maternityRegisterQueryProvider;
        }

        public Builder setMaternityRegisterProviderMetadata(@Nullable Class<? extends PncRegisterProviderMetadata> maternityRegisterProviderMetadata) {
            this.maternityRegisterProviderMetadata = maternityRegisterProviderMetadata;
            return this;
        }

        public Builder setMaternityRegisterRowOptions(@Nullable Class<? extends PncRegisterRowOptions> maternityRegisterRowOptions) {
            this.maternityRegisterRowOptions = maternityRegisterRowOptions;
            return this;
        }

        public Builder setMaternityRegisterSwitcher(@Nullable Class<? extends PncRegisterSwitcher> maternityRegisterSwitcher) {
            this.maternityRegisterSwitcher = maternityRegisterSwitcher;
            return this;
        }

        public Builder setBottomNavigationEnabled(boolean isBottomNavigationEnabled) {
            this.isBottomNavigationEnabled = isBottomNavigationEnabled;
            return this;
        }

        public Builder setPncMetadata(@NonNull PncMetadata maternityMetadata) {
            this.maternityMetadata = maternityMetadata;
            return this;
        }

        public Builder setMaxCheckInDurationInMinutes(int durationInMinutes) {
            this.maxCheckInDurationInMinutes = durationInMinutes;
            return this;
        }

        public Builder addMaternityFormProcessingTask(@NonNull Class<? extends PncFormProcessingTask> maternityFormProcessingTask) {
            this.maternityFormProcessingClasses.add(maternityFormProcessingTask);
            return this;
        }

        public PncConfiguration build() {
            return new PncConfiguration(this);
        }

    }

}
