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
        if (builder.pncRegisterProviderMetadata == null) {
            builder.pncRegisterProviderMetadata = BasePncRegisterProviderMetadata.class;
        }
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

    public ArrayList<Class<? extends PncFormProcessingTask>> getPncFormProcessingTasks() {
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
        private Class<? extends PncRegisterProviderMetadata> pncRegisterProviderMetadata;

        @Nullable
        private Class<? extends PncRegisterRowOptions> pncRegisterRowOptions;

        @NonNull
        private Class<? extends PncRegisterQueryProviderContract> pncRegisterQueryProvider;

        @Nullable
        private Class<? extends PncRegisterSwitcher> pncRegisterSwitcher;

        @NonNull
        private ArrayList<Class<? extends PncFormProcessingTask>> pncFormProcessingClasses = new ArrayList<>();

        private boolean isBottomNavigationEnabled;

        private PncMetadata pncMetadata;
        private int maxCheckInDurationInMinutes = 24 * 60;

        public Builder(@NonNull Class<? extends PncRegisterQueryProviderContract> pncRegisterQueryProvider) {
            this.pncRegisterQueryProvider = pncRegisterQueryProvider;
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

        public Builder addPncFormProcessingTask(@NonNull Class<? extends PncFormProcessingTask> pncFormProcessingTask) {
            this.pncFormProcessingClasses.add(pncFormProcessingTask);
            return this;
        }

        public PncConfiguration build() {
            return new PncConfiguration(this);
        }

    }

}
